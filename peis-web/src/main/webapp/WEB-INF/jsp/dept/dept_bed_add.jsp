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
		<title>床位维护</title>
		<link href="css/register.css" rel="stylesheet" type="text/css" />
		<link href="css/top.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="My97DatePicker/skin/WdatePicker.css"
			type="text/css" />
		<link rel="stylesheet" type="text/css"
			href="dhtmlxCombo/codebase/dhtmlxcombo.css" />
		<style type="text/css">
.box1 {
	width: 600px;
	border: 1px solid #b6cfd6;
	padding: 1px;
	margin: 0 auto;
}

.box2 {
	width: 584px;
	background: url(img/tckbg.gif) repeat-x;
	background-color: #d9eaee;
	padding: 8px;
}

.box3 {
	width: 584px;
	margin: 0 auto;
}

.middle_bed {
	width: 550px;
	border: 1px solid #93afba;
	border-bottom: none;
	border-top: none;
	padding-bottom: 5px;
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
	width: 572px;
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

div.gridbox_dhx_custom table.hdr td {
	font-family: 微软雅黑;
	font-size: 12px;
	font-weight: bold;
	vertical-align: top;
}

div.gridbox table.obj.row20px tr td {
	
}
.div_field .dhx_combo_box{
				border:1px solid red;
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
		ComboInit();
		selectText();
		myCombo.setComboValue('${bed.chgitem}');
		myCombo.setComboText('${bed.chgitemname}');
		myCombo.readonly(true);
	});
	
	var save_num = ${save_num };
	//验证重复
	function doCheckRepeat(){
		var bedno = encodeURI(encodeURI($("#bedno").val()));
		var hosnum = $("#hosnum").val();
		var wardno = $("#wardno").val();
		var old_bedno = $("#old_bedno").val();
		var reVal = true;
		if(hosnum != "" && wardno != "" && bedno != "" && bedno != old_bedno){
			$.ajax({
				cache:false,   //是否使用缓存
	            url: "dept/dept_bed_add_check.htm", 
	            async : false,   //是否异步，false为同步
	            type:"post",
	            data:"hosnum="+hosnum+"&wardno="+wardno+"&bedno="+bedno,
	            error:function(){
	            	alert("ajax请求失败");
	            },
	            success:function(reply){ 
	             	 if(reply == "Y"){
	             	 	alert("已经存在相同的【床位号】！");
	             	 	reVal =  false;
	             	 }else if(reply == "N"){
	             	 	reVal =  true;
	             	 }
	            } 
			});
		}
		return reVal;
	}
	
	//保存操作
	function doBedAdded(){
		if(checkNull("forsex","适用性别")){
			return;
		}
		if(countLen($("#roomno").val()) > 10){
			alert("【房间号】输入太长！");
			return;
		}
		/**
		if(checkNull("bedno","床位号")){
			return;
		}
		if(countLen($("#bedno").val()) > 4){
			alert("【床位号】输入太长！");
			return;
		}
		
		if(checkChar($("#bedno").val(),"床位号")){
			return;
		}**/
		if(myCombo.getComboText()==''){
			alert("【收费项目不能为空】");
			return;
		}
		if(doCheckRepeat()){
			showMsg("数据保存中...");
			$.ajax({
				cache:false,   //是否使用缓存
	            url: "dept/dept_bed_added.htm", 
	            async : false,   //是否异步，false为同步
	            type:"post",
	            data:{"hosnum":$("#hosnum").val(),"wardno":$("#wardno").val(),"deptcode":combo_deptcode.getActualValue(),"roomno":$("#roomno").val(),"bedno":encodeURI($("#bedno").val()),"forsex":combo_forsex.getActualValue(),"chgitem":myCombo.getActualValue(),"operationType":$("#operationType").val(),"old_bedno":$("#old_bedno").val()},
	            error:function(){
	            	alert("ajax请求失败");
	            	closeMsg();
	            },
	            success:function(reply){ 
	             	 if(reply == "success"){
	             	 	closeMsg();
	             	 	alert("保存成功");
	             	 	save_num ++;//保存次数
	             	 	$("#operationType").val("modify");//修改标志为：修改
	             	 	$("#old_bedno").val($("#bedno").val());//更新主键代码之一：床位号
	             	 	$("#bedAddButton").css("display","inline");//显示新增按钮
	             	 }else if(reply == "fail"){
	             	 	closeMsg();
	             	 	alert("保存失败");
	             	 }
	            } 
			});
		}
	}

//层关闭
function doBedClose(){
	parent.$("#hidden_iframe").attr("src","");
	
	if(save_num >= 1){//保存次数大于1次
		window.parent.document.getElementById("main_iframe").
				contentWindow.document.getElementById("iframe_right").
				contentWindow.reloadGrid();
	}
	parent.$.unblockUI();
}

//转到新增页面
function doSelfClear(){
	var hosnum = $("#hosnum").val();
	var wardno = $("#wardno").val();
	var url = "dept_bed_add.htm?operationType=add&defaultHos="+hosnum+"&defaultDept="+wardno+"&save_num="+save_num+"&chgitem=none";
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
			doBedAdded();
		}else if(event.keyCode==ESC_KEYCODE){
			event.preventDefault();
			event.keyCode=0;
			event.returnValue = false;
			doBedClose();
		}
	});
	
	//Combo初始化---------------------------------------------------
	var myCombo;
	function ComboInit(){
		myCombo = new dhtmlXCombo("chgitem","combo",403);
		myCombo.enableFilteringMode(false);
		myCombo.attachEvent("onKeyPressed", function(keyCode){
			if(keyCode==13){
				selectText();
			}
		});
	}
	
	
	
	function selectText(){
		$.ajax({
			async : false,   
      			cache:true,
    		 	type: 'get',
      			url: "dept/bedItemLoad.htm?input="+myCombo.getComboText()+"&dt="+new Date(),   //请求链接
      			dataType:'json',
      			error: function () {  
          			alert('loadCombo fail'); 
      			},   
      			success:function(data){
      				if("[]"!=data){
       				addOption__(myCombo,eval(data));
      				}
      			}   
		});
	}
	
	function addOption__(combo, datas){
		combo.clearAll();
		$(datas).each(function(index,elem){
			combo.addOption(elem.id,elem.value);
      		});
	}
			

</script>
	</head>

	<body>
		<div class="box1">
			<div class="box2">
				<div class="box3">
					<span style="float: left;">床位信息</span>
					<span class="boxpad"><img id="close_iframe_img"
							src="img/close.gif" align="middle" onclick="doBedClose()" /> </span>
				</div>
				<div class="box4" style="height: 225px">

					<form id="dept_form" name="dept_form" onsubmit="return false">
						<input id="operationType" name="operationType" type="hidden"
							value="${operationType}" />

						<!-- 圆头******开始 -->
						<table width="98%" border="0" cellspacing="0" cellpadding="0"
							style="margin-top: 5px;">
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
									<table id="patient_info_tb" width="537" border="0"
										cellspacing="0" cellpadding="0" style="display: block;">
										<tr>
											<td width="124" height="28" class="tit" >
												医院编码：
											</td>
											<td width="150" class="val" >
												<input id="hosnum" name="hosnum" type="text"
													class="txt4 text_field not_editable" readonly="readonly"
													value="${bed.hosnum}" />
											</td>
											<td width="95" class="tit" >
												病区代码：
											</td>
											<td class="val" >
												<input id="wardno" name="wardno" type="text"
													class="txt4 text_field not_editable" readonly="readonly"
													value="${bed.wardno}" />
											</td>
										</tr>

										<tr>
											<td class="tit" >
												科室：
											</td>
											<td class="val" style="position: relative;right: 15px; top: 3px;" >
												<select id="deptcode" name="deptcode" class="txt9"
													onchange="" style="width: 142px;">
													<option value=""></option>
													<c:forEach items="${deptList}" var="dept">
														<option value="${dept.deptcode}"
															<c:if test="${bed.deptcode!=null && dept.deptcode==bed.deptcode}">selected="selected"</c:if>>
															${dept.deptname}
														</option>
													</c:forEach>
												</select>
												<script>
													var combo_deptcode= dhtmlXComboFromSelect("deptcode");
												</script>
											</td>
											<td height="28" class="tit" >
												适用性别：
											</td>
											<td class="val" style="position: relative;right: 15px; top: 3px;">
												<select id="forsex" name="forsex"
													class="txt9 text_field_required" style="width: 142px;">
													<option value=""></option>
													<c:forEach items="${sexList}" var="sex">
														<option value="${sex.nevalue}"
															<c:if test="${bed.forsex==null?sex.isdefault == 'Y':sex.nevalue==bed.forsex}">selected="selected"</c:if>>
															${sex.contents}
														</option>
													</c:forEach>
												</select>
												<script>
													var combo_forsex= dhtmlXComboFromSelect("forsex");
													combo_forsex.attachEvent("onChange",function(){
								      					combo_forsex.getSelectedValue();
								     				 });
												</script>
											</td>
										</tr>
										<tr>
											<td height="28" class="tit" >
												房间号：
											</td>
											<td class="val" >
												<input id="roomno" name="roomno" type="text" maxlength="10"
													class="txt4 text_field" value="${bed.roomno}" />
											</td>
											<td class="tit" >
												床位号：
											</td>
											<td class="val" >
												<input id="bedno" name="bedno" type="text" 
													class="txt4 text_field_required" value="${bed.bedno}"/>
												<input id="old_bedno" name="old_bedno" type="hidden"
													value="${bed.bedno}" />
											</td>
										</tr>
										<tr>
											<td height="28" class="tit" >
												对应收费项目：
											</td>
											<td colspan="3" class="val" style="width: 415px;padding-left:4px;" align="left">
												<div id="chgitem" style="width:403px;float: left;" class="div_field" /> 
												<input id="chgitemvalue" type="hidden" value="${bed.chgitem}"/>
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
							<center id="card_manager_btn">
								<c:choose>
									<c:when test="${operationType == 'add'}">
										<button id="bedAddButton" type="button" class="btn"
											onclick="doSelfClear()" style="display: none;">
											新增
											<font color="red">(F1)</font>
										</button>
									</c:when>
									<c:when test="${operationType == 'modify'}">
										<button id="bedAddButton" type="button" class="btn"
											onclick="doSelfClear()" style="display: inline;">
											新增
											<font color="red">(F1)</font>
										</button>
									</c:when>
								</c:choose>

								<button type="button" class="btn" onclick="doBedAdded()">
									保存
									<font color="red">(F2)</font>
								</button>
								<button type="button" class="btn" onclick="doBedClose()">
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