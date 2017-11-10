var LODOP;
$(document).ready(function() {
	$("body").prepend("<div style='display:none'><object id='LODOP' classid='clsid:2105C259-1E0C-4534-8141-A753534CB4CA' width='0' height='0'><embed id='LODOP_EM' type='application/x-print-lodop' width='0' height='0'/></object></div>");
	LODOP = getLodop(document.getElementById('LODOP'), document.getElementById('LODOP_EM'));
	//LODOP.SET_LICENSES("","383984592107103110104114561061","","");
	//LODOP.SET_LICENSES("","383984592107103110104114561061","","");
	LODOP.SET_LICENSES("杭州清普信息技术有限公司","964657080837383919278901905623","","");
   //alert("SET_LICENSES执行了");
	
});
function getLodop(oOBJECT, oEMBED) {
	/***************************************************************************
	 * 本函数根据浏览器类型决定采用哪个对象作为控件实例： IE系列、IE内核系列的浏览器采用oOBJECT，
	 * 其它浏览器(Firefox系列、Chrome系列、Opera系列、Safari系列等)采用oEMBED。
	 **************************************************************************/
	var strHtml1 = "<br><font color='#FF00FF'>打印控件未安装!点击这里<a href='/his/install_lodop.exe'>执行安装</a>,安装后请刷新页面或重新进入。</font>";
	var strHtml2 = "<br><font color='#FF00FF'>打印控件需要升级!点击这里<a href='/his/install_lodop.exe'>执行升级</a>,升级后请重新进入。</font>";
	var strHtml3 = "<br><br><font color='#FF00FF'>(注：如曾安装过Lodop旧版附件npActiveXPLugin,请在【工具】->【附加组件】->【扩展】中先卸载它)</font>";
	var LODOP = oEMBED;
	
	try {
		if (navigator.appVersion.indexOf("MSIE") >= 0){
			LODOP = oOBJECT;
		}
		if ((LODOP == null) || (typeof (LODOP.VERSION) == "undefined")) {
			if (navigator.userAgent.indexOf('Firefox') >= 0) {
				$("body").prepend(strHtml3);
			}else{
				$("body").prepend(strHtml1);
			}
		} else if (LODOP.VERSION < "6.0.1.0") {
			$("body").prepend(strHtml2);
		}
		// *****如下空白位置适合调用统一功能:*********

		// *******************************************
		return LODOP;
	} catch (err) {
		$("body").prepend("Error:" + strHtml1);
		return LODOP;
	}
}

function LODOP_show(path) {
	LODOP_config(path);
	LODOP.PREVIEW();
}
//检查条码打印
function LODOP_barcodeExem(json) {
	/*	var date = new Date();
		var d= date.getFullYear();
		var m = date.getMonth()+1;
		var r = date.getDate();
		var h = date.getHours();
		var mu = date.getMinutes();
		var datestring = d+"-"+m+"-"+r+" "+h+":"+mu;*/
	if($("#exemCodeFlag").val()=="N"){
		return;
	}
	if($("#fwzLisExemCodeFlag").val()=="N"&&$("#isZxHospital").val()=="N"){
		return;
	}
	LODOP.PRINT_INIT("打印检查条码");
	//var flag=LODOP.SET_PRINTER_INDEX("barcode");
	var lodopNumber=-1;
	for(var i=0;i<LODOP.GET_PRINTER_COUNT();i++){
		var lodopName=LODOP.GET_PRINTER_NAME(i);
		if(lodopName.indexOf("barcode")!=-1){
			lodopNumber=i;
			break;
		}
	}
	var testSize=$("#testsize").val();
	if (lodopNumber>=0){
		if("1"==testSize){
			for(var i=0;i<json.length;i++){
				if(json[i][7]=="N"){
					LODOP.PRINT_INITA(0,0,"50mm","30mm","二维条码（小）");
					LODOP.SET_PRINT_PAGESIZE(1,"50mm","30mm","");
					var itemname=json[i][0];
					//说明是彩超多部位
					if((itemname.indexOf("一部位")!=-1||itemname.indexOf("二部位")!=-1)&&itemname.indexOf("彩超")!=-1){
						itemname=itemname.replace("彩超常规检查(一部位)(","彩常-").replace("彩超常规检查(≥二部位)(","彩常-").replace("彩超浅表器官检查（一部位）(","彩浅-").replace("彩超浅表器官检查（≥二部位）(","彩浅-").replace(")","").replace("）","");
					}
					var heigth=8;
					if(itemname.length<12){
						heigth=20;
					}else{
						heigth=1;
					}
					LODOP.ADD_PRINT_TEXT(heigth,8,165,46,itemname);//项目名称
					LODOP.SET_PRINT_TEXT_STYLEA(1,"",9,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(47,8,165,25,"诊断："+json[i][10]);//诊断
					LODOP.SET_PRINT_TEXT_STYLEA(2,"",9,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(64,8,165,25,"主诉："+json[i][9]);//主诉
					LODOP.SET_PRINT_TEXT_STYLEA(3,"",9,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(80,8,165,20,"开单医生："+json[i][8]);//开单医生
					LODOP.SET_PRINT_TEXT_STYLEA(4,"",9,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(96,8,165,24,json[i][1]+"  "+json[i][3]+"  "+json[i][4]);//姓名
					LODOP.SET_PRINT_TEXT_STYLEA(5,"",9,0,0,0,1,"");
					//线条
					LODOP.ADD_PRINT_RECT(93, 8, 174, 1,0, 1);
					LODOP.SET_PRINTER_INDEX(lodopNumber);
					LODOP.PRINT();
					//LODOP.PRINT_DESIGN();
				}else{
				    /**取单联**/
				}
			}
		}else{
			for(var i=0;i<json.length;i++){
				if(json[i][7]=="N"){
					var zssize=66;
					var zsname=json[i][9].length>22?json[i][9].substring(0,22):json[i][9];//主诉
					//如果主诉名字大于13就会换行
					if(zsname.length>13){
						zssize=56;
					}
					LODOP.PRINT_INITA(5,0,"60mm","40mm","二维条码（小）");
					LODOP.SET_PRINT_PAGESIZE(1,"60mm","40mm","");
					var itemname=json[i][0];
					//说明是彩超多部位
					if((itemname.indexOf("一部位")!=-1||itemname.indexOf("二部位")!=-1)&&itemname.indexOf("彩超")!=-1){
						itemname=itemname.replace("彩超常规检查(一部位)(","彩常-").replace("彩超常规检查(≥二部位)(","彩常-").replace("彩超浅表器官检查（一部位）(","彩浅-").replace("彩超浅表器官检查（≥二部位）(","彩浅-").replace(")","").replace("）","");
						//var itemname=json[i][0].length>11?json[i][0].substring(0,11):json[i][0];
						LODOP.ADD_PRINT_TEXT(8,14,215,60,itemname);//项目名称
						LODOP.SET_PRINT_TEXT_STYLEA(1,"",8,1,0,0,1,"");
					}else{
						//var itemname=json[i][0].length>11?json[i][0].substring(0,11):json[i][0];
						LODOP.ADD_PRINT_TEXT(8,14,205,60,itemname);//项目名称
						LODOP.SET_PRINT_TEXT_STYLEA(1,"",10,1,0,0,1,"");
					}
					LODOP.ADD_PRINT_TEXT(86,156,65,25,json[i][2]);//处方号
					LODOP.SET_PRINT_TEXT_STYLEA(2,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(44,17,195,25,"诊断："+json[i][10]);//诊断
					LODOP.SET_PRINT_TEXT_STYLEA(3,"",9,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(zssize,57,165,25,zsname);//主诉
					LODOP.SET_PRINT_TEXT_STYLEA(4,"",9,0,0,0,1,"");
					//LODOP.ADD_PRINT_IMAGE(38,16,128,46,"<img border='0' src='"+json[i][6]+"'/>");//二维条码
					//LODOP.ADD_PRINT_IMAGE(73,16,128,46,"<img border='0' src='../img/white.png'/>");//白色图片
					//LODOP.ADD_PRINT_TEXT(55,5,190,20,json[i][2]+" "+json[i][1]+" "+json[i][3]+" "+json[i][4]+"岁");
					LODOP.ADD_PRINT_TEXT(86,17,190,20,"开单医生："+json[i][8]);//开单医生
					LODOP.SET_PRINT_TEXT_STYLEA(5,"",9,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(104,16,100,24,json[i][1]);//姓名
					LODOP.SET_PRINT_TEXT_STYLEA(6,"",13,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(108,88,100,24,"性别："+json[i][3]);//性别
					LODOP.SET_PRINT_TEXT_STYLEA(7,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(108,150,100,24,"年龄："+json[i][4]);//年龄
					LODOP.SET_PRINT_TEXT_STYLEA(8,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(127,17,206,24,"打印："+json[i][5]);//打印时间
					LODOP.SET_PRINT_TEXT_STYLEA(9,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(66,17,50,25,"主诉：");//主诉
					LODOP.SET_PRINT_TEXT_STYLEA(10,"",9,1,0,0,1,"");
					//线条
					LODOP.ADD_PRINT_RECT(100, 8, 215, 1,0, 1);
					LODOP.SET_PRINTER_INDEX(lodopNumber);
					LODOP.PRINT();
					//LODOP.PRINT_DESIGN();
				}else{
				    /**
					var zssize=60;
					var zsname=json[i][9].length>22?json[i][9].substring(0,22):json[i][9];//主诉
					//如果主诉名字大于11就会换行
					if(zsname.length>11){
						zssize=50;
					}
					LODOP.PRINT_INITA(5,0,"60mm","40mm","二维条码（小）");
					LODOP.SET_PRINT_PAGESIZE(1,"60mm","40mm","");
					LODOP.ADD_PRINT_TEXT(8,14,65,25,"取单联");//检验联
					LODOP.SET_PRINT_TEXT_STYLEA(1,"",12,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(14,70,140,25,json[i][2]);//处方号
					LODOP.SET_PRINT_TEXT_STYLEA(2,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(33,17,195,25,"诊断："+json[i][10]);//诊断
					LODOP.SET_PRINT_TEXT_STYLEA(3,"",10,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(zssize,57,151,25,zsname);//主诉
					LODOP.SET_PRINT_TEXT_STYLEA(4,"",9,0,0,0,1,"");
					//LODOP.ADD_PRINT_IMAGE(38,16,205,46,"<img border='0' src='"+json[i][6]+"'/>");//二维条码
					//LODOP.ADD_PRINT_IMAGE(73,16,205,46,"<img border='0' src='../img/white.png'/>");//白色图片
					//LODOP.ADD_PRINT_TEXT(55,5,190,20,json[i][2]+" "+json[i][1]+" "+json[i][3]+" "+json[i][4]+"岁");
					LODOP.ADD_PRINT_TEXT(80,14,190,20,"");//项目名称
					LODOP.SET_PRINT_TEXT_STYLEA(5,"",10,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(104,16,100,24,json[i][1]);//姓名
					LODOP.SET_PRINT_TEXT_STYLEA(6,"",13,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(108,88,100,24,"性别："+json[i][3]);//性别
					LODOP.SET_PRINT_TEXT_STYLEA(7,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(108,150,100,24,"年龄："+json[i][4]);//年龄
					LODOP.SET_PRINT_TEXT_STYLEA(8,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(127,17,206,24,"打印："+json[i][5]);//打印时间
					LODOP.SET_PRINT_TEXT_STYLEA(9,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(82,17,190,20,"开单医生："+json[i][8]);//开单医生
					LODOP.SET_PRINT_TEXT_STYLEA(10,"",10,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(60,17,50,25,"主诉：");//主诉
					LODOP.SET_PRINT_TEXT_STYLEA(11,"",10,1,0,0,1,"");
					//线条
					LODOP.ADD_PRINT_RECT(100, 8, 215, 1,0, 1);
					LODOP.SET_PRINTER_INDEX("barcode");
					LODOP.PRINT();
					//LODOP.PRINT_DESIGN();**/
				}
			}
		}
	}	
}
//检验条码打印
function LODOP_barcode(json) {
	/*	var date = new Date();
		var d= date.getFullYear();
		var m = date.getMonth()+1;
		var r = date.getDate();
		var h = date.getHours();
		var mu = date.getMinutes();
		var datestring = d+"-"+m+"-"+r+" "+h+":"+mu;*/
	if($("#lisCodeFlag").val()=="N"){
		return;
	}
	if($("#fwzLisExemCodeFlag").val()=="N"&&$("#isZxHospital").val()=="N"){
		return;
	}
	LODOP.PRINT_INIT("打印检验条码");
	//var flag=LODOP.SET_PRINTER_INDEX("barcode");
	var lodopNumber=-1;
	for(var i=0;i<LODOP.GET_PRINTER_COUNT();i++){
		var lodopName=LODOP.GET_PRINTER_NAME(i);
		if(lodopName.indexOf("barcode")!=-1){
			lodopNumber=i;
			break;
		}
	}
	var testSize=$("#testsize").val();
	if (lodopNumber>=0){
		//条码尺寸
		if("1"==testSize){
			for(var i=0;i<json.length;i++){
				if(json[i][7]=="N"){
					LODOP.PRINT_INITA(-2,0,"50mm","30mm","二维条码（小）");
					LODOP.SET_PRINT_PAGESIZE(1,"50mm","30mm","");
					//var itemname=json[i][0].length>11?json[i][0].substring(0,11):json[i][0];
					var itemname=json[i][0];
					LODOP.ADD_PRINT_TEXT(4,14,170,25,itemname);//项目名称
					LODOP.SET_PRINT_TEXT_STYLEA(1,"",10,1,0,0,1,"");
					LODOP.ADD_PRINT_BARCODE(33,6,163,46,"128C",json[i][2]);
					//LODOP.ADD_PRINT_IMAGE(30,16,128,46,"<img border='0' src='"+json[i][6]+"'/>");//二维条码
					LODOP.ADD_PRINT_TEXT(82,13,163,20,json[i][1]+"   "+json[i][3]+"   "+json[i][4]);//姓名
					LODOP.SET_PRINT_TEXT_STYLEA(3,"",10,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(100,13,163,20,"开单医生："+json[i][8]);//开单医生
					LODOP.SET_PRINT_TEXT_STYLEA(4,"",10,1,0,0,1,"");
					//线条
					//LODOP.ADD_PRINT_RECT(78,15,160, 1,0, 1);
					//LODOP.SET_PRINTER_INDEX("barcode");
					LODOP.SET_PRINTER_INDEX(lodopNumber);
					LODOP.PRINT();
					//LODOP.PRINT_DESIGN();
				}else{
					if($("#lisQDCodeFlag").val()=="Y"){
						LODOP.PRINT_INITA(-2,0,"50mm","30mm","二维条码（小）");
						LODOP.SET_PRINT_PAGESIZE(1,"50mm","30mm","");
						LODOP.ADD_PRINT_TEXT(4,14,170,25,"取单联");//项目名称
						LODOP.SET_PRINT_TEXT_STYLEA(1,"",10,1,0,0,1,"");
						LODOP.ADD_PRINT_IMAGE(30,13,163,46,"<img border='0' src='"+json[i][6]+"'/>");//二维条码
						LODOP.ADD_PRINT_TEXT(82,13,163,20,json[i][1]+"   "+json[i][3]+"   "+json[i][4]);//姓名
						LODOP.SET_PRINT_TEXT_STYLEA(3,"",10,1,0,0,1,"");
						//LODOP.ADD_PRINT_RECT(78,15,160, 1,0, 1);
						//LODOP.SET_PRINTER_INDEX("barcode");
						LODOP.SET_PRINTER_INDEX(lodopNumber);
						LODOP.PRINT();
						//LODOP.PRINT_DESIGN();
					}
				}
			}
		}else{
			for(var i=0;i<json.length;i++){
				if(json[i][7]=="N"){
					LODOP.PRINT_INITA(5,0,"60mm","40mm","二维条码（小）");
					LODOP.SET_PRINT_PAGESIZE(1,"60mm","40mm","");
					var itemname=json[i][0].length>11?json[i][0].substring(0,11):json[i][0];
					LODOP.ADD_PRINT_TEXT(8,14,205,25,itemname);//项目名称
					LODOP.SET_PRINT_TEXT_STYLEA(1,"",12,1,0,0,1,"");
					//LODOP.ADD_PRINT_TEXT(48,152,65,25,json[i][2]);//处方号
					//LODOP.SET_PRINT_TEXT_STYLEA(2,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_BARCODE(33,16,163,46,"128C",json[i][2]); 
					//LODOP.ADD_PRINT_IMAGE(38,16,128,46,"<img border='0' src='"+json[i][6]+"'/>");//二维条码
					//LODOP.ADD_PRINT_IMAGE(73,16,128,46,"<img border='0' src='../img/white.png'/>");//白色图片
					//LODOP.ADD_PRINT_TEXT(55,5,190,20,json[i][2]+" "+json[i][1]+" "+json[i][3]+" "+json[i][4]+"岁");
					LODOP.ADD_PRINT_TEXT(82,17,190,20,"开单医生："+json[i][8]);//开单医生
					LODOP.SET_PRINT_TEXT_STYLEA(5,"",10,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(104,16,100,24,json[i][1]);//姓名
					LODOP.SET_PRINT_TEXT_STYLEA(6,"",13,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(108,88,100,24,"性别："+json[i][3]);//性别
					LODOP.SET_PRINT_TEXT_STYLEA(7,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(108,150,100,24,"年龄："+json[i][4]);//年龄
					LODOP.SET_PRINT_TEXT_STYLEA(8,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(127,17,206,24,"打印："+json[i][5]);//打印时间
					LODOP.SET_PRINT_TEXT_STYLEA(9,"",10,0,0,0,1,"");
					//线条
					//LODOP.ADD_PRINT_RECT(100, 8, 215, 1,0, 1);
					//LODOP.SET_PRINTER_INDEX("barcode");
					LODOP.SET_PRINTER_INDEX(lodopNumber);
					LODOP.PRINT();
					//LODOP.PRINT_DESIGN();
				}else{
					/**
					LODOP.PRINT_INITA(5,0,"60mm","40mm","二维条码（小）");
					LODOP.SET_PRINT_PAGESIZE(1,"60mm","40mm","");
					LODOP.ADD_PRINT_TEXT(8,14,65,25,"取单联");//检验联
					LODOP.SET_PRINT_TEXT_STYLEA(1,"",12,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(14,70,140,25,json[i][2]);//处方号
					LODOP.SET_PRINT_TEXT_STYLEA(2,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_IMAGE(38,16,205,46,"<img border='0' src='"+json[i][6]+"'/>");//二维条码
					LODOP.ADD_PRINT_IMAGE(73,16,205,46,"<img border='0' src='../img/white.png'/>");//白色图片
					//LODOP.ADD_PRINT_TEXT(55,5,190,20,json[i][2]+" "+json[i][1]+" "+json[i][3]+" "+json[i][4]+"岁");
					LODOP.ADD_PRINT_TEXT(80,14,190,20,"");//项目名称
					LODOP.SET_PRINT_TEXT_STYLEA(5,"",10,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(104,16,100,24,json[i][1]);//姓名
					LODOP.SET_PRINT_TEXT_STYLEA(6,"",13,1,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(108,88,100,24,"性别："+json[i][3]);//性别
					LODOP.SET_PRINT_TEXT_STYLEA(7,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(108,150,100,24,"年龄："+json[i][4]);//年龄
					LODOP.SET_PRINT_TEXT_STYLEA(8,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(127,17,206,24,"打印："+json[i][5]);//打印时间
					LODOP.SET_PRINT_TEXT_STYLEA(9,"",10,0,0,0,1,"");
					LODOP.ADD_PRINT_TEXT(82,17,190,20,"开单医生："+json[i][8]);//开单医生
					LODOP.SET_PRINT_TEXT_STYLEA(10,"",10,1,0,0,1,"");
					//线条
					LODOP.ADD_PRINT_RECT(100, 8, 215, 1,0, 1);
					LODOP.SET_PRINTER_INDEX("barcode");
					LODOP.PRINT();
					//LODOP.PRINT_DESIGN();
					**/
				}
			}
		}
	}	
}
	
//正常打印
function LODOP_print(mingxi,patientinfo,type) {
	LODOP.PRINT_INIT("ticket");
	var falg=LODOP.SET_PRINTER_INDEX("ticket");
	for(var i=0;i<2;i++){
		if(falg){
			//alert("指定打印机");
			LODOP_config(mingxi,patientinfo,type);
			//LODOP.PRINT();
			LODOP.PREVIEW();
			//LODOP.PRINT_DESIGN();
		}else{
			//alert("没指定打印机");
			LODOP_config(mingxi,patientinfo,type);
			LODOP.PREVIEW();
			//LODOP.PRINT_DESIGN();
		}
	}
}

//重打
function LODOP_print2(mingxi,patientinfo,type) {
	LODOP.PRINT_INIT("ticket");
	var falg=LODOP.SET_PRINTER_INDEX("ticket");
	for(var i=0;i<1;i++){
		if(falg){
			//alert("指定打印机");
			LODOP_config(mingxi,patientinfo,type);
			//LODOP.PRINT();
			LODOP.PREVIEW();
			//LODOP.PRINT_DESIGN();
		}else{
			//alert("没指定打印机");
			LODOP_config(mingxi,patientinfo,type);
			LODOP.PREVIEW();
			//LODOP.PRINT_DESIGN();
		}
	}
}