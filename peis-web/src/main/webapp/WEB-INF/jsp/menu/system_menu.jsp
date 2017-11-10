<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
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
    <title>系统菜单维护</title>

    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link href="css/dictionary.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" href="css/zTreeStyle/zTreeStyle.css" type="text/css"/>
    <link rel="stylesheet" href="css/zTreeStyle/zTreeIcons.css" type="text/css"/>
    <link href="libs/dhtmlx_std_full/dhtmlx.css" rel="stylesheet" type="text/css"/>

    <style type="text/css">
        div.gridbox_dhx_custom table.hdr td {
            font-family: 微软雅黑;
            font-size: 12px;
            font-weight: bold;
            vertical-align: top;
        }
        .tree li a.curSelectedNode button.bq_leaf {
            background: url(css/zTreeStyle/img/page.gif);
        }
        div.gridbox table.hdr td {
            padding-top: 6px;
            padding-bottom: 6px
        }

        div.gridbox .objbox {
            scrollbar-face-color: #E3EBF8;
            scrollbar-shadow-color: #c6d8f0;
            scrollbar-highlight-color: #FFFFFF;
            scrollbar-3dlight-color: #E3EBF8;
            scrollbar-darkshadow-color: #d8e4f3;
            scrollbar-track-color: #FFFFFF;
            scrollbar-arrow-color: #9bb8de;
        }
    </style>
</head>
<body>
<table width="100%" cellpadding="0" border="0" cellpadding="0">
    <tr>
        <td id="left_area" width="200" valign="top">
            <div id="tree_div" style="OVERFLOW-y:auto;OVERFLOW-x:hidden;border:1px solid #93AFBA;width: 195px;">
                <ul id="menuTree" class="tree"></ul>
            </div>
        </td>
        <td valign="top">
            <!-- 1 -->
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <li style="cursor: pointer;" onclick="doDeptDeleteRow()">
                        删除
                    </li>
                    <td class="font3" style="font-size: 16px;" width="70">
                        模糊搜索:
                    </td>
                    <td width="155">
                        <input id="search" name="search" type="text" class="txt text_field" style="height: 22px; width: 150px;"/>
                    </td>
                    <td align="left">
                        <div class="middle4" style="padding: 0px; margin-top: 4px;">
                            <ul id="op_ul" style="float: left;">
                                <li style="cursor: pointer; float: left;" onclick="doSearch();">
                                    搜索
                                </li>
                            </ul>
                        </div>
                    </td>
                    <td></td>
                    <td width="250">
                        <div class="middle4" style="padding: 0px; margin-top: 4px;">
                            <ul id="op_ul">
                                <li style="cursor: pointer;" onclick="doDeptAddRow()">
                                    新增
                                </li>

                            </ul>
                        </div>
                    </td>
                </tr>
            </table>
            <!-- 1 -->
            <!-- 2 -->
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td width="10"><img src="img/new_yuan1.jpg"/></td>
                    <td width="100%" background="img/new_yuan2.jpg">
                    </td>
                    <td width="10"><img src="img/new_yuan3.jpg"/></td>
                </tr>
                <tr>
                    <td background="img/new_yuan4.jpg">
                        &nbsp;
                    </td>
                    <td>
                        <!-- grid -->
                        <div id="menu_grid" style="width:100%;background-color: white; height: 300px;"></div>
                    </td>
                    <td background="img/new_yuan5.jpg">
                        &nbsp;
                    </td>
                </tr>
                <tr>
                    <td>
                        <img src="img/new_yuan6.jpg"/>
                    </td>
                    <td background="img/new_yuan7.jpg"></td>
                    <td>
                        <img src="img/new_yuan8.jpg"/>
                    </td>
                </tr>
            </table>
            <!-- 2 -->
        </td>
    </tr>
</table>


<script type="text/javascript" src="libs/jquery/jquery-1.6.1.js"></script>
<script type="text/javascript" src="libs/jquery/jquery.ztree-2.6.js"></script>
<script type="text/javascript" src="libs/jquery/jquery.blockUI.js"></script>
<script type="text/javascript" src="libs/dhtmlx_std_full/dhtmlx.js"></script>
<script type="text/javascript" src="js/demoTools.js"></script>
<script type="text/javascript" src="js/util.js"></script>
<script type="text/javascript" src="js/menu/system_menu.js"></script>
</body>
</html>
