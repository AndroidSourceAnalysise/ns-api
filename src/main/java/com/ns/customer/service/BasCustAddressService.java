package com.ns.customer.service;

import com.ns.common.model.BasCustAddress;
import com.ns.common.utils.DateUtil;
import com.ns.common.utils.GUIDUtil;
import com.jfinal.plugin.activerecord.Db;

import java.util.*;

public class BasCustAddressService {
    public static final BasCustAddressService me = new BasCustAddressService();
    private final BasCustAddress dao = new BasCustAddress().dao();
    private static final String COLUMN = " ID,CON_ID,TEL,IS_DEFAULT,MOBILE,RECIPIENTS,POSTAL_CODE,ENABLED,VERSION,STATUS,REMARK,CREATE_BY,CREATE_DT,UPDATE_DT," +
            "COUNTRY,PROVINCE,CITY,DISTRICT,ADDRESS ";

    /**
     * 创建新地址
     *
     * @param address
     * @return
     */
    public boolean createAddress(BasCustAddress address) {
        updateIsDefault(address.getIsDefault(), address.getConId());
        address.setID(GUIDUtil.getGUID());
        address.setCreateDt(DateUtil.getNow());
        address.setUpdateDt(DateUtil.getNow());
        return address.save();
    }

    /**
     * 我的地址列表
     *
     * @param conId
     * @return
     */
    public List<BasCustAddress> getAddressList(String conId) {
        List<BasCustAddress> resultList = new ArrayList<>();
        List<BasCustAddress> addressList = dao.find("select " + COLUMN + " from bas_cust_address where con_id = ? and ENABLED = 1 ORDER BY CREATE_DT desc", conId);
        ListIterator<BasCustAddress> listIterator = addressList.listIterator();
        while (listIterator.hasNext()) {
            BasCustAddress address = listIterator.next();
            if (address.getIsDefault() == 1) {
                resultList.add(address);
                listIterator.remove();
            }
        }
        resultList.addAll(addressList);
        return resultList;
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public BasCustAddress getById(String id) {
        return dao.findFirst("select " + COLUMN + " from bas_cust_address where id = ? and ENABLED = 1", id);
    }

    /**
     * 我的默认地址
     *
     * @param conId
     * @return
     */
    public BasCustAddress getDefault(String conId) {
        return dao.findFirst("select " + COLUMN + " from bas_cust_address where con_id = ? and IS_DEFAULT = 1 and ENABLED = 1", conId);
    }

    private void updateIsDefault(int isDefault, String conId) {
        if (isDefault == 1) {
            Db.update("update bas_cust_address set IS_DEFAULT = 0 where con_id = ?", conId);
        }
    }

    public boolean updateAddress(BasCustAddress address) {
        updateIsDefault(address.getIsDefault(), address.getConId());
        address.setUpdateDt(DateUtil.getNow());
        return address.update();
    }

    /**
     * 删除地址
     *
     * @param id
     */
    public boolean deleteAddress(String id) {
        return dao.deleteById(id);
    }
}
