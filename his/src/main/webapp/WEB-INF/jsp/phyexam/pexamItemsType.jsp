<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <base href="<%=basePath%>"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcombo.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxcalendar_dhx_skyblue.css"/>
    <link rel="stylesheet" type="text/css" href="js/jquery.pagination/pagination.css"/>
    <style type="text/css">
        .exm_1 {
            width: 98%;
            margin: auto
        }

        .exm_l3 {
            border-left: 1px solid #93afba;
            border-right: 1px solid #93afba;
        }

        .exm_bg {
            background: url(img/exm2.gif) repeat-x;
        }

        .bgr {
            width: 100%;
            background: url(img/bt2.jpg) repeat-x;
        }

        .span01 {
            font-family: "微软雅黑";
            font-size: 13px;
            font-weight: bold;
            color: #44839a;
            line-height: 24px;
            background: #fff;
            padding: 0px 5px 0px 5px;
        }

        .table01 {
            font-size: 13px;
            font-family: "微软雅黑";
        }

        .table02 {
            font-size: 13px;
            font-family: "微软雅黑";
            text-align: center;
            background: #000
        }

        .table02 tr td {
            width: 150px;
            height: 30px;
            background: #fff
        }

        .input01 {
            border: 1px solid #93AFBA;
            width: 100px
        }

        .sch_grid {
            border-bottom: none;
            margin: 0px;
        }

        .btn {
            margin: 0px;
            background: url(img/btn.jpg) no-repeat;
            width: 94px;
            height: 34px;
            text-align: center;
            border: 0;
            font-size: 13px;
            font-family: Microsoft YaHei, Lucida Grande, Helvetica, Tahoma, Arial, sans-serif;
            cursor: pointer;
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
        }

        .btn01:focus {
            background: url(img/ssfocus.gif) no-repeat;
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

        div.gridbox_dhx_custom table.hdr td {
            font-family: 微软雅黑;
            font-size: 12px;
            font-weight: bold;
            padding-top: 3px;
            vertical-align: top;
        }

        .dhx_combo_box {
            border: 1px solid #93AFBA;
        }

        .dhx_combo_list {
            border: 1px solid #BAC2CD;
            height: 160px;
            font-family: 微软雅黑;
            font-size: 12px;
            scrollbar-face-color: #E3EBF8;
            scrollbar-shadow-color: #c6d8f0;
            scrollbar-highlight-color: #FFFFFF;
            scrollbar-3dlight-color: #E3EBF8;
            scrollbar-darkshadow-color: #d8e4f3;
            scrollbar-track-color: #FFFFFF;
            scrollbar-arrow-color: #9bb8de;
        }

        .dhx_combo_list div {
            padding: 0px;
            height: 20px;
        }

        .text_field {
            border: 1px solid #93AFBA;
            line-height: 17px;
            width: 150px;
        }
    </style>
    <script type="text/javascript" src="js/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="js/dhtmlxcalendar.js"></script>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/jquery.pagination/jquery.pagination.js"></script>
    <script type="text/javascript" src="js/gl/util.js"></script>
    <script type="text/javascript">
        //定时器
        var comboTimeout;
        function initCombo(combo, target) {
            var index;
            //DOMelem.onclick事件无法实现
            combo.DOMelem_input.onfocus = function () {
                this.select();
                if (comboTimeout != null) {
                    window.clearTimeout(comboTimeout);
                    comboTimeout = null;
                }
                //让onclick事件先执行
                comboTimeout = window.setTimeout(function () {
                    combo.openSelect();
                }, 300);
            }
            combo.DOMelem_input.onkeydown = function (ev) {
                var event = ev || window.event;
                var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
                if (keyCode == 13 && combo.getSelectedText() != "") {
                    //设值
                    $("#" + target).val(combo.getActualValue());
                    combo.setComboText(combo.getSelectedText());
                    //跳转
                    window.setTimeout(function () {
                        $("#searchButton")[0].focus();
                    }, 0);
                } else if (keyCode == 38) {
                    index = combo.getSelectedIndex();
                    if (index == 0) {
                        index = combo.optionsArr.length - 1;
                        var old = combo.getComboText();
                        combo.selectOption(index, true, true);
                        combo.setComboText(old);
                    } else {
                        index--;
                        var old = combo.getComboText();
                        combo.selectOption(index, true, true);
                        combo.setComboText(old);
                    }
                } else if (keyCode == 40) {
                    index = combo.getSelectedIndex();
                    if (index == combo.optionsArr.length - 1) {
                        index = 0;
                        var old = combo.getComboText();
                        combo.selectOption(index, true, true);
                        combo.setComboText(old);
                    } else {
                        index++;
                        var old = combo.getComboText();
                        combo.selectOption(index, true, true);
                        combo.setComboText(old);
                    }
                } else if (keyCode == 8 || keyCode == 46 || (keyCode >= 48 && keyCode <= 57) || (keyCode >= 65 && keyCode <= 90) || (keyCode >= 96 && keyCode <= 111)) {
                    window.setTimeout(function () {
                        getMedWayList();
                    }, 10);
                }
            }
            combo.DOMlist.onmouseover = function (ev) {
                var event = ev || window.event;
                var node = checkBrowser() == "FireFox" ? event.target : event.srcElement;
                while (!node._self) {
                    node = node.parentNode;
                    if (!node) {
                        return;
                    }
                }
                ;
                for (var i = 0; i < combo.DOMlist.childNodes.length; i++) {
                    if (combo.DOMlist.childNodes[i] == node) {
                        var old = combo.getComboText();
                        combo.selectOption(i, true, true);
                        combo.setComboText(old);
                        break;
                    }
                }
            }
            combo.DOMlist.onclick = function () {
                if (combo.optionsArr.length == 0) {
                    return;
                }
                //设值
                $("#" + target).val(combo.getActualValue());
                combo.setComboText(combo.getSelectedText());
                //跳转
                window.setTimeout(function () {
                    $("#searchButton")[0].focus();
                }, 0);
            }
        }
        //获得给药方式列表
        function getMedWayList() {
            var combo = combo_medWay;
            var code = combo.getComboText().replace(/ /g, "");
            //清除下拉框值
            combo.unSelectOption();
            combo.clearAll();
            combo.openSelect();
            //清空给药方式
            $("#medWay").val("");
            if (code == "") {
                return;
            }
            for (var i = 0; i < dicts16.length; i++) {
                if (dicts16[i].inputcpy.toLowerCase().indexOf(code.toLowerCase()) != -1) {
                    combo.addOption(dicts16[i].nevalue, dicts16[i].contents);
                }
            }
            if (combo.optionsArr.length > 0) {
                var old = combo.getComboText();
                combo.selectOption(0, true, true);
                combo.setComboText(old);
            }
        }
        var medWay;
        var pageSize = 16;
        function searchMedWayCount() {
            medWay = $("#medWay").val();
            $.ajax({
                url: "medWayMaintain/searchMedWayCount.htm",
                type: "post",
                data: "medWay=" + medWay + "&time=" + (new Date()).valueOf(),
                error: function () {
                    showError("获取数据失败");
                },
                success: function (reply) {
                    if (reply == "fail") {
                        showError("获取数据失败");
                    } else {
                        if (reply == "fail") {
                            showError("获取数据失败");
                        } else {
                            $("#result").html("(共" + reply + "条)");
                            $("#pageDiv").pagination(parseInt(reply), {
                                callback: searchMedWayData,
                                items_per_page: pageSize,  //显示条数
                                prev_text: '上一页',       //上一页按钮里text
                                next_text: '下一页',       //下一页按钮里text
                                num_display_entries: 8,    //连续分页主体部分分页条目数
                                num_edge_entries: 2       //两侧首尾分页条目数
                            });
                        }
                    }
                }
            });
        }
        function searchMedWayData(index, jq) {
            $.ajax({
                url: "medWayMaintain/searchMedWayData.htm",
                type: "post",
                data: "curPage=" + (index + 1) + "&pageSize=" + pageSize + "&medWay=" + medWay + "&time=" + (new Date()).valueOf(),
                error: function () {
                    showError("获取数据失败");
                },
                success: function (reply) {
                    if (reply == "fail") {
                        showError("获取数据失败");
                    } else {
                        if (reply == "fail") {
                            showError("获取数据失败");
                        } else {
                            var jsons = eval("(" + reply + ")");
                            mygrid.clearAll();
                            for (var i = 0; i < jsons.length; i++) {
                                mygrid.addRow(
                                    jsons[i].dictid,
                                    [
                                        "",
                                        index * pageSize + i + 1,
                                        jsons[i].contents,
                                        jsons[i].option01
                                    ],
                                    i
                                );
                            }
                        }
                    }
                }
            });
            return false;
        }
        function medWayInfo(dictId) {
            window.parent.$.blockUI({
                message: "<iframe height='100%' width='100%' frameborder='0' src='medWayMaintain/medWayInfo.htm?dictId=" + dictId + "&time=" + (new Date()).valueOf() + "'></iframe>",
                css: {
                    width: "896px",
                    height: "506px",
                    border: "1px solid #b6cfd6",
                    padding: "1px",
                    left: window.parent.getLeftPos(896),
                    top: window.parent.getTopPos(506)
                }
            });
        }
        function deleteMedWayList() {
            var dictIds = mygrid.getCheckedRows(0);
            if (dictIds == "") {
                dictIds = mygrid.getSelectedRowId();
                if (dictIds == null) {
                    showError("请选择需要删除的行");
                    return;
                }
            }
            $.ajax({
                url: "medWayMaintain/deleteMedWayList.htm",
                type: "post",
                data: "dictIds=" + dictIds + "&time=" + (new Date()).valueOf(),
                error: function () {
                    showError("删除数据失败");
                },
                success: function (reply) {
                    if (reply == "fail") {
                        showError("删除数据失败");
                    } else {
                        dictIds = dictIds.split(",");
                        for (var i = 0; i < dictIds.length; i++) {
                            mygrid.deleteRow(dictIds[i]);
                        }
                        //更新记录数
                        var total = parseInt($("#result").html().replace(/[^0-9]/g, ""));
                        $("#result").html("(共" + (total - dictIds.length) + "条)");
                    }
                }
            });
        }
        function init() {
            //初始化高度
            $("#mainDiv").css("height", window.parent.document.documentElement.clientHeight - 212);
            $("#gridbox").css("height", window.parent.document.documentElement.clientHeight - 247);
            loadCount();
        }
        var pageSize = 10;
        var pageIndex = 0;
        function loadCount() {
            var typeName = $("#typeName").val();
            $.ajax({
                url: "phyexam/itemsTypeCount.htm",
                type: "post",
                data: "typeName=" + typeName + "&time=" + (new Date()).valueOf(),
                dataType: "json",
                error: function () {
                    alert("获取数据失败");
                },
                success: function (reply) {
                    var pageCount = reply.value;
                    pageIndex = 0;
                    $("#result").html("(共" + pageCount + "条)");
                    createPagination(pageCount);
                }
            });
        }

        function createPagination(pageCount) {//创建分页标签
            //分页，pageCount是总条目数，这是必选参数，其它参数都是可选
            $("#pageDiv").pagination(pageCount, {
                callback: pageCallback,
                prev_text: '上一页',       //上一页按钮里text
                next_text: '下一页',       //下一页按钮里text
                items_per_page: pageSize,  //显示条数
                num_display_entries: 6,    //连续分页主体部分分页条目数
                current_page: pageIndex,   //当前页索引
                num_edge_entries: 2        //两侧首尾分页条目数
            });
        }

        function pageCallback(index, jq) {//翻页回调
            pageIndex = index;
            itemsTypeData();
            return false;
        }

        function itemsTypeData() {
            var typeName = $("#typeName").val();
            $.ajax({
                url: "phyexam/itemsTypeData.htm",
                type: "post",
                data: "typeName=" + typeName + "&time=" + (new Date()).valueOf() + "&index=" + pageIndex + "&size=" + pageSize,
                error: function () {
                    alert("获取数据失败");
                },
                success: function (reply) {
                    if (reply == 'fail') {
                        alert("加载数据失败！");
                    } else {
                        var json = eval("(" + reply + ")");
                        mygrid.clearAll();
                        var index = pageIndex * pageSize;
                        for (var i = 0; i < json.length; i++) {
                            var rowId = getUUID().replace(/-/g, "");
                            mygrid.addRow(json[i].typeid, [
                                '',
                                ++index,
                                json[i].typename,
                                json[i].sn,
                                json[i].descriptions,
                                json[i].comments
                            ]);
                        }
                    }
                }
            });
        }

        //新增
        function doAddRow() {
            var url = "phyexam/itemsTypeAdd.htm?operationType=add";
            parent.window.openMyWin(window, "体检项目信息", "520", "275", url);
        }

        //修改
        function doModifyRow() {
            var url = "phyexam/itemsTypeAdd.htm?operationType=modify&typeId=" + mygrid.getSelectedId() + "&random=" + Math.random();
            parent.parent.window.openMyWin(window, "体检项目信息", "520", "275", url);
        }

        function doRemoveRow() {
            var checkIds = mygrid.getCheckedRows(0);
            if (checkIds == "") {
                alert('请先选择要删除的数据！');
                return;
            }
            if (doRemoveCheck(checkIds)) {
                return;
            }

            var strs = new Array(); //定义一数组
            strs = checkIds.split(",");
            if (window.confirm('是否确定要删除这些数据？')) {
                showMsg("数据删除中...");
                $.ajax({
                    cache: false,//是否使用缓存
                    url: "phyexam/itemsTypeRemove.htm",
                    async: true,   //是否异步，false为同步
                    type: "post",
                    data: "checkIds=" + checkIds,
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

        function doRemoveCheck(checkIds) {
            var reVal = true;
            if (checkIds != "") {
                $.ajax({
                    cache: false,   //是否使用缓存
                    url: "phyexam/itemsTypeRemoveCheck.htm",
                    async: false,   //是否异步，false为同步
                    type: "post",
                    data: "checkIds=" + checkIds,
                    error: function () {
                        alert("ajax请求失败");
                    },
                    success: function (reply) {
                        if (reply == "fail") {
                            alert("服务器内部错误");
                            reVal = true;
                        } else {
                            var jsons = eval("(" + reply + ")");
                            if (jsons.isExist == "Y") {
                                alert("项目名称【" + mygrid.cellById(jsons.rowId, 2).getValue() + "】下，已经存在体检项目指标，不能删除该项目！");
                                reVal = true;
                            } else {
                                reVal = false;
                            }
                        }
                    }
                });
            }
            return reVal;
        }
    </script>
</head>
<body onload="init()">
<div class="exm_1">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="34"><img src="img/tp1.jpg"/></td>
            <td style="background:url(img/tp2.jpg) repeat-x 0px 4px;"><span class="span01">查询条件</span></td>
            <td width="4"><img src="img/tp3.jpg"/></td>
        </tr>
    </table>
    <div class="exm_l3" style="margin-top:-5px">
        <table width="96%" border="0" cellpadding="0" cellspacing="0" align="center" class="table01">
            <tr>
                <td width="70" height="30">
                    类型名称：
                </td>
                <td width="120">
                    <input type="text" id="typeName" class="text_field"/>
                </td>
                <td>
                    <div id="error" style="font-family:宋体;font-size:13px;font-weight:bold;color:red;">&nbsp;</div>
                </td>
                <td width="200">
                    <input id="searchButton" class="btn01" type="button" value="查 询" onclick="searchMedWayCount()"/>
                    <input id="addButton" class="btn01" type="button" value="新 增" onclick="doAddRow()"/>
                    <input id="deleteButton" class="btn01" type="button" value="删 除" onclick="doRemoveRow()"/>
                </td>
            </tr>
        </table>
    </div>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="4" valign="top"><img src="img/bt1.jpg"/></td>
            <td class="bgr"></td>
            <td width="4" valign="top"><img src="img/bt3.jpg"/></td>
        </tr>
    </table>
</div>
<div class="exm_1">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="34"><img src="img/tp1.jpg"/></td>
            <td style="background:url(img/tp2.jpg) repeat-x 0px 4px;"><span class="span01">查询结果<span id="result"></span></span>
            </td>
            <td width="4"><img src="img/tp3.jpg"/></td>
        </tr>
    </table>
    <div id="mainDiv" class="exm_l3" style="margin-top:-5px;height:375px;padding:5px 10px 0px 10px">
        <div id="gridbox" style="width:99.8%;height:340px;"></div>
        <script>
            var mygrid = new dhtmlXGridObject('gridbox');
            mygrid.setImagePath("imgs/");
            mygrid.setHeader("选择,序号,类型名称,顺序号,说明,备注");
            mygrid.setInitWidths("40,55,*,60,200,200");
            mygrid.setColTypes("ch,ro,ro,ro,ro,ro");
            mygrid.setColAlign("center,center,left,center,left,left");
            mygrid.setSkin("dhx_custom");
            mygrid.init();
            mygrid.attachEvent("onRowDblClicked", doModifyRow);
        </script>
        <div id="pageDiv" style="float:right;height:26px;margin-top:6px;"></div>
    </div>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td width="4" valign="top"><img src="img/bt1.jpg"/></td>
            <td class="bgr"></td>
            <td width="4" valign="top"><img src="img/bt3.jpg"/></td>
        </tr>
    </table>
</div>
</body>
</html>
