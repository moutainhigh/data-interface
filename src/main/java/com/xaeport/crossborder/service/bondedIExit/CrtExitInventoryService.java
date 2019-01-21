package com.xaeport.crossborder.service.bondedIExit;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.xaeport.crossborder.data.entity.*;
import com.xaeport.crossborder.data.mapper.CrtExitInventoryMapper;
import com.xaeport.crossborder.tools.IdUtils;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CrtExitInventoryService {

    @Autowired
    CrtExitInventoryMapper crtExitInventoryMapper;

    private Log logger = LogFactory.getLog(this.getClass());

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
        BondInvtBsc bondInvtBsc = new BondInvtBsc();
        bondInvtBsc.setId(IdUtils.getUUId());
        bondInvtBsc.setEtps_inner_invt_no(paramMap.get("etps_inner_invt_no"));
        bondInvtBsc.setBizop_etpsno(paramMap.get("bizop_etpsno"));
        bondInvtBsc.setBizop_etps_nm(paramMap.get("bizop_etps_nm"));
        bondInvtBsc.setDcl_etpsno(paramMap.get("dcl_etpsno"));
        bondInvtBsc.setDcl_etps_nm(paramMap.get("dcl_etps_nm"));
        bondInvtBsc.setInvt_no(paramMap.get("invtNo"));
//        bondInvtBsc.setBizop_etps_sccd(paramMap.get("ent_code"));
//        bondInvtBsc.setDcl_etps_sccd(paramMap.get("ent_code"));
//        bondInvtBsc.setRvsngd_etps_sccd(paramMap.get("ent_code"));
        bondInvtBsc.setDcl_plc_cuscd(this.crtExitInventoryMapper.queryDcl_plc_cuscd(paramMap.get("ent_id")));
        bondInvtBsc.setPutrec_no(this.crtExitInventoryMapper.queryBws_no(paramMap.get("ent_id")));
        return bondInvtBsc;
    }

    //获取出区核注清单表体数据
    public List<NemsInvtCbecBillType> queryNemsInvtCbecBillTypeList(Map<String, String> paramMap) throws Exception {
        String InvtNos = paramMap.get("invtNo");
        List<String> guidStrs = this.crtExitInventoryMapper.queryGuidByInvtNos(InvtNos);
        String guids = String.join(",", guidStrs);
        List<ImpInventoryHead> impInventoryHeadList = this.crtExitInventoryMapper.queryInvtNos(guids);
        List<NemsInvtCbecBillType> nemsInvtCbecBillTypeList = new ArrayList<>();
        NemsInvtCbecBillType nemsInvtCbecBillType;
        for (int i = 0; i < impInventoryHeadList.size(); i++) {
            nemsInvtCbecBillType = new NemsInvtCbecBillType();
            nemsInvtCbecBillType.setId(IdUtils.getUUId());
            nemsInvtCbecBillType.setNo(i + 1);
            nemsInvtCbecBillType.setSeq_no("");
            nemsInvtCbecBillType.setBond_invt_no("");
            nemsInvtCbecBillType.setCbec_bill_no(impInventoryHeadList.get(i).getInvt_no());
            nemsInvtCbecBillType.setHead_etps_inner_invt_no(paramMap.get("etps_inner_invt_no"));
            nemsInvtCbecBillTypeList.add(nemsInvtCbecBillType);
        }
        return nemsInvtCbecBillTypeList;
    }

    //保存进口出区核注清单表头及表体数据
    public Map<String, String> saveExitBondInvt(LinkedHashMap<String, String> BondInvtBsc, ArrayList<LinkedHashMap<String, String>> nemsInvtCbecBillTypeList, Users userInfo) {
        Map<String, String> map = new HashMap<String, String>();

        this.crtExitInventoryMapper.updateInventoryDataByBondInvt(BondInvtBsc);
        this.crtExitInventoryMapper.saveBondInvtBsc(BondInvtBsc, userInfo);

        if (!CollectionUtils.isEmpty(nemsInvtCbecBillTypeList)) {
            // 更新表体数据
            for (LinkedHashMap<String, String> nemsInvtCbecBillType : nemsInvtCbecBillTypeList) {
                if (!CollectionUtils.isEmpty(nemsInvtCbecBillType)) {
                    this.crtExitInventoryMapper.saveNemsInvtCbecBillType(nemsInvtCbecBillType, userInfo);
                }
            }
        }
        map.put("result", "true");
        map.put("msg", "编辑成功，请到“出区核注清单”处进行后续操作");
        return map;
    }


}
