package com.dianba.pos.payment.service;

import com.dianba.pos.base.BasicResult;

public interface WapPaymentManager {

    BasicResult wechatPay(String sequenceNumber, String spBillCreateIP) throws Exception;
}