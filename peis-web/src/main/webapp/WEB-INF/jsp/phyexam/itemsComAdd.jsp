<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <base href="<%=basePath%>"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>体检项目定义</title>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="css/deptbtn.css"/>
    <link rel="stylesheet" type="text/css" href="css/dictionary.css"/>
    <link rel="stylesheet" href="My97DatePicker/skin/WdatePicker.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeStyle.css" type="text/css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeIcons.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <style type="text/css">
        .text_field {
            margin-left: 4px
        }

        .text_field_required {
            margin-left: 4px
        }

        .tree li button.chk.checkbox_false_part {
            background: url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -24px transparent;
        }

        .tree li button.chk.checkbox_false_part_focus {
            background: url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -12px transparent;
        }

        .tree li button.chk.checkbox_true_part {
            background: url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -24px transparent;
        }

        .tree li button.chk.checkbox_true_part_focus {
            background: url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -36px transparent;
        }

        #scopeDiv {
            height: 32px;
            padding: 0px;
            margin-top: 0px;
            margin-left: 5px;
            float: left;
        }

        #scopeDiv ul {
            list-style: none;
        }

        #scopeDiv ul li {
            width: 64px;
            height: 27px;
            text-align: center;
            line-height: 26px;
            background: url(img/ss.gif) no-repeat left 1px;
            border: 0 none;
        }

        .mustwrt .dhx_combo_box {
            border: 1px solid red;
        }
    </style>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.lrTool.js"></script>
    <script type="text/javascript" src="js/clc/comboTool.js"></script>
    <script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
    <script type="text/javascript" src="js/dept/verify.js"></script>
    <script type="text/javascript" src="js/maintenance/maintenance_tool.js"></script>
    <script type="text/javascript" src="jsfile.htm?method=dict&nekey=6002"></script>     <!-- 血管颜色 -->
    <script type="text/javascript" src="jsfile.htm?method=dict&nekey=18"></script>     <!-- 发票科目 -->
    <script type="text/javascript" src="jsfile.htm?method=dict&nekey=19"></script>     <!-- 核算科目 -->
    <script type="text/javascript" src="js/maintenance/maintenance_tool.js"></script>
    <script type="text/javascript">
        var save_num = ${save_num};
        var zTree;
        var treeNodes;
        var sheettype_combo;
        //树的参数
        var setting = {
            isSimpleData: true,
            treeNodeKey: "id",
            treeNodeParentKey: "pId",
            checkable: true,
            //用户自定义checked列
            //checkedCol: "checked",
            //级联选择父节点p；级联选择子节点s
            checkType: {"Y": "ps", "N": "ps"},
            //checkStyle:"radio",//"checkbox"
            showLine: true,
            expandSpeed: "fast"//展开速度
            //callback:{
            // click: zTreeOnClick //点击事件
            //}
        };
        $(function () {
            $.ajax({
                async: false,   //是否异步
                cache: false,   //是否使用缓存
                type: 'post',   //请求方式,post
                dataType: "json",   //数据传输格式
                data: "checkedScope=${checkedScope}&parentid=${pexam.parentid}",  //url传数据
                url: "phyexam/selectedIndTree.htm",   //请求链接
                error: function () {
                    alert('fail');
                },
                success: function (data) {
                    treeNodes = data;
                }
            });
            zTree = $("#menuTree").zTree(setting, treeNodes);   //前台树的位置
            //血管颜色
            for (var i = 0; i < dicts6002.length; i++) {
                ys.addOption(dicts6002[i].nevalue, dicts6002[i].contents);
            }
            //18  发票
            for (var i = 0; i < dicts18.length; i++) {
                fpkm.addOption(dicts18[i].nevalue, dicts18[i].contents);
            }
            //19  核算
            for (var i = 0; i < dicts19.length; i++) {
                hskm.addOption(dicts19[i].nevalue, dicts19[i].contents);
            }

            sfws.selectOption(sfws.getIndexByValue('${pexam.sfws}'), true, true);
            ys.selectOption(ys.getIndexByValue('${pexam.xgys}'), true, true);  //选中 血管颜色option
            fpkm.selectOption(fpkm.getIndexByValue('${pexam.fpkm}'), true, true);  //选中  发票科目option
            hskm.selectOption(hskm.getIndexByValue('${pexam.hskm}'), true, true);  //选中  核算科目option
            ggxm.selectOption(ggxm.getIndexByValue('${pexam.ggxm}'), true, true);  //选中  公共项目option
            tjxm.selectOption(tjxm.getIndexByValue('${pexam.tjxm}'), true, true);  //选中  体检项目的option
            cqch.selectOption(cqch.getIndexByValue('${pexam.cqch}'), true, true); //餐前餐后
            bgxs.selectOption(bgxs.getIndexByValue('${pexam.bgxs}'), true, true); //餐前餐后

            sheettype_combo = new dhtmlXCombo("sheettype", "sheettype", 102);
            fillCombo(sheettype_combo, "sheettype", true);

        });

        //赋值变更了的选择树
        function doSetChangeVal() {
            var changeNodes = zTree.getChangeCheckedNodes();
            //alert("changeNodes:"+changeNodes);
            var len = changeNodes.length;
            var addScope = "";
            var removeScope = "";
            if (len >= 1) {
                for (var i = 0; i < len; i++) {
                    if (changeNodes[i].level != 0) {
                        if (changeNodes[i].checked) {
                            addScope = addScope + changeNodes[i].id + ";";
                        } else {
                            removeScope = removeScope + changeNodes[i].id + ";";
                        }
                    }
                }
            }
            $("#addscopes").val(addScope);
            $("#removescopes").val(removeScope);
        }

        //刷新纪录的旧的选中的值
        function doRefreshCheckOld() {
            var changeNodes = zTree.getChangeCheckedNodes();
            var len = changeNodes.length;
            if (len >= 1) {
                for (var i = 0; i < len; i++) {
                    changeNodes[i].checkedOld = changeNodes[i].checked;
                }
            }
        }

        //空校验
        function checkNullSelect1(id, objname) {
            var obj = $("#" + id);
            if (obj) {
                if (obj.val() == "" || obj.val() == 'undefined') {
                    window.alert("请选择【" + objname + "】！");
                    return true;
                }
            }
            return false;
        }
        //新增、保存
        function doSave() {
            //必填验证
            if (checkNull("comname", "项目名称")) {
                return;
            }
            if (checkNullSelect("comclass", "项目类别")) {
                return;
            }
            if (checkNull("cost", "费用")) {
                return;
            }
            if (checkNull("excdept", "执行科室")) {
                return;
            }
            if (checkNullSelect("forsex", "适用性别")) {
                return;
            }
            //长度验证
            if (countLen($("#comname").val()) > 50) {
                alert("【项目名称】输入太长！");
                return;
            }
            if (countLen($("#descriptions").val()) > 100) {
                alert("【项目说明】输入太长！");
                return;
            }
            if (countLen($("#comments").val()) > 200) {
                alert("【备注】输入太长！");
                return;
            }
            //其他验证
            if (checkDouble($("#cost").val(), "费用", 2)) {
                return;
            }
            var sn = $("#sn").val();
            if (isNaN(sn)) {
                alert("显示序号不是数字");
                return;
            }
            if (isNaN($("#printnum").val())) {
                alert("打印次数不是数字");
                return;
            }
            if (isNaN($("#needtime").val())) {
                alert("所需时间不是数字");
                return;
            }
            //判断为不为空
            if (checkNullSelect1("fpkm", "发票科目")) {
                return;
            }
            if (checkNullSelect1("hskm", "核算科目")) {
                return;
            }
            if (checkNullSelect1("ggxm", "公共项目")) {
                return;
            }

            var nodes = zTree.getCheckedNodes();
            var sum = 0;
            for (var i = 0; i < nodes.length; i++) {
                var cost = parseInt(nodes[i].cost);
                sum = sum + cost;
            }
            //alert(sum);
            $("#cost").val(sum);

//	if(doCheckRepeat()){
            doSetChangeVal();
            showMsg("数据保存中...");
            $.ajax({
                cache: false,   //是否使用缓存
                url: "phyexam/itemsComSave.htm",
                async: false,   //是否异步，false为同步
                type: "post",
                data: $('#pexam_form').serialize() + "&fpkmname=" + encodeURI(encodeURI(fpkm.getComboText())) + "&hskmname=" + encodeURI(encodeURI(hskm.getComboText())) + "&xgysname=" + encodeURI(encodeURI(ys.getComboText())) + "&cqch=" + encodeURI(encodeURI(cqch.getComboText())) + "&memo=" + encodeURI(encodeURI($("#zl2").val())),
                error: function () {
                    alert("ajax请求失败");
                    closeMsg();
                },
                success: function (reply) {
                    if (reply == "fail") {
                        closeMsg();
                        alert("保存失败");
                    } else {
                        closeMsg();
                        alert("保存成功");
                        $("#operationType").val("modify");//修改标志为：修改
                        $("#itemcode").val(reply);//更新自动生成项目代码
                        $("#old_itemname").val($("#itemname").val());//更新项目名称
                        $("#selfAddButton").css("display", "inline");//显示新增按钮
                        doRefreshCheckOld();
                        save_num++;
                    }
                }
            });
//	}
        }

        //验证重复
        function doCheckRepeat() {
            var comname = $("#comname").val();
            var old_comname = $("#old_comname").val();
            var reVal = true;
            if (comname != "" && comname != old_comname) {
                $.ajax({
                    cache: false,   //是否使用缓存
                    url: "phyexam/itemsComCheckRepeat.htm",
                    async: false,   //是否异步，false为同步
                    type: "post",
                    data: "comname=" + comname,
                    error: function () {
                        alert("ajax请求失败");
                    },
                    success: function (reply) {
                        if (reply == "Y") {
                            alert("已经存在相同的【项目名称】！");
                            reVal = false;
                        } else if (reply == "N") {
                            reVal = true;
                        }
                    }
                });
            }
            return reVal;
        }

        //选择执行科室
        function doExectDeptSelect(deptcodeid, deptnameid) {
            var deptcode = $("#excdept").val();
            var url = "<%=path %>/dept/dept_execute_select.htm?hosnum=${pexam.hosnum}&deptcode=" + deptcode + "&random=" + Math.random();
            var reVal = window.showModalDialog(url, "",
                "dialogHeight: 470px; dialogWidth: 352px; dialogHide: yes; help: no; resizable: no; status: no; scroll: no");
            if (reVal) {
                var codeName = reVal.split("#_#");
                $("#" + deptcodeid).val(codeName[0]);
                $("#" + deptnameid).val(codeName[1]);
            }
        }

        //转到新增页面
        function doSelfClear() {
            var url = "itemsComAdd.htm?operationType=add&parentid=" + $("#parentid").val() + "&save_num=" + save_num;
            window.location.replace(url);
        }

        function doClose() {
            if (save_num >= 1) {//保存次数大于1次
                parent.window.mywin.reloadGrid();
            }
            parent.$.unblockUI();
        }

        function init() {
            $("#comname").focus();
        }
    </script>
</head>
<body onload="init()">
<form id="pexam_form" name="pexam_form" onsubmit="return false" style="margin: 0px;">
    <input id="operationType" name="operationType" type="hidden" value="${operationType}"/>
    <input id="parentid" name="parentid" type="hidden" value="${pexam.parentid}"/>
    <input id="comid" name="comid" type="hidden" value="${pexam.comid}"/>

    <input id="addscopes" name="addscopes" type="hidden" value=""/>
    <input id="removescopes" name="removescopes" type="hidden" value=""/>
    <!-- 圆头******开始 -->
    <table width="98%" border="0" cellspacing="0" cellpadding="0"
           style="margin-top: 10px; ">
        <tr>
            <td width="10"><img src="img/new_yuan1.jpg"/></td>
            <td background="img/new_yuan2.jpg"></td>
            <td width="10"><img src="img/new_yuan3.jpg"/></td>
        </tr>
        <tr>
            <td background="img/new_yuan4.jpg">
            </td>
            <td>
                <!-- 圆头******结束 -->
                <table id="patient_info_tb" width="750" border="0"
                       cellspacing="0" cellpadding="0" style="display: block; border-collapse: collapse">

                    <tr>
                        <td height="28" class="tit">
                            项目名称：
                        </td>
                        <td class="val" colspan="3">
                            <input id="comname" name="comname" type="text" maxlength="50"
                                   class="txt7 text_field_required" value="${pexam.comname}"/>
                            <input type="hidden" id="old_comname" name="old_comname" value="${pexam.comname}"/>
                        </td>
                        <td class="tit">
                            项目类别：
                        </td>
                        <td class="val mustwrt" style="position: relative; right: 4px; top: 3px;">
                            <select id="comclass" name="comclass"
                                    class="txt9 text_field_required" style="width: 143px;">
                                <c:forEach items="${classList}" var="tempone">
                                    <option value="${tempone.contents}"
                                            <c:if test="${pexam.comclass==null?tempone.isdefault == 'Y':tempone.contents==pexam.comclass}">selected="selected"</c:if>>
                                            ${tempone.contents}
                                    </option>
                                </c:forEach>
                            </select>
                            <script>
                                var comclass_combo = dhtmlXComboFromSelect("comclass");
                                comclass_combo.readonly(true);
                                comclass_combo.attachEvent("onChange", function () {
                                    var type = comclass_combo.getSelectedValue();
                                    if (type == '检验' || type == '外送') {
                                        $('#cost').removeAttr('readonly');
                                    } else {
                                        $('#cost').attr('readonly', 'readonly');
                                    }
                                });
                            </script>
                        </td>
                    </tr>

                    <tr>
                        <td width="90" class="tit">
                            末级节点：
                        </td>
                        <td width="160" class="val">
                            <div class="radioDiv">
                                <input id="isleafRad1" type="radio" name="isuse" value="Y" onclick="this.blur();"
                                       onchange="doChangeLeaf();"
                                       <c:if test="${pexam.isuse == null || pexam.isuse=='Y' }">checked="checked"</c:if> />是
                                <input id="isleafRad2" type="radio" name="isuse" value="N" onclick="this.blur();"
                                       onchange="doChangeLeaf();"
                                       <c:if test="${pexam.isuse=='N' }">checked="checked"</c:if> />否
                                <input id="isleaf" type="hidden" name="isleaf"
                                       value='${pexam.isuse==null?"Y":pexam.isuse }'/>
                            </div>
                        </td>
                        <td width="90" class="tit">
                            适用性别：
                        </td>
                        <td width="160" class="val mustwrt" style="position: relative; right: 4px; top: 3px;">
                            <select id="forsex" name="forsex" class="txt9 text_field_required " style="width: 142px;">
                                <option value=""></option>
                                <c:forEach items="${sexList}" var="tempone">
                                    <option value="${tempone.contents}"
                                            <c:if test="${pexam.forsex==null?tempone.isdefault == 'Y':tempone.contents==pexam.forsex}">selected="selected"</c:if>>
                                            ${tempone.contents}
                                    </option>
                                </c:forEach>
                            </select>
                            <script>
                                var z = dhtmlXComboFromSelect("forsex");
                                z.readonly(true);
                            </script>
                        </td>
                        <td height="28" width="90" class="tit">
                            执行科室：
                        </td>
                        <td class="val" width="160">
                            <input id="excdept" name="excdept" type="hidden" value="${pexam.excdept}"/>
                            <input id="excdeptname" name="excdeptname" type="text" readonly="readonly"
                                   class="show_select_input1 text_field_required" value="${pexam.excdeptname}"/>
                            <div class="show_select_tree" onclick="doExectDeptSelect('excdept','excdeptname');"
                                 title="选择一个执行科室"></div>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            费用：
                        </td>
                        <td class="val">
                            <input id="cost" name="cost" type="text" maxlength="15" onkeyup="inputDouble(this)"
                                   <c:if test="${pexam.comclass!='检验' && pexam.comclass!='外送' && pexam.comclass!=null}">readonly="readonly"</c:if>
                                   class="txt4 text_field_required" value="${pexam.cost}"/>
                        </td>
                        <td width="90" class="tit">
                            同意书：
                        </td>
                        <td width="160" class="val">
                            <input id="bookname" name="bookname" type="text" maxlength="20"
                                   class="txt4 text_field" value="${pexam.bookname}"/>
                        </td>
                        <!--
                        <td width="90" class="tit">
                            对应节点：
                        </td>
                        <td width="160" class="val" >
                            <input id="hisnode" name="hisnode" type="text" maxlength="20"
                                class="txt4 text_field" value=""/>
                        </td>
                         -->
                        <td width="90" class="tit">
                            显示序号：
                        </td>
                        <td width="160" class="val" colspan="1">
                            <input id="sn" name="sn" type="text" maxlength="20"
                                   class="txt4 text_field" value="${pexam.sn}"/>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            发票科目：
                        </td>
                        <td width="160" class="val mustwrt" style="position: relative; right: 4px; top: 3px;">
                            <select id="fpkm" name="fpkm" class="txt9 text_field_required "
                                    style="width: 142px;"></select>
                            <script>
                                var fpkm = dhtmlXComboFromSelect("fpkm");
                                fpkm.readonly(true);
                            </script>
                        </td>
                        <td width="90" class="tit">
                            核算科目：
                        </td>
                        <td width="160" class="val mustwrt" style="position: relative; right: 4px; top: 3px;">
                            <select id="hskm" name="hskm" class="txt9 text_field_required "
                                    style="width: 142px;"></select>
                            <script>
                                var hskm = dhtmlXComboFromSelect("hskm");
                                hskm.readonly(true);
                            </script>
                        </td>
                        <td width="90" class="tit">
                            公共项目：
                        </td>
                        <td width="160" class="val mustwrt" style="position: relative; right: 4px; top: 3px;">
                            <select id="ggxm" name="ggxm" class="txt9 text_field_required "
                                    style="width: 142px;"></select>
                            <script>
                                var ggxm = dhtmlXComboFromSelect("ggxm");
                                ggxm.readonly(true);
                                ggxm.addOption('Y', '是');
                                ggxm.addOption('N', '否');
                            </script>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            体检项目：
                        </td>
                        <td width="160" class="val mustwrt" style="position: relative; right: 4px; top: 3px;">
                            <select id="tjxm" name="tjxm" class="txt9 text_field_required "
                                    style="width: 142px;"></select>
                            <script>
                                var tjxm = dhtmlXComboFromSelect("tjxm");
                                tjxm.readonly(true);
                                tjxm.addOption('Y', '是');
                                tjxm.addOption('N', '否');
                                tjxm.selectOption(0, true, true);
                            </script>
                        </td>
                        <td width="90" class="tit">
                            检验仪器：
                        </td>
                        <td width="160" class="val mustwrt" style="position: relative; right: 4px; top: 3px;">
                            <select style="width:142px" id="sample" class="txt9 text_field_required ">
                                <option value="${pexam.jyyq}">${pexam.jyyq}</option>
                            </select>
                            <input id="sample_t" name="jyyq" type="hidden" value="${pexam.jyyq}"/>
                            <script>
                                var sample_combo;      //检验样本
                                sample_combo = new dhtmlXCombo("sample", "sample", 102);
                                sample_combo.clearAll();
                                sample_combo.attachEvent("onChange", function () {
                                    $("#sample_t").val(sample_combo.getComboText());
                                });
                                fillCombo(sample_combo, "sample", false);
                            </script>
                        </td>
                        <td width="90" class="tit">
                            血管颜色：
                        </td>
                        <td width="160" class="val" style="position: relative; right: 4px; top: 3px;">
                            <select id="xgys" name="xgys" class="txt9 text_field_required " style="width: 142px;">
                            </select>
                            <script>
                                var ys = dhtmlXComboFromSelect("xgys");
                            </script>
                        </td>
                    </tr>


                    <tr>
                        <td height="28" class="tit">
                            打印次数：
                        </td>
                        <td class="val">
                            <input id="printnum" name="printnum" type="text" maxlength="20"
                                   class="txt4 text_field" value="${pexam.printnum }"/>
                        </td>
                        <td width="90" class="tit">
                            温馨提示：
                        </td>
                        <td width="160" class="val">
                            <input id="wxts" name="wxts" type="text" maxlength="20"
                                   class="txt4 text_field" value="${pexam.wxts}"/>
                        </td>
                        <td width="90" class="tit">
                            是否外送：
                        </td>
                        <td width="160" class="val" colspan="1" style="position: relative; right: 4px; top: 3px;">
                            <select id="sfws" name="sfws" class="txt9  text_field" style="width: 142px;"></select>
                            <script>
                                var sfws = dhtmlXComboFromSelect("sfws");
                                sfws.readonly(true);
                                sfws.addOption('否', '否');
                                sfws.addOption('是', '是');
                                sfws.selectOption(0, true, true);
                            </script>
                        </td>
                    </tr>
                    <tr>
                        <td height="28" class="tit">
                            项目说明：
                        </td>
                        <td class="val">
                            <input id="descriptions" name="descriptions" type="text" maxlength="100"
                                   class="txt4 text_field" value="${pexam.descriptions}"/>
                        </td>
                        <td width="90" class="tit">
                            所需时间：
                        </td>
                        <td width="160" class="val">
                            <input id="needtime" name="needtime" type="text" maxlength="20"
                                   class="txt4 text_field" value="${pexam.needtime}"/>
                        </td>
                        <td width="90" class="tit">
                            项目简称：
                        </td>
                        <td width="160" class="val" style="position: relative;">
                            <input id="xmjc" name="xmjc" type="text" maxlength="20"
                                   class="txt4 text_field" value="${pexam.xmjc}"/>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            餐后餐前：
                        </td>
                        <td class="val">
                            <select id="cqch" name="cqch"
                                    style="width: 134px;position: relative; right: 4px; top: 3px;"></select>
                            <script>
                                var cqch = dhtmlXComboFromSelect("cqch");
                                cqch.readonly(true);
                                cqch.addOption('餐前', '餐前');
                                cqch.addOption('餐后', '餐后');
                                cqch.selectOption(0, true, true);
                            </script>
                        </td>
                        <td width="90" class="tit">
                            检查类型：
                        </td>
                        <td width="160" class="val">
                            <select style="width:142px; position: relative; right: 4px; top: 3px; " name="sheettype"
                                    id="sheettype">
                                <option value="${pexam.sheettype}">${pexam.sheettype}</option>
                            </select>
                        </td>
                        <td width="90" class="tit">
                            备注：
                        </td>
                        <td width="160" class="val" style="position: relative;">
                            <input id="zl2" name="zl2" type="text" maxlength="20"
                                   class="txt4 text_field" value="${pexam.memo}"/>
                        </td>

                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            合并名称：
                        </td>
                        <td class="val">
                            <input id="hbmc" name="hbmc" type="text" maxlength="100"
                                   class="txt4 text_field" value="${pexam.hbmc}"/>
                        </td>
                        <td width="90" class="tit">
                            报告显示：
                        </td>
                        <td width="160" class="val">
                            <select id="bgxs" name="bgxs" class="txt9  text_field" style="width: 142px;"></select>
                            <script>
                                var bgxs = dhtmlXComboFromSelect("bgxs");
                                bgxs.readonly(true);
                                bgxs.addOption('是', '是');
                                bgxs.addOption('否', '否');
                                bgxs.selectOption(0, true, true);
                            </script>
                        </td>
                        <td width="90" class="tit">
                            暂无：
                        </td>
                        <td width="160" class="val" style="position: relative;">
                            &nbsp;
                        </td>
                    </tr>

                    <tr>
                        <td class="tit">
                            备注：
                        </td>
                        <td colspan="5" class="val">
									<textarea id="comments" name="comments" class="txt6 text_field"
                                              style="width: 640px; margin-top: 5px;">${pexam.comments }</textarea>
                        </td>
                    </tr>
                    <tr>
                        <td height="28" class="tit">
                            体检项目：
                        </td>
                        <td colspan="5" class="val">
                            <div id="tree_div" style="OVERFLOW-y: auto; overflow-x: hidden;width: 642px; height: 230px;
										 	margin: 8px 0px 0px 4px; border: 1px solid #99b4bf; ">
                                <ul id="menuTree" class="tree"></ul>
                            </div>
                        </td>
                    </tr>
                </table>
                <!-- 圆尾******开始 -->
            </td>
            <td background="img/new_yuan5.jpg">
                &nbsp;
            </td>
        </tr>
        <tr>
            <td><img src="img/new_yuan6.jpg"/></td>
            <td background="img/new_yuan7.jpg"></td>
            <td><img src="img/new_yuan8.jpg"/></td>
        </tr>
    </table>
    <!-- 圆尾******结束 -->

    <div style="margin-top: 18px;">
        <center id="card_manager_btn" style="display: block;">
            <c:choose>
                <c:when test="${operationType == 'add'}">
                    <button id="selfAddButton" type="button" class="btn"
                            onclick="doSelfClear()" style="display: none;">
                        新增
                        <font color="red">(F1)</font>
                    </button>
                </c:when>
                <c:when test="${operationType == 'modify'}">
                    <button id="selfAddButton" type="button" class="btn"
                            onclick="doSelfClear()" style="display: inline;">
                        新增
                        <font color="red">(F1)</font>
                    </button>
                </c:when>
            </c:choose>
            <button type="button" class="btn" onclick="doSave()">
                保存
                <font color="red">(F2)</font>
            </button>
            <button type="button" class="btn" onclick="doClose()">
                关闭
                <font color="red">(Esc)</font>
            </button>
        </center>
    </div>
</form>
</body>
</html>