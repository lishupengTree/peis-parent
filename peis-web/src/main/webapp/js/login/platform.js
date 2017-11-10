var redStop = "";
var comp = "";
function openMenu(id, status) {
    var rollback = "";
    if (id == '08') {

    }
    if (status == '') return;
    if (id == '02') {
        four();
        if (redStop == "F") {
            openWin('黄色提示', '660', '250', "order/indexPrompt.htm?message=" + encodeURI(encodeURI(comp)) + "&type=yellow");
            window.setTimeout(function () {
                openMenu1(id, status);
            }, 3000);
        } else if (redStop == "T") {
            openWin('红色提示', '660', '250', "order/indexPrompt.htm?message=" + encodeURI(encodeURI(comp)) + "&type=red");
            window.setTimeout(function () {
                openMenu1(id, status);
            }, 3000);
        } else {

            openMenu1(id, status);
        }
    } else {

        openMenu1(id, status);
    }

}
function openMenu1(id, status) {
    if (status == '') return;
    if (id == '16') {
        var flag = "";
        $.ajax({
            url: "getNowServerTime.htm?&date=" + new Date(),
            async: false,
            cache: false,
            error: function () {
                return false;
            },
            success: function (reply) {
                flag = reply;
            }
        });
        //	if(flag=="true"){
        //		alert("系统正忙，该功能在11点后开放");
        //		return;
        //	}
    }
    if (id == '05') {	//判断是否为门诊护士站
        //判断是否启用皮试叫号
        $.ajax({
            async: false,
            cache: false,
            url: "number/isSkintestRead.htm?date=" + new Date(),
            error: function () {
            },
            success: function (data) {
                //如设置中为Y，则启用皮试叫号
                if (data == 'Y') {
                    var params = "channelmode=0,width=1000,height=600,depended=no,left=" + window.screen.width + ",top=0,location=0,menubar =0,alwaysLowered=0, scrollbars=0,directories =0,resizable=0,alwaysRaised=1,z-look=1,fullscreen=0";
                    var win1 = window.open("number/skin_testshow.htm?hosnum=" + $("#hosnum_skintest").val() + "&nodecode=" + $("#nodecode_skintest").val() + "&t=" + new Date(), "皮试叫号", "'" + params + "'");
                }
            }
        });
    }
    //测试节点
    if (id == '00') {
        var str = $("#menu_" + id + "").text() + 'autoComeIN.htm?key=' + $("#login_userjobno").val() + "&dept=" + $("#login_deptcode").val() + "&shayne=" + $("#login_wardid").val();
        myOpen(str, 'window');
        window.setTimeout(function () {
            window.focus();
        }, 1);
        //var str='<%=basePath%>autoComeIN.htm?key='+$("#login_userjobno").val()+"&dept="+$("#login_deptcode").val()+"&shayne="+$("#login_wardid").val();
        //window.location = str;
        return;
    }
    //document.location.href = "system.htm?id="+id;
    //window.open('system.htm?id='+id,'window','fullscreen=2');
    myOpen('system.htm?id=' + id, 'window');
    window.setTimeout(function () {
        window.focus();
    }, 1);
    var tflag = '0';
    if (id == '04') {
        $.ajax({
            async: false,
            cache: false,
            type: 'post',
            url: "expirydate.htm",
            data: {"id": id, "date": new Date()},
            error: function () {
            },
            success: function (data) {
                if (data > "0") {
                    var isExpiry = confirm("有-" + data + "-种药品即将过期，是否进入有效期管理?");
                    if (isExpiry == true) {
                        var str = '<%=basePath%>others.htm?menuid=' + id + "&validto_warning=Y";
                        window.location = str;
                        window.setTimeout(function () {
                            window.focus();
                        }, 1000);

                        return;
                    } else {

                    }
                }
            }
        });
    }
}


function myOpen(winurl, winname) {
    window.location = winurl;
    //objWin= window.open( winurl,winname, "scrollbars=yes,status=yes,resizable=yes,top=0,left=0,width="+(screen.availWidth-18)+",height="+(screen.availHeight-60));
    //objWin.focus();
    //return true;
}
function setWin() {
    $('#content').css('height', $(window).height() - 285);
}


