var newWin;
var dhxWins;

function initDhxWins(id){
	if(!dhxWins){
		dhxWins = new dhtmlXWindows();
		dhxWins.enableAutoViewport(false);
		dhxWins.attachViewportTo(id);
		dhxWins.setImagePath("dhtmlxWindows/codebase/imgs/");
	}
}

function openDhxWin(id,title,width,height,url){
	initDhxWins(id);
	var w = width;
	var h = height;
	var x = ($(window).width()-w)/2;
	var y = ($(window).height()-h)/2;
	newWin = dhxWins.createWindow(id, x, y, w, h);
	newWin.setText(title);
	newWin.setModal(true);//
	newWin.denyResize();//禁用拖动大小
	//newWin.allowResize();//启用拖动大小
	//newWin.denyPark();//禁用最小化
	newWin.allowPark();//启用最小化
	newWin.center();
	//newWin.centerOnScreen();
	//newWin.keepInViewport(true);
	newWin.attachURL(url,false);
}

function doCloseDhxWin(){
	newWin.close();
}