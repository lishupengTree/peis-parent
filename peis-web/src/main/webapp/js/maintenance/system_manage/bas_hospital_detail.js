/**
 *     卫生院维护
 */
//行政区划/节点类别/医院级别/级别等级/机构分类
var distcode_combo, nodetype_combo, hosdegree_combo, degreelevel_combo, orgtype_combo;

$(function () {
    //加载各种下拉框
    fillComboInThisPage();

    $("#nodetype_pos .dhx_combo_input").keydown(forbidInput);
    $("#orgtype_pos .dhx_combo_input").keydown(forbidInput);
    $("#hosdegree_pos .dhx_combo_input").keydown(forbidInput);
    $("#degreelevel_pos .dhx_combo_input").keydown(forbidInput);

    $('#hosnum').keyup(function () {
        $('#nodecode').val($(this).val());
    });
    //$('#nodecode').keyup(function(){
    //	$('#hosnum').val($(this).val());
    //});

    //重写partent关闭层
    parent.doClose = function () {
        parent.$.unblockUI();
    };
});
//验证页面数据
function canSave() {
    var hosnum = $.trim($('#hosnum').val());
    var nodecode = $.trim($('#nodecode').val());
    var hosname = $.trim($('#hosname').val());
    var empnumber = $.trim($('#empnumber').val());
    var beds = $.trim($('#beds').val());
    var doctors = $.trim($('#doctors').val());
    var nurses = $.trim($('#nurses').val());
    var sn = $.trim($('#sn').val());
    if (hosname == "") {
        alert("医院名称不能为空！");
        return false;
    }
    if (checkNull("shortname", "简称")) {
        return;
    }
    if (countLen($("#shortname").val()) > 10) {
        alert("【简称】输入太长！");
        return;
    }

    var reg1 = /^[A-Za-z0-9]+$/;
    if (hosnum == "") {
        alert("医院编码不能为空！");
        return false;
    }
    if (!reg1.test(hosnum) || hosnum.length != 4) {
        alert("医院编码为四位长度字母或数字");
        return false;
    }
    if (nodecode == "") {
        alert("医院编码不能为空！");
        return false;
    }
    if (!reg1.test(nodecode) || nodecode.length != 4) {
        alert("节点编码为四位长度字母或数字");
        return false;
    }

    var reg2 = /^[0-9]+$/;
    if (!reg2.test(empnumber) && empnumber != "") {
        alert("请输入数字！");
        return false;
    }
    if (!reg2.test(beds) && beds != "") {
        alert("请输入数字！");
        return false;
    }
    if (!reg2.test(doctors) && doctors != "") {
        alert("请输入数字！");
        return false;
    }
    if (!reg2.test(nurses) && nurses != "") {
        alert("请输入数字！");
        return false;
    }
    if (!reg2.test(sn) && sn != "") {
        alert("请输入数字！");
        return false;
    }
    return true;
}
//保存按钮
function dosave() {
    //---验证
    if (!canSave())return false;
    $('#msg').html("执行中...");
    $.ajax({
        cache: false,
        async: true,
        type: "post",
        error: function () {
            alert("fail.");
        },
        url: "maintenance/chg_manage/saveBasHospitals.htm",
        data: $('#the_form').serialize(),
        success: function (msg) {
            //return fixity information
            if (msg == "success") {
                $('#msg').html("保存成功！");
                $('#showtype').val(0);
                var node = "{id:\"" + $('#hosnum').val() + "\", pid:\"" + $('#supunit').val() + "\", name:\"" + $('#hosname').val() + "\"}";
                parent.window.mywin.updateThisGrid(eval("(" + node + ")"));
            } else if (msg == "fail.") {
                $('#msg').html("fail.");
            } else if (msg == "hosnumOrNodecodeIsExist") {
                alert("医院编码或节点编码已存在！");
                $('#msg').html("医院编码或节点编码已存在！");
            }
        }
    });
}

//加载各种下拉框
function fillComboInThisPage() {
    distcode_combo = new dhtmlXCombo("distcode", "distcode", 102);
    distcode_combo.clearAll();
    fillCombo(distcode_combo, "dist", true);  //行政区划 这个还有问题

    nodetype_combo = new dhtmlXCombo("nodetype", "nodetype", 102);
    nodetype_combo.clearAll();
    nodetype_combo.addOption("医院", "医院");
    nodetype_combo.addOption("院区", "院区");
    nodetype_combo.readonly(true);
    nodetype_combo.attachEvent("onSelectionChange", function () {
        if (nodetype_combo.getSelectedValue() == '院区') {
            if (!$('#hosnum').hasClass('not_editable')) {
                $('#hosnum').addClass('not_editable');
                $('#hosnum').attr('readonly', 'readonly');
                $('#hosnum').val($('#supunit').val());

                $('#nodecode').removeAttr('readonly');
                $('#nodecode').removeClass('not_editable');
            }
        }
        if (nodetype_combo.getSelectedValue() == '医院') {
            if ($('#hosnum').hasClass('not_editable')) {
                $('#hosnum').removeClass('not_editable');
                $('#hosnum').removeAttr('readonly');
                $('#hosnum').val($('#oldhosnum').val());
                $('#nodecode').attr('readonly', 'readonly');
                $('#nodecode').addClass('not_editable');
                $('#nodecode').val($('#oldhosnum').val());
            }
        }
    });

    hosdegree_combo = new dhtmlXCombo("hosdegree", "hosdegree", 102);
    hosdegree_combo.clearAll();
    hosdegree_combo.readonly(true);
    fillCombo(hosdegree_combo, "hosdegree", false);
    hosdegree_combo.attachEvent("onChange", function () {
        $('#hosdname').val(hosdegree_combo.getSelectedText());
    });
    degreelevel_combo = new dhtmlXCombo("degreelevel", "degreelevel", 102);
    degreelevel_combo.clearAll();
    degreelevel_combo.readonly(true);
    fillCombo(degreelevel_combo, "degreelevel", false);
    degreelevel_combo.attachEvent("onChange", function () {
        $('#degreelname').val(degreelevel_combo.getSelectedText());
    });
    orgtype_combo = new dhtmlXCombo("orgtype", "orgtype", 102);
    orgtype_combo.clearAll();
    orgtype_combo.readonly(true);
    orgtype_combo.setOptionWidth(130);
    fillCombo(orgtype_combo, "orgtype", false);
    orgtype_combo.attachEvent("onChange", function () {
        $('#orgtype').val(orgtype_combo.getSelectedText());
    });
}
function forbidInput(ev) {//禁止退格键
    var event = window.event || ev;
    if (event.keyCode == 8) {
        event.returnValue = false;
        if (event == ev) {
            ev.preventDefault();
        }
    }
}