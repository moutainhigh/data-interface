package com.xaeport.crossborder.controller.api.ordermanage;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.xaeport.crossborder.controller.api.BaseApi;
import com.xaeport.crossborder.data.ResponseData;
import com.xaeport.crossborder.data.entity.*;
import com.xaeport.crossborder.service.ordermanage.OrderQueryService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


/*
 * 订单申报
 */

@RestController
@RequestMapping("/api/ordermanage/queryOrder")
public class OrderQueryApi extends BaseApi {


	private Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	OrderQueryService orderQueryService;

	/*
	 * 邮件查询
	 */
	@RequestMapping("/queryOrderHeadList")
	public ResponseData queryOrderHeadList(
			@RequestParam(required = false) String startDeclareTime,
			@RequestParam(required = false) String endDeclareTime,
			@RequestParam(required = false) String orderNo,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length,
			@RequestParam(required = false) String draw


	) {
		this.logger.debug(String.format("查询邮件申报条件参数:[startDeclareTime:%s,endDeclareTime:%s,orderNo:%s,start:%s,length:%s,drew:%s]", startDeclareTime,endDeclareTime,orderNo,start,length,draw));
		Map<String, String> paramMap = new HashMap<String, String>();
		DataList<ImpOrderHead> dataList = new DataList<ImpOrderHead>();
		paramMap.put("startDeclareTime",startDeclareTime);
		paramMap.put("endDeclareTime",endDeclareTime);
		paramMap.put("orderNo",orderNo);
		paramMap.put("start", String.valueOf(Integer.parseInt(start)+1));
		paramMap.put("length",length);
		List<ImpOrderHead> resultList = new ArrayList<ImpOrderHead>();
		try {
			resultList = orderQueryService.queryOrderHeadList(paramMap);
			//查询总数
			Integer count = orderQueryService.queryOrderHeadListCount(paramMap);
			dataList.setDraw(draw);
			dataList.setData(resultList);
			dataList.setRecordsTotal(count);
			dataList.setRecordsFiltered(count);
			return new ResponseData(dataList);
		} catch (Exception e) {
			this.logger.error("订单查询失败", e);
			return new ResponseData(dataList);
		}
	}


	/*
	 * 邮件查询(查询表体)
	 * 点击查看详情
	 */
	@RequestMapping("/queryOrderBodyList")
	public ResponseData queryOrderBodyList(
			@RequestParam(required = false) String guid,
			@RequestParam(required = false) String orderNo
			/*@RequestParam(required = false) String start,
			@RequestParam(required = false) String length,
			@RequestParam(required = false) String draw*/
	) {
		this.logger.debug(String.format("查询邮件条件参数:[guid:%s,orderNo:%s]",guid,orderNo));
		Map<String, String> paramMap = new HashMap<String, String>();
		DataList<ImpOrderBody> dataList = new DataList<ImpOrderBody>();
		paramMap.put("guid",guid);
		paramMap.put("orderNo",orderNo);
		/*paramMap.put("start", String.valueOf(Integer.parseInt(start)+1));
		paramMap.put("length",length);*/
		List<ImpOrderBody> resultList = new ArrayList<ImpOrderBody>();
		try {
			resultList = orderQueryService.queryOrderBodyList(paramMap);
			/*//查询总数
			Integer count = orderQueryService.queryOrderBodyListCount(paramMap);
			dataList.setDraw(draw);*/
			dataList.setData(resultList);
			//dataList.setRecordsTotal(count);
			//dataList.setRecordsFiltered(count);
			return new ResponseData(dataList);
		} catch (Exception e) {
			this.logger.error("订单查询失败", e);
			return new ResponseData(dataList);
		}
	}

	/*
	* 点击查看邮件详情
	* */
	@RequestMapping("/seeOrderDetail")
	public ResponseData seeOrderDetail(
			@RequestParam(required = false) String guid
	) {
		if (StringUtils.isEmpty(guid)) return new ResponseData("订单为空", HttpStatus.FORBIDDEN);
		this.logger.debug(String.format("查询邮件条件参数:[guid:%s]",guid));
		Order order;
		try {
			order = orderQueryService.getOrderDetail(guid);
		} catch (Exception e) {
			this.logger.error("查询订单信息失败，entryHeadId=" + guid, e);
			return new ResponseData("请求错误", HttpStatus.BAD_REQUEST);
		}
		return new ResponseData(order);
	}

	/*
	* 订单查询中编辑
	* saveOrderDetail
	* */
	@RequestMapping("/saveOrderDetail")
	public ResponseData saveOrderDetail(@Param("entryJson") String entryJson){
		//订单信息json信息
		LinkedHashMap<String, Object> object = (LinkedHashMap<String, Object>) JSONUtils.parse(entryJson);

		// 订单表头
		LinkedHashMap<String, String> entryHead = (LinkedHashMap<String, String>) object.get("entryHead");

		// 订单表体
		ArrayList<LinkedHashMap<String, String>> entryLists = (ArrayList<LinkedHashMap<String, String>>) object.get("entryList");

		Map<String, String> rtnMap = new HashMap<>();
		try {
			// 保存分单详情信息
			rtnMap = orderQueryService.saveOrderDetail(entryHead, entryLists);
		} catch (Exception e) {
			logger.error("保存订单详细信息时发生异常", e);
			rtnMap.put("result", "false");
			rtnMap.put("msg", "保存订单详细信息时发生异常");
		}
		return new ResponseData(rtnMap);
	}
}


