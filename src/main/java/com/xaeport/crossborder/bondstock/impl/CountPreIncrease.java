package com.xaeport.crossborder.bondstock.impl;

import com.xaeport.crossborder.bondstock.CountLoader;
import com.xaeport.crossborder.data.entity.*;
import com.xaeport.crossborder.data.mapper.ReceiptMapper;
import com.xaeport.crossborder.tools.BusinessUtils;
import com.xaeport.crossborder.tools.IdUtils;
import com.xaeport.crossborder.tools.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.util.*;

//账册表体预增方法
public class CountPreIncrease implements CountLoader {
    private final Log logger = LogFactory.getLog(this.getClass());

    private ReceiptMapper receiptMapper = SpringUtils.getBean(ReceiptMapper.class);

    List<BondInvtBsc> bondInvtBscList = new ArrayList<>();
    List<BondInvtDt> bondInvtDtList = new ArrayList<>();

    @Override
    public int count(Map<String, Object> excelMap, Users user, String emsNo) {
        return 999;
    }

    @Override
    public void count(PassPortHead passPortHead) {}

    @Override
    public void count(BondInvtBsc bondInvtBsc) {
        //TODO 保税入区进行预增操作
        //获取导入的入区保税清单表头信息
        bondInvtBscList = this.receiptMapper.queryBondInvtBscList(bondInvtBsc);
        //获取导入的入区保税清单表体信息
        bondInvtDtList = this.receiptMapper.queryBondInvtDtList(bondInvtBsc);

        if (!StringUtils.isEmpty(bondInvtBscList) && !StringUtils.isEmpty(bondInvtDtList)) {
            //按照料号获取商品数据
            Map<String, List<BondInvtDt>> gdsMtnoData = BusinessUtils.classifyByGdsMtno(bondInvtDtList);
            //料号
            String gds_mtno = null;
            //账册号
            String emsNo = null;
            for (String gdsMtno : gdsMtnoData.keySet()) {
                List<BondInvtDt> bondInvtDts = new ArrayList<>();
                //获取按照料号划分的入区核注清单表体数据
                bondInvtDts = gdsMtnoData.get(gdsMtno);
                //获取料号
                gds_mtno = bondInvtDts.get(0).getGds_mtno();
                //获取账册号
                emsNo = bondInvtBscList.get(0).getPutrec_no();

                //根据账册号查询是否存在该账册
                BwlHeadType bwlHeadType = this.receiptMapper.checkBwlHeadType(emsNo);
                BwlListType bwlList = this.receiptMapper.checkBwlListType(emsNo, gds_mtno);
                if (!StringUtils.isEmpty(bwlHeadType) && !StringUtils.isEmpty(bwlList)) {
                    double qtySum = bondInvtDts.stream().mapToDouble(BondInvtDt::getQuantity).sum();
                    this.receiptMapper.addBwlListType(qtySum, emsNo, gds_mtno);
                    this.logger.info("入区核注清单成功进行预增叠加操作");
                } else if (!StringUtils.isEmpty(bwlHeadType) && StringUtils.isEmpty(bwlList)) {
                    BwlListType bwlListType = this.crtBwlListType(emsNo, gds_mtno, bondInvtDts);
                    //插入入区账册表体的数据
                    this.receiptMapper.insertBwlListType(bwlListType);
                    this.logger.info("入区核注清单成功进行预增添加操作");
                } else {
                    this.logger.info("入区核注清单解析回执：查询无对应账册信息，无法预增操作");
                    continue;
                }

            }

        }
    }

    //封装数据操作
    public BwlListType crtBwlListType(String emsNo, String gds_mtno, List<BondInvtDt> bondInvtDts) {
        BwlListType bwlListType = new BwlListType();
        BondInvtDt bondInvtDt = bondInvtDts.get(0);
        double qtySum = bondInvtDts.stream().mapToDouble(BondInvtDt::getQuantity).sum();
        bwlListType.setId(IdUtils.getUUId());
        bwlListType.setBws_no(emsNo);
        bwlListType.setGds_mtno(gds_mtno);
        bwlListType.setGdecd(bondInvtDt.getGdecd());
        bwlListType.setGds_nm(bondInvtDt.getGds_nm());
        bwlListType.setDcl_unitcd(bondInvtDt.getDcl_unitcd());
        bwlListType.setIn_qty("0");
        bwlListType.setActl_inc_qty("0");
        bwlListType.setActl_redc_qty("0");
        bwlListType.setPrevd_inc_qty(String.valueOf(qtySum));
        bwlListType.setPrevd_redc_qty("0");
        bwlListType.setCrt_time(new Date());
        bwlListType.setUpd_time(new Date());
        return bwlListType;
    }

}