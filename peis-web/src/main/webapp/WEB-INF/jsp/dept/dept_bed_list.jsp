<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<base href="<%=basePath%>" />
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="expires" content="0" />
		<title>科室维护</title>
		<link href="css/register.css" rel="stylesheet" type="text/css" />
		<link href="css/top.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css" />
		<link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css" />
		<link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css" />
		<link rel="stylesheet" type="text/css" href="css/deptbtn.css" />
		<script type="text/javascript" src="js/dept/verify.js"></script>
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
	window.onresize = function(){
		$("#gridbox").css("width",parent.$("#iframe_right").width() - 35);
	}
  	function setWin()
	{
    	$("#gridbox").css("width",parent.$("#iframe_right").width() - 35);
    	$("#gridbox").css("height",parent.$("#iframe_right").height() - 73);
    }
//转到新增、修改操作页面
function showOperationPage(url){
	parent.parent.$("#hidden_div").css("width", "604px");
	parent.parent.$("#hidden_div").css("height", "280px");
	var top_ = (window.parent.parent.document.body.clientHeight - parent.parent.$("#hidden_div").height())/2;
	var left_ = (window.parent.parent.document.body.clientWidth - parent.parent.$("#hidden_div").width())/2;
	parent.parent.$("#hidden_iframe").attr("src",url);
	parent.parent.$.blockUI(
	{
		message: parent.parent.$("#hidden_div"),
		css:{width:'600px',
		top:top_,
		left:left_,
		border:'0px solid #aaa'},
		overlayCSS:{backgroundColor: '#CCCCCC'}
	});
}    
    		
//床位新增
function doBedAddRow(){
	var url = "dept/dept_bed_add.htm?operationType=add&defaultHos=${hosnum}&defaultDept=${wardno}&chgitem=none";
	showOperationPage(url);
}

//床位修改
function doBedModifyRow(){
    var url = "dept/dept_bed_add.htm?operationType=modify&operationId="+encodeURI(encodeURI(mygrid.getSelectedId(),"UTF-8"),"UTF_8")+"&chgitem="+mygrid.cells(mygrid.getSelectedId(),5).getValue()+"&random="+Math.random();
   showOperationPage(url);
}

//床位删除
function doBedDeleteRow(){
	var checkIds = mygrid.getCheckedRows(0);
	if(checkIds==""){
		alert('请先选择要删除的数据！');
		return;
	}
	if(window.confirm('你确定要删除这些数据？')){
		showMsg("数据删除中...");
		$.ajax({
			cache:false,   //是否使用缓存
            url: "dept/dept_bed_remove.htm", 
            async : true,   //是否异步，false为同步
            type: "get",
            data: "checkIds="+encodeURI(encodeURI(checkIds)),
            error: function(){
            	alert("ajax请求失败");
            	closeMsg();
            },
            success: function(reply){
            	if(reply == "success"){
	            	var strs= new Array(); //定义一数组
				    strs = checkIds.split(",");
					for(i=0;i<strs.length;i++){
						mygrid.deleteRow(strs[i]); 
			        }
			        closeMsg();
			        alert("删除成功");
		        }else if(reply == "fail"){
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
function reloadGrid(){
	mygrid.clearAndLoad("dept/dept_bed_list_load.htm?hosnum=${hosnum}&wardno=${wardno}");
}

//搜索当前数据
function doSearch(){
	mygrid.filterBy(3,$("#search").val());
	//mygrid.filterBy(4,$("#search1").val(), true);//true表示从当前已经过滤的结果集搜索
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

	$(document).bind("keydown",function(event){
		if(event.keyCode==F5_KEYCODE){
			event.preventDefault();
			event.keyCode=0;
			event.returnValue = false;
			doBedAddRow();
		}else if(event.keyCode==F6_KEYCODE){
			event.preventDefault();
			event.keyCode=0;
			event.returnValue = false;
			doBedDeleteRow();
		}
		//else if(event.keyCode==F7_KEYCODE){
		//	event.preventDefault();
		//	event.keyCode=0;
		//	event.returnValue = false;
		//	doBedModifyRow();
		//}
	});
	</script>
	<body onload="setWin();">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="font3" style="font-size: 16px;" width="85">
					床位搜索:
				</td>
				<td width="155">
					<input id="search" name="search" type="text" class="txt text_field"
						style="height: 22px; width: 150px;" />
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
							<li style="cursor: pointer;" onclick="doBedAddRow()">
								新增
								<span class="font2">(F5)</span>
							</li>
							<li style="cursor: pointer;" onclick="doBedDeleteRow()">
								删除
								<span class="font2">(F6)</span>
							</li>
<!--							<li style="cursor: pointer;" onclick="doBedModifyRow()">-->
<!--								修改-->
<!--								<span class="font2">(F7)</span>-->
<!--							</li>-->
						</ul>
					</div>
				</td>
			</tr>
		</table>

		<table width="500" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="10"><img src="img/new_yuan1.jpg" /></td>
				<td width="480" background="img/new_yuan2.jpg">
					<img src="img/new_tp1.jpg" style="float: left;" />
					<div style="float: left; margin-top: -2px;">
						<span class="font3"> <!--								<span id="cname">行政区划</span> <span id="nekey"> </span> (<span id="stdinfo"> </span> ) -->
							总计:<span id="recfound"></span> 条</span>
					</div>
				</td>
				<td width="10"><img src="img/new_yuan3.jpg" /></td>
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
						mygrid.setHeader("选择,科室代码,房间号,床位号,适用性别,对应收费项目");
						mygrid.setInitWidths("40,180,120,80,80,*");
						mygrid.setColTypes("ch,ro,ro,ro,ro,ro");
						mygrid.setColAlign("center,center,center,center,center,left")
						mygrid.load("dept/dept_bed_list_load.htm?hosnum=${hosnum}&wardno=${wardno}");
						mygrid.attachEvent("onRowDblClicked", doBedModifyRow);
						mygrid.init();
						
						mygrid.attachEvent("onXLE", setCounter);
					</script>
				</td>
				<td background="img/new_yuan5.jpg">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td><img src="img/new_yuan6.jpg" /></td>
				<td background="img/new_yuan7.jpg"></td>
				<td><img src="img/new_yuan8.jpg" /></td>
			</tr>
		</table>
	</body>
</html>
