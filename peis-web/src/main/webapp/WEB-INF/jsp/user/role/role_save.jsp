<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="../../include.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <base href="<%=basePath%>"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>角色</title>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" href="My97DatePicker/skin/WdatePicker.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeStyle.css" type="text/css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeIcons.css" type="text/css"/>
    <style type="text/css">
        .text_field {
            margin-left: 4px
        }

        .text_field_required {
            margin-left: 4px
        }

        #patient_info_tb .tit {
            text-align: right;
        }

        #patient_info_tb .val {
            text-align: left;
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
    </style>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.lrTool.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.21.custom.min.js"></script>
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
    <link rel="stylesheet" type="text/css" href="css/dictionary.css"/>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/dept/verify.js"></script>
    <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
    <script type="text/javascript">
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
            //checkStyle: "radio",//"checkbox"
            showLine: true,
            expandSpeed: "fast"//展开速度
            //callback:{
            // click: zTreeOnClick //点击事件
            //}
        };

        var zTree;
        var treeNodes;
        $(function () {
            $.ajax({
                async: false,   //是否异步
                cache: false,   //是否使用缓存
                type: 'get',   //请求方式,post
                dataType: "json",   //数据传输格式
                //data: $("#archive_form").serialize() 表单提交数据
                //data:"method=loadAllColumnEname&code="+str  url传数据
                url: "user/role/menu_tree.htm?checkedScope=${checkedScope}",   //请求链接
                error: function () {
                    alert('fail');
                },
                success: function (data) {
                    treeNodes = data;
                }
            });

            zTree = $("#menuTree").zTree(setting, treeNodes);   //前台树的位置
        });

        var save_num = ${save_num };
        //验证重复
        function doCheckRepeat() {
            var code = $("#code").val();
            var old_code = $("#old_code").val();
            var name = $("#name").val();
            var old_name = $("#old_name").val();
            var reVal = true;
            if (code == old_code) {
                code = "";
            }
            if (name == old_name) {
                name = "";
            }
            if (code != "" || name != "") {
                $.ajax({
                    cache: false,   //是否使用缓存
                    url: "user/role/role_save_check.htm",
                    async: false,   //是否异步，false为同步
                    type: "post",
                    data: "code=" + code + "&name=" + name,
                    error: function () {
                        alert("ajax请求失败");
                    },
                    success: function (reply) {
                        if (reply == "double") {
                            alert("已经存在相同的【代码】和【名称】！");
                            reVal = false;
                        } else if (reply == "code") {
                            alert("已经存在相同的【代码】！");
                            reVal = false;
                        } else if (reply == "name") {
                            alert("已经存在相同的【名称】！");
                            reVal = false;
                        } else if (reply == "N") {
                            reVal = true;
                        }
                    }
                });
            }
            return reVal;
        }

        //新增、保存
        function doSave() {
            //必填验证
            if (checkNull("code", "代码")) {
                return;
            }
            if (checkNull("name", "名称")) {
                return;
            }
            if (checkNull("index_no", "排序号")) {
                return;
            }
            if (checkNullSelect("lvl", "级别")) {
                return;
            }
            //长度验证
            if (countLen($("#code").val()) > 50) {
                alert("【代码】输入太长！");
                return;
            }
            if (countLen($("#name").val()) > 50) {
                alert("【名称】输入太长！");
                return;
            }
            //其他验证
            if (checkChar($("#code").val(), "代码")) {
                return;
            }
            if (checkNum($("#index_no").val(), "排序号")) {
                return;
            }
            if (doCheckRepeat()) {
                showMsg("数据保存中...");
                doSetChangeVal();
                $.ajax({
                    cache: false,   //是否使用缓存
                    url: "user/role/role_saved.htm",
                    async: false,   //是否异步，false为同步
                    type: "post",
                    data: $('#role_form').serialize(),
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
                            $("#id").val(reply);//更新自动生成的主键ID
                            $("#old_code").val($("#code").val());//更新代码
                            $("#old_name").val($("#name").val());//更新名称
                            $("#selfAddButton").css("display", "inline");//显示新增按钮
                            save_num++;//保存次数
                            doRefreshCheckOld();//刷新纪录的旧的选中的值

                            parent.reloadGrid()
                        }
                    }
                });
            }
        }

        //赋值变更了的选择树
        function doSetChangeVal() {
            var changeNodes = zTree.getChangeCheckedNodes();
            var len = changeNodes.length;
            var addScope = "";
            var removeScope = "";
            if (len >= 1) {
                for (var i = 0; i < len; i++) {
                    if (changeNodes[i].checked) {
                        addScope = addScope + changeNodes[i].id + ";";
                    } else {
                        removeScope = removeScope + changeNodes[i].id + ";";

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

        //关闭层
        var parentCloseStr = parent.doClose;//保存父类doClose方法
        parent.doClose = function () {
            parent.doClose = eval(parentCloseStr);//还原父类doClose方法
            if (save_num >= 1) {//保存次数大于1次
                parent.window.mywin.reloadGrid();
            }
            parent.$.unblockUI();
        }

        //将select选中记录的text赋值给id
        function doPutSelectVal(id, obj) {
            var obj1 = $("#" + id);
            obj1.val(obj.options[obj.selectedIndex].text);
        }

        //转到新增页面
        function doSelfClear() {
            var url = "role_save.htm?operationType=add&save_num=" + save_num;
            window.location.replace(url);
        }

        var F1_KEYCODE = 112;
        var F2_KEYCODE = 113;
        var F5_KEYCODE = 116;
        var F6_KEYCODE = 117;
        var F7_KEYCODE = 118;
        var F8_KEYCODE = 119;
        var F9_KEYCODE = 120;
        var F10_KEYCODE = 121;
        var ESC_KEYCODE = 27;

        $(document).bind("keydown", function (event) {
            if (event.keyCode == F1_KEYCODE) {
                if ($("#operationType").val() == "modify") {
                    event.preventDefault();
                    event.keyCode = 0;
                    event.returnValue = false;
                    doSelfClear();
                }
            } else if (event.keyCode == F2_KEYCODE) {
                event.preventDefault();
                event.keyCode = 0;
                event.returnValue = false;
                doSave();
            } else if (event.keyCode == ESC_KEYCODE) {
                event.preventDefault();
                event.keyCode = 0;
                event.returnValue = false;
                parent.doClose();
            }
        });


    </script>
</head>

<body>
<form id="role_form" name="role_form" onsubmit="return false"
      style="margin: 0px;">
    <input id="operationType" name="operationType" type="hidden" value="${operationType}"/>
    <input id="id" name="id" type="hidden" value="${role.id}"/>
    <input id="addscopes" name="addscopes" type="hidden" value=""/>
    <input id="removescopes" name="removescopes" type="hidden" value=""/>

    <!-- 圆头******开始 -->
    <table width="550" border="0" cellspacing="0" cellpadding="0"
           style="margin-top: 10px;">
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
                <table id="patient_info_tb" width="530" border="0" cellspacing="0"
                       cellpadding="0" style="display: block;">
                    <tr>
                        <td width="125" height="28" class="tit">
                            代码：
                        </td>
                        <td width="150" class="val">
                            <input id="code" name="code" type="text" maxlength="50" onkeyup="inputChar(this)"
                                   class="txt4 text_field_required" value="${role.code}"/>
                            <input id="old_code" name="old_code" type="hidden" value="${role.code}"/>
                        </td>
                        <td width="95" class="tit">
                            名称：
                        </td>
                        <td width="150" class="val">
                            <input id="name" name="name" type="text" maxlength="50"
                                   class="txt4 text_field_required" value="${role.name}"/>
                            <input id="old_name" name="old_name" type="hidden" value="${role.name}"/>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            排序号：
                        </td>
                        <td class="val">
                            <input id="index_no" name="index_no" type="text" maxlength="2" onkeyup="inputNum(this)"
                                   class="txt4 text_field_required" value="${role.index_no}"/>
                        </td>
                        <td class="tit">
                            级别：
                        </td>
                        <td class="val" style="position: relative;right: 14px; top: 3px;">
                            <select id="lvl" name="lvl" class="txt9 text_field_required" style="width: 142px;">
                                <c:forEach items="${roleLvlList}" var="roleLvl">
                                    <option value="${roleLvl.nevalue}"
                                            <c:if test="${role.lvl!=null && role.lvl==roleLvl.nevalue}">selected="selected"</c:if>>
                                            ${roleLvl.contents}
                                    </option>
                                </c:forEach>
                                <!--										<option value="1"-->
                                <!--											<c:if test="${role.lvl == '1'}">selected="selected"</c:if>>系统级-->
                                <!--										</option>-->
                                <!--										<option value="2"-->
                                <!--											<c:if test="${role.lvl == '2'}">selected="selected"</c:if>>院区级-->
                                <!--										</option>-->
                                <!--										<option value="3"-->
                                <!--											<c:if test="${role.lvl == '3'}">selected="selected"</c:if>>科室级-->
                                <!--										</option>-->
                            </select>
                            <script>
                                var z = dhtmlXComboFromSelect("lvl");
                            </script>
                        </td>
                    </tr>

                    <tr>
                        <td class="tit" valign="middle">
                            备注：
                        </td>
                        <td colspan="3" class="val">
									<textarea id="remark" name="remark" class="txt6 text_field"
                                              style="width: 398px;margin-top: 3px;">${role.remark }</textarea>
                        </td>
                    </tr>

                    <tr>
                        <td class="tit" valign="middle">
                            菜单：
                        </td>
                        <td colspan="3" class="val">
                            <div id="tree_div" style="OVERFLOW-y: auto; overflow-x: hidden;width: 400px; height: 295px;
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

    <div style="margin-top: 16px; width: 100%; ">
        <center id="card_manager_btn" style="display: block;">
            <%--<c:choose>--%>
                <%--<c:when test="${operationType == 'add'}">--%>
                    <%--<button id="selfAddButton" type="button" class="btn"--%>
                            <%--onclick="doSelfClear()" style="display: none;">--%>
                        <%--新增--%>
                        <%--<font color="red">(F1)</font>--%>
                    <%--</button>--%>
                <%--</c:when>--%>
                <%--<c:when test="${operationType == 'modify'}">--%>
                    <%--<button id="selfAddButton" type="button" class="btn"--%>
                            <%--onclick="doSelfClear()" style="display: inline;">--%>
                        <%--新增--%>
                        <%--<font color="red">(F1)</font>--%>
                    <%--</button>--%>
                <%--</c:when>--%>
            <%--</c:choose>--%>
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
<input type="hidden" id="pos_x"/>
<input type="hidden" id="pos_y"/>
</body>
</html>