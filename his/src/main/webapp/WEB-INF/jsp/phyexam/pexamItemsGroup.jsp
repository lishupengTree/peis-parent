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
    <title>体检套餐维护</title>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="css/dictionary.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeStyle.css" type="text/css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeIcons.css" type="text/css"/>
    <style type="text/css">
        div.gridbox_dhx_custom table.hdr td {
            font-family: 微软雅黑;
            font-size: 12px;
            font-weight: bold;
            vertical-align: top;
        }

        div.gridbox table.obj.row20px tr td {
        }

        .dhx_combo_list {
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
    <script type="text/javascript" src="dhtmlxGrid/codebase/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="dhtmlxTabbar/codebase/dhtmlxtabbar.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
    <script type="text/javascript" src="js/demoTools.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="dhtmlxGrid/sources/ext/dhtmlxgrid_srnd.js"></script>
    <script type="text/javascript" src="js/demoTools.js"></script>
</head>

<script type="text/javascript">
    var zTree;
    var treeNodes;
    var opentype = $("#opentype").val();
    //树的参数
    var setting = {
        isSimpleData: true,
        treeNodeKey: "id",
        treeNodeParentKey: "pId",
        showLine: true,
        expandSpeed: "fast",//展开速度
        //fontCss: setFontCss,
        callback: {
            //click:zTreeOnClick //点击事件
        }
    };

    function setWin() {
        $("#iframe_body").css("height", parent.$("#dhxMainCont").height());
        $("#tree_div").css("height", parent.$("#dhxMainCont").height() - 8);
        $("#iframe_right").css("height", parent.$("#dhxMainCont").height() - 6);
        loadTree(true);
    }

    function loadTree(autoLoad) {
        var curNodeId = "";
        if (!autoLoad) {
            var curSelectNode = zTree.getSelectedNode();
            if (curSelectNode != null) {
                curNodeId = curSelectNode.id;
            }
        }
        $.ajax({
            async: false,   //是否异步
            cache: false,   //是否使用缓存
            type: 'post',   //请求方式,post
            dataType: "json",   //数据传输格式
            url: "phyexam/itemsGroupTree.htm",   //请求链接
            error: function () {
                alert('fail');
            },
            success: function (data) {
                treeNodes = data;
            }
        });
        zTree = $("#menuTree").zTree(setting, treeNodes);   //前台树的位置
        if (autoLoad) {
            var curNode = zTree.getNodeByParam("id", "0");
            zTree.selectNode(curNode);
            var url = "phyexam/itemsGroupList.htm?opentype=" + $('#opentype').val();
            document.getElementById("iframe_right").src = url;
        } else {
            if (curNodeId != "") {
                var curNode = zTree.getNodeByParam("id", curNodeId);
                zTree.selectNode(curNode);
            }
        }
    }

    //树点击方法
    function zTreeOnClick(event, treeId, treeNode) {
        if (treeNode.myType) {
            if (treeNode.myType == "group") {//显示套餐信息
                var url = "phyexam/itemsGroupList.htm?opentype=" + $('#opentype').val();
                document.getElementById("iframe_right").src = url;
            } else if (treeNode.myType == "item_0") {//显示体检项目信息
                var groupName = "";
                var groupid = "";
                groupName = treeNode.name;
                groupid = treeNode.id;
                var url = "maintenance/items/group/group_detail_list.htm?hosnum=${hosnum}&groupid=" + groupid + "&parentid=" + parentid
                    + "&groupname=" + encodeURI(encodeURI(groupName)) + "&myType=" + treeNode.myType;
                document.getElementById("iframe_right").src = url;
            }
        }
    }
</script>
<body onload="setWin();">
<table id="iframe_body" width="100%" cellpadding="0" border="0" cellpadding="0">
    <tr>
        <td id="left_area" width="200" valign="top">
            <div id="tree_div" style="OVERFLOW-y:auto;border:1px solid #93AFBA;width: 195px;">
                <ul id="menuTree" class="tree"></ul>
            </div>
        </td>
        <td id="right_area" valign="top">
            <iframe id="iframe_right"
                    style="width: 100%; margin: 0px 0px 0px 0px; padding: 0px 0px 0px 0px;"
                    scrolling="no" frameborder="0"></iframe>
        </td>
    </tr>
</table>
<input type="hidden" id="opentype" value="${type}"/>
</body>
</html>
