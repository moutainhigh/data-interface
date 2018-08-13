package com.xaeport.crossborder.service.waybillmanage;

import com.xaeport.crossborder.configuration.AppConfiguration;
import com.xaeport.crossborder.data.entity.Enterprise;
import com.xaeport.crossborder.data.entity.ImpLogisticsStatus;
import com.xaeport.crossborder.data.entity.Users;
import com.xaeport.crossborder.data.mapper.EnterpriseMapper;
import com.xaeport.crossborder.data.mapper.StatusImportMapper;
import com.xaeport.crossborder.data.status.StatusCode;
import com.xaeport.crossborder.tools.IdUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class StatusImportService {

    private Log log = LogFactory.getLog(this.getClass());

    @Autowired
    AppConfiguration appConfiguration;
    @Autowired
    StatusImportMapper statusImportMapper;
    @Autowired
    EnterpriseMapper enterpriseMapper;

    /*
     * 运单状态单导入
     */
    @Transactional
    public int createWaybillForm(Map<String, Object> excelMap , Users user) {
        int flag;
        try {
            String id = user.getId();
            flag = this.createImpLogisticsStatus(excelMap, user);
        } catch (Exception e) {
            flag = 2;
            this.log.error("导入失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return flag;
    }

    /*
     * 创建ImpLogisticsStatus信息
     */
    private int createImpLogisticsStatus(Map<String, Object> excelMap, Users user) throws Exception {
        int flag = 0;
        Enterprise enterprise = enterpriseMapper.getEnterpriseDetail(user.getEnt_Id());
        List<ImpLogisticsStatus> ImpLogisticsStatusList = (List<ImpLogisticsStatus>) excelMap.get("ImpLogisticsStatus");
        for (ImpLogisticsStatus anImpLogisticsStatusList : ImpLogisticsStatusList) {
            ImpLogisticsStatus impLogisticsStatus = this.ImpLogisticsStatusData(anImpLogisticsStatusList, user,enterprise);
            flag = this.statusImportMapper.isRepeatLogisticsStatusNo(impLogisticsStatus);
            if (flag > 0) {
                return 1;
            }
            this.statusImportMapper.insertImpLogisticsStatus(impLogisticsStatus);//查询无订单号则插入ImpOrderHead数据
        }
        return flag;
    }

    /*
     * 查询有无重复物流运单编号
     */
    public int getLogisticsStatusNoCount(Map<String, Object> excelMap) throws Exception{
        int flag = 0;
        List<ImpLogisticsStatus> list = (List<ImpLogisticsStatus>) excelMap.get("ImpLogisticsStatus");
        for(int i=0;i<list.size();i++){
            ImpLogisticsStatus impLogisticsStatus = list.get(i);
            flag = this.statusImportMapper.isRepeatLogisticsStatusNo(impLogisticsStatus);
            if (flag > 0) {
                return 1;
            }
        }
        return flag;
    }

    /**
     * 表自生成信息
     */
    private ImpLogisticsStatus ImpLogisticsStatusData(ImpLogisticsStatus impLogisticsStatus, Users user,Enterprise enterprise) throws Exception {
        impLogisticsStatus.setGuid(IdUtils.getUUId());//企业系统生成36 位唯一序号（英文字母大写）
        impLogisticsStatus.setApp_type("1");//企业报送类型。1-新增2-变更3-删除。默认为1。
        impLogisticsStatus.setApp_status("2");//业务状态:1-暂存,2-申报,默认为2。
        impLogisticsStatus.setLogistics_status("S");//运抵
        impLogisticsStatus.setCrt_id(StringUtils.isEmpty(user.getId()) ? "" : user.getId());//创建人
        impLogisticsStatus.setCrt_tm(new Date());//创建时间
        impLogisticsStatus.setUpd_id(StringUtils.isEmpty(user.getId()) ? "" : user.getId());//更新人
        impLogisticsStatus.setUpd_tm(new Date());//更新时间
        impLogisticsStatus.setUpd_tm(new Date());
        impLogisticsStatus.setData_status(StatusCode.YDZTDSB);//设置为待申报CBDS5,运单状态不用校验
        impLogisticsStatus.setEnt_id(enterprise.getId());
        impLogisticsStatus.setEnt_name(enterprise.getEnt_name());
        impLogisticsStatus.setEnt_customs_code(enterprise.getCustoms_code());
        return impLogisticsStatus;
    }

    public String getLogisticsNoCount(Map<String, Object> excelMap) {
        int flag = 0;
        List<ImpLogisticsStatus> list = (List<ImpLogisticsStatus>) excelMap.get("ImpLogisticsStatus");
        for(int i=0;i<list.size();i++){
            ImpLogisticsStatus impLogisticsStatus = list.get(i);
            flag = this.statusImportMapper.isEmptyLogisticsNo(impLogisticsStatus);
            if (flag == 0) {
                return impLogisticsStatus.getLogistics_no();
            }
        }
        return "true";
    }

    public String getLogisticsSuccess(Map<String, Object> excelMap) {
        int flag = 0;
        List<ImpLogisticsStatus> list = (List<ImpLogisticsStatus>) excelMap.get("ImpLogisticsStatus");
        for(int i=0;i<list.size();i++){
            ImpLogisticsStatus impLogisticsStatus = list.get(i);
            flag = this.statusImportMapper.getLogisticsSuccess(impLogisticsStatus);
            if (flag == 0) {
                return impLogisticsStatus.getLogistics_no();
            }
        }
        return "true";

    }

    public void updateLogisticsStatus(Map<String, Object> excelMap) throws Exception{
        List<ImpLogisticsStatus> list = (List<ImpLogisticsStatus>) excelMap.get("ImpLogisticsStatus");
        for(int i=0;i<list.size();i++){
            ImpLogisticsStatus impLogisticsStatus = list.get(i);
            this.statusImportMapper.updateLogisticsStatus(impLogisticsStatus);
        }
    }

    public void updateLogistics(Map<String, Object> excelMap) {
        List<ImpLogisticsStatus> list = (List<ImpLogisticsStatus>) excelMap.get("ImpLogisticsStatus");
        for(int i=0;i<list.size();i++){
            ImpLogisticsStatus impLogisticsStatus = list.get(i);
            this.statusImportMapper.updateLogistics(impLogisticsStatus);
        }
    }
}
