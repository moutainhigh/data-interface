package com.xaeport.crossborder.controller.api.ordermanage;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.xaeport.crossborder.configuration.AppConfiguration;
import com.xaeport.crossborder.configuration.SystemConstants;
import com.xaeport.crossborder.controller.api.BaseApi;
import com.xaeport.crossborder.data.ResponseData;
import com.xaeport.crossborder.data.entity.DataList;
import com.xaeport.crossborder.data.entity.OrderHeadAndList;
import com.xaeport.crossborder.data.entity.Users;
import com.xaeport.crossborder.data.status.StatusCode;
import com.xaeport.crossborder.service.ordermanage.OrderDeclareSevice;
import com.xaeport.crossborder.tools.DownloadUtils;
import com.xaeport.crossborder.tools.GetIpAddr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;


/*
 * 订单申报
 */

@RestController
@RequestMapping("/api/orderManage")
public class OrderDeclareApi extends BaseApi {

    private Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    AppConfiguration appConfiguration;
    @Autowired
    OrderDeclareSevice orderDeclareService;

    /*
     * 邮件申报查询
     */
    @RequestMapping("/queryOrderDeclare")
    public ResponseData queryOrderDeclare(
            //身份验证
            @RequestParam(required = false) String idCardValidate,
            @RequestParam(required = false) String startFlightTimes,
            @RequestParam(required = false) String endFlightTimes,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String billNo,
            //分页参数
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String length,
            @RequestParam(required = false) String draw
    ) {
        this.logger.debug(String.format("查询邮件申报条件参数:[idCardValidate:%s,startFlightTimes:%s,endFlightTimes:%s,orderNo:%s,billNo:%s,start:%s,length:%s]", idCardValidate, startFlightTimes, endFlightTimes, orderNo, billNo,start, length));
        Map<String, Object> paramMap = new HashMap<String, Object>();
        DataList<OrderHeadAndList> dataList = new DataList<>();
        //查询参数
        paramMap.put("idCardValidate", idCardValidate);
        paramMap.put("orderNo", orderNo);
        paramMap.put("startFlightTimes", startFlightTimes);
        paramMap.put("endFlightTimes", endFlightTimes);
        paramMap.put("billNo",billNo);
        //分页参数
        paramMap.put("start", Integer.parseInt(start) + 1);
        paramMap.put("length", length);
        // 固定参数
        paramMap.put("dataStatus", String.format("%s,%s,%s,%s,%s,%s,%s", StatusCode.DDDSB, StatusCode.DDSBZ, StatusCode.DDYSB, StatusCode.DDCB, StatusCode.EXPORT, StatusCode.DDBWSCZ, StatusCode.DDBWXZWC));
        paramMap.put("entId", this.getCurrentUserEntId());
        paramMap.put("roleId", this.getCurrentUserRoleId());

        List<OrderHeadAndList> resultList = new ArrayList<OrderHeadAndList>();
        try {
            //查询列表
            resultList = orderDeclareService.queryOrderDeclareList(paramMap);
            //查询总数
            Integer count = orderDeclareService.queryOrderDeclareCount(paramMap);
            dataList.setDraw(draw);
            dataList.setData(resultList);
            dataList.setRecordsTotal(count);
            dataList.setRecordsFiltered(count);
            return new ResponseData(dataList);
        } catch (Exception e) {
            this.logger.error("查询订单申报数据失败", e);
            return new ResponseData(dataList);
        }
    }


    /**
     * 订单单申报-提交海关
     *
     * @param submitKeys EntryHead.IDs
     */
    @RequestMapping(value = "/submitCustom", method = RequestMethod.POST)
    public ResponseData saveSubmitCustom(@RequestParam(required = false) String submitKeys,
                                         @RequestParam(required = false) String idCardValidate,
                                         @RequestParam(required = false) String ieFlag,
                                         @RequestParam(required = false) String entryType,
                                         HttpServletRequest request) {
        //this.log.debug(String.format("舱单申报-提交海关舱单Keys：%s", submitKeys));
        this.logger.info("订单申报客户端操作地址为 " + GetIpAddr.getRemoteIpAdd(request));
        if (StringUtils.isEmpty(submitKeys)) {
            return rtnResponse("false", "请先勾选要提交海关的订单信息！");
        }
        Users currentUser = this.getCurrentUsers();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("opStatus", StatusCode.DDSBZ);//提交海关后,状态改为订单申报中,逻辑校验在这个之前
        paramMap.put("opStatusWhere", StatusCode.DDDSB + "," + StatusCode.DDCB + "," + StatusCode.EXPORT);//可以申报的状态,订单待申报,订单重报,已经导入
        paramMap.put("currentUserId", currentUser.getId());

       /* paramMap.put("enterpriseId", this.getCurrentUserEnterpriseId());*/  //暂时不获取企业id
        paramMap.put("submitKeys", submitKeys);//订单遍号
        paramMap.put("idCardValidate", idCardValidate);
        paramMap.put("entryType", entryType);
        paramMap.put("ieFlag", ieFlag);

        // 调用订单申报Service 获取提交海关结果
        boolean flag = orderDeclareService.updateSubmitCustom(paramMap);
        if (flag) {
            return rtnResponse("true", "订单申报海关提交成功！");
        } else {
            return rtnResponse("false", "订单申报海关提交失败！");
        }
    }

    /**
     * 订单报文下载
     *
     * @param submitKeys EntryHead.IDs
     */
    @RequestMapping(value = "/orderXmlDownload", method = RequestMethod.POST)
    public ResponseData orderXmlDownload(@RequestParam(required = false) String submitKeys,
                                         @RequestParam(required = false) String idCardValidate,
                                         @RequestParam(required = false) String ieFlag,
                                         @RequestParam(required = false) String entryType,
                                         HttpServletRequest request) {
        this.logger.info("订单申报客户端操作地址为 " + GetIpAddr.getRemoteIpAdd(request));
        if (StringUtils.isEmpty(submitKeys)) {
            return rtnResponse("false", "请先勾选要下载的订单信息！");
        }
        Users currentUser = this.getCurrentUsers();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("opStatus", StatusCode.DDBWSCZ);//提交海关后,状态改为订单申报中,逻辑校验在这个之前
        paramMap.put("opStatusWhere", StatusCode.DDDSB + "," + StatusCode.DDCB + "," + StatusCode.EXPORT);//可以申报的状态,订单待申报,订单重报,已经导入
        paramMap.put("currentUserId", currentUser.getId());

        paramMap.put("submitKeys", submitKeys);//订单编号
        paramMap.put("idCardValidate", idCardValidate);
        paramMap.put("entryType", entryType);
        paramMap.put("ieFlag", ieFlag);

        // 调用订单申报Service 获取提交海关结果
        boolean flag = orderDeclareService.orderXmlDownload(paramMap);
        String orderZipPath = orderDeclareService.OrderXml(this.getCurrentUserEntId());
        if (!StringUtils.isEmpty(orderZipPath)) {
            return rtnResponse("1" + orderZipPath, "订单报文生成提交成功");
        } else {
            return rtnResponse("0" + orderZipPath, "订单报文生成提交失败");
        }
    }

    /**
     * excel 跨境电子商务进口订单模板下载
     */
    @RequestMapping(value = "/downloadFile")
    public void excelModelDownload(
            HttpServletResponse response,
            @RequestParam(value = "type") String type) {
        File file = new File(type);
        DownloadUtils.download(response, file, SystemConstants.HTTP_CONTENT_TYPE_ZIP);
    }

}
