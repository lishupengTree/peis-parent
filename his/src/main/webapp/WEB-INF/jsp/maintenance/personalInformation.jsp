<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="../include.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <base href="<%=basePath%>"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <title>个人信息设置</title>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" type="text/css" href="css/dictionary.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar_dhx_skyblue.css"/>

    <script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="js/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
    <script type="text/javascript" src="js/clc/comboTool.js"></script>
    <script type="text/javascript" src="js/gl/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/gl/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/gl/dhtmlxgrid_excell_combo.js"></script>
    <script type="text/javascript" src="js/maintenance/maintenance_tool.js"></script>
    <script type="text/javascript" src="js/dhtmlxcalendar.js"></script>
    <style type="text/css">
        body {
            margin-top: 10px;
        }

        .qdiv {
            width: 630px;
        }

        td {
            font-size: 13px;
        }

        .txt {
            height: 18px;
            vertical-align: middle;
            border: 1px solid #93AFBA;
        }

        .txtarea {
            border: 1px solid #93AFBA;
        }

        p {
            line-height: 17px;
            text-align: right;
        }

        .show_img {
            border: 1px solid #93AFBA;
            width: 98px;
            height: 98px;
            margin-top: 2px;
        }

        .btn {
            background: url(img/btn.jpg) no-repeat;
            width: 92px;
            height: 32px;
            text-align: center;
            border: 0;
            line-height: 32px;
            font-size: 14px;
            font-family: Microsoft YaHei, Lucida Grande, Helvetica, Tahoma, Arial, sans-serif;
            color: #000;
            cursor: pointer;
            margin: 5px 20px 0 0;
        }

        .show_select_tree {
            height: 21px;
            width: 39px;
            background: url("img/xz.gif");
            cursor: pointer;
            margin-left: 3px;
        }

        .float_left {
            float: left;
        }

        .msg {
            font-size: 12px;
            color: red;
        }

        .not_editable {
            background-color: #eaeaea
        }

        .red_line {
            border: 1px solid red;
        }

        ;
        .font3 {
            font-size: 12px;
            color: #44839a;
            font-weight: bold;
            background: #fff;
        }
    </style>
    <script type="text/javascript">
        var myCalendar;
        $(function () {
            $('#name').focus();
            //定义时间空间
            myCalendar = new dhtmlXCalendarObject(["birthdate"]);
            var sex = new dhtmlXCombo("sex", "sex", 102);
            var input_custom = new dhtmlXCombo("input_custom", "input_custom", 102);
            var post_code = new dhtmlXCombo("post_code", "post_code", 102);
            post_code.clearAll();
            fillCombo(post_code, "post", false);
            post_code.attachEvent("onSelectionChange", function () {
                $('#post').val(post_code.getSelectedText());
            });
            $("#roleId").val("${user.ehrrole}");
        });


        function saveInfo() {
            var name = $('#name').val();
            if (name == "") {
                alert("名字不能为空！");
                return;
            }
            $.ajax({
                cache: false,
                url: "personalInformation/saveInfo.htm",
                async: false,
                type: "post",
                data: $('#the_form').serialize(),
                error: function () {
                    alert("ajax请求失败");
                },
                success: function (reply) {
                    if ("success" == reply) {
                        alert("保存成功");
                    }
                }
            });
        }
        function saveNewPassword() {
            var password = $('#password').val();
            var oldPassword = $('#oldPassword').val();//password
            var newPassword = $('#newPassword').val();//password
            var padPassword = $('#padPassword').val();//password
            if (oldPassword != password) {
                alert("旧密码不正确！");
                return;
            }
            if (newPassword == "" || padPassword == "") {
                alert("新密码不能为空！");
                return;
            }
            if (newPassword != padPassword) {
                alert("重复密码不同！");
                return;
            }

            var userId = $('#id').val();
            $.get("personalInformation/newPassword.htm?password=" + newPassword + "&id=" + userId, function (msg) {
                if ("success" == msg) {
                    $('#password').val(newPassword);
                    alert("保存成功");
                }
            });

        }

        function saveERHPassword() {
            var ehrusername = $("#EHRuserName").val();
            var password = $("#EHRPassword").val();
            var roleId = $("#roleId").val();
            var userId = $('#id').val();
            if ("" == ehrusername) {
                alert("健康档案用户名不能为空");
                return;
            } else if ("" == password) {
                alert("健康档案密码不能为空");
                return;
            }
            $.get("personalInformation/updateEHRUserInfo.htm?username=" + ehrusername + "&password=" + password + "&roleId=" + roleId + "&id=" + userId, function (msg) {
                if ("success" == msg) {
                    alert("保存成功");
                } else if ("fail" == msg) {
                    alert("保存失败");
                }
            });
        }

    </script>
</head>
<body>

<!-- cancel begin-->
<div class="qdiv">
    <form id="the_form" name="the_form" onsubmit="return false">


        <input id="id" name="id" type="hidden" value="${user.id}"/>
        <table width="100%" cellspacing="0" cellpadding="0">
            <tr>
                <td width="10"><img src="img/new_yuan1.jpg"/></td>
                <td width="100%" background="img/new_yuan2.jpg"><img src="img/new_tp1.jpg"/><span class="font3"
                                                                                                  style="position:relative;top:-2px;">用户信息</span>
                </td>
                <td width="10"><img src="img/new_yuan3.jpg"/></td>
            </tr>
            <tr>
                <td background="img/new_yuan4.jpg">&nbsp;</td>
                <td>
                    <!-- parent info begin-->
                    <table id="p_table" width="100%" border="0" cellspacing="5" cellpadding="0">
                        <tr>
                            <td width="80" align="right">姓&emsp;&emsp;名：</td>
                            <td align="left"><input class="txt red_line" name="name" type="text" id="name"
                                                    style="width:100px" value="${user.name}"/></td>
                            <td align="right">用&ensp;户&ensp;名</td>
                            <td align="left"><input class="txt not_editable" readonly="readonly" name="user_key"
                                                    type="text" id="user_key" style="width:100px"
                                                    value="${user.user_key}"/></td>
                            <td width="80" align="right">口&emsp;&emsp;令：</td>
                            <td width="110" align="left">
                                <input class="txt not_editable" readonly="readonly" name="password" type="password"
                                       id="password" style="width:100px" value="${user.password}"/>
                            </td>
                        </tr>
                        <tr>
                            <td width="80" align="right">身&ensp;份&ensp;证：</td>
                            <td colspan="3" align="left"><input class="txt" name="idcard" type="text" id="idcard"
                                                                maxlength="18" style="width:310px"
                                                                value="${user.idcard}"/></td>
                            <td width="80" align="right">工&emsp;&emsp;号：</td>
                            <td width="110" align="left"><input class="txt not_editable" name="job_no" type="text"
                                                                id="job_no" readonly="readonly" style="width:100px"
                                                                value="${user.job_no}"/>
                            </td>
                        </tr>
                        <tr>
                            <td align="right">性&emsp;&emsp;别：</td>
                            <td align="left" width="102"><select style="width:102px" name="sex" id="sex">
                                <option value=""></option>
                                <option value="男" <c:if test="${user.sex == '男' }">selected="selected"</c:if>>男</option>
                                <option value="女" <c:if test="${user.sex == '女' }">selected="selected"</c:if>>女</option>
                            </select></td>
                            <td align="right">出身日期：</td>
                            <td align="left"><input class="txt" name="birthdate_str" type="text" id="birthdate"
                                                    readonly="readonly" style="width:100px"
                                                    value="<fmt:formatDate value="${user.birthdate}" pattern="yyyy-MM-dd"/>"/>
                            </td>
                            <td align="right">职&emsp;&emsp;称：</td>
                            <td width="102" align="left">
                                <select style="width:102px" name="post_code" id="post_code">
                                    <option value="${user.post_code}">${user.post}</option>
                                </select>
                                <input type="hidden" id="post" name="post" value="${user.post}"/>
                            </td>
                        </tr>
                        <tr>
                            <td align="right">排&ensp;序&ensp;号：</td>
                            <td align="left"><input class="txt" name="index_no" type="text" id="index_no"
                                                    style="width:100px" value="${user.index_no}"/></td>
                            <td align="right">电&emsp;&emsp;话：</td>
                            <td align="left"><input class="txt" name="phone" type="text" id="phone" style="width:100px"
                                                    value="${user.phone}"/></td>
                            <td align="right">手&emsp;&emsp;机：</td>
                            <td align="left"><input class="txt" name="mobile" type="text" id="mobile"
                                                    style="width:100px" value="${user.mobile}"/></td>
                        </tr>
                        <tr>
                            <td width="80" align="right">短&emsp;&emsp;号：</td>
                            <td width="120" align="left"><input class="txt" name="short_mobile" type="text"
                                                                id="short_mobile" style="width:100px"
                                                                value="${user.short_mobile}"/></td>
                            <td width="80" align="right">输入习惯：</td>
                            <td align="left" width="102"><select style="width:102px" name="input_custom"
                                                                 id="input_custom">
                                <option value=""></option>
                                <option value="py"
                                        <c:if test="${user.input_custom == 'py' }">selected="selected"</c:if>>拼音
                                </option>
                                <option value="wb"
                                        <c:if test="${user.input_custom == 'wb' }">selected="selected"</c:if>>五笔
                                </option>
                            </select></td>
                            <td width="80" align="right">&nbsp;</td>
                            <td width="110" align="left">&nbsp;</td>
                        </tr>
                        <tr>
                            <td align="right">邮件地址：</td>
                            <td colspan="5" align="left"><input class="txt" name="email" type="text" id="email"
                                                                style="width:505px" value="${user.email}"/></td>
                        </tr>
                        <tr>
                            <td width="80" align="right">备&emsp;&emsp;注：</td>
                            <td colspan="5" align="left">
                                <textarea name="remark" rows="2" class="txtarea" id="remark"
                                          style="width:505px">${user.remark}</textarea>
                            </td>
                        </tr>
                        <tr>
                            <td align="right">&nbsp;</td>
                            <td colspan="5" align="left">
                                <div id="save" class="btn" style="float:left;margin-left:40px;" onclick="saveInfo()">
                                    保存
                                </div>
                            </td>
                        </tr>
                    </table>

                </td>
                <td background="img/new_yuan5.jpg">&nbsp;</td>
            </tr>
            <tr>
                <td><img src="img/new_yuan60.jpg"/></td>
                <td background="img/new_yuan70.jpg"></td>
                <td><img src="img/new_yuan80.jpg"/></td>
            </tr>
        </table>
    </form>
</div>
<!-- cancel end-->

<!-- frist div begin -->
<div class="qdiv" style="margin-top:5px;">
    <form id="the_form2" name="the_form2" onsubmit="return false">
        <table>
            <tr>
                <td width="50%">
                    <table width="100%" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="10"><img src="img/new_yuan1.jpg"/></td>
                            <td width="100%" background="img/new_yuan2.jpg"><img src="img/new_tp1.jpg"/><span
                                    class="font3" style="position:relative;top:-2px;">账户信息</span></td>
                            <td width="10"><img src="img/new_yuan3.jpg"/></td>
                        </tr>
                        <tr>
                            <td background="img/new_yuan4.jpg">&nbsp;</td>
                            <td>
                                <!-- parent info begin-->
                                <table id="p_table" width="100%" border="0" cellspacing="5" cellpadding="0">
                                    <tr>
                                        <td width="280" align="right">用&ensp;户&ensp;名：</td>
                                        <td width="120" align="left"><input class="txt not_editable" readonly="readonly"
                                                                            name="userName" type="text" id="userName"
                                                                            style="width:100px"
                                                                            value="${user.user_key}"/></td>
                                        <td width="80" align="right">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                        <td width="80" align="right">&nbsp;</td>
                                        <td width="110" align="left">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td align="right">旧&ensp;密&ensp;码：</td>
                                        <td align="left"><input class="txt" name="oldPassword" type="password"
                                                                id="oldPassword" style="width:100px" value=""/></td>
                                        <td align="right">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                        <td align="right">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td width="80" align="right">新&ensp;密&ensp;码：</td>
                                        <td align="left"><input class="txt" name="newPassword" type="password"
                                                                id="newPassword" maxlength="16" style="width:100px"
                                                                value=""/></td>
                                        <td align="left">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                        <td width="80" align="right">&nbsp;</td>
                                        <td width="110" align="left">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td align="right">重复密码：</td>
                                        <td align="left"><input class="txt" name="padPassword" type="password"
                                                                id="padPassword" maxlength="16" style="width:100px"
                                                                value=""/></td>
                                        <td align="left">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                        <td align="right">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td align="right">&nbsp;</td>
                                        <td colspan="5" align="left">
                                            <div id="save" class="btn" style="float:left;margin-left:40px;"
                                                 onclick="saveNewPassword()">保存
                                            </div>
                                        </td>
                                    </tr>
                                </table>

                            </td>
                            <td background="img/new_yuan5.jpg">&nbsp;</td>
                        </tr>
                        <tr>
                            <td><img src="img/new_yuan60.jpg"/></td>
                            <td background="img/new_yuan70.jpg"></td>
                            <td><img src="img/new_yuan80.jpg"/></td>
                        </tr>
                    </table>
                </td>
                <td width="50%">
                    <table width="100%" cellspacing="0" cellpadding="0">
                        <tr>
                            <td width="10"><img src="img/new_yuan1.jpg"/></td>
                            <td width="100%" background="img/new_yuan2.jpg"><img src="img/new_tp1.jpg"/><span
                                    class="font3" style="position:relative;top:-2px;">健康档案</span></td>
                            <td width="10"><img src="img/new_yuan3.jpg"/></td>
                        </tr>
                        <tr>
                            <td background="img/new_yuan4.jpg">&nbsp;</td>
                            <td>
                                <!-- parent info begin-->
                                <table id="p_table" width="100%" border="0" cellspacing="5" cellpadding="0">
                                    <tr>
                                        <td width="280" align="right">用&ensp;户&ensp;名：</td>
                                        <td width="120" align="left"><input class="txt" name="EHRuserName" type="text"
                                                                            id="EHRuserName" style="width:100px"
                                                                            value="${user.ehruser_key}"/></td>
                                        <td width="80" align="right">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                        <td width="80" align="right">&nbsp;</td>
                                        <td width="110" align="left">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td align="right">密&ensp;&ensp;&ensp;&ensp;码：</td>
                                        <td align="left"><input class="txt" name="ERHPassword" type="password"
                                                                id="EHRPassword" style="width:100px" value=""/></td>
                                        <td align="right">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                        <td align="right">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td align="right">角&ensp;&ensp;&ensp;&ensp;色：</td>
                                        <td align="left"><select id="roleId" name="roleId" style="width:105px">
                                            <option id="1" value="1">责任医生</option>
                                            <option id="2" value="2">责任护士</option>
                                            <option id="4" value="4">中心主任</option>
                                            <option id="5" value="5">团队长</option>
                                            <option id="6" value="6">网络管理员</option>
                                            <option id="7" value="7">儿保医生</option>
                                            <option id="8" value="8">妇保医生</option>
                                            <option id="9" value="9">市妇保</option>
                                            <option id="10" value="10">区妇保</option>
                                            <option id="11" value="11">市CDC</option>
                                            <option id="12" value="12">区CDC</option>
                                            <option id="13" value="13">市卫生局</option>
                                            <option id="14" value="14">防保科长</option>
                                            <option id="21" value="21">区卫生局</option>
                                        </select></td>
                                        <td align="left">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                        <td align="right">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td align="right"></td>
                                        <td align="left"></td>
                                        <td align="left">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                        <td align="right">&nbsp;</td>
                                        <td align="left">&nbsp;</td>
                                    </tr>
                                    <tr></tr>

                                    <tr>
                                        <td align="right">&nbsp;</td>
                                        <td colspan="5" align="left">
                                            <div id="save" class="btn" style="float:left;margin-left:40px;"
                                                 onclick="saveERHPassword()">保存
                                            </div>
                                        </td>
                                    </tr>
                                </table>

                            </td>
                            <td background="img/new_yuan5.jpg">&nbsp;</td>
                        </tr>
                        <tr>
                            <td><img src="img/new_yuan60.jpg"/></td>
                            <td background="img/new_yuan70.jpg"></td>
                            <td><img src="img/new_yuan80.jpg"/></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </form>
</div>
<!-- frist div end -->
</body>

</html>
