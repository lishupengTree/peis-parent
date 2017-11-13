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
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <title>用户维护</title>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" type="text/css" href="css/deptbtn.css"/>
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
    </style>
    <style>
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
    window.onresize = function () {
        $("#gridbox").css("width", parent.$("#iframe_right").width() - 35);
    }
    function setWin() {
        $("#gridbox").css("width", parent.$("#iframe_right").width() - 35);
        $("#gridbox").css("height", parent.$("#iframe_right").height() - 73);
    }

    //其他科室用户信息
    function doAddOherRow() {
        var url = "user/user_list_other.htm?hosnum=${hosnum}&nodecode=${nodecode}&deptcode=${deptcode}&type=${type}" + "&random=" + Math.random();
        parent.parent.window.openMyWin(window, "其他科室用户信息", "804", "400", url);
    }

    //用户新增
    function doAddRow() {
        var url = "user/user_save.htm?operationType=add&defaultHos=${hosnum}&defaultNode=${nodecode}"
            + "&defaultDept=${deptcode}&defaultType=${type}";
        parent.parent.window.openMyWin(window, "用户信息", "804", "575", url);
    }

    //用户修改
    function doModifyRow() {
        var url = "user/user_save.htm?operationType=modify&operationId=" + mygrid.getSelectedId()
            + "&defaultHos=${hosnum}&defaultNode=${nodecode}&defaultDept=${deptcode}&defaultType=${type}" + "&random=" + Math.random();
        parent.parent.window.openMyWin(window, "用户信息", "804", "575", url);
    }


    //用户删除
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
                url: "user/user_remove.htm",
                async: true,   //是否异步，false为同步
                type: "post",
                data: "checkIds=" + checkIds + "&type=${type}",
                error: function () {
                    alert("ajax请求失败");
                    closeMsg();
                },
                success: function (reply) {
                    if (reply == "success") {
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
        mygrid.clearAndLoad("user/user_list_load.htm?hosnum=${hosnum}&nodecode=${nodecode}&deptcode=${deptcode}&type=${type}&islast=${islast}");
    }

    //搜索当前数据
    function doSearch() {
        mygrid.filterBy(3, $("#search").val());
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
    var ENTER_KEYCODE = 13;

    var islast = "${islast}";
    $(document).bind("keydown", function (evt) {
        var event = evt ? evt : window.event;
        var node = event.srcElement ? event.srcElement : event.target;
        if (event.keyCode == F5_KEYCODE) {
            if (islast == "") {
                event.preventDefault();
                event.keyCode = 0;
                event.returnValue = false;
                doAddRow();
            }
        } else if (event.keyCode == F6_KEYCODE) {
            event.preventDefault();
            event.keyCode = 0;
            event.returnValue = false;
            doRemoveRow();
        }
        else if (event.keyCode == F7_KEYCODE) {
            if (islast == "") {
                event.preventDefault();
                event.keyCode = 0;
                event.returnValue = false;
                doAddOherRow();
            }
        } else if (event.keyCode == ENTER_KEYCODE) {
            if (node.id == 'search') {
                doSearch();
            }
        }
    });
</script>
<body onload="setWin();">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td class="font3" style="font-size: 16px;" width="45">
            姓名:
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
                    <c:if test="${islast == null}">
                        <li style="cursor: pointer; "
                            onclick="doAddRow()">
                            新增
                            <span class="font2">(F5)</span>
                        </li>
                    </c:if>
                    <li style="cursor: pointer;" onclick="doRemoveRow()">
                        删除
                        <span class="font2">(F6)</span>
                    </li>

                    <c:if test="${islast == null}">
                        <li style="cursor: pointer;" onclick="doAddOherRow()">
                            引入
                            <span class="font2">(F7)</span>
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
                mygrid.setInitWidths("35,100,*,70,35,150,110");
                mygrid.setHeader("选择,科室名称,工号,姓名,性别,身份证,电话");
                mygrid.setColTypes("ch,ro,ro,ro,ro,ro,ro");
                mygrid.setColAlign("center,left,left,left,center,left,left");
                mygrid.load("user/user_list_load.htm?hosnum=${hosnum}&nodecode=${nodecode}&deptcode=${deptcode}&type=${type}&islast=${islast}");
                //mygrid.enableSmartRendering(true,200);
                //mygrid.preventIECaching(false);

                //if(islast == ""){//是最后一级
                mygrid.attachEvent("onRowDblClicked", doModifyRow);
                //}
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
