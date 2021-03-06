package com.xaeport.crossborder.data.provider;

import com.xaeport.crossborder.data.entity.LogInvCombine;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;

import java.util.Map;

public class WaybillDeclareSQLProvider extends BaseSQLProvider {

    public String queryWaybillDeclareDataList(Map<String, String> paramMap) throws Exception {
        final String startFlightTimes = paramMap.get("startFlightTimes");
        final String endFlightTimes = paramMap.get("endFlightTimes");
        final String billNo = paramMap.get("billNo");
        final String dataStatus = paramMap.get("dataStatus");
        final String statusDataStatus = paramMap.get("statusDataStatus");
        final String end = paramMap.get("end");
        final String entId = paramMap.get("entId");
        final String roleId = paramMap.get("roleId");
        return new SQL() {
            {
                SELECT("* from (select rownum rn,f.*from (" +
                        "SELECT" +
                        "    t.bill_no," +
                        "    (" +
                        "        SELECT" +
                        "            COUNT(1)" +
                        "        FROM" +
                        "            t_imp_logistics t2" +
                        "        WHERE" +
                        "            t2.bill_no = t.bill_no" +
                        "    ) totalCount," +
                        "    (select max(t3.app_time) from T_IMP_LOGISTICS t3 where t3.BILL_NO=t.BILL_NO and t3.DATA_STATUS=t.DATA_STATUS) appTime," +
                        "    t.data_status," +
                        "    (" +
                        "        SELECT" +
                        "            COUNT(1)" +
                        "        FROM" +
                        "            t_imp_logistics t4" +
                        "        WHERE" +
                        "            t4.DATA_STATUS = t.DATA_STATUS and t4.bill_no = t.BILL_NO " +
                        "            and (t4.DATA_STATUS like 'CBDS4%' or t4.DATA_STATUS like 'CBDS1%')" +
                        "    ) count1," +
                        "    t.DATA_STATUS sta_data_status," +
                        "    (" +
                        "        SELECT" +
                        "            COUNT(1)" +
                        "        FROM" +
                        "            t_imp_logistics t5" +
                        "        WHERE" +
                        "            t5.DATA_STATUS = t.DATA_STATUS and t5.bill_no = t.BILL_NO " +
                        "            and t5.DATA_STATUS like 'CBDS5%'" +
                        "    ) count2");
                FROM("t_imp_logistics t");
                WHERE("t.WRITING_MODE IS NULL");
                if (!StringUtils.isEmpty(billNo)) {
                    WHERE("t.bill_no = #{billNo}");
                }
                if (!roleId.equals("admin")) {
                    WHERE("t.ent_id = #{entId}");
                }
                if ((!StringUtils.isEmpty(dataStatus)) || (!StringUtils.isEmpty(statusDataStatus))) {
                    WHERE("t.data_status = #{dataStatus} or t.data_status = #{statusDataStatus}");
                }
                if (!StringUtils.isEmpty(startFlightTimes)) {
                    WHERE("t.CRT_TM >= to_date(#{startFlightTimes}||'00:00:00','yyyy-MM-dd hh24:mi:ss')");
                }
                if (!StringUtils.isEmpty(endFlightTimes)) {
                    WHERE("t.CRT_TM <= to_date(#{endFlightTimes}||'23:59:59','yyyy-MM-dd hh24:mi:ss')");
                }
                GROUP_BY("t.bill_no,t.data_status");
                ORDER_BY("t.bill_no asc) f ) WHERE rn >= #{start}");
            }
        }.toString();
    }

    public String queryWaybillDeclareCount(Map<String, String> paramMap) throws Exception {
        final String startFlightTimes = paramMap.get("startFlightTimes");
        final String endFlightTimes = paramMap.get("endFlightTimes");
        final String billNo = paramMap.get("billNo");
        final String dataStatus = paramMap.get("dataStatus");
        final String entId = paramMap.get("entId");
        final String roleId = paramMap.get("roleId");
        return new SQL() {
            {
                SELECT("COUNT(1)");
                FROM("T_IMP_LOGISTICS t LEFT JOIN T_IMP_LOGISTICS_STATUS  ON t.LOGISTICS_NO=T_IMP_LOGISTICS_STATUS.LOGISTICS_NO");
                WHERE("t.WRITING_MODE IS NULL");
                if (!StringUtils.isEmpty(billNo)) {
                    WHERE("t.bill_no = #{billNo}");
                }
                if (!roleId.equals("admin")) {
                    WHERE("t.ent_id = #{entId}");
                }
                if (!StringUtils.isEmpty(dataStatus)) {
                    WHERE(splitJointIn("t.DATA_STATUS", dataStatus));
                }
                if (!StringUtils.isEmpty(startFlightTimes)) {
                    WHERE("t.CRT_TM >= to_date(#{startFlightTimes}||'00:00:00','yyyy-MM-dd hh24:mi:ss')");
                }
                if (!StringUtils.isEmpty(endFlightTimes)) {
                    WHERE("t.CRT_TM <= to_date(#{endFlightTimes}||'23:59:59','yyyy-MM-dd hh24:mi:ss')");
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
                WHERE(splitJointIn("t.bill_no", submitKeys));
                WHERE(splitJointIn("t.DATA_STATUS", dataStatusWhere));
                SET("t.data_status = #{dataStatus}");
                SET("t.APP_TIME = sysdate");
                SET("t.upd_tm = sysdate");
                SET("t.upd_id = #{currentUserId}");
            }
        }.toString();
    }

    /*
     * 提交海关运单状态申报
     */
    public String updateSubmitWaybillToStatus(Map<String, String> paramMap) {
        final String submitKeys = paramMap.get("submitKeys");
        final String dataStatusWhere = paramMap.get("dataStatusWhere");
        final String dataStatus = paramMap.get("dataStatus");
        return new SQL() {
            {
                UPDATE("T_IMP_LOGISTICS t");
                WHERE(splitJointIn("t.bill_no", submitKeys));
                WHERE(splitJointIn("t.DATA_STATUS", dataStatusWhere));
                //SET("t.LOGISTICS_STATUS = ''");
                SET("t.data_status = #{dataStatus}");
                SET("t.APP_TIME = sysdate");
                SET("t.upd_tm = sysdate");
                SET("t.upd_id = #{currentUserId}");
            }
        }.toString();
    }

    /*
     * 修改运单状态为运单正在发往海关
     */
    public String updateImpLogisticsStatus(@Param("guid") String guid, @Param("CBDS41") String CBDS41) {
        return new SQL() {
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
                SELECT("GUID," +
                        "APP_TYPE," +
                        "APP_TIME," +
                        "APP_STATUS," +
                        "LOGISTICS_CODE," +
                        "LOGISTICS_NAME," +
                        "ORDER_NO," +
                        "LOGISTICS_NO");
                SELECT("BILL_NO," +
                        "to_char(FREIGHT,'FM999999999990.00000') as FREIGHT," +
                        "to_char(INSURED_FEE,'FM999999999990.00000') as INSURED_FEE," +
                        "CURRENCY," +
                        "to_char(WEIGHT,'FM999999999990.00000') as WEIGHT,PACK_NO");
                SELECT("GOODS_INFO," +
                        "CONSINGEE,CONSIGNEE_ADDRESS," +
                        "CONSIGNEE_TELEPHONE");
                SELECT("NOTE," +
                        "DATA_STATUS," +
                        "ENT_ID," +
                        "ENT_NAME," +
                        "ENT_CUSTOMS_CODE");
                FROM("T_IMP_LOGISTICS t");
                WHERE("data_Status = #{dataStatus}");
                WHERE("rownum <= 100");
                ORDER_BY("t.CRT_TM asc,t.LOGISTICS_NO asc");

            }
        }.toString();
    }

    /*
     * 运单状态报文数据查询
     */
    public String findWaitGeneratedStatus(final Map<String, String> paramMap) {
        final String dataStatus = paramMap.get("dataStatus");
        return new SQL() {
            {
                SELECT("GUID," +
                        "APP_TYPE," +
                        "APP_TIME," +
                        "APP_STATUS," +
                        "BILL_NO," +
                        "LOGISTICS_CODE," +
                        "LOGISTICS_NAME," +
                        "LOGISTICS_NO," +
                        "LOGISTICS_STATUS," +
                        "LOGISTICS_TIME_CHAR," +
                        "LOGISTICS_TIME");
                SELECT("NOTE," +
                        "DATA_STATUS," +
                        "ENT_ID," +
                        "ENT_NAME," +
                        "ENT_CUSTOMS_CODE");
                FROM("T_IMP_LOGISTICS t");
                WHERE("data_Status = #{dataStatus}");
                WHERE("rownum <= 100");
                ORDER_BY("t.CRT_TM asc,t.LOGISTICS_NO asc");
            }
        }.toString();
    }

    /*
     * 修改运单状态为运单状态正在发往海关
     */
    public String updateToLogisticsStatus(@Param("guid") String guid, @Param("CBDS51") String CBDS51) {
        return new SQL() {
            {
                UPDATE("T_IMP_LOGISTICS_STATUS t");
                //是否需要判断运单是否正在发往海关
                WHERE("t.GUID = #{guid}");
                SET("t.DATA_STATUS = 'CBDS51'");
                SET("t.upd_tm = sysdate");
            }
        }.toString();
    }

    /*
     * 修改运单为运单状态正在发往海关
     */
    public String updateToLogistics(@Param("logisticsNo") String logisticsNo, @Param("CBDS51") String CBDS51) {
        return new SQL() {
            {
                UPDATE("T_IMP_LOGISTICS t");
                //是否需要判断运单是否正在发往海关
                WHERE("t.LOGISTICS_NO = #{logisticsNo}");
                SET("t.DATA_STATUS = 'CBDS51'");
                SET("t.upd_tm = sysdate");
            }
        }.toString();
    }

    /*
    * 查询运单申报是否合格()
    *
    * */
    public String queryDateStatus(@Param("billNo") String billNo) {
        return new SQL() {
            {
                SELECT("count(1) count");
                FROM("T_IMP_LOGISTICS t");
                WHERE("t.bill_no = #{billNo}");
                WHERE("(t.DATA_STATUS = 'CBDS4' or t.DATA_STATUS = 'CBDS1')");
            }
        }.toString();
    }

    /*
    * 查看运单状态是否合格queryStaDateStatus
    * */
    public String queryStaDateStatus(@Param("billNo") String billNo) {
        return new SQL() {
            {
                SELECT("count(1) count");
                FROM("T_IMP_LOGISTICS t");
                WHERE("t.bill_no = #{billNo}");
                WHERE("t.DATA_STATUS ='CBDS5'");
            }
        }.toString();
    }

    /*
     *  queryCompany(@Param("ent_id") String ent_id)
     * 根据企业ID获取企业信息
     * */
    public String queryCompany(@Param("ent_id") String ent_id) {
        return new SQL() {
            {
                SELECT("t.CUSTOMS_CODE as copCode");
                SELECT("t.ENT_NAME as copName");
                SELECT("'DXP' as dxpMode");
                SELECT("t.DXP_ID as dxpId");
                SELECT("t.note as note");
                FROM("T_ENTERPRISE t");
                WHERE("t.id = #{ent_id}");
            }
        }.toString();
    }

    public String queryLogInvCombine(@Param("billNo") String billNo, @Param("orderNo") String orderNo, @Param("logisticsNo") String logisticsNo) {
        return new SQL() {
            {
                SELECT("*");
                FROM("T_LOG_INV_COMBINE");
                WHERE("BILL_NO = #{billNo}");
                WHERE("ORDER_NO = #{orderNo}");
                WHERE("LOGISTICS_NO = #{logisticsNo}");
            }
        }.toString();
    }

    public String updateLogInvCombine(@Param("billNo") String billNo, @Param("orderNo") String orderNo, @Param("logisticsNo") String logisticsNo, @Param("mark") String mark) {
        return new SQL() {
            {
                UPDATE("T_LOG_INV_COMBINE");
                SET("LOGISTICS_MARK = #{mark}");
                WHERE("BILL_NO = #{billNo}");
                WHERE("ORDER_NO = #{orderNo}");
                WHERE("LOGISTICS_NO = #{logisticsNo}");
            }
        }.toString();
    }

    public String insertLogInvCombine(@Param("logInvCombine") LogInvCombine logInvCombine) {
        return new SQL() {
            {
                INSERT_INTO("T_LOG_INV_COMBINE");
                if (!StringUtils.isEmpty(logInvCombine.getId())) {
                    VALUES("ID", "#{logInvCombine.id}");
                }
                if (!StringUtils.isEmpty(logInvCombine.getBill_no())) {
                    VALUES("BILL_NO", "#{logInvCombine.bill_no}");
                }
                if (!StringUtils.isEmpty(logInvCombine.getOrder_no())) {
                    VALUES("ORDER_NO", "#{logInvCombine.order_no}");
                }
                if (!StringUtils.isEmpty(logInvCombine.getLogistics_no())) {
                    VALUES("LOGISTICS_NO", "#{logInvCombine.logistics_no}");
                }
                if (!StringUtils.isEmpty(logInvCombine.getOrder_mark())) {
                    VALUES("ORDER_MARK", "#{logInvCombine.order_mark}");
                }
                if (!StringUtils.isEmpty(logInvCombine.getLogistics_mark())) {
                    VALUES("LOGISTICS_MARK", "#{logInvCombine.logistics_mark}");
                }
                if (!StringUtils.isEmpty(logInvCombine.getId())) {
                    VALUES("CRT_TM", "sysdate");
                }
            }
        }.toString();
    }

}
