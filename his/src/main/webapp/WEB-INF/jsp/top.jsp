<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="include.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<base href="<%=basePath%>"/>
<link href="css/top.css" rel="stylesheet" type="text/css"/>
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<script type="text/javascript" src="js/jquery.lrTool.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.21.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.blockUI.js"></script>
<script type="text/javascript" src="js/window.js"></script>
<script type="text/javascript">
	var F1_KEYCODE = 112;
	var F2_KEYCODE = 113;
	var F3_KEYCODE = 114;
	var F4_KEYCODE = 115;
	var F5_KEYCODE = 116;
	var F6_KEYCODE = 117;
	var F7_KEYCODE = 118;
	var F8_KEYCODE = 119;
	var F9_KEYCODE = 120;
	var F10_KEYCODE = 121;
	var F11_KEYCODE = 122;
	var F12_KEYCODE = 123;
	//是否按住ctrl键
	var isCtrl=false;
	
	var F1,F2,F3,F4,F5,F6,F7,F8,F9,F10,F11,ENTER,SHIFT,CTRL,CTRL_S,CTRL_Z,CTRL_X,CTRL_N,CTRL_P,KEY_37,KEY_39,CTRL_D;
	var mywin;
	function openMyWin(win,title,width,height,url)
	{
		mywin = win;
		openWin(title,width,height,url);
	}
	function forward(url,menuid)
	{
		if(url.indexOf('?')>-1)
   		{
   			document.location.href='<%=basePath%>'+url + '&menuid='+menuid;
   		}else
   		{
   			document.location.href='<%=basePath%>'+url + '?menuid='+menuid;
   			
   		}
	}
	
	document.onkeydown=function(evt){
		var event = evt?evt:window.event;
		var node = event.srcElement?event.srcElement:event.target;
		if(event.keyCode==F1_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F1);
		}else if(event.keyCode==F2_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F2);
		}else if(event.keyCode==F3_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F3);
		}else if(event.keyCode==F4_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F4);
		}else if(event.keyCode==F5_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F5);
		}else if(event.keyCode==F6_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F6);
		}else if(event.keyCode==F7_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F7);
		}else if(event.keyCode==F8_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F8);
		}else if(event.keyCode==F9_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F9);
		}else if(event.keyCode==F10_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F10);
		}else if(event.keyCode==F11_KEYCODE){
			event.keyCode=0;
			event.returnValue=false;
			if(evt){
				event.preventDefault();
			}
			eval(F11);
		}else if(event.keyCode==8){//backspace
			if((node.tagName.toLowerCase()!="input"&&node.tagName.toLowerCase()!="textarea"&&node.tagName.toLowerCase()!="text")||node.readOnly){
				event.keyCode=0;
				event.returnValue=false;
				if(evt){
					event.preventDefault();
				}
			}
		}else if(event.keyCode==13){//enter
			eval(ENTER);
		}else if(event.keyCode==16){//shift
			eval(SHIFT);
		}else if(event.keyCode==17){//ctrl
			eval(CTRL);
		}else if(isCtrl&&event.keyCode==83){//ctrl_s
			eval(CTRL_S);
		}else if(isCtrl&&event.keyCode==90){//ctrl_z
			eval(CTRL_Z);
		}else if(event.ctrlKey && event.keyCode==78){//n
			eval(CTRL_N);
		}else if(event.ctrlKey && event.keyCode==80){//p
			eval(CTRL_P);
		}else if(event.ctrlKey && event.keyCode==83){//s
			eval(CTRL_S);
		}else if(event.keyCode==37){
			eval(KEY_37);
		}else if(event.keyCode==39){
			eval(KEY_39);
		}else if(event.ctrlKey&&event.keyCode==88){//ctrl_x
			eval(CTRL_X);
		}else if(event.ctrlKey&&event.keyCode==68){//ctrl_d
			eval(CTRL_D);
		}
	}
</script>
</head>
<body>
<div class="hsp_top1" >
				<table width="100%" border="0" cellspacing="0" cellpadding="0" >
			      <tr>
			        <td width="5"></td>
			        <td style="background: url('img/login/top1.jpg') no-repeat;" width="90" height="90" >&nbsp;</td>
			        <td style="background: url('hosimg/${login_hospital.hosnum}-${login_hospital.nodecode}/img2.jpg') no-repeat;" width="270" align="right">
			        <table cellpadding="1" cellspacing="0" border="0" align="left" style="margin-left: 142px;">
			        	<tr>
			        		<td height="37">
			        			&nbsp;
			        		</td>
			        	</tr>
			        	<tr>
			        		<td>
			        			<span style="color: white;font-size: 20px;font-family:'微软雅黑';font-weight: bold;">${login_menus[systemid].name}</span>
			        		</td>
			        	</tr>
			        </table>
			        </td>
			        <td >
			        <table  border="0" cellspacing="0" cellpadding="0" class="hsp_right hsp_mar3 hsp_font"  style="width: 98%">
			        <tr>
			        <td align="right"  height="28" >
			        	<table border="0" cellspacing="0" cellpadding="0" align="right">
			              <tr align="center">
			                <td align="right">当前登录节点：<%=basePath%>&nbsp;&nbsp;&nbsp;&nbsp;</td>
			                <td align="right">${login_dept.deptname} 欢迎您，${login_user.name}！</td>
			                <td width="30" align="right"><img src="img/top4.jpg" /></td>
			                <td width="50"><a href="exit.htm" class="top_a" style="color: #fff;">退出</a></td>
			                <td width="20"><img src="img/top5.jpg" /></td>
			                <td width="50"><a href="platform.htm" class="top_a" style="color: #fff;">首页</a></td>  
			                <td width="20"><img src="img/top6.jpg" /></td>
			                <td width="50"><a href="javascript:void(0)" class="top_a" style="color: #fff;">帮助</a></td>
			              </tr>
			            </table>
			        </td>
			      </tr>
			      <tr>
			        <td  align="left">
			        <table width="215"  border="0" cellspacing="0" cellpadding="0" class="hsp_top3" align="left">
				              <tr>
				                <td align="right"><input id="search_value" name="search_value" type="text" class="hsp_top2" style="width: 125px;"/></td>
				                <td width="43" height="28">
				                	<img src="img/top7.jpg" id="search_img" style="margin-top:0px;*margin-top:-1px;" onclick="topSearch()"/>
				                </td>
				                <td>
				                	<img src="img/img_03.gif" style="cursor: pointer;" 
				                	       alt="刷新" onclick="javascript:window.location.reload();"></img>
				                </td>
				              </tr>
				    </table>
			        </td>
			      </tr>
			      <tr>
			        <td align="right">
			        	<ul class="hsp_xx" style="padding-left: 0px;">
			                <c:set var="key" value="${systemid}s" />
			                <c:forEach items="${login_menus[key]}" var="menu">
				                <c:choose>
				                	<c:when test="${menu.id == menuid}">
				                		<li id="${menu.id}" class="hsp_li2">${menu.name}<c:if test="${menu.hotkeys!=null}"><span class="hsp_font2">(${menu.hotkeys})</span></c:if></li>
				                	</c:when>
				                	<c:otherwise>
				                		<li id="${menu.id}" class="hsp_li2" onclick="openMenu('${menu.url}','${menu.id}')">${menu.name}<c:if test="${menu.hotkeys!=null}"><span class="hsp_font2">(${menu.hotkeys})</span></c:if></li>
				                	</c:otherwise>
				                </c:choose>
				                <script>
				                	<c:if test="${menu.hotkeys !=null}">
				                		${menu.hotkeys}="forward('${menu.url}','${menu.id}')";
				                	</c:if>
				                </script>
			                </c:forEach>
			            </ul>
			            <script>
			            	if(document.getElementById('${menuid}'))
			            	{
			            		document.getElementById('${menuid}').className="hsp_li1 hsp_xxhover";
			            	}
			            	function openMenu(url,menuid)
			            	{
			            		if(url=='') 
			            		{
			            			alert('菜单没有配链接地址,不能正常访问！');
			            			return;
			            		}
			            		if(url.indexOf('?')>-1)
			            		{
			            			document.location.href='<%=basePath%>'+url + '&menuid='+menuid;
			            		}else
			            		{
			            			document.location.href='<%=basePath%>'+url + '?menuid='+menuid;
			            		}
			            	}
			            </script>
			        </td>
			      </tr>
			    </table>
			        </td>
			      </tr>
				</table>
	</div>
</body>
</html>
