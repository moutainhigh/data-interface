//清单查询
sw.page.modules["bondediexit/exitManifest"] = sw.page.modules["bondediexit/exitManifest"] || {

    query: function () {
        // 获取查询表单参数
        var dcl_time = $("[name='dcl_time']").val();
        var status = $("[name='status']").val();
        var return_status = $("[name='return_status']").val();
        var passport_no = $("[name='passport_no']").val();
        var rlt_no = $("[name='rlt_no']").val();

        // 拼接URL及参数
        var url = sw.serializeObjectToURL("api/bondediexit/queryExitManifest", {
            dcl_time: dcl_time,//申报时间
            status: status,//系统数据状态
            return_status: return_status,//回执状态
            passport_no: passport_no,//核放单编号
            rlt_no: rlt_no//核注清单编号
        });

        // 数据表
        sw.datatable("#query-exitManifest-table", {
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
                        if (row.status == "BDDS4" || row.status == "INIT") {
                            return '<input type="checkbox" class="submitKey" value="' + row.etps_preent_no + '" />';
                        }
                        else {
                            return "";
                        }
                    }
                },
                {
                    data: "sas_passport_preent_no", label: "预录入编号"
                },
                {
                    data: "passport_no", label: "核放单号"
                },
                {
                    data: "rlt_no", label: "核注清单编号"
                },
                {
                    label: "企业内部编号", render: function (data, type, row) {
                    return '<a href="javascript:void(0)"  onclick="' + "javascript:sw.pageModule('bondediexit/exitManifest').updateExitManifest('" + row.etps_preent_no + "','" + row.status + "')" + '">' + row.etps_preent_no + '</a>'
                }
                },
                {
                    label: "申报状态", render: function (data, type, row) {
                    switch (row.status) {
                        case "INIT"://数据待补充
                            textColor = "text-red";
                            row.status = "数据待补充";
                            break;
                        case "BDDS4"://出区核放单待申报
                            textColor = "text-yellow";
                            row.status = "核放单待申报";
                            break;
                        case "BDDS40"://出区核放单申报中
                            textColor = "text-green";
                            row.status = "核放单申报中";
                            break;
                        case "BDDS41"://出区核放单正在发往海关
                            textColor = "text-green";
                            row.status = "核放单正在发往海关";
                            break;
                        case "BDDS42"://出区核放单申报成功
                            textColor = "text-green";
                            row.status = "核放单申报成功";
                            break;
                    }
                    return "<span class='" + textColor + "'>" + row.status + "</span>";
                }
                },
                {
                    label: "申报时间", render: function (data, type, row) {
                    if (!isEmpty(row.dcl_time)) {
                        return moment(row.dcl_time).format("YYYY-MM-DD HH:mm:ss");
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
                        value = isEmpty(row.return_status) ? "" : row.return_status;
                    }
                    return '<a href="javascript:void(0)"  onclick="' + "javascript:sw.pageModule('bondediexit/exitManifest').seePassPortRec('" + row.id + "','" + row.etps_preent_no + "')" + '">' + value + '</a>'
                }
                }
            ]
        });
    },

    deleteEPassPort: function () {
        var submitKeys = "";
        $(".submitKey:checked").each(function () {
            submitKeys += "," + $(this).val();
        });
        if (submitKeys.length > 0) {
            submitKeys = submitKeys.substring(1);
        } else {
            sw.alert("请先勾选要删除的出区核放单信息！");
            return;
        }
        var postData = {
            submitKeys: submitKeys
        };
        sw.confirm("确定删除该出区核注清单", "确认", function () {
            sw.ajax("api/bondediexit/exitmanifest/deleteExitManifest", "POST", postData, function (rsp) {
                sw.pageModule("bondediexit/exitManifest").query();
            });
        });
    },

    // 提交海关
    submitCustomEPassPort: function () {
        var submitKeys = "";
        $(".submitKey:checked").each(function () {
            submitKeys += "," + $(this).val();
        });
        if (submitKeys.length > 0) {
            submitKeys = submitKeys.substring(1);
        } else {
            sw.alert("请先勾选要提交海关的出区核放单信息！");
            return;
        }
        sw.confirm("请确认数据无误并提交海关", "确认", function () {
            sw.blockPage();
            var postData = {
                submitKeys: submitKeys
            };
            $("#submitCustom").prop("disabled", true);
            sw.ajax("api/bondediexit/exitmanifest/submitCustom", "POST", postData, function (rsp) {
                if (rsp.data.result == "true") {
                    sw.alert("提交海关成功", "提示", function () {
                    }, "modal-success");
                    $("#submitCustom").prop("disabled", false);
                    sw.page.modules["bondediexit/exitManifest"].query();
                } else {
                    sw.alert(rsp.data.msg);
                }
                $.unblockUI();
            });
        });
    },

    updateExitManifest: function (submitKeys, status) {
        if (status == "BDDS4" || status == "INIT") {
            var url = "bondediexit/seeExitManifestDetail?type=CQHFDXG&isEdit=true&mark=upd&submitKeys=" + submitKeys;
        } else {
            var url = "bondediexit/seeExitManifestDetail?type=CQHFDXG&isEdit=false&mark=upd&submitKeys=" + submitKeys;
        }
        sw.modelPopup(url, "出区核放单详情", false, 1000, 600);
    },

    init: function () {
        $(".input-daterange").datepicker({
            language: "zh-CN",
            todayHighlight: true,
            format: "yyyy-mm-dd",
            autoclose: true
        });
        $("[ws-search]").unbind("click").click(this.query).click();
        $("[ws-delete]").unbind("click").click(this.deleteEPassPort);
        $("[ws-submit]").unbind("click").click(this.submitCustomEPassPort);
        $table = $("#query-exitManifest-table");
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

    seePassPortRec: function (id, etps_preent_no) {
        var url = "bondediexit/PassPortReturnInfo?id=" + id + "&etps_preent_no=" + etps_preent_no;
        sw.modelPopup(url, "查看核放单回执详情", false, 800, 300);
    }

};

