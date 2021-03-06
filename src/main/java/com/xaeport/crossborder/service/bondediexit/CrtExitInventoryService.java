package com.xaeport.crossborder.service.bondediexit;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.xaeport.crossborder.configuration.SystemConstants;
import com.xaeport.crossborder.data.entity.*;
import com.xaeport.crossborder.data.mapper.CrtExitInventoryMapper;
import com.xaeport.crossborder.data.mapper.EnterpriseMapper;
import com.xaeport.crossborder.tools.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class CrtExitInventoryService {

    @Autowired
    CrtExitInventoryMapper crtExitInventoryMapper;
    @Autowired
    EnterpriseMapper enterpriseMapper;

    private Log logger = LogFactory.getLog(this.getClass());

    //查询进口保税清单数据
    public List<ImpInventory> queryCrtEInventoryData(Map<String, String> paramMap) throws Exception {
        return this.crtExitInventoryMapper.queryCrtEInventoryData(paramMap);
    }

    //查询进口保税清单数据
    public List<ImpInventory> queryCrtEInventoryList(Map<String, String> paramMap) throws Exception {
        return this.crtExitInventoryMapper.queryCrtEInventoryList(paramMap);
    }

    //查询进口保税清单总数
    public Integer queryCrtEInventoryCount(Map<String, String> paramMap) throws Exception {
        return this.crtExitInventoryMapper.queryCrtEInventoryCount(paramMap);
    }

    public String queryCustomsByEndId(String ent_id) {
        return this.crtExitInventoryMapper.queryCustomsByEndId(ent_id);
    }

    //获取出区核注清单表头数据
    public BondInvtBsc queryBondInvtBsc(Map<String, String> paramMap) throws Exception {
        Enterprise enterprise = enterpriseMapper.getEnterpriseDetail(paramMap.get("ent_id"));
        DclEtps etps = crtExitInventoryMapper.queryPriorityEnt(enterprise.getId());
        BondInvtBsc bondInvtBsc = new BondInvtBsc();
        bondInvtBsc.setId(IdUtils.getUUId());
        bondInvtBsc.setBusiness_type(SystemConstants.T_BOND_INVT);
        bondInvtBsc.setEtps_inner_invt_no(paramMap.get("etps_inner_invt_no"));
        bondInvtBsc.setBizop_etpsno(enterprise.getCustoms_code());
        bondInvtBsc.setBizop_etps_nm(enterprise.getEnt_name());
        if (etps == null) {
            bondInvtBsc.setDcl_etpsno("");
            bondInvtBsc.setDcl_etps_nm("");
        } else {
            bondInvtBsc.setDcl_etpsno(etps.getDcl_etps_customs_code());
            bondInvtBsc.setDcl_etps_nm(etps.getDcl_etps_name());
        }
        bondInvtBsc.setRcvgd_etpsno(enterprise.getCustoms_code());
        bondInvtBsc.setRcvgd_etps_nm(enterprise.getEnt_name());
        bondInvtBsc.setInvt_no(paramMap.get("billNo"));
        bondInvtBsc.setDcl_plc_cuscd(this.crtExitInventoryMapper.queryDcl_plc_cuscd(paramMap.get("ent_id")));
        bondInvtBsc.setImpexp_portcd(bondInvtBsc.getDcl_plc_cuscd());//IMPEXP_PORTCD
        bondInvtBsc.setPutrec_no(this.crtExitInventoryMapper.queryBws_no(paramMap.get("ent_id")));
        bondInvtBsc.setStship_trsarv_natcd("142");
        switch (bondInvtBsc.getDcl_plc_cuscd()) {
            case "9007":
                bondInvtBsc.setTrsp_modecd("7");
                break;
            case "9013":
                bondInvtBsc.setTrsp_modecd("W");
                break;
        }
        return bondInvtBsc;
    }

    //获取出区核注清单表体数据
    public List<NemsInvtCbecBillType> queryNemsInvtCbecBillTypeList(Map<String, String> paramMap) throws Exception {
//        List<String> guidStrs = this.crtExitInventoryMapper.queryGuidByBillNos(paramMap);
//        String guids = String.join(",", guidStrs);
//        List<ImpInventoryHead> impInventoryHeadList = this.crtExitInventoryMapper.queryInvtNos(guids);

        List<ImpInventory> impInventoryHeadList = this.crtExitInventoryMapper.queryGuidByBillNos(paramMap);

        List<NemsInvtCbecBillType> nemsInvtCbecBillTypeList = new ArrayList<>();
        NemsInvtCbecBillType nemsInvtCbecBillType;
        for (int i = 0; i < impInventoryHeadList.size(); i++) {
            nemsInvtCbecBillType = new NemsInvtCbecBillType();
            nemsInvtCbecBillType.setId(IdUtils.getUUId());
            nemsInvtCbecBillType.setNo(i + 1);
            nemsInvtCbecBillType.setSeq_no("");
            nemsInvtCbecBillType.setBond_invt_no("");
//            nemsInvtCbecBillType.setCbec_bill_no(impInventoryHeadList.get(i).getInvt_no());
            nemsInvtCbecBillType.setHead_etps_inner_invt_no(paramMap.get("etps_inner_invt_no"));
            nemsInvtCbecBillType.setBill_no(impInventoryHeadList.get(i).getBill_no());
            nemsInvtCbecBillType.setCount(impInventoryHeadList.get(i).getAsscount());
            nemsInvtCbecBillTypeList.add(nemsInvtCbecBillType);
        }
        return nemsInvtCbecBillTypeList;
    }

    //保存进口出区核注清单表头及表体数据
    public Map<String, String> saveExitBondInvt(
            LinkedHashMap<String, String> BondInvtBsc,
            ArrayList<LinkedHashMap<String, String>> nemsInvtCbecBillTypeList,
            Users userInfo,
            String ebcCode
    ) {
        Map<String, String> map = new HashMap<String, String>();
        String billNostr = BondInvtBsc.get("invt_no");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("roleId", userInfo.getRoleId());
        paramMap.put("port", userInfo.getPort());
        paramMap.put("businessType", SystemConstants.T_IMP_BOND_INVEN);
        paramMap.put("returnStatus", "800");
        paramMap.put("ebcCode", ebcCode);
        paramMap.put("billNo", billNostr);

        List<ImpInventory> impInventoryList = this.crtExitInventoryMapper.queryListByBillNos(paramMap);
        NemsInvtCbecBillType nemsInvtCbecBillType;
        double grossWt = 0;
        double netWt = 0;
        if (!CollectionUtils.isEmpty(impInventoryList)) {
            // 更新表体数据
            for (int i = 0; i < impInventoryList.size(); i++) {
                nemsInvtCbecBillType = new NemsInvtCbecBillType();
                nemsInvtCbecBillType.setId(IdUtils.getUUId());
                nemsInvtCbecBillType.setNo(i + 1);
                nemsInvtCbecBillType.setSeq_no("");
                nemsInvtCbecBillType.setBond_invt_no("");
                nemsInvtCbecBillType.setCbec_bill_no(impInventoryList.get(i).getInvt_no());
                nemsInvtCbecBillType.setHead_etps_inner_invt_no(BondInvtBsc.get("etps_inner_invt_no"));
                nemsInvtCbecBillType.setBill_no(impInventoryList.get(i).getBill_no());
                this.crtExitInventoryMapper.saveNemsInvtCbecBillType(nemsInvtCbecBillType, userInfo);
                grossWt += Double.valueOf(StringUtils.isEmpty(impInventoryList.get(i).getGross_weight()) ? "0" : impInventoryList.get(i).getGross_weight());
                netWt += Double.valueOf(StringUtils.isEmpty(impInventoryList.get(i).getNet_weight()) ? "0" : impInventoryList.get(i).getNet_weight());
            }
            String etpsInnerInvtNo = BondInvtBsc.get("etps_inner_invt_no");
            List<NemsInvtCbecBillType> NemsInvtCbecBillType = this.crtExitInventoryMapper.queryNemsInvtCbecBillList(etpsInnerInvtNo);

            for (int k = 0; k < NemsInvtCbecBillType.size(); k++) {
                String invtNo = NemsInvtCbecBillType.get(k).getCbec_bill_no();
                List<ImpInventoryBody> impInventoryBodyList = crtExitInventoryMapper.queryImpInventoryBodyList(invtNo);
                for (ImpInventoryBody impInventoryBody : impInventoryBodyList) {
                    InvtListType invtListType = new InvtListType();
                    invtListType.setId(IdUtils.getUUId());
                    invtListType.setHeadEtpsInnerInvtNo(etpsInnerInvtNo);
                    invtListType.setGdsSeqno(String.valueOf(impInventoryBody.getG_num()));
                    invtListType.setPutrecSeqno(impInventoryBody.getItem_record_no());
                    invtListType.setGdsMtno(impInventoryBody.getItem_no());
                    invtListType.setGdecd(impInventoryBody.getG_code());
                    invtListType.setGdsNm(impInventoryBody.getG_name());
                    invtListType.setGdsSpcfModelDesc(impInventoryBody.getG_model());
                    invtListType.setDclUnitcd(impInventoryBody.getUnit());
                    invtListType.setLawfUnitcd(impInventoryBody.getUnit1());
                    invtListType.setDclUprcAmt(impInventoryBody.getPrice());
                    invtListType.setDclTotalAmt(impInventoryBody.getTotal_price());
                    invtListType.setDclCurrcd(impInventoryBody.getCurrency());
                    invtListType.setLawfQty(impInventoryBody.getQty1());
                    invtListType.setDclQty(impInventoryBody.getQty());
                    invtListType.setNatcd(impInventoryBody.getCountry());
                    invtListType.setLvyrlfModecd("1");
                    invtListType.setModfMarkcd("3");
                    invtListType.setDestinationNatcd("142");
                    this.crtExitInventoryMapper.insertInvtListType(invtListType);
                }
            }
            BondInvtBsc.put("gross_wt", String.valueOf(grossWt));
            BondInvtBsc.put("net_wt", String.valueOf(netWt));
            this.crtExitInventoryMapper.saveBondInvtBsc(BondInvtBsc, userInfo);
        }

        this.crtExitInventoryMapper.updateInventoryDataByBondInvt(BondInvtBsc, ebcCode, userInfo);
        map.put("result", "true");
        map.put("msg", "编辑成功，请到“出区核注清单”处进行后续操作");
        return map;
    }

    //查询电商企业
    public List<Enterprise> queryEbusinessEnt(Map<String, String> paramMap) throws Exception {
        return this.crtExitInventoryMapper.queryEbusinessEnt(paramMap);
    }


}
