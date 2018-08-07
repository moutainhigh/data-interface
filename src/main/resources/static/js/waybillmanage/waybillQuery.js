/**
 * 预览打印
 * Created by Administrator on 2017/7/20.
 */
sw.page.modules["waybillmanage/waybillQuery"] = sw.page.modules["waybillmanage/waybillQuery"] || {
    init: function () {
        $("[name='startFlightTimes']").val(moment(new Date()).date(1).format("YYYYMMDD"));
        $("[name='endFlightTimes']").val(moment(new Date()).format("YYYYMMDD"));
        $(".input-daterange").datepicker({
            language: "zh-CN",
            todayHighlight: true,
            format: "yyyymmdd",
            autoclose: true
        });
        $("[ws-search]").unbind("click").click(this.query);
        $("[ws-download]").unbind("click").click(this.billDownLoad);
        $("[ws-back]").unbind("click").click(this.back);
        $(".btn[ws-search]").click();
    },

    query: function () {
        // 获取查询表单参数
        var startFlightTimes = $("[name='startFlightTimes']").val();
        var endFlightTimes = $("[name='endFlightTimes']").val();
        var logisticsNo = $("[name='logisticsNo']").val();
        var logisticsStatus = $("[name='logisticsStatus']").val();

        // 拼接URL及参数
        var url = sw.serializeObjectToURL("api/waybillManage/queryWaybillQuery", {
            startFlightTimes: startFlightTimes,
            endFlightTimes: endFlightTimes,
            logisticsNo: logisticsNo,
            logisticsStatus: logisticsStatus
        });

        sw.datatable("#query-waybillQuery-table", {
            sLoadingRecords: true,
            ordering: false,
            bSort: false, //排序功能
            serverSide: true,////服务器端获取数据
            pagingType: 'simple_numbers',
            ajax: function (data, callback, setting) {
                $.ajax({
                    type: 'GET',
                    url: sw.resolve(url),
                    data: data,
                    cache: false,
                    dataType: "json",
                    beforeSend: function () {
                        $("tbody").html('<tr class="odd"><td valign="top" colspan="13" class="dataTables_empty">载入中...</td></tr>');
                    },
                    success: function (res) {
                        var returnData = {};
                        returnData.data = res.data.data;
                        returnData.recordsFiltered = res.data.recordsFiltered;
                        returnData.draw = res.data.draw;
                        returnData.recordsTotal = res.data.recordsTotal;
                        returnData.start = data.start;
                        returnData.length = data.length;
                        callback(returnData);
                    },
                    error: function (xhr, status, error) {
                        sw.showErrorMessage(xhr, status, error);
                    }
                });
            },
            lengthMenu: [[50, 100, 1000, -1], [50, 100, 1000, "所有"]],
            searching: false,//开启本地搜索
            columns: [
                {
                    label: "物流运单编号", render: function (data, type, row) {
                        //a链接跳转到querypaymentbyid方法。并赋值参数。 cursor:pointer鼠标移动上去变手掌样式
                        var result = '<a style="cursor:pointer" title="查看" ' +
                            'onclick="' + "javascript:sw.pageModule('waybillmanage/waybillQuery').queryWaybillbyid('" + row.guid + "','"+row.logistics_no+"')" + '">' + row.logistics_no + '</a>';
                        return result;
                    }
                },
                {data: "logistics_name", label: "物流企业名称"},
                {data: "consingee", label: "收货人姓名"},
                {data: "consignee_telephone", label: "收货人电话"},
                {data: "consignee_address", label: "收货地址"},
                {
                    label: "申报日期", render: function (data, type, row) {
                    if(!isEmpty(row.app_time)){
                        return moment(row.app_time).format("YYYY-MM-DD HH:mm:ss");
                    }
                    return "";
                }
                },
                {data: "logistics_status", label: "物流签收状态"},
                {
                    label: "物流状态时间", render: function (data, type, row) {
                    if(!isEmpty(row.logistics_time)){
                        return moment(row.logistics_time).format("YYYY-MM-DD HH:mm:ss");
                    }
                    return "";
                }
                },
                {
                    data: "data_status", label: "业务状态", render: function (data, type, row) {
                    var textColor = "";
                    var value = "";
                    switch (row.data_status) {
                        case "CBDS1"://运单待申报 未导入
                            textColor = "text-yellow";
                            value = "运单待申报";
                            break;
                        case "CBDS4"://运单待申报
                            textColor = "text-yellow";
                            value = "运单待申报";
                            break;
                        case "CBDS40"://支付单申报中
                            textColor = "text-green";
                            value = "运单申报中";
                            break;
                        case "CBDS42"://支付单申报成功
                            textColor = "text-green";
                            value = "运单申报成功";
                            break;
                        case "CBDS41"://支付单已申报
                            textColor = "text-green";
                            value = "运单已申报";
                            break;
                        case "CBDS44"://支付单申报失败
                            textColor = "text-red";
                            value = "运单申报失败";
                            break;
                        case "CBDS43"://支付单重报
                            textColor = "text-red";
                            value = "运单重报";
                            break;
                        default :
                            textColor = "";
                            value = "未知";
                    }
                    return "<span class='" + textColor + "'>" + value + "</span>";
                }
                },
                {data: "return_status", label: "运单回执"},
                {data: "return_info", label: "运单回执备注"},
                {data: "returnStatus_status", label: "运单状态回执"},
                {data: "returnStatus_info", label: "运单状态回执备注"}
            ]
        });

    },

    back: function () {
        $("#bill").show();
        $("#preview").hide();
    },
    //打开一个页面，并且用路径传递参数
    queryWaybillbyid: function (guid,logistics_no) {
        var url = "waybillmanage/seeWaybillDetail?type=ZFDCX&isEdit=true&guid=" + guid+"&logistics_no="+logistics_no;
        sw.modelPopup(url, "运单详情信息", false, 900, 400);
    },
    billDownLoad: function () {
        sw.ajax("api/bill", "GET", {
            ieFlag: sw.ie,
            entryType: sw.type,
            startFlightTimes: $("[name='startFlightTimes']").val(),
            endFlightTimes: $("[name='endFlightTimes']").val(),
            billNo: $("[name='billNo']").val(),
            flag: "1"
        }, function (rsp) {
            if (rsp.status == 200) {
                var fileName = rsp.data;
                window.location.href = "/api/downloadFile?fileName=" + fileName;
            }
        });

    }
};
