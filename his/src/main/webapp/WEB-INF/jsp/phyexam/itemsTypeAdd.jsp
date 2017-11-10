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
    <link rel="stylesheet" href="My97DatePicker/skin/WdatePicker.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeStyle.css" type="text/css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeIcons.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="css/deptbtn.css"/>
    <style type="text/css">
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
    </style>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.lrTool.js"></script>
    <script type="text/javascript" src="js/clc/comboTool.js"></script>
    <script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
    <script type="text/javascript" src='My97DatePicker/WdatePicker.js'></script>

    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
    <script type="text/javascript" src="js/dept/verify.js"></script>
    <script type="text/javascript">
        function doSave() {
            //必填验证
            if (checkNull("typename", "项目名称")) {
                return;
            }
            if (checkNum($("#sn").val(), "显示顺序")) {
                return;
            }
            if (doCheckRepeat()) {
                showMsg("数据保存中...");
                $.ajax({
                    cache: false,
                    url: "phyexam/itemsTypeSave.htm",
                    async: false,
                    type: "post",
                    data: $('#pexam_form').serialize(),
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
                            if ($("#operationType").val() == 'add') {
                                $("#typeid").val(reply);//更新自动生成项目代码
                            }
                            $("#operationType").val("modify");//修改标志为：修改
                            $("#old_typename").val($("#typename").val());//更新项目名称
                            $("#selfAddButton").css("display", "inline");//显示新增按钮
                        }
                    }
                });
            }
        }
        //验证重复
        function doCheckRepeat() {
            var typename = $("#typename").val();
            var old_typename = $("#old_typename").val();
            var reVal = true;
            if (typename != "" && typename != old_typename) {
                $.ajax({
                    cache: false,   //是否使用缓存
                    url: "phyexam/itemsTypeCheckRepeat.htm",
                    async: false,   //是否异步，false为同步
                    type: "post",
                    data: "typename=" + typename,
                    error: function () {
                        alert("ajax请求失败");
                    },
                    success: function (reply) {
                        if (reply == "fail") {
                            alert("服务器内部错误！");
                            reVal = false;
                        } else {
                            if (reply == "Y") {
                                alert("已经存在相同的【项目类型名称】！");
                                reVal = false;
                            } else if (reply == "N") {
                                reVal = true;
                            }
                        }
                    }
                });
            }
            return reVal;
        }

        function init() {
            //comboInit();
            if ("${operationType}=='add'") {
                $("#typename").focus();
            } else {
                $("#typename").select();
            }
        }
        function doClose() {
            parent.window.mywin.loadCount();
            parent.$.unblockUI();
        }

    </script>
</head>

<body onload="init()">
<form id="pexam_form" name="pexam_form" onsubmit="return false" style="margin:0px;">
    <input id="operationType" name="operationType" type="hidden" value="${operationType}"/>
    <input type="hidden" id="typeid" name="typeid" value="${pexam.typeid}"/>
    <!-- 圆头******开始 -->
    <table width="98%" border="0" cellspacing="0" cellpadding="0" style="margin-top: 10px; ">
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
                <table id="patient_info_tb" width="440" border="0" cellspacing="0" cellpadding="0"
                       style="display: block;">
                    <tr>
                        <td width="80" class="tit" height="28">
                            项目名称：
                        </td>
                        <td class="val">
                            <!--  <div id = "indname1" style = "float:left;margin-left:4px" class=" mustwrt .dhx_combo_box"></div> -->
                            <input id="typename" name="typename" type="text" class="text_field_required"
                                   style="width:180px" value="${pexam.typename}"/>
                            <input type="hidden" id="old_typename" name="old_typename" value="${pexam.typename}"/>
                        </td>
                        <td width="80" class="tit">
                            显示顺序：
                        </td>
                        <td>
                            <input id="sn" name="sn" type="text" class="text_field" value="${pexam.sn}"
                                   style="width:100px"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="tit" height="28">
                            项目说明：
                        </td>
                        <td class="val" colspan="3">
                            <input id="descriptions" name="descriptions" type="text" maxlength="100"
                                   class="text_field" style="width:360px;" value="${pexam.descriptions}"/>
                        </td>
                    </tr>

                    <tr>
                        <td class="tit">
                            备注：
                        </td>
                        <td colspan="3" class="val">
									<textarea id="comments" name="comments" class="text_field"
                                              style=" margin-top:5px;width:360px;height:40px">${pexam.comments}</textarea>
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

    <div style="margin-top:8px;">
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
                    <button id="selfAddButton" type="button" class="btn" onclick="doSelfClear()"
                            style="display: inline;">
                        新增
                        <font color="red">(F1)</font>
                    </button>
                </c:when>
            </c:choose>
            <button type="button" class="btn" onclick="doSave()">
                保存
                <font color="red">(F2)</font>
            </button>
            <button type="button" class="btn" onclick="parent.doClose()">
                关闭
                <font color="red">(Esc)</font>
            </button>
        </center>
    </div>
</form>
</body>
</html>