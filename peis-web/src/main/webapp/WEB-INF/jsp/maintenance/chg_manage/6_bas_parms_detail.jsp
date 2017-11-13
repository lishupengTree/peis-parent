<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
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
		<title>系统参数维护</title>

		<link rel="stylesheet" type="text/css"
			href="dhtmlxCombo/codebase/dhtmlxcombo.css" />
		<style type="text/css">
			div.gridbox_dhx_custom table.hdr td{
				font-family:微软雅黑;
				font-size:12px;
				font-weight: bold;
				vertical-align:top;
			}
			div.gridbox table.obj.row20px tr td{
			}
			.dhx_combo_list{
			}
			</style>
<style type="text/css">

body { 
	font-size:13px;
 	font-family:Microsoft YaHei,Lucida Grande,Helvetica,Tahoma,Arial,sans-serif; 
 	color:#000;
 	margin-left:20px;
 	margin-top:10px;
 	margin-right:10px;
 }
.txt{
	height:18px;
	vertical-align:middle;
	border:1px solid #93AFBA;
	line-height:18px;
}
		.btn{
			background:url(img/btn.jpg) no-repeat; 
			width:92px; 
			height:32px; 
			text-align: center;
			border:0;
			line-height:32px;
			font-size:14px; 
			font-family:Microsoft YaHei,Lucida Grande,Helvetica,Tahoma,Arial,sans-serif;
			color:#000; 
			cursor: pointer;
			margin:5px 20px 0 0 ;
		}
.red_line{border:1px solid red;};
.txt7 {
	width: 395px
}
.txt4 {
	width: 140px;
}  /*new*/
.txt9 {
	width: 142px
}
.not_editable {
	background-color: #fffff
}
.pdt span{
float: left;

}
.selectRed span div{
border: 1px solid red;line-height: 17px
}
</style>
		<script type="text/javascript" src="dhtmlxGrid/codebase/dhtmlxcommon.js"></script>
		<script type="text/javascript" src="dhtmlxTabbar/codebase/dhtmlxtabbar.js"></script>
	  	<script type="text/javascript" src="js/util.js"></script>
		<script type="text/javascript" src="js/jquery-1.6.1.js"></script>
		<script type="text/javascript" src="js/demoTools.js"></script>	
		<script type="text/javascript" src="js/dhtmlxcombo.js"></script>
		<script type="text/javascript" src="js/clc/comboTool.js"></script>
		<script>window.dhx_globalImgPath = "dhtmlxCombo/codebase/imgs/";</script>
		<script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcommon.js"></script>
		<script type="text/javascript" src="dhtmlxCombo/codebase/dhtmlxcombo.js"></script>
		<script type="text/javascript" src="dhtmlxCombo/codebase/ext/dhtmlxcombo_whp.js"></script>
		<script type="text/javascript" src="js/window.js"></script>
		<script type="text/javascript" src="js/jquery.blockUI.js"></script>
		
		<script type="text/javascript">
			/*--BEGIN------------全局变量----------------*/
			var scope_combo;   //范围下拉框
			var sysname_combo; //分类下拉框
			var param_combo;
			
			/*--END----------------------------------*/
			
			//Now The Game Begin------------
			$(function(){
				//初始化分类下拉框和范围下拉框
				scope_combo = new dhtmlXCombo("scope","scope",140);
					//加载scope_combo下的数据
					$.get("maintenance/chg_manage/get_parms_scope.htm",function(json){ 
							scope_combo.clearAll();
							$(eval(json)).each(function(index,elem){
								scope_combo.addOption(elem.scope,elem.scope);
							});
					});
				sysname_combo = new dhtmlXCombo("sysname","sysname",140);
					//加载sysname_combo下的数据
					$.get("maintenance/chg_manage/get_bas_sysname.htm",function(json){ 
							sysname_combo.clearAll();
							$(eval(json)).each(function(index,elem){
								sysname_combo.addOption(elem.sysname,elem.sysname);
							});
					});
				sysname_combo.attachEvent("onBlur",function(){
					$('#sysname2').val(sysname_combo.getComboText());
					
				});
				param_combo = new dhtmlXCombo("parmvalue","parmvalue",140);
				param_combo.readonly(true);
					param_combo.clearAll();
					var defaultparms = $('#defaultparms').val();
					var parmsArr = new Array();
					parmsArr = defaultparms.split("|");
					for(i=0;i<parmsArr.length;i++){
						param_combo.addOption(parmsArr[i],parmsArr[i]);
					}
					
 				//是否可编辑单选事件
				$('#canedit1,#canedit2,#canedit3').click(function(){
					$('#canedit').val($(this).val());
				});
 				
				//关闭层
/*				parent.doClose = function(){
					$("#hidden_iframe").attr("src","");
					try{
						$("div")[0].focus();
					}catch(e){}
					parent.$.unblockUI();
				};
*/
			});
			
			//添加默认参数
			function addParams(){
				var defaultparms = $('#defaultparms').val();
				//必须以逗号隔开
				param_combo.clearAll();
				var parmsArr = new Array();
				parmsArr = defaultparms.split("|");
				for(i=0;i<parmsArr.length;i++){
					param_combo.addOption(parmsArr[i],parmsArr[i]);
				}
				
			}
			/*--BEGIN--function---*/
			function doSaveParm(){
				if(!checkPageData()) return false; //验证不通过
				
				$.ajax({
						cache:false,async:true,type:"post",error:function(){alert("系统发生无法预知的错误！");},
			            url: "maintenance/chg_manage/save_bas_parms.htm", 
			            data:$('#meditem_form').serialize(),
			            success:function(msg){
			            	if(msg == "success"){
			            		$('#showtype').val("0");
			            		//重新加载GRID和TREE
			            		parent.window.mywin.mygrid.clearAndLoad("maintenance/chg_manage/load_parms.htm?type=2" + "&scope=" + encodeURI(encodeURI($.trim(scope_combo.getComboText()))) + "&stamp=" + Math.random());//type=2:加载子项目
			            		parent.window.mywin.loadTree();
			            		alert("保存成功！");
			            	}
			            	else if(msg == "unknow"){alert("出现无法预知的错误！");}
			            }
		          });
			}
			
			function checkPageData(){
				var parmname = $.trim($('#parmname').val());
				var parmvalue = $.trim(param_combo.getComboText());
				var scope = $.trim(scope_combo.getComboText());//scope_combo.getSelectedText();
				var sysname = $.trim(sysname_combo.getComboText());//sysname_combo.getSelectedText();
				var descriptions = $('#descriptions').val();
				var comments = $('#comments').val();
				//参数名不能重名
				if(parmname == ""){alert("参数名不能为空！");return false;}      
				//if(parmname.length > 20){alert("参数名的长度不能大于20!");return false;}
				var returnFlag = 0;
				$.ajax({
					cache:false,async:false,error:function(){alert("系统发生无法预知的错误！");},
		            url: "maintenance/chg_manage/check_bas_parms.htm", 
		            data:"parmname=" + encodeURI(encodeURI(parmname)) + "&showtype="+$('#showtype').val() + "&parmid="+$('#parmid').val(),
		            success:function(msg){
		            	if(msg == "success"){returnFlag = 1;}
		            	else if(msg == "exist"){alert("已存在的参数名！");returnFlag = 0}
		            }
				}); if(returnFlag == 0){return false;}
				
				if(parmvalue == ""){alert("参数值不能为空！");return false;}
				//if(parmvalue.length > 20){alert("参数值的长度不能大于20!");return false;}
				if(scope == ""){alert("作用范围不能为空！");return false;}
				if(scope.length > 20){alert("参数范围的长度不能大于20!");return false;}
				if(comments.length > 50){alert("备注的长度不能大于50!");return false;}
				if(descriptions.length > 500){alert("备注的长度不能大于500!");return false;}
				if(sysname == ""){alert("参数分类不能为空！");return false;} 
				return true;
			}
			/*--END---------------*/
		</script>
	</head>
	
	<body style="margin: 0px;padding: 0px;">
	<form id="meditem_form" name="meditem_form" onsubmit="return false">
				<input type="hidden" id="showtype" name="showtype" value="${param.showtype}"/>
				<table cellspacing="0" cellpadding="0" border="0" width="95%" align="center">
				  <tr>
				    <td width="10"><img src="img/new_yuan1.jpg" /></td>
				    <td  background="img/new_yuan2.jpg"></td>
				    <td width="10"><img src="img/new_yuan3.jpg" /></td>
				  </tr>
				  <tr>
				    <td background="img/new_yuan4.jpg">&nbsp;</td>
				    <td height="32">
				    	<table width="100%" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td align="right" height="28" width="80">参数名称：</td>
								<td width="150" align="left" >
                                <input id="parmid" name="parmid" type="hidden"	value="${b.parmid}"/>
									<input id="parmname" name="parmname" type="text"
										class="txt red_line" style="width: 138px;" value="${b.parmname}"/>
								</td>
								<td width="75" align="right">默认参数：</td>
								<td width="150" align="left">
                                	<p class="pdt selectRed">
                                	  <select id="parmvalue" name="parmvalue" style="width: 140px;">
                                	    <option value="${b.parmvalue}">${b.parmvalue}</option>
                              	    </select>
                              	    </p>
                               	  </td>
							</tr>
							<tr>
							  <td align="right"  height="32">参数范围：</td>
							  <td colspan="3" align="left"><input id="defaultparms" name="defaultparms" type="text"
										class="txt" onblur="addParams()" value="${b.defaultparms}" style="width:393px;"/></td>
						  </tr>
					    <tr>
					      <td align="right"  height="32">作用范围：</td>
					      <td style="text-align: left;" class="pdt selectRed"><select id="scope" name="scope" class="txt red_line" style="width: 140px;">
					        <option value="${b.scope}">${b.scope}</option>
					        </select></td>
							  <td align="right">&nbsp;</td>
							  <td align="left">&nbsp;</td>
						  </tr>
							<tr>
								<td align="right"  height="32"><span class="pdt">可否编辑：</span></td>
								<td style="text-align: left;" class="pdt selectRed">
                                	<input id="canedit1" type="radio" name="canedit_1" value="1"  <c:if test="${b.canedit=='1' || param.showtype=='1'}">checked</c:if>/>区域
									<input id="canedit2" type="radio" name="canedit_1" value="2"  <c:if test="${b.canedit=='2'}">checked</c:if>/>医院
									<input id="canedit3" type="radio" name="canedit_1" value="0"  <c:if test="${b.canedit=='0'}">checked</c:if>/>其它
									<input id="canedit" type="hidden" name="canedit" value="${b.canedit}"/> 
								</td>
								<td align="right">参数分类：</td>
								<td align="left" class="pdt selectRed">
									<select id="sysname" name="sysname1" style="width: 140px;">
										<option value="${b.sysname}">${b.sysname}</option>
									</select>
									<input type="hidden" id="sysname2" name="sysname" value="${b.sysname}"/>
								</td>
							</tr>
							<tr>
								<td height="28" align="right">作用描述：</td>
								<td colspan="3" align="left" >
                                        <textarea name="descriptions" rows="3" class="txt" id="descriptions" style="width:393px;height:40px">${b.descriptions}</textarea>
								</td>
							</tr>
							<tr>
								<td height="50" align="right" >备注：</td>
								<td colspan="3" align="left">
									<textarea name="comments" rows="3" class="txt" id="comments" style="width:393px;height:40px">${b.comments}</textarea>
								</td>
							</tr>
						</table>
				    </td>
				    <td background="img/new_yuan5.jpg">&nbsp;</td>
				  </tr>
				   <tr>
				    <td><img src="img/new_yuan6.jpg" /></td>
				    <td background="img/new_yuan7.jpg"></td>
				    <td><img src="img/new_yuan8.jpg" /></td>
				  </tr>
				</table>
				<table cellspacing="0" cellpadding="0" style="margin-top: 10px;margin-left:160px">
						<tr>
							<td height="28" colspan="2" align="center">
								<button type="button" class="btn" onclick="doSaveParm();">
								保存</button>
							</td>
							<td height="28" colspan="2" align="center">
								<button type="button" class="btn" onclick="parent.doClose()">
								关闭</button>
							</td>
						</tr>
				</table>
		</form>
	</body>

</html>
