<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<base href="<%=basePath%>" />
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="expires" content="0" />
		<title>院区</title>
		<link href="css/register.css" rel="stylesheet" type="text/css" />
		<link href="css/top.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="My97DatePicker/skin/WdatePicker.css" type="text/css" />
		<link rel="stylesheet" type="text/css" href="dhtmlxCombo/codebase/dhtmlxcombo.css" />
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

div #patient_info_tb .tit {
	text-align: right;
}

div #patient_info_tb .val {
	text-align: left;
}
</style>
		<script type="text/javascript" src="js/jquery-1.6.1.js"></script>
		<script type="text/javascript" src="js/jquery.lrTool.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/clc/comboTool.js"></script>
		<script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
		<script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
		<script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
		<script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
		<script type="text/javascript" src='My97DatePicker/WdatePicker.js'></script>
		<script type="text/javascript" src="js/dept/verify.js"></script>
		<script type="text/javascript" src="js/jquery.blockUI.js"></script>
		<script type="text/javascript">
	$(document).ready(function(){
	});
	
	var save_num = ${save_num };
	//节点编码 和 名称不能重复
	function doCheckRepeat(){
		var hosnum = $("#hosnum").val();
		var nodecode = $("#nodecode").val();
		var old_nodecode = $("#old_nodecode").val();
		var hosname = $("#hosname").val();
		var old_hosname = $("#old_hosname").val();
		var reVal = true;
		if(nodecode == old_nodecode){
			nodecode = "";
		}
		if(hosname == old_hosname){
			hosname = "";
		}
		if(hosnum != "" && (nodecode != "" || hosname != "" )){
			$.ajax({
				cache:false,   //是否使用缓存
	            url: "dept/hospital_add_check.htm", 
	            async : false,   //是否异步，false为同步
	            type:"post",
	            data:"hosnum="+hosnum+"&nodecode="+nodecode+"&hosname="+hosname,
	            error:function(){
	            	alert("ajax请求失败");
	            },
	            success:function(reply){ 
	             	 if(reply == "double"){
	             	 	alert("已经存在相同的【节点编码】和【名称】！");
	             	 	reVal = false;
	             	 }else if(reply == "code"){
	             	 	alert("已经存在相同的【节点编码】！");
	             	 	reVal = false;
	             	 }else if(reply == "name"){
	             	 	alert("已经存在相同的【名称】！");
	             	 	reVal = false;
	             	 }else if(reply == "N"){
	             	 	reVal = true;
	             	 }
	            } 
			});
		}
		return reVal;
	}
	
	//新增、修改
	function doHospitalAdded(){
		if(checkNull("hosname","名称")){
			return;
		}
		if(checkNull("shortname","简称")){
			return;
		}
		if(document.getElementById("hosname") == "本部"){
			alert("【名称】不能修改为【本部】");
			return;
		}
		if(checkNull("nodecode","节点编码")){
			return;
		}
		/*if(document.getElementsByName("distcode")[0].value == ""){
			alert("【行政区划】不能为空！");
			return;
		}*/
		
		if(checkChar($("#nodecode").val(),"节点编码")){
			return;
		}
		if(checkNum($("#empnumber").val(),"全院人数")){
			return;
		}
		if(checkNum($("#beds").val(),"床位数")){
			return;
		}
		if(checkNum($("#doctors").val(),"医生数")){
			return;
		}
		if(checkNum($("#nurses").val(),"护士数")){
			return;
		}
		
		if(countLen($("#hosname").val()) > 100){
			alert("【名称】输入太长！");
			return;
		}
		if(countLen($("#shortname").val()) > 10){
			alert("【简称】输入太长！");
			return;
		}
		if(countLen($("#address").val()) > 255){
			alert("【地址】输入太长！");
			return;
		}
		if(countLen($("#tel").val()) > 20){
			alert("【电话】输入太长！");
			return;
		}
		if(countLen($("#introduction").val()) > 255){
			alert("【简介】输入太长！");
			return;
		}
		
		if(doCheckRepeat()){
			showMsg("数据保存中...");
			$.ajax({
				cache:false,   //是否使用缓存
	            url: "dept/hospital_added.htm", 
	            async : false,   //是否异步，false为同步
	            type:"post",
	            data:$('#hospital_form').serialize(),
	            error:function(){
	            	alert("ajax请求失败");
	            	closeMsg();
	            },
	            success:function(reply){ 
	             	 if(reply == "success"){
	             	 	closeMsg();
	             	 	alert("保存成功");
	             	 	save_num ++; //保存次数累加
	             	 	$("#operationType").val("modify");//修改标志为：修改
						$("#selfAddButton").css("display","inline");//显示新增按钮
						$("#old_nodecode").val($("#nodecode").val());//更新节点编码
						$("#old_hosname").val($("#hosname").val());//更新名称
						//设置编码不可变更
						$("#nodecode").attr("class", $("#nodecode").attr("class")+" not_editable");
						$("#nodecode").attr("readonly","readonly");
	             	 }else if(reply == "fail"){
	             	 	closeMsg();
	             	 	alert("保存失败");
	             	 }
	            } 
			});
		}
	}
	
	//关闭层并刷新页面
	function doHospitalClose(){
		parent.$("#hidden_iframe").attr("src","");
		if(save_num >= 1){//保存次数大于1次
			window.parent.document.getElementById("main_iframe").
				contentWindow.loadTree();
			window.parent.document.getElementById("main_iframe").
				contentWindow.document.getElementById("iframe_right").
				contentWindow.reloadGrid();
		}
		parent.$.unblockUI();
	}
	
	//转到新增页面
	function doSelfClear(){
		var url = "hospital_add.htm?operationType=add&defaultHos=${hos.hosnum}&defaultDist=${defaultDist }&save_num="+save_num;
		window.location.replace(url);
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
		if(event.keyCode==F1_KEYCODE){
			event.preventDefault();
			event.keyCode=0;
			event.returnValue = false;
			doSelfClear();
		}else if(event.keyCode==F2_KEYCODE){
			event.preventDefault();
			event.keyCode=0;
			event.returnValue = false;
			doHospitalAdded();
		}else if(event.keyCode==ESC_KEYCODE){
			event.preventDefault();
			event.keyCode=0;
			event.returnValue = false;
			doHospitalClose();
		}
	});
	
	
</script>
	</head>
	<body>
		<div class="box1">
			<div class="box2">
				<div class="box3">
					<span style="float: left;">院区信息</span>
					<span class="boxpad"><img id="close_iframe_img"
							src="img/close.gif" align="middle" onclick="doHospitalClose()" />
					</span>
				</div>
				<div class="box4" style="height: 275px;">
					<form id="hospital_form" name="hospital_form"
						onsubmit="return false">
						<input id="operationType" name="operationType" type="hidden"
							value="${operationType}" />

							<!--  <div id="week_li" class="week_4" style="background-image: none;margin-left: 9px;width: 752px;height: 36px;border-bottom: 1px solid #93AFBA">-->
							<!--  </div>-->

							<!-- 圆头******开始 -->
							<table width="98%" border="0" cellspacing="0" cellpadding="0" style="margin-top: 5px;">
								<tr>
									<td width="10"><img src="img/new_yuan1.jpg" /></td>
									<td background="img/new_yuan2.jpg"></td>
									<td width="10"><img src="img/new_yuan3.jpg" /></td>
								</tr>
								<tr>
									<td background="img/new_yuan4.jpg">
									</td>
									<td>
							<!-- 圆头******结束 -->
										<table id="patient_info_tb" border="0" cellspacing="0" width="750"
											cellpadding="0" style="display: block">
											<tr>
												<td height="28" class="tit">
													名称：
												</td>
												<td class="val">
													<input id="hosname" name="hosname" type="text"
														class="txt4 text_field_required" value="${hos.hosname}" />
													<input type="hidden" id="old_hosname" name="old_hosname" value="${hos.hosname}" />
													<input type="hidden" id="supunit" name="supunit" value="${hos.supunit}" />
												</td>
												<td class="tit">
													简称
<!--													节点类别：-->
												</td>
												<td class="val" >
													<input id="shortname" name="shortname" type="text" maxlength="10"
														class="txt4 text_field_required" value="${hos.shortname}" />
													<input id="nodetype" name="nodetype" type="hidden"
														class="txt4 text_field not_editable" readonly="readonly"
														value="${hos.nodetype==null?'院区':hos.nodetype}" />
												</td>
												<td width="90" class="tit">
													机构分类：
												</td>
												<td class="val" width="160" style="position: relative; right: 4px;">
													<select id="orgtype" name="orgtype" class="txt9 text_field">
														<option value="社区卫生服务站" <c:if test='${hos.orgtype==null || "社区卫生服务站" == hos.orgtype}'>selected="selected"</c:if>>社区卫生服务站</option>
														<option value="村卫生室" <c:if test='${hos.orgtype!=null && "村卫生室" == hos.orgtype}'>selected="selected"</c:if>>村卫生室</option>
													</select>
													<script>
												       var z=dhtmlXComboFromSelect("orgtype");
												    </script>
												</td>
											</tr>

											<tr>
												<td width="90" height="28" class="tit">
													节点编码：
												</td>
												<td width="160" class="val">
													<input type="hidden" id="hosnum" name="hosnum" value="${hos.hosnum}" />
													<input type="hidden" id="old_nodecode" name="old_nodecode" value="${hos.nodecode}" />
													<input id="nodecode" name="nodecode" type="text"
														maxlength="4" class='txt4 text_field_required <c:if test='${"modify"==operationType}'>not_editable</c:if>'
														<c:if test='${"modify"==operationType}'>readonly="readonly"</c:if>
														onkeyup="inputChar(this)"
														value="${hos.nodecode}" />
												</td>
												<td width="90" class="tit">
													行政区划：
												</td>
												<td class="val" width="160">
													<div style="width: 153px;float: left;" id="_distcode">
													<select id="distcode" name="distcode" class="txt9 text_field">
														<c:forEach items="${distList}" var="dist">
															<option value="${dist.nevalue}"
																<c:if test="${hos.distcode==null?(dist.nevalue == defaultDist):(dist.nevalue==hos.distcode)}">selected="selected"</c:if>>
																${dist.contents}
															</option>
														</c:forEach>
													</select>
													</div>
												</td>
												<td width="90" class="tit">
													全院人数：
												</td>
												<td width="160" class="val">
													<input id="empnumber" name="empnumber" type="text"
														class="txt4 text_field" value="${hos.empnumber}"
														onkeyup="inputNum(this)"
														maxlength="4" />
												</td>

											</tr>

											<tr>
												<td height="28" class="tit">
													床位数：
												</td>
												<td class="val">
													<input id="beds" name="beds" type="text" onkeyup="inputNum(this)"
														class="txt4 text_field" value="${hos.beds}" maxlength="4" />
												</td>
												<td class="tit">
													医生数：
												</td>
												<td class="val">
													<input id="doctors" name="doctors" type="text"
														maxlength="4" class="txt4 text_field"
														onkeyup="inputNum(this)"
														value="${hos.doctors}" />
												</td>
												<td class="tit">
													护士数：
												</td>
												<td class="val">
													<input id="nurses" name="nurses" type="text" maxlength="4"
														onkeyup="inputNum(this)"
														class="txt4 text_field" value="${hos.nurses}" />
												</td>

											</tr>



											<tr>
												<td height="28" class="tit">
													地址：
												</td>
												<td colspan="3" class="val">
													<input id="address" name="address" type="text"
														class="txt7 text_field" value="${hos.address}" />
												</td>
												<td class="tit">
													电话：
												</td>
												<td class="val">
													<input id="tel" name="tel" type="text" maxlength="20"
														class="txt4 text_field" value="${hos.tel}" />
												</td>
											</tr>

											<tr>
												<td class="tit">
													简介：
												</td>
												<td colspan="6" class="val">
													<textarea id="introduction" name="introduction"
														class="txt6 text_field" style="width: 640px; margin-top: 5px;">${hos.introduction}</textarea>
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

						<div style="margin-top: 18px">
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
								<button type="button" class="btn" onclick="doHospitalAdded()">
									保存
									<font color="red">(F2)</font>
								</button>
								<button type="button" class="btn" onclick="doHospitalClose()">
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
	</body>
</html>

<script>
	//下拉脚本
	var	combo_distcode;
	combo_distcode = dhtmlXComboFromSelect("distcode");
	//combo_distcode = new dhtmlXCombo("combo_distcode", "distcode", 142);
	combo_distcode.enableFilteringMode(false);
	combo_distcode.attachEvent("onKeyPressed", function(keyCode){
		//发起ajax请求
		$.get("dept/qhdm.htm?defaultDist=${defaultDist }", function(json){
			//hp 混拼  wb 五笔 py 拼音  null 中文
			//(keyCode, json, comboName, filterMode, quickFill (快速匹配), required(是否必填))
			if(comboFilter(keyCode, json, "combo_distcode", "hp", true, false) == 0){
			}
		});
	});
	
	//绑定焦点移开事件
	combo_distcode.attachEvent("onBlur", function(){
		if(combo_distcode.optionsArr.length >= 1 && combo_distcode.getSelectedValue() != ""){
			if(combo_distcode.getSelectedValue() == null){
				combo_distcode.setComboText(combo_distcode.optionsArr[0].text);
				combo_distcode.setComboValue(combo_distcode.optionsArr[0].value);
			}
		}else{
			combo_distcode.setComboText("");
			combo_distcode.setComboValue("");
		}
	});
	/*
	$("#_distcode input.dhx_combo_input").bind("blur", function(){
		//发起ajax请求
		$.get("dept/qhdm.htm?defaultDist=${hos.distcode }", function(json){
			//hp 混拼  wb 五笔 py 拼音  null 中文
			//(keyCode, json, comboName, filterMode, quickFill (快速匹配), required(是否必填))
			if(comboFilter(13, json, "combo_distcode", "hp", true, false) != 0){
				combo_distcode.setComboValue("");
				combo_distcode.setComboText("");
			}
		});
	});*/			
</script>
