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
.not_editable{background-color: #eaeaea}
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
			var param_combo;
			
			/*--END----------------------------------*/
			
			//Now The Game Begin------------
			$(function(){
					param_combo = new dhtmlXCombo("parmvalue","parmvalue",140);
					param_combo.readonly(true);
					param_combo.clearAll();
					var defaultparms = $('#defaultparms').val();
					var parmsArr = new Array();
					parmsArr = defaultparms.split("|");
					for(i=0;i<parmsArr.length;i++){
						param_combo.addOption(parmsArr[i],parmsArr[i]);
					}
					
					//关闭层
//					parent.doClose = function(){
//						$("#hidden_iframe").attr("src","");
//						try{
//							$("div")[0].focus();
//						}catch(e){}
//						parent.$.unblockUI();
//					};

			});
			/*--BEGIN--function---*/
			function doSaveParm(){
				$.ajax({
						cache:false,async:true,type:"post",error:function(){alert("系统发生无法预知的错误！");},
			            url: "maintenance/chg_manage/save_bas_parms.htm", 
			            data:$('#meditem_form').serialize(),
			            success:function(msg){
			            	if(msg == "success"){
			            		//重新加载GRID和TREE
			            		parent.window.mywin.grid_load($('#scope').val());//type=2:加载子项目
			            		alert("保存成功！");
			            	}
			            	else if(msg == "unknow"){alert("出现无法预知的错误！");}
			            }
		          });
			}
			/*--END---------------*/
		</script>
	</head>
	
	<body>
	<form id="meditem_form" name="meditem_form" onsubmit="return false">
				<input type="hidden" id="showtype" name="showtype" value="${param.showtype}"/>
				<table cellspacing="0" cellpadding="0">
				  <tr>
				    <td width="10"><img src="img/new_yuan1.jpg" /></td>
				    <td width="520" background="img/new_yuan2.jpg"></td>
				    <td width="10"><img src="img/new_yuan3.jpg" /></td>
				  </tr>
				  <tr>
				    <td background="img/new_yuan4.jpg">&nbsp;</td>
				    <td>
				    	<table width="100%" cellspacing="5" cellpadding="0" border="0">
							<tr>
								<td align="right" height="28" width="80">参数名称：</td>
								<td width="150" align="left" >
                                <input id="parmid" name="parmid" type="hidden"	value="${b.parmid}"/>
									<input id="parmname" name="parmname" type="text"
										class="txt not_editable" readonly="readonly" style="width: 140px;" value="${b.parmname}"/>
								</td>
								<td width="75" align="right">参数值：</td>
								<td width="150" align="left">
                                	<p class="pdt selectRed">
                                	  <select id="parmvalue" name="parmvalue" style="width: 140px;">
                                	    <option value="${b.parmvalue}">${b.parmvalue}</option>
                              	    </select>
                              	    </p>
                                    <input id="defaultparms" name="defaultparms" type="hidden" value="${b.defaultparms}" />
                               	  </td>
							</tr>
					    <tr>
					      <td align="right">作用范围：</td>
					      <td style="text-align: left;" ><input id="scope" name="scope" type="text"
										class="txt not_editable" readonly="readonly"  value="${b.scope}" style="width:140px;"/></td>
							  <td align="right">参数分类：</td>
							  <td align="left"><input style="width:138px;" class="txt not_editable" readonly="readonly" type="text" id="sysname" name="sysname" value="${b.sysname}"/></td>
						  </tr>
							<tr>
								<td align="right"><span class="pdt">可否编辑：</span></td>
								<td style="text-align: left;">
                          		<input style="width:140px;" class="txt not_editable" readonly="readonly" id="canedit1" type="text" name="canedit1" value="<c:if test="${b.canedit=='0'}">其他</c:if><c:if test="${b.canedit=='1'}">区域</c:if><c:if test="${b.canedit=='2'}">医院</c:if>"/> 
                                   <input type="hidden" id="canedit" name="canedit" value="${b.canedit}"/>
								</td>
								<td align="right"></td>
								<td align="left" ></td>
							</tr>
							<tr>
								<td height="28" align="right">作用描述：</td>
								<td colspan="3" align="left" >
                                        <textarea name="descriptions" rows="3" class="txt not_editable" readonly="readonly" id="descriptions" style="width:390px;height:40px">${b.descriptions}</textarea>
								</td>
							</tr>
							<tr>
								<td height="28" align="right">备注：</td>
								<td colspan="3" align="left">
									<textarea name="comments" rows="3" class="txt not_editable"  readonly="readonly" id="comments" style="width:390px;height:40px">${b.comments}</textarea>
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
