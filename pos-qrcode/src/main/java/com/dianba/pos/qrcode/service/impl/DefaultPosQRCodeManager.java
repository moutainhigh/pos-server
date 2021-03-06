package com.dianba.pos.qrcode.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dianba.pos.base.BasicResult;
import com.dianba.pos.base.config.AppConfig;
import com.dianba.pos.base.exception.PosIllegalArgumentException;
import com.dianba.pos.base.exception.PosNullPointerException;
import com.dianba.pos.common.util.StringUtil;
import com.dianba.pos.qrcode.config.QRCodeURLConstant;
import com.dianba.pos.qrcode.po.PosQRCode;
import com.dianba.pos.qrcode.repository.PosQRCodeJpaRepository;
import com.dianba.pos.qrcode.service.PosQRCodeManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Service
public class DefaultPosQRCodeManager implements PosQRCodeManager {

    @Autowired
    private PosQRCodeJpaRepository posQRCodeJpaRepository;
    @Autowired
    private AppConfig appConfig;

    @Override
    public PosQRCode getQRCodeByMerchantId(Long passportId) {
        PosQRCode posQRCode = posQRCodeJpaRepository.findByMerchantId(passportId);
        if (posQRCode == null) {
            throw new PosNullPointerException("该商家还未绑定二维码！");
        }
        return posQRCode;
    }

    @Override
    public PosQRCode getQRCodeByCode(String code) {
        if (code != null) {
            if (code.length() <= PosQRCode.CODE_LENGTH) {
                code = StringUtil.lpad(code, PosQRCode.CODE_LENGTH, PosQRCode.CODE_APPEND_STR);
                code = PosQRCode.CODE_PREFIX + code;
            }
        } else {
            throw new PosIllegalArgumentException("未包含支付码！");
        }
        PosQRCode posQRCode = posQRCodeJpaRepository.findByCode(code);
        if (posQRCode != null && posQRCode.getMerchantId() != -1) {
            return posQRCode;
        }
        throw new PosNullPointerException("该二维码未绑定商家信息！");
    }

    public void showQRCodeByPassportId(Long passportId, Integer width, Integer height
            , HttpServletResponse response) throws Exception {
        PosQRCode posQRCode = getQRCodeByMerchantId(passportId);
        putQRCodeInOutPutStrem(posQRCode, width, height, response);
    }

    public BasicResult showQRCodeContentByPassportId(Long passportId) throws Exception {
        PosQRCode posQRCode = getQRCodeByMerchantId(passportId);
        BasicResult basicResult = BasicResult.createSuccessResult();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("qrCodeContent", appConfig.getPosCallBackHost()
                + QRCodeURLConstant.QRCODE_URL + posQRCode.getCode());
        basicResult.setResponse(jsonObject);
        return basicResult;
    }

    public void showQRCodeByCode(String code, Integer width, Integer height
            , HttpServletResponse response) throws Exception {
        PosQRCode posQRCode = getQRCodeByCode(code);
        putQRCodeInOutPutStrem(posQRCode, width, height, response);
    }

    private void putQRCodeInOutPutStrem(PosQRCode posQRCode, Integer width, Integer height
            , HttpServletResponse response) throws Exception {
        generateQRCodeByContent(appConfig.getPosCallBackHost() + QRCodeURLConstant.QRCODE_URL
                        + posQRCode.getCode()
                , width, height, response);
    }

    public void generateQRCodeByContent(String content, Integer width, Integer height
            , HttpServletResponse response) throws Exception {
        if (width == null) {
            width = 300;
        }
        if (height == null) {
            height = 300;
        }
        String qrcodeFormat = "png";
        HashMap<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content
                , BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageWriter.writeToStream(bitMatrix, qrcodeFormat, response.getOutputStream());
    }
}
