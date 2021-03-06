package com.xaeport.crossborder.data.provider;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class WaybillQuerySQLProvider extends BaseSQLProvider {

    public String queryWaybillQueryDataList(Map<String, String> paramMap) throws Exception {
        final String startFlightTimes = paramMap.get("startFlightTimes");
        final String endFlightTimes = paramMap.get("endFlightTimes");
        final String logisticsNo = paramMap.get("logisticsNo");
        final String billNo = paramMap.get("billNo");
        final String orderNo = paramMap.get("orderNo");
        final String logisticsStatus = paramMap.get("logisticsStatus");
        final String end = paramMap.get("end");
        final String entId = paramMap.get("entId");
        final String roleId = paramMap.get("roleId");
        final String dataStatus = paramMap.get("dataStatus");
        return new SQL() {
            {
                SELECT("* from ( select rownum rn ,f.* from ( " +
                        " select " +
                        " t.bill_no," +
                        " t.LOGISTICS_NO," +
                        " t.GUID," +
                        " t.LOGISTICS_NAME," +
                        " t.CONSINGEE," +
                        " t.CONSIGNEE_TELEPHONE," +
                        " t.CONSIGNEE_ADDRESS," +
                        " t.DATA_STATUS," +
                        " t.APP_TIME," +
                        " t.RETURN_STATUS as return_status," +
                        " t.RETURN_INFO as return_info," +
                        " t.LOGISTICS_STATUS," +
                        " t.RETURN_STATUS as returnStatus_status," +
                        " t.RETURN_INFO as returnStatus_info," +
                        " t.LOGISTICS_TIME," +
                        " t.LOGISTICS_TIME_CHAR," +
                        " (select ss.status_name from t_status ss " +
                        " where ss.status_code = t.return_status ) return_status_name");
                FROM("T_IMP_LOGISTICS t");
                WHERE("t.WRITING_MODE IS NULL");
                if (!roleId.equals("admin")) {
                    WHERE("t.ent_id = #{entId}");
                }
                WHERE("(t.DATA_STATUS = #{dataStatus} or t.DATA_STATUS = #{staDataStatus})");
                if (!StringUtils.isEmpty(logisticsNo)) {
                    WHERE("t.logistics_no = #{logisticsNo}");
                }
                if (!StringUtils.isEmpty(billNo)) {
                    WHERE("t.bill_no = #{billNo}");
                }
                if (!StringUtils.isEmpty(orderNo)) {
                    WHERE("t.order_no = #{orderNo}");
                }
                if (!StringUtils.isEmpty(logisticsStatus)) {
                    if (logisticsStatus.equals("-")) {
                        WHERE("(t.return_status like '%'||#{logisticsStatus}||'%' or t.rec_return_status like '%'||#{logisticsStatus}||'%')");
                    } else {
                        WHERE("(t.return_status = #{logisticsStatus} or t.rec_return_status = #{logisticsStatus})");
                    }
                }
                if (!StringUtils.isEmpty(startFlightTimes)) {
                    WHERE("t.app_time >= to_date(#{startFlightTimes}||'00:00:00','yyyy-MM-dd hh24:mi:ss')");
                }
                if (!StringUtils.isEmpty(endFlightTimes)) {
                    WHERE("t.app_time <= to_date(#{endFlightTimes}||'23:59:59','yyyy-MM-dd hh24:mi:ss')");
                }
                if (!"-1".equals(end)) {
                    ORDER_BY("t.app_time desc ) f  ) WHERE rn between #{start} and #{end}");
                } else {
                    ORDER_BY("t.app_time desc ) f  ) WHERE rn >= #{start}");
                }
            }
        }.toString();
    }

    public String queryWaybillQueryCount(Map<String, String> paramMap) throws Exception {
        final String startFlightTimes = paramMap.get("startFlightTimes");
        final String endFlightTimes = paramMap.get("endFlightTimes");
        final String logisticsNo = paramMap.get("logisticsNo");
        final String billNo = paramMap.get("billNo");
        final String orderNo = paramMap.get("orderNo");
        final String logisticsStatus = paramMap.get("logisticsStatus");
        final String entId = paramMap.get("entId");
        final String roleId = paramMap.get("roleId");
        final String dataStatus = paramMap.get("dataStatus");
        return new SQL() {
            {
                SELECT("count(1) count");
                FROM("T_IMP_LOGISTICS t");
                WHERE("t.WRITING_MODE IS NULL");
                WHERE("(t.DATA_STATUS = #{dataStatus} or t.DATA_STATUS = #{staDataStatus})");
                if (!roleId.equals("admin")) {
                    WHERE("t.ent_id = #{entId}");
                }
                if (!StringUtils.isEmpty(logisticsNo)) {
                    WHERE("t.logistics_no = #{logisticsNo}");
                }
                if (!StringUtils.isEmpty(billNo)) {
                    WHERE("t.bill_no = #{billNo}");
                }
                if (!StringUtils.isEmpty(orderNo)) {
                    WHERE("t.order_no = #{orderNo}");
                }
                if (!StringUtils.isEmpty(logisticsStatus)) {
                    if (logisticsStatus.equals("-")) {
                        WHERE("(t.return_status like '%'||#{logisticsStatus}||'%' or t.rec_return_status like '%'||#{logisticsStatus}||'%')");
                    } else {
                        WHERE("(t.return_status = #{logisticsStatus} or t.rec_return_status = #{logisticsStatus})");
                    }
                }
                if (!StringUtils.isEmpty(startFlightTimes)) {
                    WHERE("t.app_time >= to_date(#{startFlightTimes}||'00:00:00','yyyy-MM-dd hh24:mi:ss')");
                }
                if (!StringUtils.isEmpty(endFlightTimes)) {
                    WHERE("t.app_time <= to_date(#{endFlightTimes}||'23:59:59','yyyy-MM-dd hh24:mi:ss')");
                }
            }
        }.toString();

    }

    public String seeWaybillDetail(Map<String, String> paramMap) {
        final String guid = paramMap.get("guid");
        final String logisticsno = paramMap.get("logisticsno");

        return new SQL() {
            {
                SELECT("t.GUID as guid");
                SELECT("t.APP_TYPE as appType");
                SELECT("t.APP_TIME as appTime");
                SELECT("t.APP_STATUS as appStatus");
                SELECT("t.LOGISTICS_CODE as logisticscode");
                SELECT("t.LOGISTICS_NAME as logisticsname");
                SELECT("t.LOGISTICS_NO as logisticsno");
                SELECT("t.LOGISTICS_TIME as logisticsTime");
                SELECT("t.LOGISTICS_TIME_CHAR as logisticsTimeChar");
                SELECT("t.ORDER_NO as orderno");
                SELECT("t.BILL_NO as billno");
                SELECT("t.FREIGHT as freight");
                SELECT("t.INSURED_FEE as insuredfee");
                SELECT("t.CURRENCY as currency");
                SELECT("t.WEIGHT as weight");
                SELECT("t.PACK_NO as packno");
                SELECT("t.GOODS_INFO as goodsinfo");
                SELECT("t.CONSINGEE as consingee");
                SELECT("t.CONSIGNEE_ADDRESS as consigneeaddress");
                SELECT("t.CONSIGNEE_TELEPHONE as consigneetelephone");
                SELECT("t.NOTE as note");
                SELECT("t.DATA_STATUS as dataStatus");
                SELECT("t.CRT_ID as crtId");
                SELECT("t.CRT_TM as crtTm");
                SELECT("t.UPD_ID as updId");
                SELECT("t.UPD_TM as updTm");
                FROM("T_IMP_LOGISTICS t");
                if (!StringUtils.isEmpty(guid)) {
                    WHERE("t.GUID = #{guid}");
                }
               /* if (!StringUtils.isEmpty(logisticsno)){
                    WHERE("t.ORDER_NO = #{logisticsno}");
                }*/
            }
        }.toString();
    }

    public String updateLogistics(LinkedHashMap<String, String> entryHead) {
        return new SQL() {
            {
                UPDATE("T_IMP_LOGISTICS t");
                WHERE("t.GUID = #{entryhead_guid}");
                SET("t.APP_TYPE = '2'");
                SET("t.DATA_STATUS = 'CBDS4'");
                SET("t.UPD_TM = sysdate");
                if (!StringUtils.isEmpty(entryHead.get("bill_no"))) {
                    SET("t.BILL_NO = #{bill_no}");
                }
                if (!StringUtils.isEmpty(entryHead.get("order_no"))) {
                    SET("t.ORDER_NO = #{order_no}");
                }
                if (!StringUtils.isEmpty(entryHead.get("logistics_no"))) {
                    SET("t.LOGISTICS_NO = #{logistics_no}");
                }
                if (!StringUtils.isEmpty(entryHead.get("logistics_code"))) {
                    SET("t.LOGISTICS_CODE = #{logistics_code}");
                }
                if (!StringUtils.isEmpty(entryHead.get("logistics_name"))) {
                    SET("t.LOGISTICS_NAME = #{logistics_name}");
                }
                if (!StringUtils.isEmpty(entryHead.get("logistics_time"))) {
                    SET("t.LOGISTICS_TIME_CHAR = #{logistics_time}");
                }
                if (!StringUtils.isEmpty(entryHead.get("consingee"))) {
                    SET("t.CONSINGEE = #{consingee}");
                }
                if (!StringUtils.isEmpty(entryHead.get("consignee_telephone"))) {
                    SET("t.CONSIGNEE_TELEPHONE = #{consignee_telephone}");
                }
                if (!StringUtils.isEmpty(entryHead.get("consignee_address"))) {
                    SET("t.CONSIGNEE_ADDRESS = #{consignee_address}");
                }
                if (!StringUtils.isEmpty(entryHead.get("freight"))) {
                    SET("t.FREIGHT = #{freight}");
                }
                if (!StringUtils.isEmpty(entryHead.get("insured_fee"))) {
                    SET("t.INSURED_FEE = #{insured_fee}");
                }
                if (!StringUtils.isEmpty(entryHead.get("pack_no"))) {
                    SET("t.PACK_NO = #{pack_no}");
                }
                if (!StringUtils.isEmpty(entryHead.get("weight"))) {
                    SET("t.WEIGHT = #{weight}");
                }
                if (!StringUtils.isEmpty(entryHead.get("note"))) {
                    SET("t.NOTE = #{note}");
                }
            }
        }.toString();
    }

    public String queryReturnDetail(Map<String, String> paramMap) {
        final String guId = paramMap.get("guId");
        final String logisticsNo = paramMap.get("logisticsNo");

        return new SQL() {
            {
                SELECT("t.BILL_NO");
                SELECT("t.LOGISTICS_NO");
                SELECT("t.RETURN_STATUS");
                SELECT("t.RETURN_INFO");
                SELECT("t.RETURN_TIME");
                SELECT("t.REC_RETURN_STATUS");
                SELECT("t.REC_RETURN_INFO");
                SELECT("t.REC_RETURN_TIME");
                FROM("T_IMP_LOGISTICS t");
                if (!StringUtils.isEmpty(guId)) {
                    WHERE("t.GUID = #{guId}");
                }
                if (!StringUtils.isEmpty(logisticsNo)) {
                    WHERE("t.logistics_no = #{logisticsNo}");
                }
            }
        }.toString();
    }

    public String updateLogisticsByLogic(LinkedHashMap<String, String> entryHead) {
        return new SQL() {
            {
                UPDATE("T_IMP_LOGISTICS t");
                WHERE("t.GUID = #{entryhead_guid}");
                SET("t.UPD_TM = sysdate");
                if (!StringUtils.isEmpty(entryHead.get("bill_no"))) {
                    SET("t.BILL_NO = #{bill_no}");
                }
                if (!StringUtils.isEmpty(entryHead.get("order_no"))) {
                    SET("t.ORDER_NO = #{order_no}");
                }
                if (!StringUtils.isEmpty(entryHead.get("logistics_no"))) {
                    SET("t.LOGISTICS_NO = #{logistics_no}");
                }
                if (!StringUtils.isEmpty(entryHead.get("logistics_code"))) {
                    SET("t.LOGISTICS_CODE = #{logistics_code}");
                }
                if (!StringUtils.isEmpty(entryHead.get("logistics_name"))) {
                    SET("t.LOGISTICS_NAME = #{logistics_name}");
                }
                if (!StringUtils.isEmpty(entryHead.get("logistics_time"))) {
                    SET("t.LOGISTICS_TIME_CHAR = #{logistics_time}");
                }
                if (!StringUtils.isEmpty(entryHead.get("consingee"))) {
                    SET("t.CONSINGEE = #{consingee}");
                }
                if (!StringUtils.isEmpty(entryHead.get("consignee_telephone"))) {
                    SET("t.CONSIGNEE_TELEPHONE = #{consignee_telephone}");
                }
                if (!StringUtils.isEmpty(entryHead.get("consignee_address"))) {
                    SET("t.CONSIGNEE_ADDRESS = #{consignee_address}");
                }
                if (!StringUtils.isEmpty(entryHead.get("freight"))) {
                    SET("t.FREIGHT = #{freight}");
                }
                if (!StringUtils.isEmpty(entryHead.get("insured_fee"))) {
                    SET("t.INSURED_FEE = #{insured_fee}");
                }
                if (!StringUtils.isEmpty(entryHead.get("pack_no"))) {
                    SET("t.PACK_NO = #{pack_no}");
                }
                if (!StringUtils.isEmpty(entryHead.get("weight"))) {
                    SET("t.WEIGHT = #{weight}");
                }
                if (!StringUtils.isEmpty(entryHead.get("note"))) {
                    SET("t.NOTE = #{note}");
                }
            }
        }.toString();
    }

    public String queryVerifyDetail(Map<String, String> paramMap) {
        final String guid = paramMap.get("guid");
        return new SQL() {
            {
                SELECT("t.CB_HEAD_ID");
                SELECT("t.STATUS");
                SELECT("t.RESULT");
                FROM("T_VERIFY_STATUS t");
                WHERE("t.CB_HEAD_ID = #{guid}");
                WHERE("t.STATUS = 'N'");
            }
        }.toString();
    }

    public String queryImpLogisticsByBillNo(Map<String, String> paramMap) {
        final String billNo = paramMap.get("billNo");
        final String dataStatus = paramMap.get("dataStatus");
        return new SQL() {
            {
                SELECT("t.*");
                FROM("T_IMP_LOGISTICS t");
                WHERE("t.BILL_NO = #{billNo}");
                WHERE(splitJointIn("t.DATA_STATUS", dataStatus));
            }
        }.toString();
    }


}
