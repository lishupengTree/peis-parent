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
    <title>系统参数维护</title>
    <link href="css/register.css" rel="stylesheet" type="text/css"/>
    <link href="css/top.css" rel="stylesheet" type="text/css"/>
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
    <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
    <script type="text/javascript" src="js/demoTools.js"></script>
    <script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
    <script type="text/javascript" src="js/dhtmlxcombo.js"></script>
    <script type="text/javascript" src="dhtmlxGrid/sources/ext/dhtmlxgrid_srnd.js"></script>
    <script type="text/javascript" src="dhtmlxGrid/codebase/ext/dhtmlxgrid_filter.js"></script>
    <script type="text/javascript" src="js/window.js"></script>
    <script type="text/javascript" src="js/jquery.blockUI.js"></script>


    <script type="text/javascript">

        /*--BEGIN----设定全局变亮------------------*/
        var setting = { //树的参数
            isSimpleData: true,
            treeNodeKey: "id",
            icon: "folder_Open.gif",
            treeNodeParentKey: "pId",
            showLine: true,
            expandSpeed: "fast",//展开速度
            callback: {click: zTreeOnClick}
        }; //点击事件
        var zTree;      //树
        var treeNodes;	//树的节点
        var mygrid;     //显示数据用的GRID
        /*--END---------------------------------*/
        var thisScope;
        //Now begin.
        $(function () {

            //1.设置窗口大小，使之自适应
            $("#tree_div").css("height", parent.$("#dhxMainCont").height() - 10);
            $("#gridbox").css("height", $("#tree_div").height() - 71);
            //2.加载树
            loadTree();
            grid_load("", false);
            //选中root
            var root = zTree.getNodeByParam("id", 1);
            zTree.selectNode(root);
        });
        /*--BEGIN-----------function list-----------------*/
        //加载树
        function loadTree() {
            $.ajax({
                async: false, cache: false, type: 'get', dataType: "json", error: function () {
                    alert('fail');
                },
                url: "maintenance/chg_manage/load_parms_tree.htm",
                success: function (data) {
                    treeNodes = data;
                    zTree = $("#menuTree").zTree(setting, treeNodes);
                }
            });
        }
        //新增
        function doAdd() {
            parent.window.openMyWin(window, "系统参数信息", 595, 370, 'maintenance/chg_manage/bas_parms_detail.htm?showtype=1');
        }
        //计数
        function setCounter() {
            var span = document.getElementById("tatalcount");
            span.style.color = "";
            span.innerHTML = mygrid.getRowsNum();
        }
        //树点击方法--得到树的节点分类值
        function zTreeOnClick(event, treeId, treeNode) {
            var scope = treeNode.name;
            if (treeNode.isParent) {
                mygrid.clearAndLoad("maintenance/chg_manage/load_parms.htm?type=1&thisScope=" + encodeURI(encodeURI(thisScope)) + "&stamp=" + Math.random());
            }  //type=1:加载默认
            else {
                grid_load(scope, true);
            }//type=2:加载子项目
            var span = document.getElementById("topic");
            span.innerHTML = "【" + scope + "】";
        }
        //搜索
        function bind_parm_ss_click() {
            var parmname = $.trim($('#search').val());
            mygrid.filterBy(1, parmname);
            //I'm so sorry to note you！
            //mygrid.clearAll();
            //mygrid.load(encodeURI("maintenance/SearchParmsByParm.htm?stamp="+Math.random()+"&parmname="+parmname));
            //var span = document.getElementById("topic");
            //span.innerHTML = "综合查询参数名称中包含["+parmname+"]";
        }
        function doDelete() {
            var ids = mygrid.getCheckedRows(0);
            if (ids == "") {
                alert('请先选择要删除的数据！');
                return;
            }
            if (!window.confirm('你确定要删除这些数据？')) {
                return;
            }

            var idArr = new Array(); //定义一数组
            idArr = ids.split(",");
            var delFlag = 0;
            for (i = 0; i < idArr.length; i++) {
                $.ajax({
                    async: false,
                    error: function () {
                        alert("连接服务器中断！");
                        return;
                    },
                    url: "maintenance/chg_manage/del_bas_parms.htm",
                    data: "parmid=" + idArr[i] + "&stamp=" + Math.random(),
                    success: function (msg) {
                        if (msg == "success") {
                            mygrid.deleteRow(idArr[i]);
                            setCounter();
                            loadTree(thisScope);
                        } else if (msg == "unknow") {
                            delFlag++;
                        }
                    }
                });
            }
            if (delFlag == 0) {
                alert("删除成功！");
            }
            else {
                alert(delFlag + "条数据删除失败！");
            }
        }
        /*--END-------------------------------------------*/
        function grid_load(scope, type) {  //type: false - 所有  ; ture - 按scope
            if (type) {
                mygrid.clearAndLoad("maintenance/chg_manage/load_parms.htm?"
                    + "type=2&scope=" + encodeURI(encodeURI(scope))
                    + "&scope=" + encodeURI(encodeURI(scope))
                    + "&stamp=" + Math.random());
            } else {
                mygrid.clearAndLoad("maintenance/chg_manage/load_parms.htm?"
                    + "type=1&scope=" + encodeURI(encodeURI(scope))
                    + "&stamp=" + Math.random());
            }
        }

    </script>

</head>
<body>
<input type="hidden" id="thisScope" value="${login_menus[systemid].name}"/>
<table id="iframe_body" width="100%" cellpadding="0" border="0" cellpadding="0">
    <tr>
        <td id="left_area" width="200" valign="top">
            <div id="tree_div" style="OVERFLOW-y:auto;border:1px solid #93AFBA;width: 195px;">
                <ul id="menuTree" class="tree" style="widows: 195px;"></ul>
            </div>
        </td>
        <td id="right_area" valign="top">
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td width="70">
                        <span class="font3">名称搜索:</span>
                    </td>
                    <td width="155">
                        <input id="search" name="search" type="text" class="txt text_field"
                               style="width:150px; height:22px; vertical-align:middle;border:1px solid #93AFBA;line-height:17px;"/>
                    </td>
                    <td align="left">
                        <div class="middle4" style="padding: 0px;margin-top: 4px;">
                            <ul id="op_ul" style="float: left;">
                                <li style="cursor:pointer;float: left;" onclick="bind_parm_ss_click();">搜索</li>
                            </ul>
                        </div>
                    </td>
                    <td></td>
                    <td width="280">
                        <div id="but_d" class="middle4" style="padding: 0px;margin-top: 1px;">
                            <ul id="op_ul">
                                <li style="cursor:pointer; " onclick="doAdd();">新增</li>
                                <li style="cursor:pointer;" onclick="doDelete();">删除</li>
                            </ul>
                        </div>
                    </td>
                </tr>
            </table>

            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td width="10"><img src="img/new_yuan1.jpg"/></td>
                    <td width="100%" background="img/new_yuan2.jpg"><img src="img/new_tp1.jpg" style="float: left;"/>
                        <div style="float: left;margin-top: -2px;"><span class="font3"><span id="topic">【系统参数】</span>总计:<span
                                id="tatalcount"></span> 条 </span></div>
                    </td>
                    <td width="10"><img src="img/new_yuan3.jpg"/></td>
                </tr>
                <tr>
                    <td background="img/new_yuan4.jpg">&nbsp;</td>
                    <td>
                        <div id="gridbox" style="width:100%;background-color:white;"></div>
                        <script>
                            mygrid = new dhtmlXGridObject('gridbox');
                            mygrid.enableAutoWidth(true);
                            mygrid.setImagePath("imgs/");
                            mygrid.setSkin("dhx_custom");
                            mygrid.setHeader("选择,名称,值,作用范围,作用描述");
                            mygrid.setInitWidths("40,150,100,100,*");
                            mygrid.setColTypes("ch,ro,ro,ro,ro");
                            mygrid.setColAlign("center,left,center,left,left");
                            mygrid.attachEvent("onXLE", setCounter);
                            mygrid.enableSmartRendering(true, 200);
                            mygrid.preventIECaching(false);
                            mygrid.attachEvent("onRowDblClicked", function (rid) {
                                parent.window.openMyWin(window, "系统参数信息", 595, 370, "maintenance/chg_manage/bas_parms_detail.htm?showtype=0&permission=1&parmid=" + rid); //type=0：查看
                            });
                            mygrid.init();

                        </script>
                    </td>
                    <td background="img/new_yuan5.jpg">&nbsp;</td>
                </tr>
                <tr style="padding-bottom:">
                    <td><img src="img/new_yuan6.jpg"/></td>
                    <td background="img/new_yuan7.jpg">&nbsp;</td>
                    <td><img src="img/new_yuan8.jpg"/></td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
