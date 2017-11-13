<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="../include.jsp" %>
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
    <title>用户</title>
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

        #instype_required .dhx_combo_box {
            border: 1px solid red
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
    <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
    <script type="text/javascript" src="js/dept/verify.js"></script>
    <script type="text/javascript" src="js/gl/util.js"></script>
    <script type="text/javascript" src="jsfile.htm?method=dict&nekey=6000"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            if (${deptAllSize} == 0){
                //没有人事关系，即对应科室被删除
                alert("选择科室（病区）已经不存在，不能操作用户，请刷新树！");
                //window.parent.document.getElementById("main_iframe").contentWindow.loadTree();
                parent.doClose();
            }
            //加载人员级别combo
            for (var i = 0; i < dicts6000.length; i++) {
                userlevel.addOption(dicts6000[i].nevalue, dicts6000[i].contents);
            }
            userlevel.selectOption(userlevel.getIndexByValue('${user.userlevel}'), true, true);  //选中 一个option
        });

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
                url: "user/role_tree.htm?checkedScope=${checkedScope}",   //请求链接
                error: function () {
                    alert('fail');
                },
                success: function (data) {
                    treeNodes = data;
                    zTree = $("#menuTree").zTree(setting, treeNodes);   //前台树的位置
                }
            });


        });

        //赋值变更了的选择树
        function doSetChangeVal() {
            var changeNodes = zTree.getChangeCheckedNodes();
            var len = changeNodes.length;
            var addScope = "";
            var removeScope = "";
            if (len >= 1) {
                for (var i = 0; i < len; i++) {
                    if (changeNodes[i].checked) {
                        if (changeNodes[i].isLast && changeNodes[i].isLast == 'Y') {//只添加叶子节点
                            addScope = addScope + changeNodes[i].id + ";";
                        }
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

        var save_num = ${save_num };
        //验证重复
        function doCheckRepeat() {
            var user_key = $("#user_key").val();
            var old_user_key = $("#old_user_key").val();
            var reVal = true;
            if (user_key != "" && user_key != old_user_key) {
                $.ajax({
                    cache: false,   //是否使用缓存
                    url: "user/user_save_check.htm",
                    async: false,   //是否异步，false为同步
                    type: "post",
                    data: "user_key=" + user_key,
                    error: function () {
                        alert("ajax请求失败");
                    },
                    success: function (reply) {
                        if (reply == "Y") {
                            alert("已经存在相同的【用户名】！");
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
            if (checkNull("name", "姓名")) {
                return;
            }
            if (checkNull("user_key", "用户名")) {
                return;
            }
            if (checkNull("password", "口令")) {
                return;
            }
            if (checkNull("index_no", "排序号")) {
                return;
            }
            if (checkNull("job_no", "工号")) {
                return;
            }
            if (combo_person_dept.getComboText() == "") {
                alert("【人事关系】不能为空！");
                return;
            }
            if (userlevel.getComboText() == "") {
                //alert("【人员级别】不能为空！");
                //return;
            }
            if (checkNullSelect("input_custom", "输入习惯")) {
                return;
            }
            //长度验证
            if (countLen($("#name").val()) > 40) {
                alert("【姓名】输入太长！");
                return;
            }
            if (countLen($("#user_key").val()) > 40) {
                alert("【用户名】输入太长！");
                return;
            }
            /*if(countLen($("#remark").val()) > 1000){
             alert("【备注】输入太长！");
             return;
             }*/
            //其他验证
            if (checkNum($("#index_no").val(), "排序号")) {
                return;
            }
            if (checkNum($("#short_mobile").val(), "短号")) {
                return;
            }
            if (checkChar($("#job_no").val(), "工号")) {
                return;
            }
            if (document.getElementById("idcard").value != "" && !checkSid(document.getElementById("idcard"))) {
                return;
            }
            if ($("#phone").val() != "" && !checkPhone($("#phone").val())) {
                alert("输入【电话】不合法!");
                return;
            }
            if ($("#mobile").val() != "" && !checkMobile($("#mobile").val())) {
                alert("输入【手机】不合法!");
                return;
            }
            if ($("#email").val() != "" && !checkEmail($("#email").val())) {
                alert("输入【邮件地址】不合法！");
                return;
            }
            if (doCheckRepeat()) {
                showMsg("数据保存中...");
                doSetChangeVal();
                $.ajax({
                    cache: false,   //是否使用缓存
                    url: "user/user_saved.htm",
                    async: false,   //是否异步，false为同步
                    type: "post",
                    data: $('#user_form').serialize() + "&userlevel=" + userlevel.getActualValue() + "&job_no=" + $('#job_no').val(),
                    error: function () {
                        alert("ajax请求失败");
                        closeMsg();
                    },
                    success: function (reply) {
                        if (reply == "fail") {
                            closeMsg();
                            alert("保存失败");
                        } else if (reply == "noexists") {
                            closeMsg();
                            alert("选择科室（病区）已经不存在，不能保存用户，请刷新树！");
                            parent.doClose();
                            //window.parent.document.getElementById("main_iframe").contentWindow.loadTree();
                        } else if (reply.indexOf("该工号已被使用") > -1) {
                            closeMsg();
                            alert(reply);
                        } else {
                            closeMsg();
                            alert("保存成功");
                            $("#operationType").val("modify");//修改标志为：修改
                            $("#id").val(reply);//更新自动生成的主键ID
                            $("#old_user_key").val($("#user_key").val());//更新科室名称
                            $("#selfAddButton").css("display", "inline");//显示新增按钮
                            save_num++;//保存次数
                            doRefreshCheckOld();
                        }
                    }
                });
            }
        }

        //关闭层
        var parentCloseStr = parent.doClose;//保存父类doClose方法
        parent.doClose = function () {
            parent.doClose = eval(parentCloseStr);//还原父类doClose方法
            if (save_num >= 1) {//保存次数大于1次
                //window.parent.document.getElementById("main_iframe").contentWindow.doChangeSelectNode("${defaultDept}");
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
            var url = "user_save.htm?operationType=add&defaultHos=${user.hosnum}&defaultNode=${user.nodecode}"
                + "&defaultDept=${defaultDept}&defaultType=${defaultType}&save_num=" + save_num;
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
                event.preventDefault();
                event.keyCode = 0;
                event.returnValue = false;
                doSelfClear();
            } else if (event.keyCode == F2_KEYCODE) {
                event.preventDefault();
                event.keyCode = 0;
                event.returnValue = false;
                doDeptAdded();
            } else if (event.keyCode == ESC_KEYCODE) {
                event.preventDefault();
                event.keyCode = 0;
                event.returnValue = false;
                doDeptClose();
            }
        });


    </script>
</head>

<body>
<form id="user_form" name="user_form" onsubmit="return false" style="margin: 0px;">
    <input id="operationType" name="operationType" type="hidden" value="${operationType}"/>
    <input id="hosnum" name="hosnum" type="hidden" value="${user.hosnum}"/>
    <input id="nodecode" name="nodecode" type="hidden" value="${user.nodecode}"/>
    <input id="defaultDept" name="defaultDept" type="hidden" value="${defaultDept}"/>
    <input id="defaultType" name="defaultType" type="hidden" value="${defaultType}"/>
    <input id="id" name="id" type="hidden" value="${user.id}"/>

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
                <table id="patient_info_tb" width="730" height="" border="0"
                       cellspacing="0" cellpadding="0" style="display: block;">
                    <tr>
                        <td width="100" height="28" class="tit">
                            姓名：
                        </td>
                        <td width="150" class="val">
                            <input id="name" name="name" type="text" maxlength="40"
                                   class="txt4 text_field_required" value="${user.name}"/>
                        </td>
                        <td width="95" class="tit">
                            用户名：
                        </td>
                        <td width="150" class="val">
                            <input id="user_key" name="user_key" type="text" maxlength="40"
                                   class="txt4 text_field_required" value="${user.user_key}"/>
                            <input type="hidden" id="old_user_key" name="old_user_key" value="${user.user_key}"/>
                        </td>
                        <td width="100" class="tit">
                            口令：
                        </td>
                        <td class="val">
                            <input id="password" name="password" type="password" maxlength="40"
                                   class="txt4 text_field_required"
                                   value="${user.password == null?'123456':user.password }"/>
                        </td>
                    </tr>

                    <tr>
                        <td width="100" height="28" class="tit">
                            身份证：
                        </td>
                        <td class="val">
                            <input id="idcard" name="idcard" type="text" maxlength="18"
                                   onblur="setBirth('temp_birthdate',this)" class="txt4 text_field"
                                   value="${user.idcard}"/>
                        </td>
                        <td width="100" class="tit">
                            工号：
                        </td>
                        <td class="val">
                            <input id="job_no" name="job_no" type="text" maxlength="20" onkeyup="inputChar(this)"
                                   class="txt4 text_field_required" value="${user.job_no}" disabled="disabled"/>
                        </td>
                        <td width="100" class="tit">
                            性别：
                        </td>
                        <td class="val" style="padding-left: 0px; position: relative; left: 2px; top: 3px;">
                            <select id="sex" name="sex"
                                    class="txt9 text_field" style="width: 142px;">
                                <option value=""></option>
                                <c:forEach items="${sexList}" var="sex">
                                    <option value="${sex.contents}"
                                            <c:if test="${user.sex==null?sex.isdefault == 'Y':sex.contents==user.sex}">selected="selected"</c:if>>${sex.contents}</option>
                                </c:forEach>
                            </select>
                            <script>
                                var z1 = dhtmlXComboFromSelect("sex");
                            </script>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            出生日期：
                        </td>
                        <td class="val">
                            <input id="temp_birthdate" name="temp_birthdate" class="Wdate txt4 text_field" type="text"
                                   onclick="WdatePicker()"
                                   value="<fmt:formatDate value="${user.birthdate}" pattern="yyyy-MM-dd"/> "/>
                        </td>
                        <td class="tit">
                            职称：
                        </td>
                        <td class="val" style="position: relative; right: 1px; top: 3px;">
                            <select id="post_code" name="post_code"
                                    class="txt9 text_field" onchange="doPutSelectVal('post', this)"
                                    style="width: 142px;">
                                <option value=""></option>
                                <c:forEach items="${postList}" var="post">
                                    <option value="${post.nevalue}"
                                            <c:if test="${user.post_code==null?post.isdefault == 'Y':post.nevalue==user.post_code}">selected="selected"</c:if>>
                                            ${post.contents}
                                    </option>
                                </c:forEach>
                            </select>
                            <input type="hidden" id="post" name="post" value="${user.post }"/>
                            <script>
                                var combo_post_code = dhtmlXComboFromSelect("post_code");
                            </script>
                        </td>
                        <td class="tit">
                            排序号：
                        </td>
                        <td class="val">
                            <input id="index_no" name="index_no" type="text" maxlength="3"
                                   class="txt4 text_field_required" onkeyup="inputNum(this)"
                                   value="${user.index_no}"/>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            电话：
                        </td>
                        <td class="val">
                            <input id="phone" name="phone" type="text" maxlength="13"
                                   class="txt4 text_field" onkeyup="inputNum1(this)"
                                   value="${user.phone}"/>
                        </td>
                        <td class="tit">
                            手机：
                        </td>
                        <td class="val">
                            <input id="mobile" name="mobile" type="text"
                                   maxlength="11" class="txt4 text_field" onkeyup="inputNum(this)"
                                   value="${user.mobile}"/>
                        </td>
                        <td class="tit">
                            短号：
                        </td>
                        <td class="val">
                            <input id="short_mobile" name="short_mobile" type="text"
                                   maxlength="5" class="txt4 text_field" onkeyup="inputNum(this)"
                                   value="${user.short_mobile}"/>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            邮件地址：
                        </td>
                        <td class="val">
                            <input id="email" name="email" type="text" onkeyup="inputChar1(this)"
                                   maxlength="50" class="txt4 text_field"
                                   value="${user.email}"/>
                        </td>
                        <td class="tit">
                            人事关系：
                        </td>
                        <td class="val" style="position: relative; right: 1px; top: 3px;">
                            <div id="instype_required" style="width: 153px;float: left; margin-left: 1px;">
                                <select id="person_dept" name="person_dept" class="txt9 text_field"
                                        style="width: 142px;">
                                    <option value="" selected="selected"></option>
                                    <c:forEach items="${deptAllList}" var="dept_one">
                                        <option value="${dept_one.deptcode}"
                                                <c:if test="${(operationType == 'add')?(dept_one.deptcode == defaultDept):(user.person_dept != null && dept_one.deptcode==user.person_dept)}">selected="selected"</c:if>>${dept_one.deptname}</option>
                                    </c:forEach>
                                </select>
                                <script>
                                    var combo_person_dept = dhtmlXComboFromSelect("person_dept");
                                    combo_person_dept.attachEvent("onChange", function () {
                                        combo_person_dept.getSelectedValue();
                                    });
                                </script>
                            </div>
                        </td>
                        <td class="tit">
                            输入习惯：
                        </td>
                        <td class="val" style="position: relative; left: 2px; top: 3px;">
                            <select id="input_custom" name="input_custom"
                                    class="txt9 text_field_required" style="width: 142px;">
                                <option value=""></option>
                                <option value="py"
                                        <c:if test="${user.input_custom == 'py' }">selected="selected"</c:if>>拼音
                                </option>
                                <option value="wb"
                                        <c:if test="${user.input_custom == 'wb' }">selected="selected"</c:if>>五笔
                                </option>
                            </select>
                            <script>
                                var z4 = dhtmlXComboFromSelect("input_custom");
                                input_custom = z4.getSelectedText();
                            </script>
                        </td>

                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            智慧医疗：
                        </td>
                        <td class="val">
                            <input id="posid" name="posid" type="text"
                                   maxlength="11" class="txt4 text_field" onkeyup="inputNum(this)"
                                   value="${user.posid}"/>
                        </td>

                        <td height="28" class="tit">
                            使用分诊台：
                        </td>
                        <td class="val">
                            <input id="console_sign" name="console_sign" type="checkbox"
                                   <c:if test="${user.console_sign == 'Y'}">checked="checked"</c:if>/>
                        </td>
                        <td height="28" class="tit">
                            人员级别：
                        </td>
                        <td class="val">
                            <div id="userlevel" style="margin-left: 3px;"></div>
                            <script>
                                var userlevel = new dhtmlXCombo("userlevel", "alfa3", 142);
                                userlevel.readonly(true);
                            </script>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            备注：
                        </td>
                        <td colspan="5" class="val">
									<textarea id="remark" name="remark"
                                              class="txt6 text_field"
                                              style="width: 640px;margin-top: 3px;">${user.remark }</textarea>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            角色：
                        </td>
                        <td colspan="5" class="val">
                            <div id="tree_div" style="OVERFLOW-y: auto; overflow-x: hidden;width: 642px; height: 180px;
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

    <div style="margin-top: 16px;">
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

<script>
    //下拉脚本
    var combo_person_dept;
    combo_person_dept = dhtmlXComboFromSelect("person_dept");
    combo_person_dept.enableFilteringMode(false);
    combo_person_dept.attachEvent("onKeyPressed", function (keyCode) {
        //发起ajax请求
        $.get("dept/dept_all.htm?hosnum=${user.hosnum }&nodecode=${user.nodecode }", function (json) {
            //hp 混拼  wb 五笔 py 拼音  null 中文
            //(keyCode, json, comboName, filterMode, quickFill (快速匹配), required(是否必填))
            if (comboFilter(keyCode, json, "combo_person_dept", "hp", true, false) == 0) {
            }
        });
    });
    //绑定焦点移开事件
    combo_person_dept.attachEvent("onBlur", function () {
        if (combo_person_dept.optionsArr.length >= 1 && combo_person_dept.getSelectedValue() != "") {
            if (combo_person_dept.getSelectedValue() == null) {
                combo_person_dept.setComboText(combo_person_dept.optionsArr[0].text);
                combo_person_dept.setComboValue(combo_person_dept.optionsArr[0].value);
            }
        } else {
            combo_person_dept.setComboText("");
            combo_person_dept.setComboValue("");
        }
    });
</script>