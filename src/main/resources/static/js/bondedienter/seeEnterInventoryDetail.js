// 非空判断
function isNotEmpty(obj) {
    if (typeof(obj) == "undefined" || null == obj || "" == obj) {
        return false;
    }
    return true;
}

// 错误提示
function hasError(errorMsg) {
    $("#errorMsg").html(errorMsg).removeClass("hidden");
}

// 清楚错误提示
function clearError() {
    $("#errorMsg").html("").addClass("hidden");
}

// Select2初始化
function selecterInitDetail(selectId, value, data) {
    $("#" + selectId).select2({
        data: $.map(data, function (val, key) {
            var obj = {
                id: key,
                text: val + "[" + key + "]"
            }
            return obj;
        }),
        placeholder: value,
        dropdownParent: $("#dialog-popup")
    }).val(value).trigger('change');
}

// 表头变化
var headChangeKeyVal = {};

// 表体变化
var listChangeKeyVals = {};

// 表体ID匹配正则
var pattern = /^.*_[0-9]+$/;

// 计算表头总价值
function sumTotalPricesInvent() {
    var totalPrices = 0;
    $(".detailPage input[id^=total_price]").each(function () {
        var decTotal = $(this).val();
        totalPrices = parseFloat(totalPrices) + parseFloat(decTotal);
    });
    $("#total_sum").val(parseFloat(totalPrices).toFixed(5));
    headChangeKeyVal["total_sum"] = $("#total_sum").val();
}

// 计算表体申报总价
function sumDeclTotalInvent(dVal, qty, gno, listChangeKeyVal) {
    var declTotal = parseFloat(dVal * qty).toFixed(5);
    $("#total_price_" + gno).val(declTotal);
    listChangeKeyVal["total_price"] = $("#total_price_" + gno).val();
}

function inputChangeInvent(id) {
    $(".detailPage input,select").change(function () {
        var key = $(this).attr("id");
        var val = $(this).val();
        if (!isNotEmpty(val)) {
            return;
        }
        if (pattern.test(key)) {
            var gno = key.substring(key.lastIndexOf("_") + 1, key.length);
            var keys = key.substring(0, key.lastIndexOf("_"));
            var listChangeKeyVal;
            if (listChangeKeyVals[gno]) {
                listChangeKeyVal = listChangeKeyVals[gno];
            } else {
                listChangeKeyVal = {};
            }
            // 修改字段为单价
            if (keys == "price") {// 单价
                var dVal = parseFloat(val);
                var qty = parseFloat($("#g_qty_" + gno).val());
                sumDeclTotalInvent(dVal, qty, gno, listChangeKeyVal);
                sumTotalPricesInvent();
            } else if (keys == "g_qty") {// 数量
                console.log(keys);
                var qty = parseFloat(val);
                var dVal = parseFloat($("#price_" + gno).val());
                sumDeclTotalInvent(dVal, qty, gno, listChangeKeyVal);
                sumTotalPricesInvent();
            }
            // 记录变更信息
            listChangeKeyVal[keys] = val;
            listChangeKeyVal["g_no"] = gno;
            listChangeKeyVal["entryhead_guid"] = id;
            listChangeKeyVals[gno] = listChangeKeyVal;
        } else {
            headChangeKeyVal[key] = val;
        }
        console.log(headChangeKeyVal, listChangeKeyVal);
    }).focus(function () {
        clearError();
    });
}

//数据字典
var supv_modecd = {
    /*"1210":"保税电商",
    "1239":"保税电商A"*/
    "0":"普通清单"
};

sw.page.modules["bondedienter/seeEnterInventoryDetail"] = sw.page.modules["bondedienter/seeEnterInventoryDetail"] || {


    detailParam: {
        url: "",
        callBackUrl: "",
        isShowError: true,
        isEdit: "true",
        disableField: [
            "dec_type",
            "invt_type",
            "dcl_typecd",

            "putrec_seqno",
            "gds_seqno",
            "gds_mtno",
            "gdecd",
            "gds_nm",
            "gds_spcf_model_desc",
            "dcl_unitcd",
            "dcl_qty",
            "dcl_uprc_amt",
            "dcl_total_amt",
            "dcl_currcd",
            "usd_stat_total_amt"

        ]
    },
    // 保存成功时回调查询
    callBackQuery: function () {
        $("#dialog-popup").modal("hide");
    },
    // 取消返回
    cancel: function (invt_no) {
        //将两个数据表里的数据删除
        console.log(invt_no);
        sw.ajax("api/crtEnterInven/deleteEnterInven/" + invt_no, "DELETE", {}, function (rsp) {
            $("#dialog-popup").modal("hide");
        });
        //$("#dialog-popup").modal("hide");
    },
    // 禁用字段
    disabledFieldInput: function () {
        var disableField = sw.page.modules["bondedienter/seeEnterInventoryDetail"].detailParam.disableField;
        for (i = 0; i < disableField.length; i++) {
            $(".detailPage input[id^=" + disableField[i] + "],select[id^=" + disableField[i] + "]").attr("disabled", "disabled");
        }
    },
    // 装载表头信息
    fillEntryHeadInfo: function (entryHead) {
        for (var key in sw.dict.customs) {
            var customsCode = key;
            var name = sw.dict.customs[key];
            var option = $("<option>").text(name).val(customsCode);
            $("#dcl_plc_cuscds").append(option);
           // $("#impexp_portcds").append(option);
        }
        for (var key in sw.dict.customs) {
            var customsCode = key;
            var name = sw.dict.customs[key];
            var option = $("<option>").text(name).val(customsCode);
            //$("#dcl_plc_cuscds").append(option);
            $("#impexp_portcds").append(option);
        }
        $("#bizop_etpsno").val(entryHead.bizop_etpsno);
        $("#bizop_etps_nm").val(entryHead.bizop_etps_nm);
        $("#dcl_etpsno").val(entryHead.dcl_etpsno);
        $("#dcl_etps_nm").val(entryHead.dcl_etps_nm);
        $("#putrec_no").val(entryHead.putrec_no);
        $("#rcvgd_etpsno").val(entryHead.rcvgd_etpsno);
        $("#rcvgd_etps_nm").val(entryHead.rcvgd_etps_nm);

        $("#impexp_markcd").val(entryHead.impexp_markcd);
        $("#mtpck_endprd_markcd").val(entryHead.mtpck_endprd_markcd);
        $("#dclcus_flag").val(entryHead.dclcus_flag);//是否报关是固定的
        $("#dclcus_typecd").val(entryHead.dclcus_typecd);//报关单类型代码
        $("#dec_type").val(entryHead.dec_type);//报关单类型

        $("#dcl_plc_cuscd").val(entryHead.dcl_plc_cuscd);
        $("#impexp_portcd").val(entryHead.impexp_portcd);

        $("#invt_type").val(entryHead.bond_invt_typecd);//保税电商
        $("#corr_entry_dcl_etps_sccd").val(entryHead.corr_entry_dcl_etps_sccd);//社会信用代码
        $("#corr_entry_dcl_etps_no").val(entryHead.corr_entry_dcl_etps_no);//代码
        $("#corr_entry_dcl_etps_nm").val(entryHead.corr_entry_dcl_etps_nm);//名称
        $("#dcl_typecd").val(entryHead.dcl_typecd);//备案申请
        $("#rmk").val(entryHead.rmk);

        //selecterInitDetail("impexp_portcd", entryHead.impexp_portcd, sw.dict.customs);
        //selecterInitDetail("dcl_plc_cuscd", entryHead.dcl_plc_cuscd, sw.dict.customs);
        selecterInitDetail("trsp_modecd", entryHead.trsp_modecd, sw.dict.trafMode);
        selecterInitDetail("supv_modecd", entryHead.supv_modecd, supv_modecd);
        selecterInitDetail("stship_trsarv_natcd", entryHead.stship_trsarv_natcd, sw.dict.countryArea);

    },

    //加载表体信息
    fillEntryListInfo: function (entryLists) {
        for (var i = 0; i < entryLists.length; i++) {
            var g_num = entryLists[i].putrec_seqno;
            var str =
                "<tr>" +
                "<td ><input class=\"form-control input-sm\" maxlength=\"4\" id='putrec_seqno_" + g_num + "' value='" + entryLists[i].putrec_seqno + "' /></td>" +
                "<td ><input class=\"form-control input-sm\" maxlength=\"250\" id='gds_mtno_" + g_num + "' value='" + (isEmpty(entryLists[i].gds_mtno) ? "":entryLists[i].gds_mtno) + "' /></td>" +
                "<td ><input class=\"form-control input-sm\" maxlength=\"10\" id='gdecd_" + g_num + "' value='" + (isEmpty(entryLists[i].gdecd) ? "":entryLists[i].gdecd) + "' /></td>" +
                "<td ><input class=\"form-control input-sm\" maxlength=\"510\" id='gds_nm_" + g_num + "' value='" + (isEmpty(entryLists[i].gds_nm) ? "":entryLists[i].gds_nm) + "' /></td>" +
                "<td ><input class=\"form-control input-sm\" maxlength=\"510\" id='gds_spcf_model_desc_" + g_num + "' value='" + (isEmpty(entryLists[i].gds_spcf_model_desc) ? "":entryLists[i].gds_spcf_model_desc) + "' /></td>" +
                /*"<td ><select class=\"form-control input-sm\" maxlength=\"510\" id='dcl_unitcd_" + g_num + "' value='" + entryLists[i].dcl_unitcd + "' /></td>" +*/
                "<td ><select class=\"form-control input-sm\" style=\"width:100%\" maxlength=\"50\" id='dcl_unitcd_" + g_num + "' value='" + entryLists[i].dcl_unitcd + "' /></td>" +
                "<td ><input class=\"form-control input-sm\" maxlength=\"510\" id='dcl_qty_" + g_num + "' value='" + (isEmpty(entryLists[i].dcl_qty) ? "":entryLists[i].dcl_qty) + "' /></td>" +
                "<td ><input class=\"form-control input-sm\" maxlength=\"510\" id='dcl_uprc_amt_" + g_num + "' value='" + parseFloat(entryLists[i].dcl_uprc_amt).toFixed(5) + "' /></td>" +
                "<td ><input class=\"form-control input-sm\" maxlength=\"510\" id='dcl_total_amt_" + g_num + "' value='" + parseFloat(entryLists[i].dcl_total_amt).toFixed(5) + "' /></td>" +
                "<td ><input class=\"form-control input-sm\" maxlength=\"510\" id='dcl_currcd_" + g_num + "' value='" + (isEmpty(entryLists[i].dcl_currcd) ? "人民币":entryLists[i].dcl_currcd) + "' /></td>" +
                /*"<td ><input class=\"form-control input-sm\" maxlength=\"510\" id='usd_stat_total_amt_" + g_num + "' value='" + parseFloat(entryLists[i].usd_stat_total_amt).toFixed(5) + "' /></td>" +*/
                "</tr>";
            $("#table_body").append(str);
            selecterInitDetail("dcl_unitcd_" + g_num, entryLists[i].dcl_unitcd, sw.dict.unitCode);
          /*  selecterInitDetail("country_" + g_num, entryLists[i].country, sw.dict.countryArea);
            selecterInitDetail("g_unit_" + g_num, entryLists[i].unit, sw.dict.unitCodes);
            selecterInitDetail("unit_1_" + g_num, entryLists[i].unit1, sw.dict.unitCodes);
            selecterInitDetail("unit_2_" + g_num, entryLists[i].unit2, sw.dict.unitCodes);*/
        }
        this.enterPage(1, 5);
    },

    enterPage: function (pno, psize) {

        var itable = document.getElementById("table_body");//通过ID找到表格
        var num = itable.rows.length;//表格所有行数(所有记录数)
        var totalPage = 0;//总页数
        var pageSize = psize;//每页显示行数
        //总共分几页
        if (num / pageSize > parseInt(num / pageSize)) {
            totalPage = parseInt(num / pageSize) + 1;
        } else {
            totalPage = parseInt(num / pageSize);
        }
        var currentPage = pno;//当前页数
        var startRow = (currentPage - 1) * pageSize + 1;//开始显示的行  1
        var endRow = currentPage * pageSize;//结束显示的行   15
        endRow = (endRow > num) ? num : endRow;
        //遍历显示数据实现分页
        for (var i = 1; i < (num + 1); i++) {
            var irow = itable.rows[i - 1];

            if (i >= startRow && i <= endRow) {
                // irow.style.display = "block";
                $(irow).show();
            } else {
                // irow.style.display = "none";
                $(irow).hide();
            }
        }
        var tempStr = "";
        if (currentPage > 1) {
            tempStr += "<li class='prev'><a href='javascript:void(0)' onClick=\"sw.page.modules['bondedienter/seeEnterInventoryDetail'].enterPage(" + (currentPage - 1) + "," + psize + ")\"> 上一页&nbsp;&nbsp;</a><li>";
            for (var j = 1; j <= totalPage; j++) {
                if (currentPage == j) {
                    tempStr += "<li class='active'><a href='javascript:void(0)' onClick=\"sw.page.modules['bondedienter/seeEnterInventoryDetail'].enterPage(" + j + "," + psize + ")\">" + j + "&nbsp;&nbsp;</a><li>";
                } else if (j == 1 || j == totalPage) {
                    tempStr += "<li><a href='javascript:void(0)' onClick=\"sw.page.modules['bondedienter/seeEnterInventoryDetail'].enterPage(" + j + "," + psize + ")\">" + j + "&nbsp;&nbsp;</a><li>";
                } else {
                    tempStr += "<li><a hidden='hidden' href='javascript:void(0)' onClick=\"sw.page.modules['bondedienter/seeEnterInventoryDetail'].enterPage(" + j + "," + psize + ")\">" + j + "&nbsp;&nbsp;</a><li>";
                }

            }
        }
        else {
            tempStr += "<li> <a href='javascript:void(0)'> 上一页&nbsp;&nbsp;</a></li>";
            for (var j = 1; j <= totalPage; j++) {
                if (currentPage == j) {
                    tempStr += "<li class='active'><a href='javascript:void(0)' onClick=\"sw.page.modules['bondedienter/seeEnterInventoryDetail'].enterPage(" + j + "," + psize + ")\">" + j + "&nbsp;&nbsp;</a><li>";
                } else if (j == 1 || j == totalPage) {
                    tempStr += "<li><a href='javascript:void(0)' onClick=\"sw.page.modules['bondedienter/seeEnterInventoryDetail'].enterPage(" + j + "," + psize + ")\">" + j + "&nbsp;&nbsp;</a><li>";
                } else {
                    tempStr += "<li><a hidden='hidden' href='javascript:void(0)' onClick=\"sw.page.modules['bondedienter/seeEnterInventoryDetail'].enterPage(" + j + "," + psize + ")\">" + j + "&nbsp;&nbsp;</a><li>";
                }

            }
        }

        if (currentPage < totalPage) {
            tempStr += "<li class='next'><a href='javascript:void(0)' onClick=\"sw.page.modules['bondedienter/seeEnterInventoryDetail'].enterPage(" + (currentPage + 1) + "," + psize + ")\"> 下一页&nbsp;&nbsp;</a><li>";
        } else {
            tempStr += "<li> <a href='javascript:void(0)'> 下一页&nbsp;&nbsp;</a></li>";
        }

        $("#listData").text("核注清单数据共计" + num + "条");
        document.getElementById("barcon").innerHTML = tempStr;
    },

    // 标记问题字段
    errorMessageShow: function (vertify) {
        if (vertify) {
            var result = JSON.parse(vertify.result);
            var gno = result.g_num;
            var field = result.field;

            if (isNotEmpty(gno)) {
                $("#" + field + "_" + gno).addClass("bg-red");
                $("#" + field + "_" + gno).parent().find(".select2-selection--single").addClass("bg-red");
            } else {
                $("#" + field).addClass("bg-red");
                $("#" + field).parent().find(".select2-selection--single").addClass("bg-red");
            }
        }
    },

    // 保存清单编辑信息
    saveEntryInfo: function (invt_no, type, ieFlag) {
        if (!this.valiFieldInventory()) {
            return;
        }
        var entryLists = new Array();
        for (var key in listChangeKeyVals) {
            entryLists.push(listChangeKeyVals[key]);
        }
        headChangeKeyVal["invt_no"] = invt_no;
        var entryData = {
            entryHead: headChangeKeyVal,
            entryList: entryLists
        };

        sw.ajax(this.detailParam.url, "POST", "entryJson=" + encodeURIComponent(JSON.stringify(entryData)), function (rsp) {
            if (rsp.data.result) {
                setTimeout(function () {
                    sw.alert(rsp.data.msg, "提示", null, "modal-info");
                }, 500);
                sw.page.modules["bondedienter/seeEnterInventoryDetail"].callBackQuery();
            } else {
                hasError(rsp.data.msg);
            }
        }, function (status, err, xhr) {
            hasError(xhr.data);
        });
    },

    // 查询订单详情
    query: function () {

        // 表头变化
        headChangeKeyVal = {};
        // 表体变化
        listChangeKeyVals = {};

        //从路径上找参数
        var param = sw.getPageParams("bondedienter/seeEnterInventoryDetail");
        var etps_inner_invt_no = param.etps_inner_invt_no;
        var data = {
            etps_inner_invt_no: etps_inner_invt_no
        };
        $.ajax({
            method: "GET",
            url: "api/crtEnterInven/seeEnterInventoryDetail",
            data: data,
            success: function (data, status, xhr) {
                if (xhr.status == 200) {
                    var entryModule = sw.page.modules["bondedienter/seeEnterInventoryDetail"];
                    console.log(data.data.bondInvtDtList);
                    var entryHead = data.data.bondInvtBsc;
                    var entryLists = data.data.bondInvtDtList;
                    var vertify = data.data.verify;
                    if (isNotEmpty(entryHead)) {
                        entryModule.fillEntryHeadInfo(entryHead);
                    }
                    if (isNotEmpty(entryLists)) {
                        entryModule.fillEntryListInfo(entryLists);
                    }
                    // 根据错误字段中的值加高亮显示
                    if (entryModule.detailParam.isShowError) {
                        entryModule.errorMessageShow(vertify);
                    }
                    headChangeKeyVal["entryhead_guid"] = param.guid;
                    // 添加输入框内容变更事件，捕获数据变更信息
                    inputChangeInvent(param.guid);
                    entryModule.disabledFieldInput();
                }
            }
        });
    },

    //校验
    valiFieldInventory: function () {
        // 校验表头
        var validataHeadField = {
            "bizop_etpsno": "经营企业编号",
            "bizop_etps_nm": "经营企业名称",
            "dcl_etpsno": "申报企业编号",
            "dcl_etps_nm": "申报企业名称",
            "putrec_no": "备案编号",
            "rcvgd_etpsno": "收货企业编号",
            "rcvgd_etps_nm": "收货企业名称",
            "impexp_portcd": "进出口口岸代码",
            "dcl_plc_cuscd": "申报地关区代码",
            "impexp_markcd": "进出口标记代码",
            "mtpck_endprd_markcd": "料件成品标记代码",
            "supv_modecd": "监管方式代码",
            "trsp_modecd": "运输方式代码",
            "dclcus_flag": "是否报关标志",
            "dclcus_typecd": "报关类型代码",
            "dec_type": "报关单类型",
            "stship_trsarv_natcd": "起运抵国别代码",
            "invt_type": "清单类型",
            "dcl_typecd": "申报类型",
            "corr_entry_dcl_etps_sccd":"对应报关单申报单位社会统一信用代码",
            "corr_entry_dcl_etps_no":"对应报关单申报单位代码",
            "corr_entry_dcl_etps_nm":"对应报关单申报单位名称"

        };

        // 校验表体
        var validataListField = {
            //"putrec_seqno": "备案序号",
            "gds_seqno": "商品序号",
            "gds_mtno": "商品料号",
            "gdecd": "商品编码",
            "gds_nm": "商品名称",
            "gds_spcf_model_desc": "商品规格",
            "dcl_unitcd": "申报计量单位",
            "dcl_qty": "申报数量",
            "dcl_uprc_amt": "申报单价",
            "dcl_total_amt": "申报总价",
            //"dcl_currcd": "币制",
            //"usd_stat_total_amt": "美元统计总金额"
        };

        var fieldId, fieldName, fieldVal;
        // 表头数据校验
        for (fieldId in validataHeadField) {
            fieldName = validataHeadField[fieldId];
            fieldVal = $("#" + fieldId).val();
            if (!isNotEmpty(fieldVal)) {
                hasError("[" + fieldName + "]不能为空");
                return false;
            }
            if (fieldId == "net_weight") {
                // var net_weight = parseFloat($("#net_weight").val()).toFixed(5);
                // var gross_weight = parseFloat($("#gross_weight").val()).toFixed(5);
                var net_weight = parseFloat($("#net_weight").val());
                var gross_weight = parseFloat($("#gross_weight").val());
                if (net_weight > gross_weight) {
                    hasError("[净重]不能大于毛重");
                    return false;
                }
            }
        }

        var gno, fields;
        // 表体数据校验
        for (var key in validataListField) {
            fields = $("input[id^=" + key + "],select[id^=" + key + "]");
            for (var i = 0; i < fields.length; i++) {
                fieldId = $(fields[i]).attr("id");
                fieldVal = $(fields[i]).val();
                gno = fieldId.substring(fieldId.lastIndexOf("_") + 1, fieldId.length);
                if (!isNotEmpty(fieldVal)) {
                    hasError("序号[" + gno + "]-[" + validataListField[key] + "]不能为空");
                    return false;
                }
            }
        }

        return true;
    },

    init: function () {
        //从路径上获取参数
        var param = sw.getPageParams("bondedienter/seeEnterInventoryDetail");
        var etps_inner_invt_no = param.etps_inner_invt_no;
        var type = param.type;
        var isEdit = param.isEdit;

        console.log("type:"+type);
        console.log("isEdit:"+isEdit);

        $(".input-daterange").datepicker({
            language: "zh-CN",
            todayHighlight: true,
            format: "yyyy-mm-dd",
            autoclose: true
        });

        switch (type) {
            //新建
            case "XJHFD": {
                // 不可编辑状态
                if (isEdit == "true") {
                    this.detailParam.disableField = [
                        //当前禁用的字段,需要禁用的字段值在这里改
                        /*"dec_type",
                        "invt_type",
                        "dcl_typecd",

                        "putrec_seqno",
                        "gds_mtno",
                        "gdecd",
                        "gds_nm",
                        "gds_spcf_model_desc",
                        "dcl_unitcd",
                        "dcl_qty",
                        "dcl_uprc_amt",
                        "dcl_total_amt",
                        "dcl_currcd",
                        "usd_stat_total_amt"*/
                    ];
                }else{
                    this.detailParam.disableField = [
                        //当前禁用的字段,需要禁用的字段值在这里改
                       /* "bizop_etpsno",
                        "bizop_etps_nm",
                        "dcl_etpsno",
                        "dcl_etps_nm",
                        "putrec_no",
                        "rcvgd_etpsno",
                        "rcvgd_etps_nm",
                        "impexp_portcd",
                        "dcl_plc_cuscd",
                        "impexp_markcd",
                        "mtpck_endprd_markcd",
                        "supv_modecd",
                        "trsp_modecd",
                        "dclcus_flag",
                        "dclcus_typecd",
                        "dec_type",
                        "stship_trsarv_natcd",
                        "invt_type",
                        "dcl_typecd",
                        "rmk",

                        "putrec_seqno",
                        "gds_mtno",
                        "gdecd",
                        "gds_nm",
                        "gds_spcf_model_desc",
                        "dcl_unitcd",
                        "dcl_qty",
                        "dcl_uprc_amt",
                        "dcl_total_amt",
                        "dcl_currcd",
                        "usd_stat_total_amt"*/
                    ];
                }
                //保存的路径
                this.detailParam.url = "/api/crtEnterInven/saveInventoryDetail";
                //返回之后的查询路径
                //this.detailParam.callBackUrl = "detailmanage/detailQuery";
                //this.detailParam.isShowError = false;
                //点击取消
                $("#ws-page-back").click(function () {
                    sw.page.modules["bondedienter/seeEnterInventoryDetail"].cancel(etps_inner_invt_no);
                });
                break;
            }
            case "RQHZQD": {
                // 不可编辑状态
                console.log(isEdit+"2222222222222")
                if (isEdit == "true") {
                    this.detailParam.disableField = [
                        //当前禁用的字段,需要禁用的字段值在这里改
                       /* "dec_type",
                        "invt_type",
                        "dcl_typecd",

                        "putrec_seqno",
                        "gds_seqno",
                        "gds_mtno",
                        "gdecd",
                        "gds_nm",
                        "gds_spcf_model_desc",
                        "dcl_unitcd",
                        "dcl_qty",
                        "dcl_uprc_amt",
                        "dcl_total_amt",
                        "dcl_currcd",
                        "usd_stat_total_amt"*/
                    ];
                }else{
                    this.detailParam.disableField = [
                        //当前禁用的字段,需要禁用的字段值在这里改
                        /*"bizop_etpsno",
                        "bizop_etps_nm",
                        "dcl_etpsno",
                        "dcl_etps_nm",
                        "putrec_no",
                        "rcvgd_etpsno",
                        "rcvgd_etps_nm",
                        "impexp_portcd",
                        "dcl_plc_cuscd",
                        "impexp_markcd",
                        "mtpck_endprd_markcd",
                        "supv_modecd",
                        "trsp_modecd",
                        "dclcus_flag",
                        "dclcus_typecd",
                        "dec_type",
                        "stship_trsarv_natcd",
                        "invt_type",
                        "dcl_typecd",
                        "rmk",

                        "putrec_seqno",
                        "gds_mtno",
                        "gdecd",
                        "gds_nm",
                        "gds_spcf_model_desc",
                        "dcl_unitcd",
                        "dcl_qty",
                        "dcl_uprc_amt",
                        "dcl_total_amt",
                        "dcl_currcd",
                        "usd_stat_total_amt"*/
                    ];
                }
                //保存的路径
                this.detailParam.url = "/api/crtEnterInven/saveInventoryDetail";
                //返回之后的查询路径
                //this.detailParam.callBackUrl = "detailmanage/detailQuery";
                //this.detailParam.isShowError = false;
                //点击取消
                $("#ws-page-back").click(function () {
                    sw.page.modules["bondedienter/seeEnterInventoryDetail"].callBackQuery();
                });
                break;
            }

        } // 不可编辑状态
        if (isEdit == "false") {
          /*  this.detailParam.disableField = [

                "bizop_etpsno",
                "bizop_etps_nm",
                "dcl_etpsno",
                "dcl_etps_nm",
                "putrec_no",
                "rcvgd_etpsno",
                "rcvgd_etps_nm",
                "impexp_portcd",
                "dcl_plc_cuscd",
                "impexp_markcd",
                "mtpck_endprd_markcd",
                "supv_modecd",
                "trsp_modecd",
                "dclcus_flag",
                "dclcus_typecd",
                "dec_type",
                "stship_trsarv_natcd",
                "invt_type",
                "dcl_typecd",
                "rmk",

                "putrec_seqno",
                "gds_mtno",
                "gdecd",
                "gds_nm",
                "gds_spcf_model_desc",
                "dcl_unitcd",
                "dcl_qty",
                "dcl_uprc_amt",
                "dcl_total_amt",
                "dcl_currcd",
                "usd_stat_total_amt"
            ];*/
            // 屏蔽保存取消按钮
            $("#btnDiv").addClass("hidden");
        } else {
            // 显示保存取消按钮
            $("#btnDiv").removeClass("hidden");
        }
        // 查询详情
        this.query();

        //点击保存(未确认数据)
        $("#ws-page-apply").click(function () {
            sw.page.modules["bondedienter/seeEnterInventoryDetail"].saveEntryInfo(etps_inner_invt_no, type, sw.ie);
        });
       /* //点击取消
        $("#ws-page-back").click(function () {
            sw.page.modules["bondedienter/seeEnterInventoryDetail"].cancel(etps_inner_invt_no);
        });*/
    },


}