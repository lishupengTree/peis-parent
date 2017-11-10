<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="../include.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>选择系统</title>
    <link href="css/login.css" rel="stylesheet" type="text/css"/>

    <style>
        .img_color {
            filter: Alpha(opacity=20);
            -moz-opacity: .1;
            opacity: 0.1;
        }
    </style>
</head>
<body style="background:url(img/login/dengl1.jpg) repeat-x" onload="setWin()">

<div style="display: none;">
    <input id="war_med" type="hidden" value=""/>
    <input id="stop_med" type="hidden" value=""/>
    <input id="war_pre" type="hidden" value=""/>
    <input id="stop_pre" type="hidden" value=""/>
    <input id="war_hel" type="hidden" value=""/>
    <input id="stop_hel" type="hidden" value=""/>
    <input id="war_jyy" type="hidden" value=""/>
    <input id="stop_jyy" type="hidden" value=""/>
    <input id="war_jyu" type="hidden" value=""/>
    <input id="stop_jyu" type="hidden" value=""/>
    <input id="war_kjy" type="hidden" value=""/>
    <input id="stop_kjy" type="hidden" value=""/>
    <input id="Prompt" type="hidden" value=""/>
    <input type="hidden" id="nodecode_skintest" value="${login_hospital.nodecode}"/>
    <input type="hidden" id="hosnum_skintest" value="${login_hospital.hosnum}"/>
    <input type="hidden" id="login_deptcode" value="${login_dept.deptcode}"/>
    <input type="hidden" id="login_wardid" value="${login_ward.deptcode}"/>
    <input type="hidden" id="login_userjobno" value="${login_user.job_no}"/>
</div>

<div class="login">
    <table width="1000" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td colspan="3"><img src="img/login/dengl2.jpg"/></td>
        </tr>
        <tr>
            <td width="109" rowspan="2"><img src="img/login/dengl3.jpg"/></td>
            <td width="369" height="46"><img src="img/hosimg/0000-0000/img1.jpg"/>
            </td>
            <td width="522" rowspan="2"><img src="img/login/dengl7.jpg"/></td>
        </tr>
        <tr>
            <td background="img/login/dengl6.jpg" height="38" valign="top"><span class="login7">${login_user.name}，您好！</span>欢迎登录 ${login_dept.deptname}</td>
        </tr>
    </table>
    <div class="login8">
        <table width="1000" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td width="478" align="center" valign="top">
                    <table width="360" border="0" cellspacing="0" cellpadding="0" class="login9">
                        <tr>
                            <td colspan="2" align="left" height="40"><img src="img/login/tongzhi.jpg"/></td>
                        </tr>
                        <tr>
                            <td colspan="2" height="180" valign="top">
                                <div>
                                    <%--<jsp:include page="/notice/homeNotice.htm"/>--%>
                                </div>
                            </td>
                        </tr>
                        <tr align="left">
                            <td height="40" valign="top"><img src="img/login/dengl11.jpg"/></td>
                            <td height="40" valign="top"></td>
                        </tr>
                        <tr>
                            <td align="left" height="40">
                                <span><a style="margin-left:20px;" href="install_lodop.exe"><img  src="img/login/printer.png"/></a></span>
                            </td>

                        </tr>
                        <tr align="left">
                            <td height="80" valign="top"><img src="img/login/dengl13.jpg"/></td>
                            <td valign="top"><a href="/peis/exit.htm"><img src="img/login/tuichu.jpg"/></a></td>
                        </tr>
                        <tr align="left">
                            <td colspan="2" height="25">获取更多帮助可发送电子邮件至：<a href="" class="login12">li_shupeng@126.com</a>
                            </td>
                        </tr>
                        <tr align="left">
                            <td colspan="2" height="25"></td>
                        </tr>
                    </table>
                </td>
                <td align="center" valign="top">
                    <table width="426" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr valign="top">
                            <td height="460" style="text-align: center;" id="content">
                                <c:forEach items="${system_menus}" var="menu">
                                    <table width="106" border="0" cellspacing="0" cellpadding="0"
                                           style="float: left;${my_menu_ids[menu.id]==true?'cursor: pointer;':''}"
                                           onclick="openMenu('${menu.id}','${my_menu_ids[menu.id]}')">
                                        <tr>
                                            <td height="50">
                                                <img src="${menu.image}" class="${my_menu_ids[menu.id]==true?'':''}"/>
                                                <span style="display:none;" id="menu_${menu.id}">${ menu.url}</span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="${my_menu_ids[menu.id]==true?'login15':''}" height="40">${menu.name}</td>
                                        </tr>
                                    </table>
                                </c:forEach>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
        <table width="1000" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><img src="img/login/dengl20.jpg" height="15"/></td>
            </tr>
        </table>
    </div>
</div>


<script type="text/javascript" src="libs/jquery/jquery-1.6.1.js"></script>
<script type="text/javascript" src="js/login/platform.js"></script>
</body>
</html>