package com.xaeport.crossborder.data.provider;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;

import java.util.Map;

public class WaybillDeclareSQLProvider extends BaseSQLProvider{

    public String queryWaybillDeclareDataList(Map<String, String> paramMap) throws Exception{
        final String startFlightTimes = paramMap.get("startFlightTimes");
        final String endFlightTimes = paramMap.get("endFlightTimes");
        final String logisticsNo = paramMap.get("logisticsNo");
        final String logisticsStatus = paramMap.get("logisticsStatus");
        final String end = paramMap.get("end");
        return new SQL(){
            {
                SELECT("* from ( select rownum rn ,f.* from ( " +
                        " select t.LOGISTICS_NO," +
                        " t.LOGISTICS_NAME,"+
                        " t.CONSINGEE,"+
                        " t.CONSIGNEE_TELEPHONE," +
                        " t.CONSIGNEE_ADDRESS," +
                        " t.DATA_STATUS," +
                        " t.APP_TIME" );
                FROM("T_IMP_LOGISTICS t");
                if(!StringUtils.isEmpty(logisticsNo)){
                    WHERE("t.logistics_no = #{logisticsNo}");
                }
                if (!StringUtils.isEmpty(logisticsStatus)){
                    WHERE("t.data_status = #{logisticsStatus}");
                }
                if(!StringUtils.isEmpty(startFlightTimes)){
                    WHERE("t.app_time >= to_date(#{startFlightTimes}||'00:00:00','yyyy-MM-dd hh24:mi:ss')");
                }
                if(!StringUtils.isEmpty(endFlightTimes)){
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

    public String queryWaybillDeclareCount(Map<String, String> paramMap) throws Exception{
        final String startFlightTimes = paramMap.get("startFlightTimes");
        final String endFlightTimes = paramMap.get("endFlightTimes");
        final String logisticsNo = paramMap.get("logisticsNo");
        final String logisticsStatus = paramMap.get("logisticsStatus");
        return new SQL(){
            {
                SELECT("COUNT(1)");
                FROM("T_IMP_LOGISTICS t");
                if(!StringUtils.isEmpty(logisticsNo)){
                    WHERE("t.LOGISTICS_NO = #{logisticsNo}");
                }
                if (!StringUtils.isEmpty(logisticsStatus)){
                    WHERE("t.DATA_STATUS = #{logisticsStatus}");
                }
                if(!StringUtils.isEmpty(startFlightTimes)){
                    WHERE("t.APP_TIME >= to_date(#{startFlightTimes}||'00:00:00','yyyy-MM-dd hh24:mi:ss')");
                }
                if(!StringUtils.isEmpty(endFlightTimes)){
                    WHERE("t.APP_TIME <= to_date(#{endFlightTimes}||'23:59:59','yyyy-MM-dd hh24:mi:ss')");
                }
            }
        }.toString();

    }
    /*
     * 提交海关
     */
    public String updateSubmitWaybill(Map<String, String> paramMap) {
        final String submitKeys = paramMap.get("submitKeys");
        final String dataStatusWhere = paramMap.get("dataStatusWhere");
        final String dataStatus = paramMap.get("dataStatus");
        return new SQL() {
            {
                UPDATE("T_IMP_LOGISTICS t");
                WHERE(splitJointIn("t.LOGISTICS_NO", submitKeys));
                WHERE(splitJointIn("t.DATA_STATUS", dataStatusWhere));
                SET("t.data_status = #{dataStatus}");
                SET("t.APP_TIME = sysdate");
                SET("t.upd_tm = sysdate");
                SET("t.upd_id = #{currentUserId}");
            }
        }.toString();
    }
    /*
     * 修改运单状态为支付单已申报
     */
    public String updateImpLogisticsStatus(@Param("guid") String guid, @Param("CBDS41") String CBDS41){
        return new SQL(){
            {
                UPDATE("T_IMP_LOGISTICS t");
                WHERE("t.GUID = #{guid}");
                SET("t.DATA_STATUS = 'CBDS41'");
                SET("t.upd_tm = sysdate");
            }
        }.toString();
    }
    /*
     * 运单报文数据查询
     */
    public String findWaitGenerated(final Map<String, String> paramMap) {
        final String dataStatus = paramMap.get("dataStatus");
        return new SQL() {
            {
                SELECT("GUID,APP_TYPE,APP_TIME,APP_STATUS,LOGISTICS_CODE,LOGISTICS_NAME,LOGISTICS_NO");
                SELECT("BILL_NO,to_char(FREIGHT,'FM999999999990.00000') as FREIGHT,to_char(INSURED_FEE,'FM999999999990.00000') as INSURED_FEE,CURRENCY,to_char(WEIGHT,'FM999999999990.00000') as WEIGHT,PACK_NO");
                SELECT("GOODS_INFO,CONSINGEE,CONSIGNEE_ADDRESS,CONSIGNEE_TELEPHONE");
                SELECT("NOTE,DATA_STATUS,CRT_ID,CRT_TM,UPD_ID,UPD_TM");
                FROM("T_IMP_LOGISTICS t");
                WHERE("data_Status = #{dataStatus}");
                WHERE("rownum<=100");
                ORDER_BY("t.CRT_TM asc,t.LOGISTICS_NO asc");
            }
        }.toString();
    }
}
