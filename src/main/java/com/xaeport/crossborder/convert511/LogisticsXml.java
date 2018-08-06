package com.xaeport.crossborder.convert511;


import com.xaeport.crossborder.data.entity.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 生成支付单报文
 * Created by zwj on 2017/07/18.
 */
@Component
public class LogisticsXml {

    private Log log = LogFactory.getLog(this.getClass());

    /**
     * 构建EntryList 节点
     */
    public void getLogisticsList(Document document, Element rootElement, CEB511Message ceb511Message) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        List<LogisticsHead> logisticsHeadsList = ceb511Message.getLogisticsHeadList();

        Element Logistics;
        Element LogisticsHead;
        Element guid;
        Element appType;
        Element appTime;
        Element appStatus;
        Element logisticsCode;
        Element logisticsName;
        Element logisticsNo;
        Element billNo;
        Element freight;
        Element insuredFee;
        Element currency;
        Element weight;
        Element packNo;
        Element goodsInfo;
        Element consignee;
        Element consigneeAddress;
        Element consigneeTelephone;
        Element note;

        for (int i = 0; i < logisticsHeadsList.size(); i++) {

            Logistics = document.createElement("ceb:Logistics");
            LogisticsHead = document.createElement("ceb:LogisticsHead");

            guid = document.createElement("ceb:guid");
            guid.setTextContent(logisticsHeadsList.get(i).getGuid());

            appType = document.createElement("ceb:appType");
            appType.setTextContent(logisticsHeadsList.get(i).getAppType());

            appTime = document.createElement("ceb:appTime");
            appTime.setTextContent(logisticsHeadsList.get(i).getAppTime());

            appStatus = document.createElement("ceb:appStatus");
            appStatus.setTextContent(logisticsHeadsList.get(i).getAppStatus());

            logisticsCode = document.createElement("ceb:logisticsCode");
            logisticsCode.setTextContent(logisticsHeadsList.get(i).getLogisticsCode());

            logisticsName = document.createElement("ceb:logisticsName");
            logisticsName.setTextContent(logisticsHeadsList.get(i).getLogisticsName());

            logisticsNo = document.createElement("ceb:logisticsNo");
            logisticsNo.setTextContent(logisticsHeadsList.get(i).getLogisticsNo());

            billNo = document.createElement("ceb:billNo");
            billNo.setTextContent(logisticsHeadsList.get(i).getBillNo());

            freight = document.createElement("ceb:freight");
            freight.setTextContent(logisticsHeadsList.get(i).getFreight());

            insuredFee = document.createElement("ceb:insuredFee");
            insuredFee.setTextContent(logisticsHeadsList.get(i).getInsuredFee());

            currency = document.createElement("ceb:currency");
            currency.setTextContent(logisticsHeadsList.get(i).getCurrency());

            weight = document.createElement("ceb:weight");
            weight.setTextContent(logisticsHeadsList.get(i).getWeight());

            packNo = document.createElement("ceb:packNo");
            packNo.setTextContent(logisticsHeadsList.get(i).getPackNo());

            goodsInfo = document.createElement("ceb:goodsInfo");
            goodsInfo.setTextContent(logisticsHeadsList.get(i).getGoodsInfo());

            consignee = document.createElement("ceb:consignee");
            consignee.setTextContent(logisticsHeadsList.get(i).getConsingee());

            consigneeAddress = document.createElement("ceb:consigneeAddress");
            consigneeAddress.setTextContent(logisticsHeadsList.get(i).getConsigneeAddress());

            consigneeTelephone = document.createElement("ceb:consigneeTelephone");
            consigneeTelephone.setTextContent(logisticsHeadsList.get(i).getConsigneeTelephone());

            note = document.createElement("ceb:note");
            note.setTextContent(logisticsHeadsList.get(i).getNote());

            LogisticsHead.appendChild(guid);
            LogisticsHead.appendChild(appType);
            LogisticsHead.appendChild(appTime);
            LogisticsHead.appendChild(appStatus);
            LogisticsHead.appendChild(logisticsCode);
            LogisticsHead.appendChild(logisticsName);
            LogisticsHead.appendChild(logisticsNo);
            LogisticsHead.appendChild(billNo);
            LogisticsHead.appendChild(freight);
            LogisticsHead.appendChild(insuredFee);
            LogisticsHead.appendChild(currency);
            LogisticsHead.appendChild(weight);
            LogisticsHead.appendChild(packNo);
            LogisticsHead.appendChild(goodsInfo);
            LogisticsHead.appendChild(consignee);
            LogisticsHead.appendChild(consigneeAddress);
            LogisticsHead.appendChild(consigneeTelephone);
            LogisticsHead.appendChild(note);

            Logistics.appendChild(LogisticsHead);

            rootElement.appendChild(Logistics);

        }
    }

    //    public Element getEntryHead(Document document, SignedData signedData) {
//        Element entryHead = document.createElement("EntryHead");
//        EntryHead entryInfo = signedData.getEntryHead();
//        List<Element> list = new ArrayList<>();
//        if (entryInfo != null) {
//            this.getEntryHeadChildren(document, signedData, list);
//        }
//        for (int i = 0; i < list.size(); i++) {
//            entryHead.appendChild(list.get(i));
//        }
//        return entryHead;
//    }

//    /**
//     * 构建EntryHead 节点
//     */
//    public void getEntryHeadChildren(Document document, SignedData signedData, List<Element> list) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        SimpleDateFormat sdfSfm = new SimpleDateFormat("yyyyMMddhhmmss");
//        Element opType = document.createElement("OpType");
//        opType.setTextContent(signedData.getEntryHead().getOptype());
//        list.add(opType);
//        Element preEntryId = document.createElement("PreEntryId");
//        preEntryId.setTextContent(signedData.getEntryHead().getPre_entry_id());
//        list.add(preEntryId);
//        Element entryId = document.createElement("EntryId");
//        entryId.setTextContent(signedData.getEntryHead().getEntry_id());
//        list.add(entryId);
//        Element iEFlag = document.createElement("IEFlag");
//        iEFlag.setTextContent(signedData.getEntryHead().getI_e_flag());
//        list.add(iEFlag);
//        Element iEPort = document.createElement("IEPort");
//        iEPort.setTextContent(signedData.getEntryHead().getI_e_port());
//        list.add(iEPort);
//        Element iEDate = document.createElement("IEDate");
//        iEDate.setTextContent(sdf.format(signedData.getEntryHead().getI_e_date()));
//        list.add(iEDate);
//        Element dDate = document.createElement("DDate");
//        dDate.setTextContent(sdfSfm.format(signedData.getEntryHead().getD_date()));
//        list.add(dDate);
//        Element destinationPort = document.createElement("DestinationPort");
//        destinationPort.setTextContent(signedData.getEntryHead().getDestination_port());
//        list.add(destinationPort);
//        Element trafName = document.createElement("TrafName");
//        trafName.setTextContent(signedData.getEntryHead().getTraf_name());
//        list.add(trafName);
//        Element voyageNo = document.createElement("VoyageNo");
//        voyageNo.setTextContent(signedData.getEntryHead().getVoyage_no());
//        list.add(voyageNo);
//        Element trafMode = document.createElement("TrafMode");
//        trafMode.setTextContent(signedData.getEntryHead().getTraf_mode());
//        list.add(trafMode);
//        Element tradeCo = document.createElement("TradeCo");
//        tradeCo.setTextContent(signedData.getEntryHead().getTrade_co());
//        list.add(tradeCo);
//        Element tradeName = document.createElement("TradeName");
//        tradeName.setTextContent(signedData.getEntryHead().getTrade_name());
//        list.add(tradeName);
//        Element districtCode = document.createElement("DistrictCode");
//        districtCode.setTextContent(signedData.getEntryHead().getDistrict_code());
//        list.add(districtCode);
//        Element ownerCode = document.createElement("OwnerCode");
//        ownerCode.setTextContent(signedData.getEntryHead().getOwner_code());
//        list.add(ownerCode);
//        Element ownerName = document.createElement("OwnerName");
//        ownerName.setTextContent(signedData.getEntryHead().getOwner_name());
//        list.add(ownerName);
//
//
//        Element agentCode = document.createElement("AgentCode");
//        agentCode.setTextContent(signedData.getEntryHead().getAgent_code());
//        list.add(agentCode);
//
//        Element agentName = document.createElement("AgentName");
//        agentName.setTextContent(signedData.getEntryHead().getAgent_name());
//        list.add(agentName);
//
//        Element contrNo = document.createElement("ContrNo");
//        contrNo.setTextContent(signedData.getEntryHead().getContr_no());
//        list.add(contrNo);
//
//        Element billNo = document.createElement("BillNo");
//        billNo.setTextContent(signedData.getEntryHead().getBill_no());
//        list.add(billNo);
//
//        Element assBillNo = document.createElement("AssBillNo");
//        assBillNo.setTextContent(signedData.getEntryHead().getAss_bill_no());
//        list.add(assBillNo);
//
//        Element tradeCountry = document.createElement("TradeCountry");
//        tradeCountry.setTextContent(signedData.getEntryHead().getTrade_country());
//        list.add(tradeCountry);
//
//        Element tradeMode = document.createElement("TradeMode");
//        tradeMode.setTextContent(signedData.getEntryHead().getTrade_mode());
//        list.add(tradeMode);
//
//        Element cutMode = document.createElement("CutMode");
//        cutMode.setTextContent(signedData.getEntryHead().getCut_mode());
//        list.add(cutMode);
//
//        Element transMode = document.createElement("TransMode");
//        transMode.setTextContent(signedData.getEntryHead().getTrans_mode());
//        list.add(transMode);
//
//        Element feeMark = document.createElement("FeeMark");
//        feeMark.setTextContent(signedData.getEntryHead().getFee_mark());
//        list.add(feeMark);
//
//        Element feeCurr = document.createElement("FeeCurr");
//        feeCurr.setTextContent(signedData.getEntryHead().getFee_curr());
//        list.add(feeCurr);
//
//        Element feeRate = document.createElement("FeeRate");
//        if (signedData.getEntryHead().getFee_rate() != 0) {
//            feeRate.setTextContent(String.valueOf(signedData.getEntryHead().getFee_rate()));
//        }
//        list.add(feeRate);
//
//        Element insurMark = document.createElement("InsurMark");
//        insurMark.setTextContent(signedData.getEntryHead().getInsur_mark());
//        list.add(insurMark);
//
//        Element insurCurr = document.createElement("InsurCurr");
//        insurCurr.setTextContent(signedData.getEntryHead().getInsur_curr());
//        list.add(insurCurr);
//
//        Element insurRate = document.createElement("InsurRate");
//        if (signedData.getEntryHead().getInsur_rate() != 0) {
//            insurRate.setTextContent(String.valueOf(signedData.getEntryHead().getInsur_rate()));
//        }
//        list.add(insurRate);
//
//        Element OtherMark = document.createElement("OtherMark");
//        OtherMark.setTextContent(signedData.getEntryHead().getOther_mark());
//        list.add(OtherMark);
//
//        Element otherCurr = document.createElement("OtherCurr");
//        otherCurr.setTextContent(signedData.getEntryHead().getOther_curr());
//        list.add(otherCurr);
//
//        Element otherRate = document.createElement("OtherRate");
//        if (signedData.getEntryHead().getOther_rate() != 0) {
//            otherRate.setTextContent(String.valueOf(signedData.getEntryHead().getOther_rate()));
//        }
//        list.add(otherRate);
//
//        Element packNo = document.createElement("PackNo");
//        packNo.setTextContent(String.valueOf(signedData.getEntryHead().getPack_no()));
//        list.add(packNo);
//
//        Element grossWt = document.createElement("GrossWt");
//        grossWt.setTextContent(String.valueOf(signedData.getEntryHead().getGross_wt()));
//        list.add(grossWt);
//
//        Element netWt = document.createElement("NetWt");
//        netWt.setTextContent(String.valueOf(signedData.getEntryHead().getNet_wt()));
//        list.add(netWt);
//
//        Element wrapType = document.createElement("WrapType");
//        wrapType.setTextContent(signedData.getEntryHead().getWrap_type());
//        list.add(wrapType);
//
//        Element noteS = document.createElement("NoteS");
//        noteS.setTextContent(signedData.getEntryHead().getNote_s());
//        list.add(noteS);
//
//        Element declPort = document.createElement("DeclPort");
//        declPort.setTextContent(signedData.getEntryHead().getDecl_port());
//        list.add(declPort);
//
//        Element coOwner = document.createElement("CoOwner");
//        if (signedData.getEntryHead().getEntry_type().equals(SystemConstant.ENTRY_TYPE_C)) {
//            String str = signedData.getEntryHead().getTrade_co();
//            if (!StringUtils.isEmpty(str)) {
//                coOwner.setTextContent(str.substring(5, 6)); // B类该字段必须为空；C类不能为空（截取Trade_co的第六位）
//            }
//        }
//        list.add(coOwner);
//
//        Element relativeId = document.createElement("RelativeId");
//        relativeId.setTextContent(signedData.getEntryHead().getRelative_id());
//        list.add(relativeId);
//
//        Element inputNo = document.createElement("InputNo");
//        inputNo.setTextContent(signedData.getEntryHead().getInput_no());
//        list.add(inputNo);
//
//        Element inputCompanyCo = document.createElement("InputCompanyCo");
//        inputCompanyCo.setTextContent(signedData.getEntryHead().getInput_company_co());
//        list.add(inputCompanyCo);
//
//        Element inputCompanyName = document.createElement("InputCompanyName");
//        inputCompanyName.setTextContent(signedData.getEntryHead().getInput_company_name());
//        list.add(inputCompanyName);
//
//        Element agentType = document.createElement("AgentType");
//        agentType.setTextContent(signedData.getEntryHead().getAgent_type());
//        list.add(agentType);
//
//        Element declareNo = document.createElement("DeclareNo");
//        if ("0".equals(signedData.getEntryHead().getAgent_type())) {
//            declareNo.setTextContent(signedData.getEntryHead().getDeclare_no());
//        }
//        list.add(declareNo);
//
//        Element customsField = document.createElement("CustomsField");
//        customsField.setTextContent(signedData.getEntryHead().getCustoms_field());
//        list.add(customsField);
//
//        Element kjId = document.createElement("KjId");
//        kjId.setTextContent(signedData.getEntryHead().getKj_id());
//        list.add(kjId);
//
//        Element sendName = document.createElement("SendName");
//        sendName.setTextContent(signedData.getEntryHead().getSend_name());
//        list.add(sendName);
//
//        Element receiveName = document.createElement("ReceiveName");
//        receiveName.setTextContent(signedData.getEntryHead().getReceive_name());
//        list.add(receiveName);
//
//        Element sendCountry = document.createElement("SendCountry");
//        sendCountry.setTextContent(signedData.getEntryHead().getSend_country());
//        list.add(sendCountry);
//
//        Element sendCity = document.createElement("SendCity");
//        sendCity.setTextContent(signedData.getEntryHead().getSend_city());
//        list.add(sendCity);
//
//        Element sendId = document.createElement("SendId");
//        sendId.setTextContent(signedData.getEntryHead().getSend_id());
//        list.add(sendId);
//
//        Element totalValue = document.createElement("TotalValue");
//        totalValue.setTextContent(String.valueOf(signedData.getEntryHead().getTotal_value()));
//        list.add(totalValue);
//
//        Element currCode = document.createElement("CurrCode");
//        currCode.setTextContent(signedData.getEntryHead().getCurr_code());
//        list.add(currCode);
//
//        Element mainGName = document.createElement("MainGName");
//        mainGName.setTextContent(signedData.getEntryHead().getMain_gname());
//        list.add(mainGName);
//
//        Element entryType = document.createElement("EntryType");
//        entryType.setTextContent(signedData.getEntryHead().getEntry_type());
//        list.add(entryType);
//
//        Element sendIdType = document.createElement("SendIdType");
//        sendIdType.setTextContent(signedData.getEntryHead().getSend_id_type());
//        list.add(sendIdType);
//    }

}