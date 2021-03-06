//清单查询
sw.page.modules["bondediexit/crtExitInventory"] = sw.page.modules["bondediexit/crtExitInventory"] || {

    query: function () {
        // 获取查询表单参数
        var billNo = $("[name='billNo']").val();
        var returnStatus = $("[name='returnStatus']").val();
        var customCode = $("[name='customCode']").val();

        // 拼接URL及参数
        var url = sw.serializeObjectToURL("api/bondediexit/querycrtexitinventory", {
            billNo: billNo,//提运单号
            returnStatus: returnStatus,//回执状态
            customCode: customCode//海关编码
        });

        // 数据表
        sw.datatable("#query-crtExitInventory-table", {
            ajax: url,
            lengthMenu: [[50, 100, 1000], [50, 100, 1000]],
            searching: false,//开启本地搜索
            columns: [
                //还需判断下状态
                {
                    label: '<input type="checkbox" name="cb-check-all"/>',
                    orderable: false,
                    data: null,
                    render: function (data, type, row) {
                        if (row.return_status == "800") {
                            return '<input type="checkbox" class="submitKey" value="' +
                                row.bill_no + '" />';
                        }
                        else {
                            return "";
                        }
                    }
                },
                {
                    data: "bill_no", label: "提运单号"
                },
                {
                    data: "asscount", label: "分单数量"
                }
            ]
        });
    },

    crtExitInventoryData: function () {
        var submitKeys = "";
        var customCode = $("[name='customCode']").val();
        $(".submitKey:checked").each(function () {
            submitKeys += "," + $(this).val();
        });
        if (submitKeys.length > 0) {
            submitKeys = submitKeys.substring(1);
        } else {
            sw.alert("请勾选保税清单信息！");
            return;
        }
        var postData = {
            submitKeys: submitKeys,
            customCode: customCode
        };
        sw.pageModule('bondediexit/crtExitInventory').seeExitInventoryDetail(submitKeys, customCode);
    },

    seeExitInventoryDetail: function (submitKeys, customCode) {
        var url = "bondediexit/seeExitInventoryDetail?type=CQHZQDCJ&isEdit=true&mark=crt&submitKeys=" + submitKeys + "&customsCode=" + customCode;
        sw.modelPopup(url, "新建出区核注清单信息", false, 1000, 700);
    },

    EbusinessEnt: function () {
        sw.ajax("api/bondediexit/EbusinessEnt", "GET", "", function (rsp) {
            var result = rsp.data;
            for (var idx in result) {
                var customsCode = result[idx].customs_code;
                var name = result[idx].ent_name;
                var option = $("<option>").text(name).val(customsCode);
                $("#customsCode").append(option);
            }
        });
    },

    init: function () {
        $(".input-daterange").datepicker({
            language: "zh-CN",
            todayHighlight: true,
            format: "yyyy-mm-dd",
            autoclose: true
        });
        $("[ws-search]").unbind("click").click(this.query);
        $("#crtExitInventory").unbind("click").click(this.crtExitInventoryData);
        $(".btn[ws-search]").click();
        $table = $("#query-crtExitInventory-table");
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
        this.EbusinessEnt();
    }

};

