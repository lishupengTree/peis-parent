<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
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
        //$("#gridbox").css("width",parent.$("#iframe_right").width() - 35);
    }
    function setWin() {
        $("#gridbox").css("width", $(window).width() - 35);
        $("#gridbox").css("height", $(window).height() - 83);
    }

    //计算数据总数
    function setCounter() {
        var span = document.getElementById("recfound");
        span.style.color = "";
        span.innerHTML = mygrid.getRowsNum();
    }

    //用户保存
    function doOtherSave() {
        var checkIds = mygrid.getCheckedRows(0);

        if (checkIds == "") {
            alert('请先选择要保存的用户！');
            return;
        }
        showMsg("数据保存中...");
        $.ajax({
            cache: false,   //是否使用缓存
            url: "user/user_other_saved.htm",
            async: false,   //是否异步，false为同步
            type: "post",
            data: "checkIds=" + checkIds + "&hosnum=${hosnum}&nodecode=${nodecode}&deptcode=${deptcode}&type=${type}",
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
                    parent.window.mywin.reloadGrid();
                    parent.doClose();
                }
            }
        });
    }


    //搜索当前数据
    function doSearch() {
        mygrid.filterBy(2, $("#search").val());//科室过滤
        mygrid.filterBy(4, $("#search1").val(), true);//姓名过滤
    }

    //关闭层
    var parentCloseStr = parent.doClose;//保存父类doClose方法
    parent.doClose = function () {
        parent.doClose = eval(parentCloseStr);//还原父类doClose方法
        parent.$("#hidden_iframe").attr("src", "");
        parent.$.unblockUI();
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

    $(document).bind("keydown", function (evt) {
        var event = evt ? evt : window.event;
        var node = event.srcElement ? event.srcElement : event.target;
        if (event.keyCode == F2_KEYCODE) {
            event.preventDefault();
            event.keyCode = 0;
            event.returnValue = false;
            doOtherSave();
        } else if (event.keyCode == ESC_KEYCODE) {
            event.preventDefault();
            event.keyCode = 0;
            event.returnValue = false;
            parent.doClose();
        } else if (event.keyCode == ENTER_KEYCODE) {
            if (node.id == 'search' || node.id == 'search1') {
                doSearch();
            }
        }
    });
</script>
<body onload="setWin();">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td class="font3" style="font-size: 15px;" width="70">
            科室名称:
        </td>
        <td width="105">
            <input id="search" name="search" type="text" class="txt text_field"
                   style="height: 22px; width: 100px;"/>
        </td>
        <td class="font3" style="font-size: 15px;" width="40">
            姓名:
        </td>
        <td width="105">
            <input id="search1" name="search1" type="text" class="txt text_field"
                   style="height: 22px; width: 100px;"/>
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
                    <li style="cursor: pointer;" onclick="parent.doClose();">
                        关闭
                        <span class="font2">(Esc)</span>
                    </li>
                </ul>
                <ul id="op_ul">
                    <li style="cursor: pointer;" onclick="doOtherSave();">
                        保存
                        <span class="font2">(F2)</span>
                    </li>
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
                mygrid.setInitWidths("35,80,120,*,80,40,180");
                mygrid.setHeader("选择,科室类别,科室名称,用户名,姓名,性别,身份证");
                mygrid.setColTypes("ch,ro,ro,ro,ro,ro,ro");
                mygrid.setColAlign("center,left,left,left,left,center,left");
                mygrid.load("user/user_list_other_load.htm?hosnum=${hosnum}&nodecode=${nodecode}&deptcode=${deptcode}");
                //mygrid.enableSmartRendering(true,200);
                //mygrid.preventIECaching(false);

                //mygrid.attachEvent("onRowDblClicked", doModifyRow);
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
