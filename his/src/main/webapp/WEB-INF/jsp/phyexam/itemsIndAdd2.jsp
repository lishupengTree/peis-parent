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
    <title>体检项目指标</title>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="css/deptbtn.css"/>
    <link rel="stylesheet" type="text/css" href="css/dictionary.css"/>
    <style type="text/css">
        .dhx_combo_list {
            border: 1px solid #BAC2CD;
            height: 160px;
            font-family: 微软雅黑;
            font-size: 12px;
            scrollbar-face-color: #E3EBF8;
            scrollbar-shadow-color: #c6d8f0;
            scrollbar-highlight-color: #FFFFFF;
            scrollbar-3dlight-color: #E3EBF8;
            scrollbar-darkshadow-color: #d8e4f3;
            scrollbar-track-color: #FFFFFF;
            scrollbar-arrow-color: #9bb8de;
        }

        .dhx_combo_list div {
            padding: 0px;
            height: 20px;
        }

        .dhx_combo_box {
            height: 20px;
            border: 1px solid #93AFBA;
        }

        .dhx_combo_box input {
            height: 19px;
            line-height: 20px;
        }

        .text_field {
            margin-left: 4px
        }

        .text_field_required {
            margin-left: 4px
        }

        .mustwrt .dhx_combo_box {
            border-color: red;
            border: 1px solid red;
        }

        .textmst {
            border-color: red;
            border: 1px solid red;
        }
    </style>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/dept/verify.js"></script>

    <!-- combo -->
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>
    <script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
    <script type="text/javascript" src="js/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="js/clc/comboTool.js"></script>

    <!-- grid -->
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid_excell_combo.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
    <script type="text/javascript" src="js/gl/util.js"></script>

    <script type="text/javascript">

        var save_num = ${save_num};
        var indnames_arr = [];//存放小项列表
        var unitArr = [];//存放小项列表
        var combo_name;//小项combo
        var operationType = "${operationType}";//新增add 修改查看 modify
        var hosnum = "${hosnum}";
        function init() {
            comboInit();
            initCombo(combo_name);
            //$("#indname1").focus();
            //alert("operationType:"+operationType);
            //alert("${details.parentid}");
            if (operationType == "add") {
                $("#parentid").val("${details.parentid}");
                $("#sn").val("${details.sn}");
                $("#indid").val("${details.indid}");
            } else {
                $("#parentid").val("${details.parentid}");
                $("#sn").val("${details.sn}");
                $("#indid").val("${details.indid}");
                $("#indname").val("${details.indid}");
                $("#pdcode").val("${details.pdcode}");
                $("#pdname").val("${details.pdname}");
                $("#isjy").val("${details.isjy}");
                $("#iszh").val("${details.iszh}");
                combo_name.setComboValue("${details.indid}");
                combo_name.setComboText("${details.indname}");
                combo_isjy.setComboValue("${details.isjy}");
                combo_isjy.setComboText("${details.isjy}");
                combo_iszh.setComboValue("${details.iszh}");
                combo_iszh.setComboText("${details.iszh}");
                //combo_name.readonly(true);
                // alert(hosnum);
                combo_name.disable(true);
            }
        }

        function comboInit() {
            combo_name = new dhtmlXCombo("indname1", "alfa3", 390);
            //combo_name.readonly(true);
            var parentid = $("#parentid").val();
            $.ajax({
                url: "phyexam/getItemsInd2.htm",
                type: "post",
                data: "parentid=" + parentid + "&time=" + (new Date()).valueOf(),
                error: function () {
                    alert("获取数据失败");
                    combo_name.closeAll();
                },
                success: function (reply) {
                    if (reply == "fail") {
                        alert("获取数据失败");
                        combo_name.closeAll();
                    } else {
                        var jsons = eval('(' + reply + ')');
                        for (var i = 0; i < jsons.length; i++) {
                            combo_name.addOption(jsons[i].indid, jsons[i].indname);
                            indnames_arr.push(jsons[i].indname);
                            unitArr.push({
                                indid: jsons[i].indid,
                                indname: jsons[i].indname,
                                inputcpy: jsons[i].inputcpy,
                                inputcwb: jsons[i].inputcwb
                            });
                        }
                    }
                }
            });
            // combo_name.setComboValue("${details.indid}");
            // combo_name.setComboText("${details.indname}");
            // $("#indname").val(combo_name.getComboText());
            // $("#indid").val(combo_name.getActualValue());
        }


        //初始化combo
        function initCombo(combo) {
            //失去焦点验证是否是“回车”或者是“鼠标点选”
            combo.DOMelem_input.onblur = function () {
                if (combo.getComboText() != '') {
                    for (var i = 0; i < indnames_arr.length; i++) {
                        var indname = indnames_arr[i];
                        if (combo.getComboText() == indname) {
                            alert("该小项名称已被使用,请使用其他名称！");
                        }
                    }
                    //combo.setComboText('');
                }
            }

            combo.DOMelem_input.onkeydown = function (ev) {
                var event = ev || window.event;
                var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
                //alert("keyCode:"+keyCode);
                if (keyCode == 13) {
                    if (combo.getSelectedText() != "" && combo.DOMlist.style.display == "block") {
                        //combo.setComboText(combo.optionsArr[combo.getSelectedIndex()].text);
                        window.setTimeout(function () {
                            filterInds();
                            combo.openSelect();
                            combo.DOMelem_input.focus();//获取焦点
                        }, 10);

                    }
                } else if (keyCode == 38) {
                    index = combo.getSelectedIndex();
                    if (index == 0) {
                        index = combo.optionsArr.length - 1;
                        var old = combo.getComboText();
                        combo.selectOption(index, true, true);
                        combo.setComboText(old);
                    } else {
                        index--;
                        var old = combo.getComboText();
                        combo.selectOption(index, true, true);
                        combo.setComboText(old);
                    }
                } else if (keyCode == 40) {
                    index = combo.getSelectedIndex();
                    if (index == combo.optionsArr.length - 1) {
                        index = 0;
                        var old = combo.getComboText();
                        combo.selectOption(index, true, true);
                        combo.setComboText(old);
                    } else {
                        index++;
                        var old = combo.getComboText();
                        combo.selectOption(index, true, true);
                        combo.setComboText(old);
                    }
                } else if (keyCode == 191 || keyCode == 32 || keyCode == 8 || keyCode == 46 || (keyCode >= 48 && keyCode <= 57) || (keyCode >= 65 && keyCode <= 90) || (keyCode >= 96 && keyCode <= 111)) {
                    window.setTimeout(function () {
                        filterInds();//过滤小项项目
                        //filterUnit($("#inTypeName").val());
                    }, 10);
                }
            }


        }
        //---小项过滤  按中文名字-----
        function filterInds() {
            var combo = combo_name;
            var code = combo.getComboText().replace(/ /g, "");
            combo.unSelectOption();
            combo.clearAll();
            combo.openSelect();
            for (var i = 0; i < unitArr.length; i++) {
                /*
                 if(unitArr[i].inputcpy.toLowerCase().indexOf(code.toLowerCase())!=-1){
                 combo.addOption(unitArr[i].indid,unitArr[i].indname);
                 }
                 */
                if (unitArr[i].indname.toLowerCase().indexOf(code.toLowerCase()) != -1) {
                    combo.addOption(unitArr[i].indid, unitArr[i].indname);
                }
            }
            if (combo.optionsArr.length > 0) {
                var old = combo.getComboText();
                combo.selectOption(0, true, true);
                combo.setComboText(old);
            }
        }


        //转到新增页面
        function doSelfClear() {
            var url = "itemsIndAdd2.htm?operationType=add&parentid=" + $("#parentid").val() + "&save_num=" + save_num;
            window.location.replace(url);
        }

        //新增、保存
        function doSave() {
            //必填验证
            if (combo_name.getComboText() == null && combo_name.getComboText() == "") {
                alert("指标名称不能为空！");
                return;
            } else {
                if (operationType == "add") {
                    for (var i = 0; i < indnames_arr.length; i++) {
                        var indname = indnames_arr[i];
                        if (combo_name.getComboText() == indname) {
                            alert("该小项名称已被使用,请使用其他名称！");
                            return;
                        }
                    }
                }
            }

            if (combo_isjy.getComboText() == null && combo_isjy.getComboText() == "") {
                alert("是否检验项目不能为空！");
                return;
            }
            if (combo_iszh.getComboText() == null && combo_iszh.getComboText() == "") {
                alert("是否组合项目不能为空！");
                return;
            }

            if (checkNullSelect("forsex", "适用性别")) {
                return;
            }
            if (checkNullSelect("itemcost", "小项价格")) {
                $('#itemcost').focus();
                return;
            }
            if (isNaN($('#itemcost').val())) {
                alert('小项价格只能输入数字');
                $('#itemcost').focus();
                return;
            }

            if (checkNullSelect("resulttype", "结果类型")) {
                return;
            }
            //长度验证
            if (countLen($("#minpromp").val()) > 100) {
                alert("【低值提示】输入太长！");
                return;
            }
            if (countLen($("#maxpromp").val()) > 100) {
                alert("【高值提示】输入太长！");
                return;
            }
            if (countLen($("#comments").val()) > 200) {
                alert("【备注】输入太长！");
                return;
            }
            //其他验证
            if ($("#minval").val() != "" && checkDouble($("#minval").val(), "参考低值", 2)) {
                return;
            }
            if ($("#maxval").val() != "" && checkDouble($("#maxval").val(), "参考高值", 2)) {
                return;
            }
            $("#indname").val(combo_name.getComboText());
            var operationType = $("#operationType").val();//操作类型
            if (operationType == 'add') {

            } else {
                //alert("22:"+combo_name.getActualValue());
                $("#indid").val(combo_name.getActualValue());
            }
            $("#isjy").val(combo_isjy.getActualValue());
            $("#iszh").val(combo_iszh.getActualValue());
            //alert("isjy:iszh:"+combo_isjy.getActualValue()+";"+combo_iszh.getActualValue());

            showMsg("数据保存中...");
            $.ajax({
                cache: false,   //是否使用缓存
                url: "phyexam/itemsIndSave2.htm",
                async: false,   //是否异步，false为同步
                type: "post",
                data: $('#pexam_details_form').serialize(),
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
                        $("#old_indname").val($("#indname").val());//更新项目名称
                        $("#selfAddButton").css("display", "inline");//显示新增按钮
                        save_num++;
                        //doSaveComRes();
                    }
                }
            });

        }

        /*
         //验证重复
         function doCheckRepeat(){
         var indname = $("#indname").val();
         var old_indname = $("#old_indname").val();
         var reVal = true;
         if(indname != "" && indname != old_indname){
         $.ajax({
         cache:false,//是否使用缓存
         url:"phyexam/itemsIndCheckRepeat.htm",
         async:false, //是否异步，false为同步
         type:"post",
         data:"indname="+indname,
         error:function(){
         alert("ajax请求失败");
         },
         success:function(reply){
         if(reply=="fail"){
         alert("服务器内部错误！");
         reVal = false;
         }else{
         if(reply == "Y"){
         alert("已经存在相同的【指标名称】！");
         reVal = false;
         }else if(reply == "N"){
         reVal = true;
         }
         }
         }
         });
         }
         return reVal;
         }
         */

        //关闭层
        /*
         var parentCloseStr = parent.doClose;//保存父类doClose方法
         parent.doClose = function(){
         parent.doClose = eval(parentCloseStr);//还原父类doClose方法
         if(save_num >= 1){//保存次数大于1次
         window.parent.document.getElementById("main_iframe").contentWindow.loadTree();
         parent.window.mywin.reloadGrid();
         }
         parent.$.unblockUI();
         }
         */

        function doClose() {
            if (save_num >= 1) {//保存次数大于1次
                //window.parent.document.getElementById("main_iframe").contentWindow.loadTree();
                parent.window.mywin.reloadGrid();
            }
            parent.$.unblockUI();
        }

        function openAdd() {
            window.showModalDialog("addckz.htm", window, "dialogHeight: 416px; dialogWidth: 545px; dialogHide: yes; help: no; resizable: no; status: no; scroll: yes");
        }
    </script>
</head>

<body onload="init()">
<form id="pexam_details_form" name="pexam_details_form" onsubmit="return false" style="margin: 0px;">
    <input id="hosnum" name="hosnum" type="hidden" value="${hosnum}"/>
    <input id="operationType" name="operationType" type="hidden" value="${operationType}"/>
    <input id="parentid" name="parentid" type="hidden"/>
    <input id="indid" name="indid" type="hidden"/>
    <input id="indname" name="indname" type="hidden" value=""/>
    <input id="isjy" name="isjy" type="hidden" value=""/>
    <input id="iszh" name="iszh" type="hidden" value=""/>
    <!-- 圆头******开始 -->
    <table width="90%" border="0" cellspacing="0" cellpadding="0"
           style="margin-top: 10px; ">
        <tr>
            <td width="10"><img src="img/new_yuan1.jpg"/></td>
            <td width="100%" background="img/new_yuan2.jpg"><img src="img/new_tp1.jpg"/><span class="font3"
                                                                                              style="position:relative;top:-2px;">体检指标</span>
            </td>
            <td width="10"><img src="img/new_yuan3.jpg"/></td>
        </tr>
        <tr>
            <td background="img/new_yuan4.jpg">
            </td>
            <td>
                <!-- 圆头******结束 -->
                <table id="patient_info_tb" width="730" border="0" cellspacing="0" cellpadding="0"
                       style="display: block; border-collapse: collapse">
                    <tr>
                        <td height="28" class="tit">
                            指标名称：
                        </td>
                        <td class="val" colspan="3">
                            <div id="indname1" style="float:left;margin-left:4px"
                                 class="txt7 text_field_required mustwrt .dhx_combo_box"></div>
                            <!--  <input id="indname" name="indname" type="text" maxlength="50" class="txt7 text_field_required" value="${details.indname}"/>-->
                            <input type="hidden" id="old_indname" name="old_indname" value="${details.indname}"/>

                        </td>
                        <td class="tit">
                            适用性别：
                        </td>
                        <td class="val" style="position: relative; right: 4px; top: 3px;">
                            <select id="forsex" name="forsex" class="txt9 text_field_required" style="width: 142px;">
                                <option value=""></option>
                                <c:forEach items="${sexList}" var="tempone">
                                    <option value="${tempone.contents}"
                                            <c:if test="${details.forsex==null?tempone.isdefault == 'Y':tempone.contents==details.forsex}">selected="selected"</c:if>>
                                            ${tempone.contents}
                                    </option>
                                </c:forEach>
                            </select>
                            <script>
                                var z = dhtmlXComboFromSelect("forsex");
                            </script>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" width="90" class="tit">
                            默&ensp;认&ensp;值：
                        </td>
                        <td width="160" class="val">
                            <input id="defaultv" name="defaultv" type="text"
                                   class="txt4 text_field" value="${details.defaultv}"/>
                        </td>
                        <td width="90" class="tit">
                            结果类型：
                        </td>
                        <td width="160" class="val" style="position: relative; right: 4px; top: 3px;">
                            <select id="resulttype" name="resulttype" class="txt9 text_field_required"
                                    style="width: 142px;">
                                <option value=""></option>
                                <c:forEach items="${resultList}" var="tempone">
                                    <option value="${tempone.contents}"
                                            <c:if test="${details.resulttype==null?tempone.isdefault == 'Y':tempone.contents==details.resulttype}">selected="selected"</c:if>>
                                            ${tempone.contents}
                                    </option>
                                </c:forEach>
                            </select>
                            <script>
                                var z = dhtmlXComboFromSelect("resulttype");
                            </script>
                        </td>
                        <td width="90" class="tit">
                            数值单位：
                        </td>
                        <td width="160" class="val" style="position: relative; right: 4px; top: 3px;">
                            <select id="resultunit" name="resultunit" class="txt9 text_field" style="width: 142px;">
                                <option value=""></option>
                                <c:forEach items="${unitList}" var="tempone">
                                    <option value="${tempone.contents}"
                                            <c:if test="${details.resultunit==null?tempone.isdefault == 'Y':tempone.contents==details.resultunit}">selected="selected"</c:if>>
                                            ${tempone.contents}
                                    </option>
                                </c:forEach>
                            </select>
                            <script>
                                var z = dhtmlXComboFromSelect("resultunit");
                            </script>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            参考下限：
                        </td>
                        <td class="val">
                            <input id="minval" name="minval" type="text" maxlength="15" onkeyup="inputDouble(this)"
                                   class="txt4 text_field" value="${details.minval}"/>
                        </td>
                        <td class="tit">
                            低值提示：
                        </td>
                        <td class="val" colspan="3">
                            <input id="minpromp" name="minpromp" type="text" maxlength="100"
                                   class="txt7 text_field" value="${details.minpromp}"/>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            参考上限：
                        </td>
                        <td class="val">
                            <input id="maxval" name="maxval" type="text" maxlength="15" onkeyup="inputDouble(this)"
                                   class="txt4 text_field" value="${details.maxval}"/>
                        </td>
                        <td class="tit">
                            高值提示：
                        </td>
                        <td class="val" colspan="3">
                            <input id="maxpromp" name="maxpromp" type="text" maxlength="100"
                                   class="txt7 text_field" value="${details.maxpromp}"/>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            小项价格：
                        </td>
                        <td class="val">
                            <input id="itemcost" name="itemcost" type="text" maxlength="15" onkeyup="inputDouble(this)"
                                   class="txt4 text_field text_field_required" value="${details.itemcost}"/>
                        </td>
                        <td class="tit">
                            暂留字段：
                        </td>
                        <td class="val" colspan="3">
                            <input id="zanliu" name="zanliu" type="text" maxlength="100"
                                   class="txt7 text_field" value=""/>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            显示顺序：
                        </td>
                        <td class="val">
                            <input id="sn" name="sn" type="text" maxlength="15" class="txt4 text_field"/>
                        </td>
                        <td height="28" class="tit">
                            特殊项目：
                        </td>
                        <td class="val">
                            <input id="tsxm" name="tsxm" type="text" class="txt4 text_field" value="${details.tsxm}"/>
                        </td>
                        <td class="val" colspan="2">
                            <input id="searchButton" class="btn01 head" type="button"
                                   value="添加参考值" onclick="openAdd();"/>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            上传编码：
                        </td>
                        <td class="val">
                            <input id="pdcode" name="pdcode" type="text" maxlength="15" class="txt4 text_field"/>
                        </td>
                        <td height="28" class="tit">
                            上传项目：
                        </td>
                        <td class="val">
                            <input id="pdname" name="pdname" type="text" maxlength="15" class="txt4 text_field"/>
                        </td>
                        <td colspan="2">
                            <table border="0" cellspacing="0" cellpadding="0"
                                   style="display: block; border-collapse: collapse">
                                <tr>
                                    <td height="28">
                                        检验项目：
                                    </td>
                                    <td>
                                        <select id="isjy2" name="isjy2" class="text_field textmst" style="width: 40px;">
                                            <option value="是">是</option>
                                            <option value="否">否</option>
                                        </select>
                                        <script>
                                            var combo_isjy = dhtmlXComboFromSelect("isjy2");
                                            combo_isjy.selectOption(1, true, true);
                                        </script>
                                    </td>
                                    <td height="28">
                                        组合项目：
                                    </td>
                                    <td>
                                        <select id="iszh2" name="iszh2" class="text_field" style="width: 40px;">
                                            <option value="是">是</option>
                                            <option value="否">否</option>
                                        </select>
                                        <script>
                                            var combo_iszh = dhtmlXComboFromSelect("iszh2");
                                            combo_iszh.selectOption(1, true, true);
                                        </script>
                                    </td>
                                </tr>
                            </table>
                        </td>


                    </tr>


                    <tr>
                        <td class="tit">
                            备注：
                        </td>
                        <td colspan="5" class="val">
                            <textarea id="comments" name="comments" class="txt6 text_field"
                                      style="width: 640px; margin-top: 5px;">${details.comments}</textarea>
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
    <div style="margin-top: 18px;">
        <center id="card_manager_btn" style="display: block;">
            <c:choose>
                <c:when test="${operationType == 'add'}">
                    <c:if test="${hosnum=='0000'}">
                        <button id="selfAddButton" type="button" class="btn"
                                onclick="doSelfClear()" style="display: none;">
                            新增
                            <font color="red">(F1)</font>
                        </button>
                    </c:if>
                </c:when>
                <c:when test="${operationType == 'modify'}">
                    <c:if test="${hosnum=='0000'}">
                        <button id="selfAddButton" type="button" class="btn"
                                onclick="doSelfClear()" style="display: inline;">
                            新增
                            <font color="red">(F1)</font>
                        </button>
                    </c:if>
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