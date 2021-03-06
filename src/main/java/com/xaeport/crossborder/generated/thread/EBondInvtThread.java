package com.xaeport.crossborder.generated.thread;

import com.xaeport.crossborder.configuration.AppConfiguration;
import com.xaeport.crossborder.convert.exitbondinvt.EBaseBondInvtXML;
import com.xaeport.crossborder.data.entity.*;
import com.xaeport.crossborder.data.mapper.ExitInventoryMapper;
import com.xaeport.crossborder.data.status.StatusCode;
import com.xaeport.crossborder.tools.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * 进口出区核注清单报文
 */
public class EBondInvtThread implements Runnable {

    private Log logger = LogFactory.getLog(this.getClass());
    private ExitInventoryMapper exitInventoryMapper;
    private AppConfiguration appConfiguration;
    private EBaseBondInvtXML eBaseBondInvtXML;

    //无参数的构造方法。
    public EBondInvtThread() {
    }

    //有参数的构造方法。
    public EBondInvtThread(ExitInventoryMapper exitInventoryMapper, AppConfiguration appConfiguration, EBaseBondInvtXML eBaseBondInvtXML) {
        this.exitInventoryMapper = exitInventoryMapper;
        this.appConfiguration = appConfiguration;
        this.eBaseBondInvtXML = eBaseBondInvtXML;
    }

    @Override
    public void run() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("status", StatusCode.CQHZQDSBZ);//在map中添加状态（dataStatus）为：出区核注清单申报中（BDDS20）
        InvtMessage invtMessage = new InvtMessage();
        List<BondInvtBsc> bondInvtBscList;
        List<BondInvtDt> bondInvtDtList;
        List<NemsInvtCbecBillType> nemsInvtCbecBillTypeList;
        InvtHeadType invtHeadType;
        InvtListType invtListType;
        ExitInvtListType exitInvtListType;
        List<ExitInvtListType> exitInvtListTypeList;
        List<InvtListType> invtListTypeList;
        String headEtpsInnerInvtNo = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String xmlName;

        while (true) {

            try {
                bondInvtBscList = exitInventoryMapper.findWaitGenerated(paramMap);

                if (CollectionUtils.isEmpty(bondInvtBscList)) {
                    // 如无待生成数据，则等待3s后重新确认
                    try {
                        Thread.sleep(3000);
                        logger.debug("未发现需生成出区核注清单报文的数据，中等待3秒");
                    } catch (InterruptedException e) {
                        logger.error("出区核注清单报文生成器暂停时发生异常", e);
                    }
                    continue;
                }

                for (BondInvtBsc bondInvtBsc : bondInvtBscList) {
                    try {
                        invtHeadType = new InvtHeadType();
                        invtListTypeList = new ArrayList<>();
                        xmlName = bondInvtBsc.getEtps_inner_invt_no();
                        headEtpsInnerInvtNo = bondInvtBsc.getEtps_inner_invt_no();
                        invtHeadType.setBondInvtNo(bondInvtBsc.getBond_invt_no());
                        invtHeadType.setPutrecNo(bondInvtBsc.getPutrec_no());
                        invtHeadType.setEtpsInnerInvtNo(bondInvtBsc.getEtps_inner_invt_no());
                        invtHeadType.setBizopEtpsSccd(bondInvtBsc.getBizop_etps_sccd());
                        invtHeadType.setBizopEtpsno(bondInvtBsc.getBizop_etpsno());
                        invtHeadType.setBizopEtpsNm(bondInvtBsc.getBizop_etps_nm());
                        invtHeadType.setRcvgdEtpsno(bondInvtBsc.getRcvgd_etpsno());
                        invtHeadType.setRvsngdEtpsSccd(bondInvtBsc.getRvsngd_etps_sccd());
                        invtHeadType.setRcvgdEtpsNm(bondInvtBsc.getRcvgd_etps_nm());
                        invtHeadType.setDclEtpsSccd(bondInvtBsc.getDcl_etps_sccd());
                        invtHeadType.setDclEtpsno(bondInvtBsc.getDcl_etpsno());
                        invtHeadType.setDclEtpsNm(bondInvtBsc.getDcl_etps_nm());
                        invtHeadType.setInvtDclTime(sdf.format(bondInvtBsc.getInvt_dcl_time()));
                        invtHeadType.setImpexpPortcd(bondInvtBsc.getImpexp_portcd());
                        invtHeadType.setDclPlcCuscd(bondInvtBsc.getDcl_plc_cuscd());
                        invtHeadType.setImpexpMarkcd(bondInvtBsc.getImpexp_markcd());
                        invtHeadType.setMtpckEndprdMarkcd(bondInvtBsc.getMtpck_endprd_markcd());
                        invtHeadType.setSupvModecd(bondInvtBsc.getSupv_modecd());
                        invtHeadType.setTrspModecd(bondInvtBsc.getTrsp_modecd());
                        invtHeadType.setDclcusFlag(bondInvtBsc.getDclcus_flag());
                        invtHeadType.setDclcusTypecd(bondInvtBsc.getDclcus_typecd());
                        invtHeadType.setVrfdedMarkcd(bondInvtBsc.getVrfded_markcd());
                        invtHeadType.setInputCode(bondInvtBsc.getDcl_etpsno());
                        invtHeadType.setInputName(bondInvtBsc.getDcl_etps_nm());
                        invtHeadType.setListStat("");
                        invtHeadType.setCorrEntryDclEtpsNo(bondInvtBsc.getDcl_etpsno());
                        invtHeadType.setCorrEntryDclEtpsNm(bondInvtBsc.getDcl_etps_nm());
                        invtHeadType.setDecType("");
                        invtHeadType.setAddTime(sdf.format(bondInvtBsc.getInvt_dcl_time()));
                        invtHeadType.setStshipTrsarvNatcd(bondInvtBsc.getStship_trsarv_natcd());
                        invtHeadType.setInvtType("8");
                        try {
                            // 更新清单状态
                            this.exitInventoryMapper.updateBondInvtBscStatus(headEtpsInnerInvtNo, StatusCode.CQHZQDYSB);
                            this.logger.debug(String.format("成功更新出区核注清单[headEtpsInnerInvtNo: %s]状态为: %s", headEtpsInnerInvtNo, StatusCode.CQHZQDYSB));
                        } catch (Exception e) {
                            String exceptionMsg = String.format("更新出区核注清单[headEtpsInnerInvtNo: %s]状态时异常", headEtpsInnerInvtNo);
                            this.logger.error(exceptionMsg, e);
                        }

                        nemsInvtCbecBillTypeList = this.exitInventoryMapper.queryBondInvtListByHeadNo(headEtpsInnerInvtNo);

                        for (int j = 0; j < nemsInvtCbecBillTypeList.size(); j++) {
                            invtListType = new InvtListType();
                            invtListType.setBondInvtNo(StringUtils.isEmpty(nemsInvtCbecBillTypeList.get(j).getBond_invt_no()) ? "" : nemsInvtCbecBillTypeList.get(j).getBond_invt_no());
                            invtListType.setSeqNo(StringUtils.isEmpty(nemsInvtCbecBillTypeList.get(j).getSeq_no()) ? "" : nemsInvtCbecBillTypeList.get(j).getSeq_no());
                            invtListType.setCbecBillNo(nemsInvtCbecBillTypeList.get(j).getCbec_bill_no());
                            invtListTypeList.add(invtListType);
                        }

                        bondInvtDtList = this.exitInventoryMapper.queryExitInvtListType(headEtpsInnerInvtNo);
                        exitInvtListTypeList = new ArrayList<>();
                        for (BondInvtDt bondInvtDt : bondInvtDtList) {
                            exitInvtListType = new ExitInvtListType();
                            exitInvtListType.setGdsSeqno(String.valueOf(bondInvtDt.getGds_seqno()));
                            exitInvtListType.setPutrecSeqno(String.valueOf(bondInvtDt.getPutrec_seqno()));
                            exitInvtListType.setGdsMtno(bondInvtDt.getGds_mtno());
                            exitInvtListType.setGdecd(bondInvtDt.getGdecd());
                            exitInvtListType.setGdsNm(bondInvtDt.getGds_nm());
                            exitInvtListType.setGdsSpcfModelDesc(bondInvtDt.getGds_spcf_model_desc());
                            exitInvtListType.setDclUnitcd(bondInvtDt.getDcl_unitcd());
                            exitInvtListType.setLawfUnitcd(bondInvtDt.getLawf_unitcd());
                            exitInvtListType.setNatcd(bondInvtDt.getNatcd());
                            exitInvtListType.setDclUprcAmt(bondInvtDt.getDcl_uprc_amt());
                            exitInvtListType.setDclTotalAmt(bondInvtDt.getDcl_total_amt());
                            exitInvtListType.setDclCurrcd(bondInvtDt.getDcl_currcd());
                            exitInvtListType.setLawfQty(bondInvtDt.getLawf_qty());
                            exitInvtListType.setDclQty(bondInvtDt.getDcl_qty());
                            exitInvtListType.setLvyrlfModecd(bondInvtDt.getLvyrlf_modecd());
                            exitInvtListType.setModfMarkcd(bondInvtDt.getModf_markcd());
                            exitInvtListType.setDestinationNatcd(bondInvtDt.getDestination_natcd());
                            exitInvtListTypeList.add(exitInvtListType);
                        }

                        invtMessage.setInvtHeadType(invtHeadType);
                        invtMessage.setExitInvtListTypeList(exitInvtListTypeList);
                        invtMessage.setInvtListTypeList(invtListTypeList);
                        invtMessage.setOperCusRegCode(invtHeadType.getDclEtpsno());
                        invtMessage.setSysId("Z8");

                        //开始生成报文
                        this.entryProcess(invtMessage, xmlName, bondInvtBsc);

                    } catch (Exception e) {
                        String exceptionMsg = String.format("封装出区核注清单报文数据[headEtpsInnerInvtNo: %s]异常", headEtpsInnerInvtNo);
                        this.logger.error(exceptionMsg, e);
                    }
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(5000);
                    logger.error("生成出区核注清单报文时异常，等待5秒重新开始获取数据", e);
                } catch (InterruptedException ie) {
                    logger.error("出区核注清单报文生成器暂停时发生异常", ie);
                }
            }
        }
    }

    private void entryProcess(InvtMessage invtMessage, String xmlName, BondInvtBsc bondInvtBsc) throws TransformerException, IOException {
        try {
            // 生成申报报文
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String fileName = "INVT_" + "Exit_" + xmlName + "_" + sdf.format(new Date()) + ".xml";
            EnvelopInfo envelopInfo = this.setEnvelopInfo(xmlName, bondInvtBsc);
            byte[] xmlByte = this.eBaseBondInvtXML.createXML(invtMessage, "EBondInvt", envelopInfo);//flag
            saveXmlFile(fileName, xmlByte);
            this.logger.debug(String.format("成功生成出区核注清单清单报文[fileName: %s]", fileName));
        } catch (Exception e) {
            String exceptionMsg = String.format("处理出区核注清单报文[headGuid: %s]异常", xmlName);
            this.logger.error(exceptionMsg, e);
        }
    }

    private void saveXmlFile(String fileName, byte[] xmlByte) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String backFilePath = this.appConfiguration.getXmlPath().get("sendBakPath") + File.separator + "ExitBondinvt" + File.separator + sdf.format(new Date()) + File.separator + fileName;
        this.logger.debug(String.format("出区核注清单报文发送备份文件[backFilePath: %s]", backFilePath));

        String sendFilePath = this.appConfiguration.getXmlPath().get("sascebPath") + File.separator + fileName;
        this.logger.debug(String.format("出区核注清单报文发送文件[sascebPath: %s]", sendFilePath));

        String sendWmsFilePath = this.appConfiguration.getXmlPath().get("sendWmsPath") + File.separator + fileName;
        this.logger.debug(String.format("出区核注清单报文发送WMS[sendWmsPath: %s]", sendWmsFilePath));

        File backupFile = new File(backFilePath);
        FileUtils.save(backupFile, xmlByte);
        this.logger.debug(String.format("出区核注清单报文发送备份文件[backFilePath: %s]生成完毕", backFilePath));

        File sendFile = new File(sendFilePath);
        FileUtils.save(sendFile, xmlByte);
        this.logger.info("出区核注清单发送完毕" + fileName);
        this.logger.debug(String.format("出区核注清单报文发送文件[sascebPath: %s]生成完毕", sendFilePath));

        File sendWmsFile = new File(sendWmsFilePath);
        FileUtils.save(sendWmsFile, xmlByte);
        this.logger.info("出区核注清单发送完毕" + fileName);
        this.logger.debug(String.format("出区核注清单报文发送WMS[sendWmsPath: %s]生成完毕", sendWmsFilePath));
    }

    private EnvelopInfo setEnvelopInfo(String xmlName, BondInvtBsc bondInvtBsc) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = "INVT_" + "Exit_" + xmlName + "_" + sdf.format(new Date()) + ".zip";
        SimpleDateFormat sdfXml = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        EnvelopInfo envelopInfo = new EnvelopInfo();
        envelopInfo.setVersion("1.0");
        envelopInfo.setMessage_id(bondInvtBsc.getEtps_inner_invt_no());
        envelopInfo.setFile_name(fileName);
        envelopInfo.setMessage_type("INV101");
        envelopInfo.setSender_id(this.exitInventoryMapper.getDxpId(bondInvtBsc.getCrt_ent_id()));
        envelopInfo.setReceiver_id("DXPEDCSAS0000001");
        envelopInfo.setSend_time(sdfXml.format(bondInvtBsc.getInvt_dcl_time()));
        envelopInfo.setIc_Card(this.exitInventoryMapper.getDclEtpsIcCard(bondInvtBsc.getCrt_ent_id(), bondInvtBsc.getDcl_etpsno()));
        return envelopInfo;
    }
}
