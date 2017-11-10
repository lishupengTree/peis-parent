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
    <title>登录</title>
    <link href="css/login.css" rel="stylesheet" type="text/css"/>
    <link href="libs/dhtmlx_std_full/dhtmlx.css" rel="stylesheet" type="text/css"/>
    <style type="test/css">
        .hsp_xx {
            list-style: none;
            height: 32px;
        }
    </style>
</head>
<body style="background:url(img/login/login1.jpg) repeat-x" onload="set()">

<object id="LODOP_OB" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0>
    <embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object>

<div class="login">
    <table width="1000" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="267"><img src="img/login/login2.jpg"/></td>
            <td width="515"><img src="img/login/login3.jpg"/></td>
            <td width="218"><img src="img/login/login4.jpg"/></td>
        </tr>
        <tr>
            <td><img src="img/login/login5.jpg"/></td>
            <td><img src="img/login/login6.jpg"/></td>
            <td><img src="img/login/login7.jpg"/></td>
        </tr>
        <tr>
            <td><img src="img/login/login8.jpg"/></td>
            <td background="img/login/login9.jpg" valign="top">
                <table width="370" border="0" cellspacing="0" cellpadding="0" style="margin-top:80px;margin-left:45px;">
                    <tr>
                        <td height="26" width="60">用户名：</td>
                        <td colspan="2"><input id="key" type="text" class="login4" value=""
                                               onblur="javascript:getDepts(this.value);" tabindex="1"
                                               onkeydown="enter(this.id);"/></td>
                        <td width="95" rowspan="3"><a href="javascript:checkUser()" tabindex="4"><img
                                src="img/login/enter.jpg" border="0"/></a></td>
                    </tr>
                    <tr>
                        <td height="26">密&emsp;码：</td>
                        <td colspan="2"><input id="password" type="password" value="" class="login4" tabindex="2"
                                               style="font-size: 24px;" onkeydown="enter(this.id);"/></td>
                    </tr>
                    <tr>
                        <td height="26">科&emsp;室：</td>
                        <td width="130">
                            <table cellpadding="0" cellspacing="0">
                                <tr>
                                    <td><label><select id="dept" class="login5" tabindex="3"
                                                       style="width: 200px;"></select></label></td>
                                    <td><label id="shayne_div" style="display: none;"><select id="shayne" class="login5"
                                                                                              tabindex="3"></select></label>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td width="">&nbsp;</td>
                    </tr>
                </table>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td align="center" height="35">
                            <font color="red" style="font-size: 14px;"><b>请保管好自己设置的密码，并定期修改！</b></font>
                        </td>
                    </tr>

                </table>

            </td>
            <td><img src="img/login/login10.jpg"/></td>
        </tr>
        <tr>
            <td><img src="img/login/login11.jpg"/></td>
            <td><img src="img/login/login12.jpg"/></td>
            <td><img src="img/login/login13.jpg"/></td>
        </tr>
        <tr>
            <td><img src="img/login/login14.jpg"/></td>
            <td><img src="img/login/login15.jpg"/></td>
            <td><img src="img/login/login16.jpg"/></td>
        </tr>
    </table>
    <input type="hidden" id="mac"/><input type="hidden" id="ip"/>
</div>

<script type="text/javascript" src="libs/jquery/jquery-1.6.1.js"></script>
<script type="text/javascript" src="libs/jquery/jquery.cookie.js"></script>
<script type="text/javascript">
    window.dhx_globalImgPath = "libs/dhtmlx_std_full/imgs/";
</script>
<script type="text/javascript" src="libs/dhtmlx_std_full/dhtmlx.js"></script>
<%--<script type="text/javascript" src="js/LodopFuncs.js"></script>--%>
<script type="text/javascript" src="js/login/login.js"></script>

</body>
</html>
