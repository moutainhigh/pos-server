package com.dianba.pos.box.service;

import com.dianba.pos.base.BasicResult;
import com.dianba.pos.box.po.BoxItemLabel;
import com.dianba.pos.box.vo.BoxItemVo;
import com.dianba.pos.box.vo.ItemStorageVo;

import java.util.List;

/**
 * Created by zhangyong on 2017/7/4.
 */
public interface BoxItemLabelManager {
    /**
     * 无人便利店商品入库
     * 选择录入对应电子标签，记录商品与电子标签对应关系
     *
     * @return
     */
    BasicResult itemStorage(ItemStorageVo itemStorageVo);

    BasicResult showItemsByRFID(Long passportId, String rfids);

    List<BoxItemVo> getItemsByRFID(Long passportId, String rfids);

    List<BoxItemLabel> updateItemLabelToPaid(String rfids);
}
