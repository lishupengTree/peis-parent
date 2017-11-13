<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
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
		<title>科室</title>
		<link href="css/register.css" rel="stylesheet" type="text/css" />
		<link href="css/top.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="My97DatePicker/skin/WdatePicker.css"
			type="text/css" />
		<link rel="stylesheet" type="text/css"
			href="dhtmlxCombo/codebase/dhtmlxcombo.css" />
		<link rel="stylesheet" type="text/css" href="css/deptbtn.css" />
		<style type="text/css">
.box1 {
	width: 800px;
	border: 1px solid #b6cfd6;
	padding: 1px;
	margin: 0 auto;
}

.box2 {
	width: 784px;
	background: url(img/tckbg.gif) repeat-x;
	background-color: #d9eaee;
	padding: 8px;
}

.box3 {
	width: 784px;
	margin: 0 auto;
}

.box3 span {
	font-size: 13px;
	color: #6ba3b6;
	font-family: "微软雅黑";
	font-weight: bold;
	height: 24px;
	vertical-align: top;
}

.boxpad {
	float: right;
}

.box4 {
	width: 772px;
	border: 1px solid #b6cfd6;
	background-color: #fff;
	padding: 5px;
	font-size: 13px;
	font-family: "微软雅黑";
	line-height: 22px;
	clear: both;
}

.text_field {
	margin-left: 4px
}

.text_field_required {
	margin-left: 4px
}

#instype_required .dhx_combo_box {
	border: 1px solid red
}

#sex_required .dhx_combo_box {
	border: 1px solid red
}

.pdl {
	padding-left: 1px
}

.pd2 {
	padding-left: 2px
}

div.gridbox_dhx_custom table.hdr td {
	font-family: 微软雅黑;
	font-size: 12px;
	font-weight: bold;
	vertical-align: top;
}

div.gridbox table.obj.row20px tr td {
	
}

div #patient_info_tb .tit {
	text-align: right;
}

div #patient_info_tb .val {
	text-align: left;
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

div .radioDiv{
	border: 1px solid red; 
	width: 142px; margin-left: 4px;
}
div .radioDivNo{
	border: 0px; 
	width: 142px; margin-left: 4px;
}
</style>
		<script type="text/javascript" src="js/jquery-1.6.1.js"></script>
		<script type="text/javascript" src="js/jquery.lrTool.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/clc/comboTool.js"></script>
		<script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
		<script type="text/javascript"
			src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
		<script type="text/javascript"
			src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
		<script type="text/javascript"
			src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
		<script type="text/javascript" src='My97DatePicker/WdatePicker.js'></script>

		<script type="text/javascript" src="js/dhtmlxgrid.js"></script>
		<script type="text/javascript" src="js/dhtmlxgridcell.js"></script>
		<link href="css/register.css" rel="stylesheet" type="text/css" />
		<link href="css/top.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css"
			href="dhtmlxCombo/codebase/dhtmlxcombo.css" />
		<link rel="stylesheet" type="text/css"
			href="dhtmlxGrid/codebase/dhtmlxgrid.css" />
		<link rel="stylesheet" type="text/css"
			href="css/dhtmlxgrid_dhx_custom.css" />
		<link rel="stylesheet" type="text/css" href="css/dictionary.css" />
		<script type="text/javascript" src="js/jquery.blockUI.js"></script>
		<script type="text/javascript" src="js/dept/verify.js"></script>
		<script type="text/javascript">
	$(document).ready(function(){
		if($("#deptcode").val() != ""){
			$("#scopeButton").attr("disabled", false);
		}else{
			$("#scopeButton").attr("disabled", true);
		}
		
		/*if($("#checkedCodes").val() != ""){
			changeDisable("isleafRad", true);
		}*/
		
		/*var operationType = "${operationType}";
		if(operationType == "modify" && doChangeCheck(true)){
			changeDisable("isleafRad", true);
		}*/
	});
	
	
	var save_num = ${save_num };
	var save_tree_num = ${save_tree_num };
	//验证重复
	function doCheckRepeat(){
		var hosnum = $("#hosnum").val();
		var nodecode = $("#nodecode").val();
		var deptname = $("#deptname").val();
		var old_deptname = $("#old_deptname").val();
		var reVal = true;
		if(hosnum != "" && nodecode != "" && deptname != "" && deptname != old_deptname){
			$.ajax({
				cache:false,   //是否使用缓存
	            url: "dept/dept_add_check.htm", 
	            async : false,   //是否异步，false为同步
	            type:"post",
	            data:"hosnum="+hosnum+"&nodecode="+nodecode+"&deptname="+deptname,
	            error:function(){
	            	alert("ajax请求失败");
	            },
	            success:function(reply){ 
	             	 if(reply == "Y"){
	             	 	alert("已经存在相同的【科室名称】！");
	             	 	reVal = false;
	             	 }else if(reply == "N"){
	             	 	reVal = true;
	             	 }
	            } 
			});
		}
		return reVal;
	}
	
	//新增、保存
	function doDeptAdded(){
		if(fetchRadioVal("clinicaltype") == ""){
			alert("请选择【临床类别】！");
			return;
		}
		if(fetchRadioVal("isleafRad") == ""){
			alert("请选择【末级判断】！");
			return;
		}
		if(checkNullSelect("depttype","科室类别")){
			return;
		}
		if(checkNull("deptname","科室名称")){
			return;
		}
		if(countLen($("#deptname").val()) > 30){
			alert("【科室名称】输入太长！");
			return;
		}
		if(countLen($("#shortname").val()) > 10){
			alert("【科室简称】输入太长！");
			return;
		}
		if(countLen($("#location").val()) > 30){
			alert("【坐落】输入太长！");
			return;
		}
		if(checkNum($("#prepay").val(),"预交金额")){
			return;
		}
		if(doCheckRepeat()){
			showMsg("数据保存中...");
			$.ajax({
				cache:false,   //是否使用缓存
	            url: "dept/dept_added.htm", 
	            async : false,   //是否异步，false为同步
	            type:"post",
	            data:$('#dept_form').serialize(),
	            error:function(){
	            	alert("ajax请求失败");
	            	closeMsg();
	            },
	            success:function(reply){ 
	             	if(reply == "fail"){
	             		closeMsg();
	             	 	alert("保存失败");
	             	 }else{
	             	 	closeMsg();
	             	 	alert("保存成功");
	             	 	$("#operationType").val("modify");//修改标志为：修改
	             	 	$("#deptcode").val(reply);//更新自动生成的科室编码
						$("#old_deptname").val($("#deptname").val());//更新科室名称
						$("#old_depttype").val($("#depttype").val());//更新科室类别
						$("#selfAddButton").css("display","inline");//显示新增按钮
						if($("#isleaf").val() != $("#old_isleaf").val()
							|| $("#deptclass").val() == '病区'){//科室的非末级节点或者病区的全部
							save_tree_num ++;//末级判断发生变更，需要更新树
							$("#old_isleaf").val($("#isleaf").val());
						}
						/*if($("#isleaf").val() == "Y"){//判断是否允许选择--科室服务范围
							$("#scopeButton").attr("disabled", false);
						}else{
							save_tree_num ++;//不是末级，需要更新树
							$("#scopeButton").attr("disabled", true);
						}*/
						save_num ++;//保存次数
	             	 } 
	            } 
			});
		}
	}
	
	//关闭层
	function doDeptClose(){
		if(save_tree_num >= 1){//保存次数大于1次
			window.parent.document.getElementById("main_iframe").contentWindow.loadTree();
		}
		if(save_num >= 1){//保存次数大于1次
			window.parent.document.getElementById("main_iframe").contentWindow.document.getElementById("iframe_right").contentWindow.reloadGrid();
		}
		parent.$("#hidden_iframe").attr("src","");
		parent.$.unblockUI();
	}

//选择服务范围
function scopeSelect(){
	var checkedCodes = $("#checkedCodes").val();
	var defaultDept = $("#deptcode").val();
	//var defaultType = $("#depttype").val();
	var defaultType = z.getComboText();//update_0424
	defaultType = encodeURI(encodeURI(defaultType));
	var random = Math.random();
	
	var url = "dept_scope_add.htm?defaultHos=${dept.hosnum}&defaultNode=${dept.nodecode}&defaultDept="+defaultDept+"&defaultType="+defaultType
		+"&yqName="+encodeURI(encodeURI("${yqName}")+"&checkedScope="+checkedCodes)+"&random="+random;
	//window.open(url);
	//alert();
	window.showModalDialog(url,window,"dialogHeight: 470px; dialogWidth: 352px; dialogHide: yes; help: no; resizable: no; status: no; scroll: no");
	
	/*$("#hidden_div1").css("width", "242px");
	$("#hidden_div1").css("height", "399px");
	//var top_ = ($(window).height() - $("#hidden_div1").height())/2;
	var left_ = ($(window).width() - $("#hidden_div1").width())/2;
	var top_ = 0;
	$("#hidden_iframe1").attr("src",url);
	$.blockUI(
	{
		message: $("#hidden_div1"),
		css:{width:'242px',
		top:top_,
		left:left_,
		border:'0px solid #aaa'},
		overlayCSS:{backgroundColor: '#CCCCCC'},
	});	*/
}

//验证是否可以变更末级判断
function doChangeCheck(noShow){
	var reVal = true;
	var hosnum = $("#hosnum").val();
	var deptcode = $("#deptcode").val();
	var idstr = hosnum + ";" + deptcode;
	var isleaf = $("#isleaf").val();
	if(idstr != ""){
		$.ajax({
			cache:false,   //是否使用缓存
            url: "dept/dept_child_check.htm", 
            async : false,   //是否异步，false为同步
            type:"post",
            data:"idstr="+idstr+"&deptclass=${dept.deptclass}&isleaf="+isleaf,
            error:function(){
            	alert("ajax请求失败");
            },
            success:function(reply){ 
             	 if(reply == "N"){
             	 	reVal = false;
             	 }else{
             	 	if(!noShow){
	             	 	var ids = reply.split("_");
	             	 	if(ids.length >= 2){
	             	 		alert("该病区下已经存在床位，不能变更末级判断！");
	             	 	}else{
	             	 		alert("该科室存在下级科室，不能变更末级判断！");
	             	 	}
             	 	}
             	 	reVal = true;
             	 }
            } 
		});
	}
	return reVal;
}

//末级判断 变化
function leafChange(name){
	var leafs = document.getElementsByName(name);
	var operationType = "${operationType}";
	if(operationType == "modify" && doChangeCheck()){
		var leaf = $("#isleaf").val();
		for(var i = 0; i < leafs.length; i ++ ){
			if(leafs[i].value == leaf){
				leafs[i].checked = true;
			}else{
				leafs[i].checked = false;
			}
		}
		changeDisable("isleafRad", true);
	}else{
		for(var i = 0; i < leafs.length; i ++ ){
			if(leafs[i].checked){
				$("#isleaf").val(leafs[i].value);
				/*if($("#old_isleaf").val() == "Y"){
					if(leafs[i].value == "Y"){
						$("#scopeButton").attr("disabled", false);
					}else{
						$("#scopeButton").attr("disabled", true);
					}
				}*/
			}
		}
	}
}

//转到新增页面	
function doSelfClear(){
	var url = "dept_add.htm?operationType=add&defaultHos=${dept.hosnum}&defaultNode=${dept.nodecode}&defaultDept=${dept.parentid}"
		+"&defaultClass="+encodeURI(encodeURI("${dept.deptclass}"))+"&yqName="+encodeURI(encodeURI("${yqName}"))
		+"&save_num="+save_num+"&save_tree_num="+save_tree_num;
	window.location.replace(url);
}

$("#scopeButton li").hover(function(){
		$(this).css("cursor","pointer");
	},function(){
		$(this).css("cursor","default");
	}
);

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
		if(event.keyCode==F1_KEYCODE){
			event.preventDefault();
			event.keyCode=0;
			event.returnValue = false;
			doSelfClear();
		}else if(event.keyCode==F2_KEYCODE){
			event.preventDefault();
			event.keyCode=0;
			event.returnValue = false;
			doDeptAdded();
		}else if(event.keyCode==ESC_KEYCODE){
			event.preventDefault();
			event.keyCode=0;
			event.returnValue = false;
			doDeptClose();
		}
	});
	

</script>
	</head>

	<body>
		<div class="box1">
			<div class="box2">
				<div class="box3">
					<span style="float: left;">科室信息</span>
					<span class="boxpad"><img id="close_iframe_img"
							src="img/close.gif" align="middle" onclick="doDeptClose()" /> </span>
				</div>
				<div class="box4" style="height: 345px">

					<form id="dept_form" name="dept_form" onsubmit="return false" style="margin: 0px;">
						<input id="operationType" name="operationType" type="hidden"
							value="${operationType}" />

						<!-- 圆头******开始 -->
						<table width="98%" border="0" cellspacing="0" cellpadding="0" 
							style="margin-top: 5px; ">
							<tr>
								<td width="10"><img src="img/new_yuan1.jpg" /></td>
								<td background="img/new_yuan2.jpg"></td>
								<td width="10"><img src="img/new_yuan3.jpg" /></td>
							</tr>
							<tr>
								<td background="img/new_yuan4.jpg"></td>
								<td>
									<!-- 圆头******结束 -->
									<table id="patient_info_tb" width="737" border="0"
										cellspacing="0" cellpadding="0" style="display: block;">
										<tr>
											<td width="100" height="28" class="tit">
												医院编码：
											</td>
											<td width="150" class="val">
												<input id="hosnum" name="hosnum" type="text"
													class="txt4 text_field not_editable" readonly="readonly"
													value="${dept.hosnum}" />
											</td>
											<td width="95" class="tit">
												院区编码：
											</td>
											<td width="150" class="val">
												<input id="nodecode" name="nodecode" type="text"
													class="txt4 text_field not_editable" readonly="readonly"
													value="${dept.nodecode}" />

											</td>
											<td width="100" class="tit">
												父类科室：
											</td>
											<td class="val">
												<input id="parentid" name="parentid" type="text"
													class="txt4 text_field not_editable" readonly="readonly"
													value="${dept.parentid}" />
												<input id="deptcode" name="deptcode" type="hidden"
													value="${dept.deptcode}" />
											</td>
										</tr>

										<tr>
											<td width="100" height="28" class="tit">
												科室大类：
											</td>
											<td class="val">
												<input id="deptclass" name="deptclass" type="text"
													class="txt4 text_field not_editable" readonly="readonly"
													value="${dept.deptclass}" />
												<!--											<input type="hidden" id="hosnum" name="hosnum"-->
												<!--												value="${dept.hosnum}" />-->
												<!--											<input type="hidden" id="nodecode" name="nodecode"-->
												<!--												value="${dept.nodecode}" />-->
												<!--											<input type="hidden" id="parentid" name="parentid"-->
												<!--												value="${dept.parentid}" />-->
												<!--											<input id="deptcode" name="deptcode" type="hidden"-->
												<!--												value="${dept.deptcode}" />-->
											</td>
											<td width="100" class="tit">
												临床类别：
											</td>
											<td width="150" class="val">
												<div class="radioDiv">
												<input id="clinicaltype1" type="radio" name="clinicaltype"
													value="临床"
													<c:if test="${dept.clinicaltype=='临床' || dept.clinicaltype==null }">checked="checked"</c:if> />
												临床
												<input id="clinicaltype2" type="radio" name="clinicaltype"
													value="非临床"
													<c:if test="${dept.clinicaltype=='非临床' }">checked="checked"</c:if> />
												非临床
												</div>
											</td>
											<td width="100" class="tit">
												科室类别：
											</td>
											<td class="val" style="padding-left: 0px; position: relative;left: 2px; top: 2px;">
												<input id="old_depttype" name="old_depttype" type="hidden"
													value="${dept.depttype}" />
												<select id="depttype" name="depttype"
													class="txt9 text_field_required" style="width: 142px;">
													<option value=""></option>
													<c:forEach items="${depttypeList}" var="depttype">
														<option value="${depttype.contents}"
															<c:if test="${dept.depttype==null?depttype.isdefault == 'Y':depttype.contents==dept.depttype}">selected="selected"</c:if>>
															${depttype.contents}
														</option>
													</c:forEach>
												</select>
												<script>
											       var z=dhtmlXComboFromSelect("depttype");
											    </script>
											</td>
										</tr>

										<tr>
											<td height="28" class="tit">
												科室名称：
											</td>
											<td colspan="3" class="val">
												<input id="deptname" name="deptname" type="text"
													maxlength="30" class="txt7 text_field_required"
													value="${dept.deptname}" />
												<input id="old_deptname" name="old_deptname" type="hidden"
													value="${dept.deptname}" />
											</td>
											<td class="tit">
												科室简称：
											</td>
											<td class="val">
												<input id="shortname" name="shortname" type="text"
													maxlength="10" class="txt4 text_field"
													value="${dept.shortname}" />
											</td>
										</tr>

										<tr>
											<td height="28" class="tit">
												末级判别：
											</td>
											<td class="val">
												<div class="radioDiv">
												<input id="isleafRad1" type="radio" name="isleafRad"
													value="Y" onchange="leafChange('isleafRad')" onclick="this.blur();"
													<c:if test="${dept.isleaf=='Y' || dept.isleaf==null }">checked="checked"</c:if> />
												是
												<input id="isleafRad2" type="radio" name="isleafRad"
													value="N" onchange="leafChange('isleafRad')" onclick="this.blur();"
													<c:if test="${dept.isleaf=='N' }">checked="checked"</c:if> />
												否
												</div>
												<input id="isleaf" type="hidden" name="isleaf" value='${dept.isleaf==null?"Y":dept.isleaf }' />
												<input id="old_isleaf" type="hidden" name="old_isleaf" value='${dept.isleaf==null?"Y":dept.isleaf }' />
											</td>
											<td class="tit">
												核算科室：
											</td>
											<td class="val">
												<div class="radioDivNo">
												<input id="isaccdept1" type="radio" name="isaccdept"
													value="Y"
													<c:if test="${dept.isaccdept=='Y' }">checked="checked"</c:if> />
												是
												<input id="isaccdept2" type="radio" name="isaccdept"
													value="N"
													<c:if test="${dept.isaccdept=='N' }">checked="checked"</c:if> />
												否
												</div>
											</td>
											<td class="tit">
												门诊标志：
											</td>
											<td class="val">
												<div class="radioDivNo">
												<input id="clcflag1" type="radio" name="clcflag" value="Y"
													<c:if test="${dept.clcflag=='Y' }">checked="checked"</c:if> />
												是
												<input id="clcflag2" type="radio" name="clcflag" value="N"
													<c:if test="${dept.clcflag=='N' }">checked="checked"</c:if> />
												否
												</div>
											</td>
										</tr>

										<tr>
											<td height="28" class="tit">
												急诊标志：
											</td>
											<td class="val">
												<div class="radioDivNo">
												<input id="emcflag1" type="radio" name="emcflag" value="Y"
													<c:if test="${dept.emcflag=='Y' }">checked="checked"</c:if> />
												是
												<input id="emcflag2" type="radio" name="emcflag" value="N"
													<c:if test="${dept.emcflag=='N' }">checked="checked"</c:if> />
												否
												</div>
											</td>
											<td class="tit">
												住院标志：
											</td>
											<td class="val">
												<div class="radioDivNo">
												<input id="inpflag1" type="radio" name="inpflag" value="Y"
													<c:if test="${dept.inpflag=='Y' }">checked="checked"</c:if> />
												是
												<input id="inpflag2" type="radio" name="inpflag" value="N"
													<c:if test="${dept.inpflag=='N' }">checked="checked"</c:if> />
												否
												</div>
											</td>
											<td class="tit">
												产科标志：
											</td>
											<td class="val">
												<div class="radioDivNo">
												<input id="matflag1" type="radio" name="matflag" value="Y"
													<c:if test="${dept.matflag=='Y' }">checked="checked"</c:if> />
												是
												<input id="matflag2" type="radio" name="matflag" value="N"
													<c:if test="${dept.matflag=='N' }">checked="checked"</c:if> />
												否
												</div>
											</td>
										</tr>

										<tr>
											<td height="28" class="tit">
												草药标志：
											</td>
											<td class="val">
												<div class="radioDivNo">
												<input id="herbsflag1" type="radio" name="herbsflag"
													value="Y"
													<c:if test="${dept.herbsflag=='Y' }">checked="checked"</c:if> />
												是
												<input id="herbsflag2" type="radio" name="herbsflag"
													value="N"
													<c:if test="${dept.herbsflag=='N' }">checked="checked"</c:if> />
												否
												</div>
											</td>
											<td class="tit">
												成药标志：
											</td>
											<td class="val">
												<div class="radioDivNo">
												<input id="cnmflag1" type="radio" name="cnmflag" value="Y"
													<c:if test="${dept.cnmflag=='Y' }">checked="checked"</c:if> />
												是
												<input id="cnmflag2" type="radio" name="cnmflag" value="N"
													<c:if test="${dept.cnmflag=='N' }">checked="checked"</c:if> />
												否
												</div>
											</td>
											<td class="tit">
												西药标志：
											</td>
											<td class="val">
												<div class="radioDivNo">
												<input id="wmflag1" type="radio" name="wmflag" value="Y"
													<c:if test="${dept.wmflag=='Y' }">checked="checked"</c:if> />
												是
												<input id="wmflag2" type="radio" name="wmflag" value="N"
													<c:if test="${dept.wmflag=='N' }">checked="checked"</c:if> />
												否
												</div>
											</td>
										</tr>

										<tr>
											<td height="28" class="tit">
												卫生材料：
											</td>
											<td class="val">
												<div class="radioDivNo">
												<input id="materialflag1" type="radio" name="materialflag" value="Y"
													<c:if test="${dept.materialflag=='Y' }">checked="checked"</c:if> />
												是
												<input id="materialflag2" type="radio" name="materialflag" value="N"
													<c:if test="${dept.materialflag=='N' }">checked="checked"</c:if> />
												否
												</div>
											</td>
											
											<td class="tit">
											坐&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;落：
											</td>
											<td class="val">
												<input id="location" name="location" type="text"
													maxlength="6" class="txt4 text_field"
													value="${dept.location}" />
											</td>
											<td class="tit">
												预交金额：
											</td>
											<td class="val">
												<input id="prepay" name="prepay" type="text" maxlength="6"
													onkeyup="inputNum(this)"
													class="txt4 text_field" value="${dept.prepay}" />
											</td>
										</tr>
										<tr>
											<td height="28" class="tit">
												服务范围：
											</td>
											<td colspan="5" class="val" valign="middle">
												<input type="text" id="checkedNames" name="checkedNames"
													class="show_select_input3" readonly="readonly" value="${checkedNames }" />
<!--												<div id="scopeDiv">-->
<!--													<ul>-->
<!--														<li id="scopeButton" style="cursor: pointer;"-->
<!--															onclick="scopeSelect();" title="先保存科室，才能操作">-->
<!--															选择-->
<!--														</li>-->
<!--													</ul>-->
<!--												</div>-->
													<div id="scopeButton" class="show_select_tree" onclick="scopeSelect();" title="先保存科室，才能选择服务范围"></div>
												<input type="hidden" id="checkedCodes" name="checkedCodes"
													value="${checkedCodes }" />
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
								<td><img src="img/new_yuan6.jpg" /></td>
								<td background="img/new_yuan7.jpg"></td>
								<td><img src="img/new_yuan8.jpg" /></td>
							</tr>
						</table>
						<!-- 圆尾******结束 -->

						<div style="margin-top: 18px;">
							<center id="card_manager_btn" style="display: block;">
								<c:choose>
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
								</c:choose>
								<button type="button" class="btn" onclick="doDeptAdded()">
									保存
									<font color="red">(F2)</font>
								</button>
								<button type="button" class="btn" onclick="doDeptClose()">
									关闭
									<font color="red">(Esc)</font>
								</button>
							</center>
						</div>
					</form>
					<input type="hidden" id="pos_x" />
					<input type="hidden" id="pos_y" />
				</div>
			</div>
		</div>
		<div id="hidden_div1" style="display: none;">
			<iframe id="hidden_iframe1" width="100%" height="100%" src=""
				topmargin="0" leftmargin="0" marginheight="0" scrolling="no"
				marginwidth="0" frameborder="no"></iframe>
		</div>
	</body>
</html>