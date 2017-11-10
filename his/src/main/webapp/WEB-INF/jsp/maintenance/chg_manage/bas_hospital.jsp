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
    <title>卫生院</title>

    <link href="css/top.css" rel="stylesheet" type="text/css"/>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css"/>
    <link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeStyle.css" type="text/css"/>
    <link rel="stylesheet" href="zTreeStyle/zTreeIcons.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="css/dictionary.css"/>
    <style type="text/css">
        div.gridbox_dhx_custom table.hdr td {
            font-family: 微软雅黑;
            font-size: 12px;
            font-weight: bold;
            vertical-align: top;
        }

        .tree li a.curSelectedNode button.bq_leaf {
            background: url(zTreeStyle/img/page.gif);
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
    <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
    <script type="text/javascript" src="dhtmlxGrid/codebase/dhtmlxcommon.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="dhtmlxGrid/codebase/ext/dhtmlxgrid_filter.js"></script>
    <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
    <script type="text/javascript" src="js/demoTools.js"></script>
    <script type="text/javascript" src="js/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>

    <script type="text/javascript">
        var maintain_grid;   //维护GRID  通用
        var zTree;
        var treeNodes;
        $(function () {
            //高度 自适应  代码
            $("#tree_div").css("height", parent.$("#dhxMainCont").height() - 10);
            $("#maintain_grid").css("height", $("#tree_div").height() - 71);
            //$("#maintain_grid").css("height",parent.$("#dhxMainCont").height() -6);
            //1.加载树
            loadTree();
            //2.加载根节点数据
            loadByNodeId("0000");   //基础医院中root是0000
            //3.展开root
            var root = zTree.getNodeByParam("id", "0000");
            zTree.expandNode(root, true, false);
            zTree.selectNode(root);
        });
        //计数器
        function setCounter() {
            var span = document.getElementById("recfound");
            span.style.color = "";
            span.innerHTML = maintain_grid.getRowsNum();
        }
        //加载显示数据,通过itemid 作为父点
        function loadByNodeId(id) {
            maintain_grid.clearAndLoad("maintenance/chg_manage/loadBasHospitals.htm?supunit=" + id + "&t=" + Math.random());
        }
        //树的参数
        var setting = {
            isSimpleData: true,
            treeNodeKey: "id",
            treeNodeParentKey: "pid",
            showLine: true,
            expandSpeed: "fast",//展开速度
            callback: {
                click: zTreeOnClick //点击事件
            }
        };

        /*------------- 加载树-----------*/
        function loadTree() {
            $.ajax({
                async: false, cache: false,
                dataType: "json",
                url: "maintenance/chg_manage/getBasHospitalsTree.htm",
                error: function () {
                    alert('fail');
                },
                success: function (data) {
                    treeNodes = data;
                }
            });
            zTree = $("#maintainTree").zTree(setting, treeNodes);
        }
        //树点击方法
        function zTreeOnClick(event, treeId, treeNode) {
            loadByNodeId(treeNode.id);
            return false;
        }

        /*----选择删除------BEGIN----------------------------*/
        function doDelete() {
            var treeNode = zTree.getSelectedNode();
            var ids_all = maintain_grid.getCheckedRows(0);
            if (ids_all == "") {
                alert("没有选择项！");
                return false;
            }
            if (window.confirm('确定删除？')) {
                var id_arr = new Array();
                id_arr = ids_all.split(",");
                delFalg = 0;
                for (i = 0; i < id_arr.length; i++) {
                    $.ajax({
                        async: false, cache: false,
                        url: "maintenance/chg_manage/delBasHospitals.htm",
                        data: "hosnumNodeCode=" + id_arr[i] + "&t" + Math.random(),
                        success: function (msg) {
                            if ("hasChildren" == msg) {  //存在子医院
                                delFalg++;
                            } else if ("success" == msg) {
                                maintain_grid.deleteRow(id_arr[i]);
                                //var treeNodeT =	zTree.getNodeByParam("id",id_arr[i].split(";")[0]); //同时删除树节点
                                //zTree.removeNode(treeNodeT);
                            } else {//OH my god , what's happened?
                            }
                        },
                        error: function () {
                            alert("fail.");
                            return false;
                        }
                    });
                }
                if (delFalg == 0) {

                    loadTree();
                    var treeNode = zTree.getNodeByParam("id", treeNode.id);
                    zTree.expandNode(treeNode, true, false, true);
                    zTree.selectNode(treeNode);
                    window.setTimeout(function () {
                        alert("删除成功！");
                    }, 100);
                }
                else {
                    loadTree();
                    var treeNode = zTree.getNodeByParam("id", treeNode.id);
                    zTree.expandNode(treeNode, true, false, true);
                    zTree.selectNode(treeNode);
                    window.setTimeout(function () {
                        alert(delFalg + " 个卫生站存在子站，请先删除子站！");
                    }, 100);
                }
            }
        }
        /*-----选择删除----END------------------------------*/
        /*------搜索----Begin-------------------------------*/
        function doSearch() {
            var searchContent = $.trim($('#search').val());
            maintain_grid.filterBy(2, searchContent);
        }
        /*------搜索----END-----------------------------*/
        /*-----添加事件----BEGIN-----------------------------*/
        function doAdd() {
            var urlStr = "";
            var treeNode = zTree.getSelectedNode();
            if (treeNode == null) {
                alert("请选择父节点!");
                return false;
            }
            urlStr = "maintenance/chg_manage/basHospitalsDetail.htm?t=" + Math.random()
                + "&parentname=" + encodeURI(encodeURI(treeNode.name))
                + "&supunit=" + treeNode.id
                + "&showtype=1";  //1:是添加新
            parent.window.openMyWin(window, "新卫生站", 710, 400, urlStr);
        }
        /*-----添加事件---END-----------------------------*/
        /*-----双击事件-----------------------------------*/
        function onRowDblClicked(rId) {
            var urlStr = "";
            var treeNode = zTree.getSelectedNode();
            urlStr = "maintenance/chg_manage/basHospitalsDetail.htm?t=" + Math.random()
                + "&hosnumNodeCode=" + rId
                + "&supunit=" + treeNode.id
                + "&parentname=" + encodeURI(encodeURI(treeNode.name))
                + "&showtype=0";  //0是查看详细信息
            parent.window.openMyWin(window, "卫生站信息", 710, 400, urlStr);
        }

        /*----添加树节点--------------------------------*/
        function addTreeNode(node, isleaf) {
            if (isleaf == "N") {
                var partentNode = zTree.getNodeByParam("id", node.pid);
                zTree.addNodes(partentNode, node);
            }
            loadByNodeId(node.pid);
        }
        /*----更新成功 更新GRID---更新TREE-----------------------------*/
        function updateThisGrid(node) {
            loadByNodeId(node.pid);
            loadTree();
            var partentNode = zTree.getNodeByParam("id", node.pid);
            zTree.expandNode(partentNode, true, false, true);
            zTree.selectNode(partentNode);
        }
    </script>
</head>
<body>
<table width="100%" cellpadding="0" border="0" cellpadding="0">
    <tr>
        <td id="left_area" width="200" valign="top">
            <div id="tree_div" style="OVERFLOW-y:auto;OVERFLOW-x:auto;border:1px solid #93AFBA;width: 195px;">
                <ul id="maintainTree" class="tree"></ul>
            </div>
        </td>
        <td valign="top">
            <!-- 1 -->
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="font3" style="font-size: 16px;" width="70">
                        医院名称:
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
                                <li style="cursor: pointer;" onclick="doAdd()">
                                    新增
                                </li>
                                <li style="cursor: pointer;" onclick="doDelete()">
                                    删除
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
                        <img style="float: left;" src="img/new_tp1.jpg"/>
                        <div style="float: left;margin-top: -2px;">
											<span class="font3">
												<span>卫生站点</span>
												<span>（总计:<span id="recfound">0</span>个）</span>
											</span>
                        </div>
                    </td>
                    <td width="10"><img src="img/new_yuan3.jpg"/></td>
                </tr>
                <tr>
                    <td background="img/new_yuan4.jpg">
                        &nbsp;
                    </td>
                    <td>
                        <!-- grid -->
                        <div id="maintain_grid" style="width:100%;background-color: white; height: 300px;"></div>
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
<script type="text/javascript">
    maintain_grid = new dhtmlXGridObject('maintain_grid');
    maintain_grid.enableAutoWidth(true);
    maintain_grid.setImagePath("imgs/");
    maintain_grid.setSkin("dhx_custom");
    maintain_grid.setInitWidths("40,60,200,60,80,*");
    maintain_grid.setHeader("选择,医院编码,名称,类型,电话,地址");
    maintain_grid.setColTypes("ch,ro,ro,ro,ro,ro");
    maintain_grid.setColAlign("center,center,left,center,center,left");
    maintain_grid.attachEvent("onRowDblClicked", onRowDblClicked);
    maintain_grid.attachEvent("onXLE", setCounter);
    maintain_grid.init();
</script>
</body>

</html>
