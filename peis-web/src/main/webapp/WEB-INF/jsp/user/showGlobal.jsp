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
    <title>用户维护</title>
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

        /*.tree li button.ico_docu{ background:url(zTreeStyle/img/page.gif);}*/
        /*.tree li a.curSelectedNode button.ico_docu{background:url(zTreeStyle/img/page.gif);}*/
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
    <script type="text/javascript">
        var hosnum = '';
        var nodecode = '';
        //var deptcode=${nodecode};
        var zTreeObj;
        function reloadTree() {
            var setting1 = {
                showLine: true,
                expandSpeed: "fast",
                nameCol: "name",
                treeNodeKey: "id",
                treeNodeParentKey: "pid",
                isSimpleData: true
            };

            $.ajax({
                async: false,
                type: "GET",
                url: "user/loadHosZtree.htm?time=" + new Date().getMilliseconds(),
                dataType: "json",
                error: function () {
                    alert("服务器内部错误！");
                },
                success: function (data) {
                    if (data.status) {
                        var zNodes1 = clone(eval('(' + data.value + ')'));//clone(zNodes)
                        zTreeObj = $("#hosTree").zTree(setting1, zNodes1);
                    } else {
                        alert(data.message);
                    }
                }
            });
        }

        $(document).ready(function () {
            reloadTree();
            //window.setTimeout("loadTree(true)", 50);

            $("#iframe_body").css("height", parent.$("#dhxMainCont").height());
            $("#hos_div").css("height", parent.$("#dhxMainCont").height() - 8);
            $("#tree_div").css("height", parent.$("#dhxMainCont").height() - 8);
            $("#iframe_right").css("height", parent.$("#dhxMainCont").height() - 6);
        });

        //树的参数
        var setting = {
            isSimpleData: true,
            treeNodeKey: "id",
            treeNodeParentKey: "pId",
            showLine: true,
            expandSpeed: "fast",//展开速度
            //fontCss: setFontCss,
            callback: {
                click: zTreeOnClick //点击事件
            }
        };
        var zTree;
        var treeNodes;

        function loadTree(hosnum_, nodecode_) {
            if (hosnum_ != null) {
                hosnum = hosnum_;
            }
            if (nodecode_ != null) {
                nodecode = nodecode_;
            }
            $.ajax({
                async: false,   //是否异步
                cache: false,   //是否使用缓存
                type: 'post',   //请求方式,post
                dataType: "json",   //数据传输格式
                data: "hosnum=" + hosnum + "&hoscode=" + nodecode, // url传数据
                url: "user/tree.htm",   //请求链接
                error: function () {
                    alert('服务器内部错误！');
                },
                success: function (data) {
                    treeNodes = data;
                }
            });
            zTree = $("#menuTree").zTree(setting, treeNodes);   //前台树的位置
            document.getElementById("iframe_right").src = "";
            //移除 村卫生室和社区服务站 2个节点。 --lsp
            var node01 = zTree.getNodeByParam("id", '03');
            var node02 = zTree.getNodeByParam("id", '04');
            zTree.removeNode(node01);
            zTree.removeNode(node02);
        }

        //树点击方法
        function zTreeOnClick(event, treeId, treeNode) {
            if (treeNode.treeType) {
                var type = treeNode.treeType;
                var url = "";
                if (type == "ks") {//科室
                    if (treeNode.isLast == "N") {//科室中间级别
                        if (treeNode.topShow) {//院区一级，没有科室代码，查找上级科室是空的
                            url = "user/user_list.htm?hosnum=" + hosnum + "&nodecode=" + treeNode.yqId + "&type=ks&deptcode=&islast=N";
                        } else {
                            url = "user/user_list.htm?hosnum=" + hosnum + "&nodecode=" + treeNode.yqId + "&type=ks&deptcode=" + treeNode.id;
                        }
                        document.getElementById("iframe_right").src = url;
                    }
                    else if (treeNode.isLast == "Y") {//科室最后一级，显示用户信息
                        url = "user/user_list.htm?hosnum=" + hosnum + "&nodecode=" + treeNode.yqId + "&type=ks&deptcode=" + treeNode.id;
                        document.getElementById("iframe_right").src = url;
                    }
                } else if (type == "bq") {//病区
                    if (treeNode.isLast == "N") {//科室中间级别
                        if (treeNode.topShow) {//院区一级，没有科室代码，查找上级科室是空的
                            url = "user/user_list.htm?hosnum=" + hosnum + "&nodecode=" + treeNode.yqId + "&type=bq&deptcode=&islast=N";
                        } else {
                            url = "user/user_list.htm?hosnum=" + hosnum + "&nodecode=" + treeNode.yqId + "&type=bq&deptcode=" + treeNode.id;
                        }
                        document.getElementById("iframe_right").src = url;
                    } else if (treeNode.isLast == "Y") {//病区最后一级，显示用户信息
                        url = "user/user_list.htm?hosnum=" + hosnum + "&nodecode=" + treeNode.yqId + "&type=bq&deptcode=" + treeNode.id;
                        document.getElementById("iframe_right").src = url;
                    }
                }
            } else {
                if (treeNode.open) {
                    zTree.expandNode(treeNode, false, false);//收缩
                } else {
                    zTree.expandNode(treeNode, true, false);//展开
                }
            }
        }

        function doChangeSelectNode(nodeId) {
            zTree.refresh();
            var curNode = zTree.getNodeByParam("id", nodeId);
            zTree.selectNode(curNode);
            var url = "user/user_list.htm?hosnum=" + hosnum + "&nodecode=" + curNode.yqId + "&type=" + curNode.treeType + "&deptcode=" + curNode.id;
            document.getElementById("iframe_right").src = url;
        }
    </script>
</head>
<body>
<table id="iframe_body" width="100%" cellpadding="0" border="0" cellpadding="0">
    <tr>
        <td width="200" valign="top">
            <div id="hos_div" style="OVERFLOW-y:auto;border:1px solid #93AFBA;width: 195px;">
                <ul id="hosTree" class="tree"></ul>
            </div>
        </td>
        <td id="left_area" width="200" valign="top">
            <div id="tree_div" style="OVERFLOW-y:auto;border:1px solid #93AFBA;width: 195px;">
                <ul id="menuTree" class="tree"></ul>
            </div>
        </td>
        <td id="right_area" valign="top">
            <iframe id="iframe_right" style="width: 100%; margin: 0px 0px 0px 0px; padding: 0px 0px 0px 0px;"
                    scrolling="no" frameborder="0"></iframe>
        </td>
    </tr>
</table>
</body>
</html>
