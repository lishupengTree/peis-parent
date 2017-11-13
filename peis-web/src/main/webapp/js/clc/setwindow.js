
/*--- 页面自动适应代码----*/
$(function () {
	$("#left_area,#right_area").css("height", $(window).height() - 92);
	var right = $("#right_area");
	var left = $("#left_area");
	$('#left_area_tree').css('height',$(window).height() - 90 - 6 - 45);
	$(".dhx_tabcontent_zone").css("width", right.width() - 2);
	$(".dhx_tabcontent_zone").css("height", right.height() - 25);
	$(".dhx_tabbar_lineA,.dhx_tabbar_lineB").css("top", "24px");
	$(".dhx_tabbar_lineA,.dhx_tabbar_lineB").css("width", right.width() - 2);
	
	//绑定隐藏按钮
	$("#middle_line").bind("click", function () {
		if ("none" != $("#left_area").css("display")) {
			$("#middle_line img").attr("src", "img/ysz3_1.gif");
			$("#left_area").hide(0);
			var right1 = $("#right_area");
			var left1 = $("#left_area");
			$(".dhx_tabcontent_zone,.dhx_tabbar_lineA,.dhx_tabbar_lineB").css("width", right.width() - 2);
			$(".dhx_tabcontent_zone div").css("width", "100%");
			$("#mainArea_11").css("width","100%");
			$("iframe").css("width","100%");
		} else {
	
			$("#left_area").show(0);
			$("#middle_line img").attr("src", "img/ysz3.gif");
			var right2 = $("#right_area");
			var left2 = $("#left_area");
			$(".dhx_tabcontent_zone,.dhx_tabbar_lineA,.dhx_tabbar_lineB").css("width",right.width() - 2);
			$(".dhx_tabcontent_zone div").css("width", "100%");
		}
	});
});
$(window).resize(function () {
	$("#left_area,#right_area").css("height", $(window).height() - 92);
	var right = $("#right_area");
	var left = $("#left_area");
	$('#left_area_tree').css('height',$(window).height() - 90 - 6 - 45);
	$(".dhx_tabcontent_zone").css("width", right.width() - 2);
	$(".dhx_tabcontent_zone").css("height", right.height() - 25);
	$(".dhx_tabbar_lineA,.dhx_tabbar_lineB").css("top", "24px");
	$(".dhx_tabbar_lineA,.dhx_tabbar_lineB").css("width", right.width() - 2);
});


 



