package com.xaeport.crossborder.data.provider;


import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class PaymentDeclareSQLProvider extends BaseSQLProvider {

    /*
     * 支付单申报数据查询
	 */
    public String queryPaymentDeclareList(Map<String, String> paramMap) throws Exception {

        final String orderNo = paramMap.get("orderNo");
        final String payTransactionId = paramMap.get("payTransactionId");
        final String end = paramMap.get("end");

        return new SQL() {
            {
                SELECT(
                        " * from ( select rownum rn, f.* from ( " +
                        " SELECT " +
                        "    t.PAY_TRANSACTION_ID," +
                        "    t.ORDER_NO," +
                        "    t.PAY_NAME," +
                        "    t.EBP_NAME," +
                        "    t.PAYER_NAME," +
                        "    t.AMOUNT_PAID," +
                        "    t.PAY_TIME," +
                        "    t.NOTE," +
                        "    t.RETURN_STATUS,"+
                        "    t.DATA_STATUS");
                FROM ("T_IMP_PAYMENT t");
                if (!StringUtils.isEmpty(orderNo)) {
                    WHERE("t.order_no = #{orderNo}");
                }
                if (!StringUtils.isEmpty(payTransactionId)) {
                    WHERE("t.PAY_TRANSACTION_ID = #{payTransactionId}");
                }
                if (!"-1".equals(end)) {
                    ORDER_BY("t.upd_tm desc ) f  )  WHERE rn between #{start} and #{end}");
                } else {
                    ORDER_BY("t.upd_tm desc ) f  )  WHERE rn >= #{start}");
                }
            }
        }.toString();
    }

    /*
     * 支付单申报总数查询
     */
    public String queryPaymentDeclareCount(Map<String,String> paramMap) throws Exception {

        final String orderNo = paramMap.get("orderNo");
        final String payTransactionId = paramMap.get("payTransactionId");

        return new SQL() {
            {
                SELECT("COUNT(1)");
                FROM("T_IMP_PAYMENT t");
                if (!StringUtils.isEmpty(orderNo)) {
                    WHERE("t.ORDER_NO = #{orderNo}");
                }
                if (!StringUtils.isEmpty(payTransactionId)) {
                    WHERE("t.PAY_TRANSACTION_ID = #{payTransactionId}");
                }
            }
        }.toString();
    }
}
