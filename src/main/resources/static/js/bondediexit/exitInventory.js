//清单查询
sw.page.modules["bondediexit/exitInventory"] = sw.page.modules["bondediexit/exitInventory"] || {

    query: function () {
        debugger;
        // 获取查询表单参数
        var entry_dcl_time = $("[name='entry_dcl_time']").val();
        var status = $("[name='status']").val();
        var return_status = $("[name='return_status']").val();
        var bond_invt_no = $("[name='bond_invt_no']").val();

        // 拼接URL及参数
        var url = sw.serializeObjectToURL("api/bondediexit/queryexitinventory", {
            entry_dcl_time: entry_dcl_time,//申报时间
            status: status,//系统数据状态
            return_status: return_status,//回执状态
            bond_invt_no: bond_invt_no//核注清单编号
        });

        // 数据表
        sw.datatable("#query-exitInventory-table", {
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
            lengthMenu: [[50, 100, 1000], [50, 100, 1000]],
            searching: false,//开启本地搜索
            columns: [
                {
                    label: '<input type="checkbox" name="cb-check-all"/>',
                    orderable: false,
                    data: null,
                    render: function (data, type, row) {
                        if (row.status == "BDDS2") {
                            return '<input type="checkbox" class="submitKey" value="' +
                                row.etps_inner_invt_no + '" />';
                        }
                        else {
                            return "";
                        }
                    }
                },
                {
                    label: "企业内部清单编码", render: function (data, type, row) {
                    return '<a href="javascript:void(0)"  onclick="' + "javascript:sw.pageModule('bondediexit/exitInventory').seeExitInventoryInfo('" + row.etps_inner_invt_no + "')" + '">' + row.etps_inner_invt_no + '</a>'
                }
                },
                {
                    data: "bond_invt_no", label: "核注清单编号"
                },
                {
                    data: "status", label: "申报状态"
                },
                {
                    data: "entry_dcl_time", label: "申报时间"
                },
                {
                    data: "return_status", label: "回执状态"
                },
                {
                    data: "return_time", label: "回执时间"
                },
                {
                    data: "return_info", label: "回执备注"
                }
            ]
        });
    },

    // 提交海关
    submitCustomByCode: function () {
        var submitKeys = "";
        $(".submitKey:checked").each(function () {
            submitKeys += "," + $(this).val();
        });
        if (submitKeys.length > 0) {
            submitKeys = submitKeys.substring(1);
        } else {
            sw.alert("请先勾选要提交海关的出区核注清单信息！");
            return;
        }

        sw.confirm("请确认数据无误并提交海关", "确认", function () {

            sw.blockPage();

            var postData = {
                submitKeys: submitKeys
            };

            $("#submitCustom").prop("disabled", true);

            sw.ajax("api/bondediexit/exitinventory/submitCustom", "POST", postData, function (rsp) {
                if (rsp.data.result == "true") {
                    sw.alert("提交海关成功", "提示", function () {
                    }, "modal-success");
                    $("#submitCustom").prop("disabled", false);
                    sw.page.modules["bondediexit/exitInventory"].query();
                } else {
                    sw.alert(rsp.data.msg);
                }
                $.unblockUI();
            });
        });
    },

    deleteByCode: function () {
        var submitKeys = "";
        $(".submitKey:checked").each(function () {
            submitKeys += "," + $(this).val();
        });
        if (submitKeys.length > 0) {
            submitKeys = submitKeys.substring(1);
        } else {
            sw.alert("请先勾选要删除的出区核注清单信息！");
            return;
        }
        var postData = {
            submitKeys: submitKeys
        };
        sw.confirm("确定删除该出区核注清单", "确认", function () {
            sw.ajax("api/bondediexit/exitinventory/deleteExitInventory", "POST", postData, function (rsp) {
                sw.pageModule("bondediexit/exitInventory").query();
            });
        });
    },

    init: function () {

        $(".input-daterange").datepicker({
            language: "zh-CN",
            todayHighlight: true,
            format: "yyyy-mm-dd",
            autoclose: true
        });

        $("[ws-search]").unbind("click").click(this.query).click();
        $("[ws-delete]").unbind("click").click(this.deleteByCode);
        $("[ws-submit]").unbind("click").click(this.submitCustomByCode);

        $table = $("#query-exitInventory-table");
        $table.on("change", ":checkbox", function () {
            if ($(this).is("[name='cb-check-all']")) {
                //全选
                $(":checkbox", $table).prop("checked", $(this).prop("checked"));
            } else {
                //复选
                var checkbox = $("tbody :checkbox", $table);
                $(":checkbox[name='cb-check-all']", $table).prop('checked', checkbox.length == checkbox.filter(':checked').length);
            }
        });
    },

    seeExitInventoryInfo: function (etpsInnerInvtNo) {
        var url = "bondediexit/seeExitInventoryDetail?type=CQHZQDXG&isEdit=true&mark=upd&submitKeys=" + etpsInnerInvtNo;
        sw.modelPopup(url, "查看清单详情", false, 1100, 930);
    }

    // seeInventoryRec: function (guid, data_status) {
    //     var url = "detailmanage/seeInventoryRec?type=QDCX&isEdit=true&guid=" + guid + "&data_status=" + data_status;
    //     sw.modelPopup(url, "查看清单回执详情", false, 800, 300);
    // }

};

