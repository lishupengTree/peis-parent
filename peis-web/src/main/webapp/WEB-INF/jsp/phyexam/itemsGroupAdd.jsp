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
    <title>体检项目定义</title>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="css/deptbtn.css"/>
    <link rel="stylesheet" type="text/css" href="css/dictionary.css"/>
    <link rel="stylesheet" href="My97DatePicker/skin/WdatePicker.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeStyle.css" type="text/css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeIcons.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>

    <style type="text/css">
        .text_field {
            margin-left: 4px
        }

        .text_field_required {
            margin-left: 4px
        }

        .tree li button.chk.checkbox_false_part {
            background: url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -24px transparent;
        }

        .tree li button.chk.checkbox_false_part_focus {
            background: url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -12px transparent;
        }

        .tree li button.chk.checkbox_true_part {
            background: url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -24px transparent;
        }

        .tree li button.chk.checkbox_true_part_focus {
            background: url("zTreeStyle/img/checkbox.png") no-repeat scroll 0 -36px transparent;
        }

        #scopeDiv {
            height: 32px;
            padding: 0px;
            margin-top: 0px;
            margin-left: 5px;
            float: left;
        }

        #scopeDiv ul {
            list-style: none;
        }

        #scopeDiv ul li {
            width: 64px;
            height: 27px;
            text-align: center;
            line-height: 26px;
            background: url(img/ss.gif) no-repeat left 1px;
            border: 0 none;
        }
    </style>
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="js/jquery.lrTool.js"></script>
    <script type="text/javascript" src="js/clc/comboTool.js"></script>
    <script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>
    <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
    <script type="text/javascript" src="js/dept/verify.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid_excell_combo.js"></script>
    <script type="text/javascript" src="js/gl/util.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="jsfile.htm?method=dict&nekey=6005"></script>
    <script type="text/javascript">
        var save_num = ${save_num};
        var zTree;
        var treeNodes;
        var dept_combo;
        //树的参数
        var setting = {
            isSimpleData: true,
            treeNodeKey: "id",
            treeNodeParentKey: "pId",
            checkable: true,
            checkType: {"Y": "ps", "N": "ps"},
            showLine: true,
            expandSpeed: "fast",//展开速度
            callback: {
                click: zTreeOnClick //点击事件
            },
            fontCss: getFontCss
        };

        function getFontCss(treeId, treeNode) {
            return (!!treeNode.highlight) ? {color: "#A60000", "font-weight": "bold"} : {
                color: "#333",
                "font-weight": "normal"
            };
        }

        var setting0 = {
            isSimpleData: true,
            treeNodeKey: "id",
            treeNodeParentKey: "pId",
            checkable: true,
            checkType: {"Y": "ps", "N": "ps"},
            showLine: true,
            expandSpeed: "fast"//展开速度
        };

        $(function () {
            //传过来的参数值如果是“1”，则为护士页面，隐藏操作按钮。
            if ($("#opentype").val() == "1") {
                $("#button1").hide();
            }
            $('#dicKey').bind('keypress', function (event) {
                if (event.keyCode == "13") {
                    search_ztree('menuTree', 'dicKey');
                }
            });
            $.ajax({
                async: false,   //是否异步
                cache: false,   //是否使用缓存
                type: 'post',   //请求方式,post
                dataType: "json",   //数据传输格式
                data: "checkedScope=${checkedScope}&operationType=" + $('#operationType').val() + "&opentype=" + $("#opentype").val(),  //url传数据
                url: "phyexam/selectedComTree.htm",   //请求链接
                error: function () {
                    alert('fail');
                },
                success: function (data) {
                    treeNodes = data;
                }
            });
            var groupid = $('#groupid').val();
            if ($('#operationType').val() == 'introduce') {
                $.ajax({
                    cache: false,
                    async: false,
                    type: 'get',
                    dataType: 'json',
                    url: "phyexam/getWSJItems.htm?groupid=" + groupid,
                    success: function (reply) {
                        mygrid.clearAll();
                        for (var i = 0; i < reply.length; i++) {
                            mygrid.addRow(reply[i].comid, [reply[i].comname, '']);
                        }
                    }
                });
                dept_combo = mygrid.getColumnCombo(1);
                dept_combo.setSize(mygrid.getColWidth(1));
                dept_combo.readonly(true);
                $.ajax({
                    cache: false,
                    async: false,
                    type: 'get',
                    dataType: 'json',
                    url: "phyexam/getDept.htm?groupid=" + groupid,
                    success: function (reply) {
                        for (var i = 0; i < reply.length; i++) {
                            dept_combo.addOption(reply[i].deptcode, reply[i].excdeptname);
                        }
                    }
                });
                zTree = $("#menuTree").zTree(setting0, treeNodes);//前台树的位置
                $('#tree_div').attr("disabled", "true");
            } else {
                zTree = $("#menuTree").zTree(setting, treeNodes);//前台树的位置
            }

            //加载优惠比例combo
            //for(var i=0;i<dicts6005.length;i++){
            //	yhbl.addOption(dicts6005[i].nevalue+'%',dicts6005[i].contents+'%');
            //}
            //yhbl.selectOption(0,true,true);
            //yhbl.selectOption(yhbl.getIndexByValue('${group.yhbl}'),true,true);  //选中 一个option
        });
        //新增、保存
        function doSave() {
            var nodes = zTree.getCheckedNodes();
            var sum = 0;
            for (var i = 0; i < nodes.length; i++) {
                var cost = parseInt(nodes[i].cost);
                sum = sum + cost;
                //alert(cost);
            }

            $("#cost").val(sum);
            //必填验证
            if (checkNull("groupname", "套餐名称")) {
                return;
            }
            //var yhbl_v = yhbl.getComboText();
            ///if(yhbl_v=='' || yhbl_v==null){
            //	alert('优惠比例不能为空');
            ///	return;
            //}
            //if(checkNull("cost","费用")){
            //	return;
            //}
            //长度验证
            if (countLen($("#groupname").val()) > 50) {
                alert("【套餐名称】输入太长！");
                return;
            }
            if (countLen($("#comments").val()) > 200) {
                alert("【备注】输入太长！");
                return;
            }
            //其他验证
            if ($("#cost").val() != "" && checkDouble($("#cost").val(), "费用", 2)) {
                return;
            }
            if ($("#sprice").val() != "" && checkDouble($("#sprice").val(), "售价", 2)) {
                return;
            }

            if (doCheckRepeat()) {
                doSetChangeVal();
                showMsg("数据保存中...");
                $.ajax({
                    cache: false,   //是否使用缓存
                    url: "phyexam/itemsGroupSave.htm",
                    async: false,   //是否异步，false为同步
                    type: "post",
                    data: $('#pexam_form').serialize(),
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
                            $("#operationType").val("modify");//修改标志为：修改
                            $("#old_groupname").val($("#groupname").val());//更新项目名称
                            $("#selfAddButton").css("display", "inline");//显示新增按钮
                            doRefreshCheckOld();
                            save_num++;
                        }
                    }
                });
            }
        }
        function doIntroduce() {
            var rownum = mygrid.getRowsNum();
            for (var i = 0; i < rownum; i++) {
                if (mygrid.cells2(i, 1).getValue() == '') {
                    alert('请选择执行科室');
                    return;
                }
            }
            if (!window.confirm("引入卫生局套餐将有可能生成同名项目，是否确定？")) {
                return;
            }
            var groupid = $('#groupid').val();
            var arr = [];
            for (var j = 0; j < mygrid.getRowsNum(); j++) {
                arr.push({
                    comid: mygrid.getRowId(j),
                    excdept: mygrid.cells2(j, 1).getValue(),
                    excdeptname: mygrid.cells2(j, 1).getText()
                });
            }
            $.ajax({
                cache: false,
                async: false,
                url: "phyexam/itemsGroupIntroduce.htm",
                type: 'post',
                data: "groupid=" + groupid + "&arr=" + toJSON(arr),
                error: function () {
                    alert("ajax请求失败");
                    closeMsg();
                },
                success: function (reply) {
                    if (reply == "fail") {
                        parent.doClose();
                        alert("引入失败");
                    } else if (reply == 'success') {
                        parent.doClose();
                        alert("引入成功");
                    } else {
                        parent.doClose();
                        alert("系统内部出错！");
                    }
                }
            });
        }

        //验证重复
        function doCheckRepeat() {
            var groupname = $("#groupname").val();
            var old_groupname = $("#old_groupname").val();
            var reVal = true;
            if (groupname != "" && groupname != old_groupname) {
                $.ajax({
                    cache: false,   //是否使用缓存
                    url: "phyexam/itemsGroupCheckRepeat.htm",
                    async: false,   //是否异步，false为同步
                    type: "post",
                    data: "groupname=" + groupname,
                    error: function () {
                        alert("ajax请求失败");
                    },
                    success: function (reply) {
                        if (reply == "Y") {
                            alert("已经存在相同的【套餐名称】！");
                            reVal = false;
                        } else if (reply == "N") {
                            reVal = true;
                        }
                    }
                });
            }
            return reVal;
        }

        //赋值变更了的选择树
        function doSetChangeVal() {
            var changeNodes = zTree.getChangeCheckedNodes();
            var len = changeNodes.length;
            var addScope = "";
            var removeScope = "";
            if (len >= 1) {
                for (var i = 0; i < len; i++) {
                    if (changeNodes[i].level != 0) {//排除根节点
                        if (changeNodes[i].checked) {
                            addScope = addScope + changeNodes[i].id + ";";
                        } else {
                            removeScope = removeScope + changeNodes[i].id + ";";
                        }
                    }
                }
            }
            $("#addscopes").val(addScope);
            $("#removescopes").val(removeScope);
        }

        //刷新纪录的旧的选中的值
        function doRefreshCheckOld() {
            var changeNodes = zTree.getChangeCheckedNodes();
            var len = changeNodes.length;
            if (len >= 1) {
                for (var i = 0; i < len; i++) {
                    changeNodes[i].checkedOld = changeNodes[i].checked;
                }
            }
        }

        //转到新增页面
        function doSelfClear() {
            var url = "phyexam/itemsGroupAdd.htm?operationType=add&save_num=" + save_num + "&opentype=" + $("#opentype").val();
            window.location.replace(url);
        }
        function doClose() {
            if (save_num >= 1) {//保存次数大于1次
                parent.window.mywin.reloadGrid();
            }
            parent.$.unblockUI();
        }
        function initIteams() {
            var farmItem = $("#farmItem").val();
            if (farmItem != "") {
                $("#farmItemSelect option").each(function (ind, ele) {
                    if ($(this).val() == farmItem) {
                        $(this).attr("selected", "selected");
                    }
                });
            }
        }
        function zTreeOnClick(event, treeId, treeNode) {
            mygrid.clearAll();
            if (treeNode.level == 0) {

            } else {
                $.ajax({
                    cache: false,
                    async: false,
                    url: "phyexam/getInds.htm?comid=" + treeNode.id + "&operationType=" + $("#operationType").val(),
                    type: "get",
                    dataType: 'json',
                    error: function () {
                        alert("fail");
                    },
                    success: function (reply) {
                        for (var i = 0; i < reply.length; i++) {
                            mygrid.addRow(reply[i].indid, [reply[i].indname, reply[i].forsex, reply[i].resulttype, reply[i].sn]);
                        }
                    }
                });
            }
        }

        function search_ztree(treeId, searchConditionId) {
            var searchCondition = $('#' + searchConditionId).val();
            changeColor(searchConditionId);
        }
        function changeColor(searchConditionId) {
            updateNodes(false);
            if ($('#dicKey').val() != "") {
                updateNodes(false);
                var nodeList = zTree.getNodesByParamFuzzy("name", $('#dicKey').val());
                if (nodeList && nodeList.length > 0) {
                    updateNodes(true);
                }

            } else {
                updateNodes(false);
                //var nodes = zTree.getNodes();
                //zTree.removeNode(nodes[0]);
                //zTree = $("#menuTree").zTree(setting, treeNodes);
            }
            $('#' + searchConditionId).val('');
            $('#' + searchConditionId).focus();
            //var scrollHeight = 0;
            //var nodes = zTree.getNodes();
            //$('#tree_div').animate({scrollTop:scrollHeight},200);
        }
        //更新树节点  入参是一个布尔值
        function updateNodes(highlight) {
            var nodeList = zTree.getNodesByParamFuzzy("name", $('#dicKey').val());
            for (var i = 0; i < nodeList.length; i++) {
                nodeList[i].highlight = highlight;
                zTree.updateNode(nodeList[i]);
            }
        }
        //================================
        //以下代码 没用到。。
        //==============================

        function highlightAndExpand_ztree(treeId, highlightNodes, flag) {
            //var zTree = $.fn.zTree.getZzTree(treeId);
            //alert(highlightNodes.length);
            for (var i = 0; i < highlightNodes.length; i++) {
                alert(highlightNodes[i].highlight)
                //高亮显示节点，并展开
                zTree.selectNode(highlightNodes[i], true);


            }
            var zTree = zTree;
            //<1>. 先把全部节点更新为普通样式
            var treeNodes1 = zTree.transformToArray(zTree.getNodes());
            for (var i = 0; i < treeNodes1.length; i++) {
                treeNodes1[i].highlight = false;
                zTree.updateNode(treeNodes1[i]);
            }
            //alert(highlightNodes.length);
            //close_ztree(treeId);
            //return ;

            //<3>.把指定节点的样式更新为高亮显示，并展开
            if (highlightNodes != null) {
                for (var i = 0; i < highlightNodes.length; i++) {
                    alert(highlightNodes[i].highlight)
                    //高亮显示节点，并展开
                    highlightNodes[i].highlight = true;
                    zTree.updateNode(highlightNodes[i]);

                }
            }
        }

        /**
         * 递归得到指定节点的父节点的父节点....直到根节点
         */
        function getParentNodes_ztree(treeId, node) {
            if (node != null) {
                var treeObj = zTree;
                var parentNode = node.getParentNode();
                return getParentNodes_ztree(treeId, parentNode);
            } else {
                return node;
            }
        }

        /**
         * 设置树节点字体样式
         */
        function setFontCss_ztree(treeId, treeNode) {
            if (treeNode.id == 0) {
                //根节点
                return {color: "#333", "font-weight": "bold"};
            } else if (treeNode.isParent == false) {
                //叶子节点
                return (!!treeNode.highlight) ? {color: "#ff0000", "font-weight": "bold"} : {
                    color: "#660099",
                    "font-weight": "normal"
                };
            } else {
                //父节点
                return (!!treeNode.highlight) ? {color: "#ff0000", "font-weight": "bold"} : {
                    color: "#333",
                    "font-weight": "normal"
                };
            }
        }

        /**
         * 展开树
         * @param treeId
         */
        function expand_ztree(treeId) {
            var treeObj = $.fn.zTree.getZTreeObj(treeId);
            treeObj.expandAll(true);
        }

        /**
         * 收起树：只展开根节点下的一级节点
         * @param treeId
         */
        function close_ztree(treeId) {
            var treeObj = zTree;
            var nodes = treeObj.transformToArray(treeObj.getNodes());
            var nodeLength = nodes.length;
            for (var i = 0; i < nodeLength; i++) {
                if (nodes[i].id == '0') {
                    //根节点：展开
                    treeObj.expandNode(nodes[i], true, true, false);
                } else {
                    //非根节点：收起
                    treeObj.expandNode(nodes[i], false, true, false);
                }
            }
        }
    </script>
</head>

<body onload="initIteams()">
<form id="pexam_form" name="pexam_form" onsubmit="return false" style="margin: 0px;">
    <input id="operationType" name="operationType" type="hidden" value="${operationType}"/>
    <input id="hosnum" name="hosnum" type="hidden" value="${group.hosnum}"/>
    <input id="groupid" name="groupid" type="hidden" value="${group.groupid}"/>

    <input id="addscopes" name="addscopes" type="hidden" value=""/>
    <input id="removescopes" name="removescopes" type="hidden" value=""/>
    <input id="farmItem" name="farmItem" type="hidden" value="${group.farmitem}"/>
    <!-- 圆头******开始 -->
    <table width="98%" border="0" cellspacing="0" cellpadding="0"
           style="margin-top: 10px; ">
        <tr>
            <td width="10"><img src="img/new_yuan1.jpg"/></td>
            <td background="img/new_yuan2.jpg"></td>
            <td width="10"><img src="img/new_yuan3.jpg"/></td>
        </tr>
        <tr>
            <td background="img/new_yuan4.jpg">
            </td>
            <td>
                <!-- 圆头******结束 -->
                <table id="patient_info_tb" width="750" border="0"
                       cellspacing="0" cellpadding="0" style="display: block; border-collapse: collapse">

                    <tr>
                        <td height="28" width="80" class="tit">
                            套餐名称：
                        </td>
                        <td class="val" width="220">
                            <input id="groupname" name="groupname" type="text" maxlength="50"
                                   class="text_field_required" style="width:250px" value="${group.groupname}"/>
                            <input type="hidden" id="old_groupname" name="old_groupname" value="${group.groupname}"/>
                        </td>
                        <td height="28" width="60" class="tit">
                            费用：
                        </td>
                        <td class="val" width="80">
                            <input id="cost" name="cost" type="text" maxlength="15" onkeyup="inputDouble(this)"
                                   readonly="readonly"
                                   class="txt4 text_field" style="width:70px" value="${group.cost}"/>
                        </td>
                        <td height="28" width="80" class="tit">
                            取报告日：
                        </td>
                        <td class="val" width="170">
                            <input id="workday" name="workday" type="text" maxlength="15"
                                   class="txt4 text_field" style="width:70px" value="${group.workday }"/>
                        </td>

                        <!--
                        <td height="28" width="80" class="tit">
                            优惠比例：
                        </td>
                        <td class="val" width="170" >
                            <select id="yhblSelect" name="yhblSelect" style="width:155px"></select>
                            <script>
                              var yhbl=dhtmlXComboFromSelect("yhblSelect");
                              yhbl.readonly(true);
                            </script>
                        </td>
                         -->

                        <!--
								<td height="28" width="80" class="tit">
									社保项目：
								</td>
								<td class="val" width="170" >
									<select id="farmItemSelect" name="farmItemSelect" style="width:155px">
										<option></option>
										<c:forEach var="farmItems" items="${farmItems}">
											<option value="${farmItems.itemcode }">${farmItems.itemname }</option>
										</c:forEach>
									</select>
								</td>
								 -->
                    </tr>
                    <tr>
                        <td height="28" width="80" class="tit">
                            售价：
                        </td>
                        <td class="val" width="170">
                            <input id="sprice" name="sprice" type="text" maxlength="15"
                                   class="txt4 text_field" style="width:70px" value="${group.sprice }"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="tit">
                            备注：
                        </td>
                        <td colspan="5" class="val">
									<textarea id="comments" name="comments" class="txt6 text_field"
                                              style="width: 640px; margin-top: 3px;">${group.comments }</textarea>
                        </td>
                    </tr>

                    <tr>
                        <td height="28" class="tit">
                            体检项目：
                        </td>
                        <td colspan="2" class="val">

                            <div id="tree_div" style="OVERFLOW-y: auto; overflow-x: hidden;width: 302px; height: 320px;
										 	margin: 8px 0px 0px 4px; border: 1px solid #99b4bf; ">
                                <div style="text-align:left;margin:5px;height: 15px;">按名字过滤：
                                    <input type="text" id="dicKey" class="txt4 text_field"/>
                                    <button type="button" onclick="search_ztree('menuTree', 'dicKey')">搜索</button>
                                </div>
                                <ul id="menuTree" class="tree"></ul>

                            </div>
                            <div style="position:fixed;right:414px;bottom: 95px;   border solid 1px #ccc;cursor:pointer;font-family:微软雅黑;"
                                 onclick="$('#tree_div').animate({scrollTop:0},200);"> 返回顶部
                            </div>
                        </td>
                        <td colspan="3">
                            <div id='gridbox'
                                 style="width: 360px;height: 320px; margin: 8px 0px 0px 4px; border: 1px solid #99b4bf; ">
                            </div>
                            <c:if test="${operationType != 'introduce'}">
                                <script>
                                    var mygrid = new dhtmlXGridObject('gridbox');
                                    mygrid.enableAutoWidth(true);
                                    mygrid.setImagePath("imgs/");
                                    mygrid.setSkin("dhx_custom");
                                    mygrid.setInitWidths("*,60,60,60");
                                    mygrid.setHeader("指标名称,适用性别,结果类型,显示顺序");
                                    mygrid.setColTypes("ro,ro,ro,ed");
                                    mygrid.setColAlign("left,center,center,right");
                                    mygrid.attachEvent("onEditCell", function (stage, rId, cInd, nValue, oValue) {
                                        var comid = zTree.getSelectedNode().id;
                                        var indid = mygrid.getSelectedRowId();
                                        var sn = mygrid.cellById(indid, 3).getValue();
                                        if (checkNum(sn, '显示顺序')) {
                                            return;
                                        }
                                        if (stage == 2 && cInd == 3) {
                                            if (nValue != '' && nValue != oValue && nValue != '') {
                                                $.ajax({
                                                    cache: false,
                                                    async: false,
                                                    url: "phyexam/saveIndSN.htm?sn=" + sn + "&comid=" + comid + "&indid=" + indid,
                                                    type: 'get',
                                                    error: function () {
                                                        return false;
                                                    },
                                                    success: function (reply) {
                                                        if (reply != 'success') {
                                                            return false;
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        return true;
                                    });
                                    mygrid.init();
                                </script>
                            </c:if>
                            <c:if test="${operationType == 'introduce'}">
                                <script>
                                    var mygrid = new dhtmlXGridObject('gridbox');
                                    mygrid.enableAutoWidth(true);
                                    mygrid.setImagePath("imgs/");
                                    mygrid.setSkin("dhx_custom");
                                    mygrid.setInitWidths("140,*");
                                    mygrid.setHeader("组合项目,执行科室");
                                    mygrid.setColTypes("ro,combo");
                                    mygrid.setColAlign("left,left");
                                    mygrid.init();
                                </script>
                            </c:if>
                        </td>
                    </tr>
                </table>
                <!-- 圆尾******开始 -->
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
    <!-- 圆尾******结束 -->

    <div style="margin-top: 16px;">
        <center id="card_manager_btn" style="display: block;">
            <!--<c:choose>
						<c:when test="${operationType == 'add'}">
							<button id="selfAddButton" type="button" class="btn"
								onclick="doSelfClear()" style="display: none;">
								新增
								<font color="red">(F1)</font>
							</button>
						</c:when>
						<c:when test="${operationType == 'modify'}">
							<button id="selfAddButton" type="button" class="btn"
								onclick="doSelfClear()" style="display: inline;">
								新增
								<font color="red">(F1)</font>
							</button>
						</c:when>
					</c:choose>-->
            <c:choose>
                <c:when test="${operationType == 'introduce'}">
                    <button id="selfAddButton" type="button" class="btn"
                            onclick="doIntroduce()" style="display: inline;">
                        引入
                        <font color="red">(F1)</font>
                    </button>
                </c:when>
                <c:when test="${operationType != 'introduce'}">
                    <button id="button1" type="button" class="btn" onclick="doSave()">
                        保存
                        <font color="red">(F2)</font>
                    </button>
                </c:when>
            </c:choose>
            <button id="button2" type="button" class="btn" onclick="doClose()">
                关闭
                <font color="red">(Esc)</font>
            </button>
        </center>
    </div>
</form>
<input type="hidden" id="opentype" value="${opentype}"/>
</body>
</html>