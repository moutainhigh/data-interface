/**
 * 保税清单查询
 * Created by lzy on 2018-12-26
 */
sw.page.modules["bondinvenmanage/bondinvenquery"] = sw.page.modules["bondinvenmanage/bondinvenquery"] || {

    query: function () {
        // 获取查询表单参数
        var startFlightTimes = $("[name='startFlightTimes']").val();
        var endFlightTimes = $("[name='endFlightTimes']").val();
        var orderNo = $("[name='orderNo']").val();
        var logisticsNo = $("[name='logisticsNo']").val();
        var preNo = $("[name='preNo']").val();
        var invtNo = $("[name='invtNo']").val();
        var returnStatus = $("[name='returnStatus']").val();

        // 拼接URL及参数
        var url = sw.serializeObjectToURL("api/bondinvenmanage/querybondinvenquery", {
            startFlightTimes: startFlightTimes,//申报开始时间
            endFlightTimes: endFlightTimes,//申报结束时间
            orderNo: orderNo,//订单编号
            logisticsNo: logisticsNo,//物流运单编号
            preNo: preNo,//电子口岸标识编号
            invtNo: invtNo,//海关清单编号
            returnStatus: returnStatus//回执状态
        });

        // 数据表
        sw.datatable("#query-detailQuery-table", {
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
                    label: "订单编号", render: function (data, type, row) {
                    return '<a href="javascript:void(0)"  onclick="' + "javascript:sw.pageModule('bondinvenmanage/bondinvenquery').seeBondInvenInfo('" + row.guid + "','" + row.order_no + "','" + row.return_status + "')" + '">' + row.order_no + '</a>'
                }
                },
                {data: "logistics_no", label: "物流运单编号"},
                {data: "invt_no", label: "海关清单编号"},
                {
                    label: "电商平台名称", render: function (data, type, row) {
                    return '<div style="width:100px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;" ' +
                        'title="' + row.ebp_name + '">' + row.ebp_name + '</div>';
                }
                },
                {
                    label: "电商企业名称", render: function (data, type, row) {
                    return '<div style="width:100px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;" ' +
                        'title="' + row.ebc_name + '">' + row.ebc_name + '</div>';
                }
                },
                {
                    label: "物流企业名称", render: function (data, type, row) {
                    return '<div style="width:100px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;" ' +
                        'title="' + row.logistics_name + '">' + row.logistics_name + '</div>';
                }
                },
                {
                    label: "申报日期", render: function (data, type, row) {
                    if (!isEmpty(row.app_time)) {
                        return moment(row.app_time).format("YYYY-MM-DD HH:mm:ss");
                    }
                    return "";
                }
                },
                {
                    label: "回执状态", render: function (data, type, row) {
                    var value = "";
                    if (!isEmpty(row.return_status_name)) {
                        value = row.return_status_name
                    } else {
                        value = row.return_status;
                    }
                    return '<a href="javascript:void(0)"  onclick="' + "javascript:sw.pageModule('bondinvenmanage/bondinvenquery').seeBondInvenRec('" + row.guid + "','" + row.data_status + "')" + '">' + value + '</a>'
                }
                }
            ]
        });
    },

    init: function () {
        $("[name='startFlightTimes']").val(moment(new Date()).subtract('days',7).format("YYYY-MM-DD"));
        $("[name='endFlightTimes']").val(moment(new Date()).format("YYYY-MM-DD"));
        $(".input-daterange").datepicker({
            language: "zh-CN",
            todayHighlight: true,
            format: "yyyy-mm-dd",
            autoclose: true
        });
        $("[ws-search]").unbind("click").click(this.query).click();
        $(".btn[ws-search]").click();
    },

    seeBondInvenInfo: function (guid, order_no, return_status) {
        if (return_status == 100) {
            var url = "bondinvenmanage/seebondinvendetail?type=BSQDCX&isEdit=true&guid=" + guid + "&orderNo=" + order_no;
        } else {
            var url = "bondinvenmanage/seebondinvendetail?type=BSQDCX&isEdit=false&guid=" + guid + "&orderNo=" + order_no;
        }
        sw.modelPopup(url, "查看保税清单详情", false, 1100, 930);
    },

    seeBondInvenRec: function (guid, data_status) {
        var url = "bondinvenmanage/seebondinvenrec?type=BSQDHZ&isEdit=true&guid=" + guid + "&data_status=" + data_status;
        sw.modelPopup(url, "查看保税清单回执详情", false, 800, 300);
    }

};
