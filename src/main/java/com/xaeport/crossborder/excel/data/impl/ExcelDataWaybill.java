package com.xaeport.crossborder.excel.data.impl;

import com.xaeport.crossborder.data.entity.ImpLogistics;
import com.xaeport.crossborder.excel.data.ExcelData;
import com.xaeport.crossborder.excel.headings.ExcelHeadWaybill;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.*;

public class ExcelDataWaybill implements ExcelData {
    private Log log = LogFactory.getLog(this.getClass());
    private int orderNoIndex; //物流运单编号";//head
    private int logisticsNoIndex; //物流运单编号";//head
    private int logisticsCodeIndex; //物流企业代码";//head
    private int logisticsNameIndex; //物流企业名称";//head
    private int consigneeIndex; //收货人姓名";//head
    private int consigneeTelephoneIndex; //收货人电话";//head
    private int consigneeAddressIndex; //收件地址";//head
    private int freightIndex; //运费";//head
    private int insuredFeeIndex; //保价费";//head
    private int grossWeightIndex; //毛重";//head
    private int noteIndex; //备注";//head


    public Map<String, Object> getExcelData(List<List<String>> excelData) throws Exception {
        long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        this.getIndexValue(excelData.get(0));//初始化表头索引
        List<ImpLogistics> impLogisticsList = this.getImpLogisticsData(excelData);
        map.put("ImpLogistics", impLogisticsList);
        this.log.debug("封装数据耗时" + (System.currentTimeMillis() - start));
        return map;
    }

    /**
     * 封装ImpLogistics方法
     */
    public List<ImpLogistics> getImpLogisticsData(List<List<String>> excelData) {
        List<ImpLogistics> listData = new ArrayList<>();
        ImpLogistics impLogistics;
        DecimalFormat df = new DecimalFormat("0.00000");
        for (int i = 1; i < excelData.size(); i++) {
            impLogistics = new ImpLogistics();
            impLogistics.setOrder_no(excelData.get(i).get(orderNoIndex));//物流运单编号
            impLogistics.setLogistics_no(excelData.get(i).get(logisticsNoIndex));//物流运单编号
            impLogistics.setLogistics_code(excelData.get(i).get(logisticsCodeIndex));//物流企业代码
            impLogistics.setLogistics_name(excelData.get(i).get(logisticsNameIndex));//物流企业名称
            impLogistics.setConsingee(excelData.get(i).get(consigneeIndex));//收货人姓名
            impLogistics.setConsignee_telephone(excelData.get(i).get(consigneeTelephoneIndex));//收货人电话
            impLogistics.setConsignee_address(excelData.get(i).get(consigneeAddressIndex));//收件地址
            impLogistics.setNote(excelData.get(i).get(noteIndex));//备注
            impLogistics.setFreight(getDouble(excelData.get(i).get(freightIndex)));//运费
            impLogistics.setInsured_fee(getDouble(excelData.get(i).get(insuredFeeIndex)));//保价费
            impLogistics.setWeight(getDouble(excelData.get(i).get(grossWeightIndex)));//毛重
            listData.add(impLogistics);
        }
        return listData;
    }

    /**
     * 初始化索引值
     *
     * @param waybillLists
     */
    public void getIndexValue(List<String> waybillLists) {
        orderNoIndex = waybillLists.indexOf(ExcelHeadWaybill.orderNo);
        logisticsNoIndex = waybillLists.indexOf(ExcelHeadWaybill.logisticsNo);
        logisticsCodeIndex = waybillLists.indexOf(ExcelHeadWaybill.logisticsCode);
        logisticsNameIndex = waybillLists.indexOf(ExcelHeadWaybill.logisticsName);
        consigneeIndex = waybillLists.indexOf(ExcelHeadWaybill.consignee);
        consigneeTelephoneIndex = waybillLists.indexOf(ExcelHeadWaybill.consigneeTelephone);
        consigneeAddressIndex = waybillLists.indexOf(ExcelHeadWaybill.consigneeAddress);
        freightIndex = waybillLists.indexOf(ExcelHeadWaybill.freight);
        insuredFeeIndex = waybillLists.indexOf(ExcelHeadWaybill.insuredFee);
        grossWeightIndex = waybillLists.indexOf(ExcelHeadWaybill.grossWeight);
        noteIndex = waybillLists.indexOf(ExcelHeadWaybill.note);
    }


    protected String getString(String str) {
        if (!StringUtils.isEmpty(str)) {
            return str;
        } else {
            return "";
        }

    }

    protected String getDouble(String str) {
        DecimalFormat df = new DecimalFormat("0.00000");
        if (!StringUtils.isEmpty(str)) {
            return df.format(Double.parseDouble(str));
        } else {
            return "0";
        }
    }
}
