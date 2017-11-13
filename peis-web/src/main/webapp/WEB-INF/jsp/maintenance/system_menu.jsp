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
    <script type="text/javascript" src="dhtmlxTabbar/codebase/dhtmlxtabbar.js"></script>
    <script type="text/javascript" src="js/util.js"></script>
    <script type="text/javascript" src="js/dhtmlxgrid.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
    <script type="text/javascript" src="js/demoTools.js"></script>
    <script type="text/javascript" src="js/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>


    <script type="text/javascript">
        var menu_grid;
        var zTree;
        var treeNodes;

        $(function () {
            //高度 自适应  代码
            $("#tree_div").css("height", parent.$("#dhxMainCont").height() - 10);
            $("#menu_grid").css("height", $("#tree_div").height() - 71);
            //加载树
            loadTree();

            //创建 grid
            menu_grid = new dhtmlXGridObject('menu_grid');
            menu_grid.enableAutoWidth(true);
            menu_grid.setImagePath("imgs/");
            menu_grid.setSkin("dhx_custom");
            menu_grid.setInitWidths("40,80,0,150,*,0,0,0,50,50,50");
            menu_grid.setHeader("&nbsp;" +
                ",编号,PID,名称,链接,图片链接,菜单类型,打开类型,索引,热键,打开");
            menu_grid.setColTypes("ch,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro");
            menu_grid.setColAlign("center,center,center,center,center,center,center,center,center,center,center");
            menu_grid.attachEvent("onRowDblClicked", onRowDblClicked);
            menu_grid.init();
            menu_grid.setColumnHidden(2, true);//隐藏后面的
            menu_grid.setColumnHidden(5, true);//隐藏后面的
            menu_grid.setColumnHidden(6, true);//隐藏后面的
            menu_grid.setColumnHidden(7, true);//隐藏后面的

            loadTreeById("root");
            //展开root
            var root = zTree.getNodeByParam("id", "root");
            zTree.expandNode(root, true, false);
            zTree.selectNode(root);
        });

        function checkAll() {//全选
            var chk0 = "dhtmlxGrid/codebase/imgs/item_chk0.gif";
            var chk1 = "dhtmlxGrid/codebase/imgs/item_chk1.gif";
            if ($('#all_img').attr('src') == chk0) {
                $('#all_img').attr('src', chk1);
                for (var i = 0; i < menu_grid.getRowsNum(); i++) {
                    menu_grid.cells2(i, 0).setValue(1);
                }
            } else {
                $('#all_img').attr('src', chk0);
                for (var i = 0; i < menu_grid.getRowsNum(); i++) {
                    menu_grid.cells2(i, 0).setValue(0);
                }
            }
        }

        //加载树,通过id
        function loadTreeById(id) {
            menu_grid.clearAndLoad("maintenance/load_menu.htm?nodeid=" + id);
        }
        //树的参数
        var setting = {
            isSimpleData: true,
            treeNodeKey: "id",
            treeNodeParentKey: "pid",
            showLine: true,
            expandSpeed: "fast",//展开速度
            //fontCss: setFontCss,
            callback: {
                click: zTreeOnClick //点击事件
            }
        };

        /*------------- 加载树-----------*/
        function loadTree() {
            $.ajax({
                async: false,   //是否异步
                cache: false,   //是否使用缓存
                type: 'get',   //请求方式,post
                dataType: "json",   //数据传输格式
                url: "maintenance/system_menu_tree.htm",   //请求链接
                error: function () {
                    alert('fail');
                },
                success: function (data) {
                    treeNodes = data;
                }
            });
            zTree = $("#menuTree").zTree(setting, treeNodes);   //前台树的位置
        }

        //树点击方法
        function zTreeOnClick(event, treeId, treeNode) {
            //alert(treeNode.id);
            loadTreeById(treeNode.id);
            return false;
        }

        /*----选择删除------BEGIN----------------------------*/
        function doDeptDeleteRow() {
            var ids_all = menu_grid.getCheckedRows(0);
            if (ids_all == "") {
                alert("没有选择项！");
                return false;
            }
            if (true) {
                var id_arr = new Array();
                id_arr = ids_all.split(",");

                delFalg = 0;
                for (i = 0; i < id_arr.length; i++) {
                    var tempDate = new Date();
                    var tempstatus = tempDate.getMilliseconds();

                    $.ajax({
                        type: "get",
                        async: false,   //是否异步
                        cache: false,
                        url: "maintenance/del_system_menu.htm",
                        data: "id=" + id_arr[i] + "&tempstatus" + tempstatus,
                        success: function (msg) {
                            if ("hasChildren" == msg) {  //存在子菜单
                                delFalg++;
                            } else {
                                menu_grid.deleteRow(id_arr[i]);
                                var treeNodeT = zTree.getNodeByParam("id", id_arr[i]); //同时删除树节点
                                zTree.removeNode(treeNodeT);
                            }
                        },
                        error: function () {
                            alert("fail.");
                            return false;
                        }
                    });
                }
                if (delFalg == 0) {

                } else {
                    alert(delFalg + " 个菜单记录存在子菜单，请先删除子菜单！");
                }
            }
        }
        /*-----选择删除----END------------------------------*/
        /*------模糊搜索----Begin---------------------------*/
        function doSearch() {
            var searchContent = $('#search').val();
            if (searchContent == "") {
                alert("查询值为空!");
                return false;
            }
            menu_grid.clearAndLoad("maintenance/system_menu_dim_search.htm?searchContent=" + encodeURI(encodeURI(searchContent)));
        }
        /*------模糊搜索----END-----------------------------*/
        /*-----添加事件----BEGIN-----------------------------*/
        function doDeptAddRow() {
            var treeNode = zTree.getSelectedNode();
            if (treeNode == null) {
                alert("请选择父亲节点!");
                return false;
            }

            var pid = treeNode.id;   //新增的父节点
            var tempId = "";         //生成的子节点
            if (pid != "root") {
                tempId = pid;
            }
            var thisNodes = zTree.getNodesByParam("pid", pid);
            var index_no = "0";
            var menu_type = "";

            var lastNode = thisNodes[thisNodes.length - 1];  //获取 最后一个node
            if (lastNode != null) { //子节点不为空
                index_no = parseInt(lastNode.index_no) + 1;
                menu_type = lastNode.menu_type; //菜单
            }
            var urlStr = "maintenance/system_menu_info.htm?showtype=1&pid=" + pid + "&menu_type=" + encodeURI(encodeURI(menu_type)) + "&index_no=" + index_no;
            parent.window.openMyWin(window, "添加新项目", 480, 300, urlStr);

        }
        /*-----添加事件---END-----------------------------*/
        /*-----双击事件-----------------------------------*/
        function onRowDblClicked() {
            var id = menu_grid.getSelectedRowId();
            var pid = menu_grid.cells(id, 2).getValue();
            var name = menu_grid.cells(id, 3).getValue();
            var url = menu_grid.cells(id, 4).getValue();
            var image = menu_grid.cells(id, 5).getValue();
            var menu_type = menu_grid.cells(id, 6).getValue();
            var open_type = menu_grid.cells(id, 7).getValue();
            var index_no = menu_grid.cells(id, 8).getValue();
            var hotkeys = menu_grid.cells(id, 9).getValue();
            var default_open = menu_grid.cells(id, 10).getValue();
            var urlStr = "maintenance/system_menu_info.htm?showtype=0&id=" + id + "&pid=" + pid;
            urlStr += "&name=" + encodeURI(encodeURI(name)) + "&url=" + url + "&image=" + image + "&menu_type=" + encodeURI(encodeURI(menu_type)) + "&open_type=" + open_type;
            urlStr += "&index_no=" + index_no + "&hotkeys=" + hotkeys + "&default_open=" + default_open;
            parent.window.openMyWin(window, "详细信息", 480, 300, urlStr);
        }

        /*----添加树节点--------------------------------*/
        function addTreeNode(pid, node) {
            var partentNode = zTree.getNodeByParam("id", pid);
            zTree.addNodes(partentNode, node);
            loadTreeById(pid);
        }
        /*----更新成功 更新GRID--------------------------------*/
        function updateThisGrid(node) {
            //更新树节点
            var thisNode = zTree.getNodeByParam("id", node.id);
            thisNode.name = node.name;
            thisNode.index_no = node.index_no;
            thisNode.menu_type = node.menu_type;
            zTree.updateNode(thisNode, true);
            loadTreeById(node.pid);
        }
    </script>
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
                    <td class="font3" style="font-size: 16px;" width="70">
                        模糊搜索:
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
                                <li style="cursor: pointer;" onclick="doDeptDeleteRow()">
                                    删除
                                </li>
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
</body>
</html>
