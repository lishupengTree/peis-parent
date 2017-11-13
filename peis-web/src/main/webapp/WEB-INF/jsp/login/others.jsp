<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<base href="<%=basePath%>"/>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="expires" content="0" />
		<title>所有功能</title>
		<link href="css/register.css" rel="stylesheet" type="text/css"/>
		<link href="css/top.css" rel="stylesheet" type="text/css"/>
		<link rel="stylesheet" type="text/css" href="dhtmlxTabbar/codebase/dhtmlxtabbar.css"/>
		<link rel="stylesheet" type="text/css" href="dhtmlxGrid/codebase/dhtmlxgrid.css"/>
		<link rel="stylesheet" type="text/css" href="css/dhtmlxgrid_dhx_custom.css"/>
		<link rel="stylesheet" href="zTreeStyle/zTreeStyle.css" type="text/css"/>
		<link rel="stylesheet" href="zTreeStyle/zTreeIcons.css" type="text/css"/>
		<style>
		div.gridbox table.hdr td{padding-top: 6px;padding-bottom: 6px}
		div.gridbox .objbox{scrollbar-face-color: #E3EBF8; scrollbar-shadow-color: #c6d8f0; scrollbar-highlight-color: #FFFFFF; scrollbar-3dlight-color: #E3EBF8; scrollbar-darkshadow-color:#d8e4f3; scrollbar-track-color: #FFFFFF; scrollbar-arrow-color: #9bb8de;}
		.btn01{
			width:64px;
			height:26px;
			background:url(img/ss.gif) no-repeat;
			border:0px;
			font-size:13px;
			color:#6ba3b6;
			font-family:微软雅黑;
			font-weight:bold;
		}
		.btn01:focus{
			background:url(img/ssfocus.gif) no-repeat;
		}
		</style>
        <script type="text/javascript" src="js/jquery-1.6.1.js"></script>
        <script type="text/javascript" src="js/jquery.ztree-2.6.js"></script>
        <script type="text/javascript" src="js/jquery.blockUI.js"></script>
		<script type="text/javascript" src="dhtmlxTabbar/codebase/dhtmlxcommon.js"></script>
		<script type="text/javascript" src="dhtmlxTabbar/codebase/dhtmlxtabbar.js"></script>
		<script type="text/javascript" src="js/dhtmlxgrid.js"></script>
		<script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
		<script type="text/javascript" src="js/demoTools.js"></script>
        <script type="text/javascript" src="js/clc/others.js"></script>
        <script type="text/javascript" src="js/clc/setwindow.js"></script>
        <script type="text/javascript" src="js/gl/util.js"></script>
		<script >
		var simpleNodes = eval('(' + '${others}' + ')');
		</script>
		</head>
	<body>
		<div class="top">
			<jsp:include page="../top.jsp"/>
		</div>
		<!-- to fix -->
		<!-- 库存预警标志 --><input type="hidden" id="isWarningSign" value="${param.isWarningSign }"/>
		<!-- 库存预警MID --><input type="hidden" id="warningMid" value="${param.warningMid }"/>
		<input type="hidden" id="validto_warning" value="${validto_warning }"/>
		<div id="middle_area">
			<table width="100%" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td id="left_area" width="230" valign="top" align="center">
						<div class="ysz1" style="color: #6ba3b6;" id="menuname">${login_menus[systemid].name}</div>
						<div id="left_area_tree" style="overflow-y:auto;margin-left: 5px;width: 220px;">
							<ul id="menuTree" class="tree"></ul>
						</div>
						
					</td>
					<!-- 1 -->
					<td id="middle_line" width="8px" style="background-image:url(img/ysz2.gif); background-repeat:repeat-y; background-position:center">
						<img src="img/ysz3.gif" />
					</td>
					<td id="right_area" style="padding: 1px;">
						<div id="tab_bar" style="height:100%;" valign="top"></div>
						<div id="mainArea" style="display:none;">
							<iframe id="main_iframe"  width="100%" height="600" src="" topmargin="0" leftmargin="0" 
							marginheight="0"  marginwidth="0" frameborder="no" style="overflow-y:auto;overflow-x:hidden;"
							></iframe>
						</div>
					</td>
				</tr>
			</table>
		</div>
		<div id="hidden_div" style="display: none;">
			<iframe id="hidden_iframe" width="100%" height="100%" src="" topmargin="0" leftmargin="0" 
				marginheight="0" scrolling="no" marginwidth="0" frameborder="no" ></iframe>
		</div>
		<input id="ortype" value="${login_hospital.orgtype}" type="hidden"/>
	</body>
</html>
