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
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <title>检验项目维护</title>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="css/deptbtn.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <style type="text/css">
        div.gridbox_dhx_custom table.hdr td {
            font-family: 微软雅黑;
            font-size: 12px;
            font-weight: bold;
            vertical-align: top;
        }

        div.gridbox table.obj.row20px tr td {

        }

        .btn01 {
            width: 64px;
            height: 26px;
            background: url(img/ss.gif) no-repeat;
            border: 0px;
            font-size: 13px;
            color: #6ba3b6;
            font-family: 微软雅黑;
            font-weight: bold;
            cursor: pointer;
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
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="dhtmlxGrid/sources/ext/dhtmlxgrid_srnd.js"></script>
    <script type="text/javascript" src="dhtmlxGrid/sources/ext/dhtmlxgrid_filter.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/dept/verify.js"></script>
</head>

<script type="text/javascript">
    var parentid = "${parentid}";
    window.onresize = function () {
        $("#gridbox").css("width", parent.$("#iframe_right").width() - 35);
    }
    function setWin() {
        $("#gridbox").css("width", parent.$("#iframe_right").width() - 35);
        $("#gridbox").css("height", parent.$("#iframe_right").height() - 73);
    }

    //新增
    function doAddRow() {
        var url = "phyexam/itemsComAdd.htm?operationType=add&parentid=${parentid}";
        parent.parent.window.openMyWin(window, "体检项目信息", "810", "721", url);
    }

    //修改
    function doModifyRow() {
        var url = "phyexam/itemsComAdd.htm?operationType=modify&comId=" + mygrid.getSelectedId() + "&random=" + Math.random();
        parent.parent.window.openMyWin(window, "体检项目信息", "810", "721", url);
    }


    //验证要删除科室下面是否存在科室
    function doRemoveCheck(checkIds) {
        var reVal = true;
        if (checkIds != "") {
            $.ajax({
                cache: false,   //是否使用缓存
                url: "maintenance/items/pexam/pexam_remove_check.htm",
                async: false,   //是否异步，false为同步
                type: "post",
                data: "checkIds=" + checkIds,
                error: function () {
                    alert("ajax请求失败");
                },
                success: function (reply) {
                    if (reply == "N") {
                        reVal = false;
                    } else {
                        var ids = reply.split("_");
                        if (ids.length >= 2) {
                            alert("项目名称【" + mygrid.cellById(ids[1], 1).getValue() + "】下，已经存在体检项目指标，不能删除该项目！");
                        } else {
                            alert("项目名称【" + mygrid.cellById(ids[0], 1).getValue() + "】下，已经存在子项目，不能删除该项目！");
                        }
                        reVal = true;
                    }
                }
            });
        }
        return reVal;
    }

    //删除
    function doRemoveRow() {
        var checkIds = mygrid.getCheckedRows(0);
        if (checkIds == "") {
            alert('请先选择要删除的数据！');
            return;
        }
        var strs = new Array(); //定义一数组
        strs = checkIds.split(",");
        if (window.confirm('是否确定要删除这些数据？')) {
            showMsg("数据删除中...");
            $.ajax({
                cache: false,   //是否使用缓存
                url: "phyexam/itemsComRemove.htm",
                async: true,   //是否异步，false为同步
                type: "post",
                data: "checkIds=" + checkIds,
                error: function () {
                    alert("ajax请求失败");
                    closeMsg();
                },
                success: function (reply) {
                    if (reply == "success") {
                        var tree_num = 0;
                        for (i = 0; i < strs.length; i++) {
                            mygrid.deleteRow(strs[i]);
                        }
                        closeMsg();
                        alert("删除成功");
                    } else if (reply == "fail") {
                        closeMsg();
                        alert("删除失败");
                    }
                }
            });
        }
    }

    //计算数据总数
    function setCounter() {
        var span = document.getElementById("recfound");
        span.style.color = "";
        span.innerHTML = mygrid.getRowsNum();
    }

    //刷新
    function reloadGrid() {
        mygrid.clearAndLoad("phyexam/loadItemsCom.htm?parentId=" + parentid);
    }

    //搜索当前数据
    function doSearch() {
        mygrid.filterBy(2, $("#search").val());  //参数1： 检索的列索引， 此处代表检索第三列。    //参数2： 搜索的值
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
        if (event.keyCode == F5_KEYCODE) {
            event.preventDefault();
            event.keyCode = 0;
            event.returnValue = false;
            doAddRow();
        } else if (event.keyCode == F6_KEYCODE) {
            event.preventDefault();
            event.keyCode = 0;
            event.returnValue = false;
            doRemoveRow();
        }
    });
</script>
<body onload="setWin();">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td class="font3" style="font-size: 16px;" width="70">
            项目名称:
        </td>
        <td width="155">
            <input id="search" name="search" type="text" class="txt text_field"
                   style="height: 22px; width: 150px;"/>
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
                    <c:if test="${parentid!=''}">
                        <li style="cursor: pointer;" onclick="doAddRow()">
                            新增
                            <span class="font2">(F5)</span>
                        </li>
                        <li style="cursor: pointer;" onclick="doRemoveRow()">
                            删除
                            <span class="font2">(F6)</span>
                        </li>
                    </c:if>
                    <c:if test="${parentid==''}">
                        <li style="font-color:gray">
                            新增
                            <span color="gray">(F5)</span>
                        </li>
                        <li style="font-color:gray">
                            删除
                            <span color="gray">(F6)</span>
                        </li>
                    </c:if>
                </ul>
            </div>
        </td>
    </tr>
</table>
<table width="500" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td width="10"><img src="img/new_yuan1.jpg"/></td>
        <td width="480" background="img/new_yuan2.jpg">
            <img src="img/new_tp1.jpg" style="float: left;"/>
            <div style="float: left; margin-top: -2px;">
						<span class="font3"> 
							总计:<span id="recfound"></span> 条</span>
            </div>
        </td>
        <td width="10"><img src="img/new_yuan3.jpg"/></td>
    </tr>
    <tr>
        <td background="img/new_yuan4.jpg">
            &nbsp;
        </td>
        <td>
            <div id="gridbox" style="background-color: white; height: 200px;"></div>
            <script>
                var mygrid = new dhtmlXGridObject('gridbox');
                mygrid.enableAutoWidth(true);
                mygrid.setImagePath("imgs/");
                mygrid.setSkin("dhx_custom");
                mygrid.setInitWidths("35,50,*,60,60,250,60,60,50");
                mygrid.setHeader("选择,项目编码,项目名称,备注,项目类别,执行科室,适用性别,费用,序号"); //9
                mygrid.setColTypes("ch,ro,ro,ro,ro,ro,ro,ro,ro");
                mygrid.setColAlign("center,center,left,center,center,left,left,left,center");
                if (parentid != '') {
                    mygrid.load("phyexam/loadItemsCom.htm?parentId=" + parentid);
                }
                mygrid.attachEvent("onRowDblClicked", doModifyRow);
                mygrid.init();
                mygrid.attachEvent("onXLE", setCounter);
            </script>
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
</body>
</html>
