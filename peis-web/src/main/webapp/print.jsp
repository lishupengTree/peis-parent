<%@page pageEncoding="utf-8"%>
<html>
	<head>
		<script language="javascript" src="js/gl/LodopFuncs.js"></script> 
		<object  id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0>  
        	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0></embed> 
		</object> 
		<script type="text/javascript">
			var LODOP=getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM')); 
			//打印预览
			function preview() {		
				createPage();
				LODOP.PREVIEW();		
			}
			//打印
			function print() {		
				createPage();
				LODOP.PRINT();		
			}
			//打印设计
			function design() {	
				createPage2();	
				LODOP.PRINT_DESIGN();		
			}
			function createPage() {
				LODOP.PRINT_INIT("");
				LODOP.ADD_PRINT_RECT(45,191,455,511,0,1);//1
				LODOP.ADD_PRINT_TEXT(58,347,149,40,"个人名片");//2
				LODOP.ADD_PRINT_RECT(113,302,245,130,0,2);//3
				LODOP.ADD_PRINT_TEXT(120,348,85,25,"姓名：");//4
				LODOP.ADD_PRINT_TEXT(120,460,100,25,"test1");//5
				LODOP.ADD_PRINT_TEXT(165,348,85,25,"手机号：");//6
				LODOP.ADD_PRINT_TEXT(165,460,100,25,"13136181761");//7
				LODOP.ADD_PRINT_TEXT(210,348,85,25,"联系地址");//8
				LODOP.ADD_PRINT_TEXT(210,460,100,25,"address1");//9

				LODOP.ADD_PRINT_RECT(248,301,245,131,0,2);//10
				LODOP.ADD_PRINT_TEXT(255,348,85,25,"姓名：");//11
				LODOP.ADD_PRINT_TEXT(255,460,100,25,"test2");//12
				LODOP.ADD_PRINT_TEXT(300,348,82,25,"手机号：");//13
				LODOP.ADD_PRINT_TEXT(300,460,100,25,"13136181762");//14
				LODOP.ADD_PRINT_TEXT(346,348,85,25,"联系地址：");//15
				LODOP.ADD_PRINT_TEXT(345,460,100,25,"address2");//16
				
				LODOP.SET_PRINT_STYLEA(2,"FontSize",20);
				LODOP.SET_PRINT_STYLEA(2,"TextAlign","center");
				LODOP.SET_PRINT_STYLEA(2,"TextFrame",8);
			}
			function createPage2(){
				LODOP.ADD_PRINT_TEXT(120,460,100,25,"test1");//5
				LODOP.ADD_PRINT_TEXT(165,460,100,25,"13136181761");//7
				LODOP.ADD_PRINT_TEXT(210,460,100,25,"address1");//9
				
				LODOP.ADD_PRINT_TEXT(255,460,100,25,"test2");//12
				LODOP.ADD_PRINT_TEXT(300,460,100,25,"13136181762");//14
				LODOP.ADD_PRINT_TEXT(345,460,100,25,"address2");//16
			}
			function test(){
				LODOP.PRINT_INIT("");
				LODOP.ADD_PRINT_TABLE(100,20,500,80,document.getElementById("div").innerHTML);
				//LODOP.SET_SAVE_MODE("QUICK_SAVE",true);//快速生成（无表格样式,数据量较大时或许用到）
				LODOP.SAVE_TO_FILE("新文件名.xls");
			}
		</script>
		
	</head>
	<body>
		<a href="javascript:preview()"><b>打印预览</b></a>
		<a href="javascript:print()"><b>打印</b></a>
		<a href="javascript:design()"><b>打印设计</b></a>
		<a href="javascript:test()"><b>导出</b></a>
		<div id="div">
			<table id="table" border="1" style="width:500px;height:200px;text-align:center;font-size:20px;">
				<tr style="font-weight: bold;color:blue;"><td colspan="3">个人名片</td></tr>
				<tr style="color:blue;background-color:gray"><td>姓名</td><td>手机号</td><td>联系地址</td></tr>
				<tr><td>test1</td><td>13136181761</td><td>address1</td></tr>
				<tr><td>test2</td><td>13136181762</td><td>address2</td></tr>
				<tr><td>test3</td><td>13136181763</td><td>address3</td></tr>
				<tr><td>test4</td><td>13136181764</td><td>address4</td></tr>
				<tr><td>test5</td><td>13136181765</td><td>address5</td></tr>
				<tr><td>test6</td><td>13136181766</td><td>address6</td></tr>
			</table>
		</div>
	</body>
</html>