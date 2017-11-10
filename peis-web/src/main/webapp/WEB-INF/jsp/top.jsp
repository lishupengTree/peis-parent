<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ include file="include.jsp" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <base href="<%=basePath%>"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link href="libs/dhtmlx_std_full/dhtmlx.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div class="hsp_top1">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="5"></td>
            <td style="background: url('img/login/top1.jpg') no-repeat;" width="90" height="90">&nbsp;</td>
            <td style="background: url('img/hosimg/0000-0000/img2.jpg') no-repeat;"
                width="270" align="right">
                <table cellpadding="1" cellspacing="0" border="0" align="left" style="margin-left: 142px;">
                    <tr>
                        <td height="37">
                            &nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <span style="color: white;font-size: 20px;font-family:'微软雅黑';font-weight: bold;">${login_menus[systemid].name}</span>
                        </td>
                    </tr>
                </table>
            </td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" class="hsp_right hsp_mar3 hsp_font"
                       style="width: 98%">
                    <tr>
                        <td align="right" height="28">
                            <table border="0" cellspacing="0" cellpadding="0" align="right">
                                <tr align="center">
                                    <td align="right">当前登录节点：<%=basePath%>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                    <td align="right">${login_dept.deptname} 欢迎您，${login_user.name}！</td>
                                    <td width="30" align="right"><img src="img/top4.jpg"/></td>
                                    <td width="50"><a href="exit.htm" class="top_a" style="color: #fff;">退出</a></td>
                                    <td width="20"><img src="img/top5.jpg"/></td>
                                    <td width="50"><a href="platform.htm" class="top_a" style="color: #fff;">首页</a></td>
                                    <td width="20"><img src="img/top6.jpg"/></td>
                                    <td width="50"><a href="javascript:void(0)" class="top_a" style="color: #fff;">帮助</a></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td align="left">
                            <table width="215" border="0" cellspacing="0" cellpadding="0" class="hsp_top3" align="left">
                                <tr>
                                    <td align="right"><input id="search_value" name="search_value" type="text"
                                                             class="hsp_top2" style="width: 125px;"/></td>
                                    <td width="43" height="28">
                                        <img src="img/top7.jpg" id="search_img" style="margin-top:0px;*margin-top:-1px;"
                                             onclick="topSearch()"/>
                                    </td>
                                    <td>
                                        <img src="img/img_03.gif" style="cursor: pointer;"
                                             alt="刷新" onclick="javascript:window.location.reload();"></img>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                            <ul class="hsp_xx" style="padding-left: 0px;">
                                <c:set var="key" value="${systemid}s"/>
                                <c:forEach items="${login_menus[key]}" var="menu">
                                    <c:choose>
                                        <c:when test="${menu.id == menuid}">
                                            <li id="${menu.id}" class="hsp_li2">${menu.name}<c:if
                                                    test="${menu.hotkeys!=null}"><span
                                                    class="hsp_font2">(${menu.hotkeys})</span></c:if></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li id="${menu.id}" class="hsp_li2"
                                                onclick="openMenu('${menu.url}','${menu.id}')">${menu.name}<c:if
                                                    test="${menu.hotkeys!=null}"><span
                                                    class="hsp_font2">(${menu.hotkeys})</span></c:if></li>
                                        </c:otherwise>
                                    </c:choose>
                                    <script>
                                        <c:if test="${menu.hotkeys !=null}">
                                        ${menu.hotkeys} = "forward('${menu.url}','${menu.id}')";
                                        </c:if>
                                    </script>
                                </c:forEach>
                            </ul>
                            <script>
                                if (document.getElementById('${menuid}')) {
                                    document.getElementById('${menuid}').className = "hsp_li1 hsp_xxhover";
                                }
                                function openMenu(url, menuid) {
                                    if (url == '') {
                                        alert('菜单没有配链接地址,不能正常访问！');
                                        return;
                                    }
                                    if (url.indexOf('?') > -1) {
                                        document.location.href = '<%=basePath%>' + url + '&menuid=' + menuid;
                                    } else {
                                        document.location.href = '<%=basePath%>' + url + '?menuid=' + menuid;
                                    }
                                }
                            </script>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</div>

<script type="text/javascript" src="libs/jquery/jquery-1.6.1.js"></script>
<script type="text/javascript" src="libs/jquery/jquery.blockUI.js"></script>
<script type="text/javascript" src="libs/jquery/jquery.lrTool.js"></script>
<script type="text/javascript" src="libs/jquery/jquery-ui-1.8.21.custom.min.js"></script>
<script type="text/javascript" src="js/window.js"></script>
</body>
</html>
