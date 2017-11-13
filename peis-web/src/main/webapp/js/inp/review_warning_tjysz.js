//var reviewCount=0;//需要复核的总数
//var reviewNum=0;//当前显示的条目
//var reviewObject;//需要复核的对象
//var reviewObjec_bak = [];
//var reviewss = "";
//var picflag = "0"; //添加图片的标记  0代表不添加 1代表添加
$(function () {
    var html_scroll = "<div id='scrollMsg' style='cursor:pointer;width:530px;height:35px;line-height:35px;overflow:hidden;color:white;position:absolute;left:600px;top:12px;'>"
        + "<ul>"
        + "<li id='s_li' style='height:40px;padding-left:10px;'><img src='img/msgtip.gif'/><span id='warningCount1' onclick='doClick111();' style='font-size:18px;color:#FF4040;'></span></li>"
        + "</ul>"
        + "</div>";
    $('#review_warning').append(html_scroll);
    $('#warningCount1').html("科室小结建议填写格式");
//    //设置滚动间隔
//	setInterval('AutoScroll("#scrollMsg")',5000);
//	setInterval('resetWarningSigns()',30000);
//
//	resetWarningSigns();
//	AutoScroll("#scrollMsg");
});
/**
 * 重设预警信息,
 */
function resetWarningSigns() {
    reviewCount = 0;
    $('#warningCount1').html("科室小结建议填写格式");
}

function AutoScroll(obj) {
    $(obj).find("ul:first").animate({
        marginTop: "-35px"
    }, 500, function () {
        $(this).css({marginTop: "0px"}).find("li:first").appendTo(this);
        if (reviewCount == 0) {
            $('#warningCount1').html("科室小结建议填写格式");
        } else {
        }
    });
}


function doClick111() {
    //弹出框
    openWin('科室小结建议填写格式', 500, 300, 'pexam/deptsumSuggestInfo.htm');
}