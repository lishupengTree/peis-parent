var grid_patient_appoint;//人员列表
var examid;//预约id
var groupinfo;//groupid
var isclick;
var clickexamid;//选中的预约编号
var clikcstatus;
var bool = false;
var pageIndex = 0;//页面索引初始值  
var pageSize = 15;//每页显示条数初始化，修改显示条数，修改这里即可 
var selectexamid = 'default';//选中的体检单位编号--初始化为“default”
var method = "";
var searchvalue = "";
var loadByPexamid = "";//读卡后赋值的
var loadByIdNum = "";//读卡后赋值的
var myCalendar, myCalendar1;
var pagesize;  //预约列表的页大小

$(document).ready(function () {
    adjustDisp();
    var autoheight = ($(window).height() - 140);
    $("#dleft").css("height", autoheight);
    $("#leftnav").css("height", autoheight - 80);
    //计算分页的大小  
    pagesize = Math.floor((autoheight - 40) / 62) - 1;

    $("#main_info").css("height", autoheight - 22);
    $("#grid_patient_appoint").css("height", autoheight - 25);
    $("#examitems0").css("height", autoheight - 220);

    $("#dsigle").css("height", autoheight - 252);
    $("#dsigle0").css("height", autoheight - 252);
    $("#grid_patient_appoint2").css("height", autoheight - 65);

    grid_patient_appoint = new dhtmlXGridObject('grid_patient_appoint2');
    grid_patient_appoint.setImagePath("dhtmlxGrid/codebase/imgs/");
    //                               0     1   2    3    4      5   6      7    8     9    10     11   12
    grid_patient_appoint.setHeader("序号,体检号,姓名,性别,出生日期,年龄,证件类别,证件号,农保号,职业,婚姻状况,备注,编号");
    grid_patient_appoint.setInitWidths("50,80,60,50,84,40,60,*,100,50,120,80,80");
    grid_patient_appoint.setColAlign("center,center,center,center,center,center,center,center,center,center,center,center,center");
    grid_patient_appoint.setColTypes("ed,ed,ed,ed,ed,ed,ed,ed,ed,ed,ed,ed,ed");
    grid_patient_appoint.setSkin("dhx_custom");
    grid_patient_appoint.attachEvent("onRowDblClicked", opendisplay);
    grid_patient_appoint.setColumnHidden(1, true);  //体检号
    grid_patient_appoint.setColumnHidden(8, true);  //农保号
    grid_patient_appoint.setColumnHidden(11, true);  //备注
    grid_patient_appoint.setColumnHidden(12, true);  //编号
    grid_patient_appoint.init();

    //初始化
    var imgs = [];
    for (var i = 0; i < 3; i++) {
        var obj = document.createElement("img");
        obj.className = "dhx_combo_img";
        obj.src = "dhtmlxCombo/codebase/imgs/combo_select_dhx_blue.gif";
        imgs.push(obj);
    }

    combo_examType.DOMelem.appendChild(imgs[0]);
    combo_examType.readonly(true, true);
    combo_examType.addOption([["职工体检", "职工体检"], ["商业体检", "商业体检"]]);
    //屏蔽了["学生体检","学生体检"]

    //时间控件初始化
    myCalendar = new dhtmlXCalendarObject(["bookdate"]);
    myCalendar1 = new dhtmlXCalendarObject(["findname"]);
    myCalendar1.attachEvent("onClick", function (date) {
        //alert("Date is set to "+date)
        findAppointment(1);
    })

    combo_appointType.DOMelem.appendChild(imgs[1]);
    combo_appointType.readonly(true, true);
    combo_appointType.addOption([["Y", "是"], ["N", "否"]]);

    combo_tjyear.DOMelem.appendChild(imgs[2]);
    //combo_tjyear.readonly(true,true);
    combo_tjyear.addOption([["2014", "2014"], ["2015", "2015"], ["2016", "2016"], ["2017", "2017"], ["2018", "2018"], ["2019", "2019"], ["2020", "2020"]]);

    //折扣失去焦点
    $('#discount').blur(function () {
        var unitprice_1 = $("#unitprice").val();//单价
        var examqty_1 = $("#examqty").val();//人数
        var discount_1 = $("#discount").val();//金额
        var discamt_1 = changeTwoDecimal_f(unitprice_1 * examqty_1 * discount_1);
        $('#discamt').val(discamt_1);
    });

    //单价失去焦点
    $('#unitprice').blur(function () {
        var unitprice_1 = $("#unitprice").val();//单价
        var examqty_1 = $("#examqty").val();//人数
        var discount_1 = $("#discount").val();//金额
        var discamt_1 = changeTwoDecimal_f(unitprice_1 * examqty_1 * discount_1);
        $('#discamt').val(discamt_1);
    });

    //人数失去焦点
    $('#examqty').blur(function () {
        var unitprice_1 = $("#unitprice").val();//单价
        var examqty_1 = $("#examqty").val();//人数
        var discount_1 = $("#discount").val();//金额
        var discamt_1 = changeTwoDecimal_f(unitprice_1 * examqty_1 * discount_1);
        $('#discamt').val(discamt_1);
    });

    //体检名称回车事件
    $("#examname").keydown(function (ev) {
        var event = ev || window.event;
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        if (keyCode == 13) {
            setTimeout(function () {
                combo_examType.openSelect();
            }, 100);
        }
    });
    //体检类型回车 体检单位获取焦点
    combo_examType.attachEvent("onKeyPressed", function (keyCode) {
        if (keyCode == 13) {
            $("#unitname").focus();
        }
    });

    //体检单位回车
    $("#unitname").keydown(function (ev) {
        var event = ev || window.event;
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        if (keyCode == 13) {
            setTimeout(function () {
                myCalendar.show();
            }, 100);
        }
    });

    //时间控件点击事件
    myCalendar.attachEvent("onClick", function (date) {
        //设置体检名称
        $("#examname").val(getExamname(date));
        $("#unitprice").focus();
    });


    //单价回车
    $("#unitprice").keydown(function (ev) {
        var event = ev || window.event;
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        if (keyCode == 13) {
            $("#examqty").focus();
        }
    });

    //人数回车
    $("#examqty").keydown(function (ev) {
        var event = ev || window.event;
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        if (keyCode == 13) {
            $("#discount").focus();
        }
    });

    //折扣回车
    $("#discount").keydown(function (ev) {
        var event = ev || window.event;
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        if (keyCode == 13) {
            $("#discamt").focus();
        }
    });

    //金额回车
    $("#discamt").keydown(function (ev) {
        var event = ev || window.event;
        var keyCode = checkBrowser() == "FireFox" ? event.which : event.keyCode;
        if (keyCode == 13) {
            $("#combo_appointType").focus();
        }
    });


    findAppointment('1');
    //分页的自适应高度
    //$("#fypage").css("height", autoheight - 50);

});

//分页
function createPagination1(pageCount) {//创建分页标签
    //分页，pageCount是总条目数，这是必选参数，其它参数都是可选
    $("#pagination1").pagination(pageCount, {
        callback: pageCallback,
        prev_text: '上一页',       //上一页按钮里text  
        next_text: '下一页',       //下一页按钮里text  
        items_per_page: pageSize,  //显示条数  
        num_display_entries: 6,    //连续分页主体部分分页条目数  
        current_page: pageIndex,   //当前页索引  
        num_edge_entries: 2        //两侧首尾分页条目数  
    });
}

//分页回调函数
function pageCallback(index, jq) {//翻页回调
    pageIndex = index;
    loadAppPatData(clickexamid);
    return false;
}

/*
 *加载人员列表条数
 *examid预约id
 */
function loadAppPatCount(examid) {
    $.ajax({
        async: false,
        cache: false,
        ifModified: true,
        type: "GET",
        url: "pexam/loadAppPatCount.htm",
        data: "now=" + new Date().getMilliseconds() + "&examid=" + examid,
        dataType: "json",
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            if (data.status) {
                var pageCount = data.value;
                pageIndex = 0;
                createPagination1(pageCount);
            } else {
                alert(data.message);
            }
        }
    });
}

function loadAppPatData(examid) {
    var urlData = "";
    if ("loadAll" == method) {
        urlData = "examid=" + selectexamid + "&method=" + method + "&time=" + (new Date()).valueOf() + "&index=" + pageIndex + "&size=" + pageSize;
    } else if ("loadBySearch" == method) {
        urlData = "examid=" + selectexamid + "&method=" + method + "&searchvalue=" + searchvalue + "&time=" + (new Date()).valueOf() + "&index=" + pageIndex + "&size=" + pageSize;
    } else if (method == 'loadByPexamid') {
        urlData = "time=" + (new Date()).valueOf() + "&examid=" + selectexamid + "&loadByPexamid=" + loadByPexamid + "&method=" + method;
    } else if (method == 'loadByIdNum') {
        urlData = "time=" + (new Date()).valueOf() + "&examid=" + selectexamid + "&loadByIdNum=" + loadByIdNum + "&method=" + method;
    }
    grid_patient_appoint.clearAndLoad("pexam/getAppPatList.htm?examid=" + examid + "&index=" + pageIndex + "&size=" + pageSize + "&urlData=" + encodeURI(encodeURI(urlData)));
    setTimeout(function () {
        if ("loadBySearch" == method) {
            var rowIds = grid_patient_appoint.getAllRowIds();
            if (rowIds == '' || rowIds == null) {
                alert("无此人信息！");
            }
        }
    }, 500);
}

//新建人员双击grid
function opendisplay(rowId, colIndex) {
    //var pexamid=grid_patient_appoint.cells(rowId,10).getValue();
    //openWin('人员录入','770','238','pexam/newpatientpar.htm?examid='+clickexamid+'&pexamid='+pexamid);
    var str = "dblclick";
    var examid = grid_patient_appoint.cells(rowId, 1).getValue();
    var pexamid = grid_patient_appoint.cells(rowId, 11).getValue();
    openWin('修改体检人员信息', '760', '750', 'phyexam/personInfoEntry.htm?examid=' + examid + '&pexamid=' + pexamid + "&str=" + str);
}

//尚未被调用
function additems(itemcode, itemname) {
    //验证已选项目是否存在
    var trs = document.getElementById('examitems').rows;
    for (var i = 0; i < trs.length; i++) {
        var name = $(trs[i].cells[2]).text();
        if (itemname == name) {
            alert("此项目已存在");
            return false;
        }
    }
    var myDate = new Date();
    var stamp = myDate.getMilliseconds();
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/getitembycode.htm?stamp=" + stamp + "&itemcode=" + itemcode,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            //得当前行数
            var trs = document.getElementById('examitems').rows;
            var jsons = eval('(' + data + ')');
            var price = $('#totalprice').text();
            for (i = 0; i < jsons.length; i++) {
                var index = i + 1 + trs.length;
                var sprice = jsons[i].cost;
                price = FloatAdd(price, sprice);
                $('#examitems tbody').append("<tr onclick='remove_item(this)' style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20'>" + index + "</td><td>" + jsons[i].itemname + "</td><td width='100' align='right'>" + jsons[i].cost + "</td><td width='100' align='right' style='display:none'>" + jsons[i].itemcode + "</td></tr>");
            }
            $('#exnum').text(jsons.length + trs.length);
            $('#totalprice').text(price);
        }
    });
}

//添加项目验证方式改变了
function additems2(itemcode, itemname) {
    //验证已选项目是否存在
    var trs = document.getElementById('examitems').rows;
    var groupids = "";
    for (var i = 0; i < trs.length; i++) {
        var name = $(trs[i].cells[2]).text();
        groupids = groupids + $(trs[i].cells[4]).text() + ",";
        if (itemname == name) {
            alert("此项目在存在");
            return false;
        }
    }
    groupids = groupids.substring(0, groupids.length - 1);

    var myDate = new Date();
    var stamp = myDate.getMilliseconds();
    //根据groupid查询，下面的id是否包含

    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/checkitemsingroup.htm?stamp=" + stamp + "&itemcode=" + itemcode + "&groupids=" + groupids,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            if (data == 'exist') {
                alert('此项目在套餐中已存在');
            } else {

                $.ajax({
                    async: false,
                    cache: false,
                    type: 'get',
                    url: "pexam/getitembycode.htm?stamp=" + stamp + "&itemcode=" + itemcode,
                    error: function () {
                        alert('fail');
                    },
                    success: function (data) {
                        var trs = document.getElementById('examitems').rows;
                        var jsons = eval('(' + data + ')');
                        var price = $('#totalprice').text();
                        for (i = 0; i < jsons.length; i++) {
                            var index = i + 1 + trs.length;
                            var sprice = jsons[i].cost;
                            price = FloatAdd(price, sprice);
                            $('#examitems tbody').append("<tr onclick='remove_item(this)' style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20'>" + index + "</td><td>" + jsons[i].itemname + "</td><td width='100' align='right'>" + jsons[i].cost + "</td><td width='100' align='right' style='display:none'>" + jsons[i].itemcode + "</td><td width='100' align='right' style='display:none'>n</td></tr>");
                        }

                        var trs = document.getElementById('examitems').rows;
                        $('#exnum').text(trs.length);
                    }
                });
            }

        }
    });
}

function addgroup(groupid) {
    $('#examitems tr').remove();
    groupinfo = groupid;
    var myDate = new Date();
    var stamp = myDate.getMilliseconds();
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/getitemsbygroup.htm?stamp=" + stamp + "&groupid=" + groupid,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            var jsons = eval('(' + data + ')');
            var price = 0;
            for (i = 0; i < jsons.length; i++) {
                var index = i + 1;
                var sprice = jsons[i].cost;
                price = FloatAdd(price, sprice);
                $('#examitems').append("<tr onclick='remove_item(this)' style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20'>" + index + "</td><td>" + jsons[i].itemname + "</td><td width='100' align='right'>" + jsons[i].cost + "</td><td width='100' align='right' style='display:none'>" + jsons[i].itemcode + "</td></tr>");
            }
            $('#exnum').text(jsons.length);
            $('#totalprice').text(price);
        }
    });
}

var gprice = 0;
//不需拆分项目添加套餐
function addgroup2(groupid, groupname, cost) {
    //非隐藏元素个数
    var groupnum = $("#examitems tr:visible").size();
    var groupindex = groupnum + 1;
    var trs = document.getElementById('examitems').rows;
    for (var i = 0; i < trs.length; i++) {
        var name = $(trs[i].cells[2]).text();
        if (groupname == name) {
            alert("此套餐已存在");
            return false;
        }
    }
    groupinfo = groupid;
    var gpname = groupname;
    //$('#examitems').append("<tr onclick='remove_item(this)' style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20'>"+groupindex+"</td><td>"+gpname+"</td><td width='100' align='right'>"+cost+"</td><td width='100' align='right' style='display:none'>"+groupid+"</td><td width='100' align='right' style='display:none'>y</td></tr>");
    var html = "<tr onclick='remove_item(this)' style='cursor: pointer;'>"
        + "<td width='20' height='28'><img src='img/jiej4.jpg' /></td>"
        //+"<td width='20'>"+groupindex+"</td><td id='"+groupid+"' onmouseout='removedetails(\""+groupid+"\")' onmouseover='showdetails(\""+groupid+"\")'>"+gpname+"</td>"
        + "<td width='20'>" + groupindex + "</td><td id='" + groupid + "'>" + gpname + "</td>"
        + "<td width='100' align='right'>" + cost + "</td><td width='100' align='right' style='display:none'>" + groupid + "</td>"
        + "<td width='100' align='right' style='display:none'>y</td>"
        + "</tr>"
    $('#examitems').append(html);
    gprice = $('#totalprice').text();
    gprice = FloatAdd(gprice, cost);
    $('#totalprice').text(gprice);
    var trs = document.getElementById('examitems').rows;
    $('#exnum').text(trs.length);
}
//新建
function creatNew() {
    var myDate = new Date();
    var stamp = myDate.getMilliseconds();
    var str = "yuyue";
    if (examid != "" && examid != null) {
        $.ajax({
            async: false,
            cache: false,
            type: 'get',
            url: "pexam/getmaininfo.htm?stamp=" + stamp + "&examid=" + clickexamid,
            error: function () {
                alert('fail');
            },
            success: function (data) {
                if (data == 'fail') {
                    alert("加载数据失败");
                } else {
                    var jsons = eval('(' + data + ')');
                    var Pexam_main = jsons[0];
                    var unit = Pexam_main.examname;
                    var examtype = Pexam_main.examtype;
                    openWin('新建体检人员', '760', '760', 'phyexam/personInfoEntry.htm?examid=' + clickexamid + '&unit=' + encodeURI(encodeURI(unit)) + "&examtype=" + encodeURI(encodeURI(examtype)) + "&str=" + str);
                }
            }
        });
    } else {
        alert('请先选择一个预约');
        return;
    }

}

//单击预约列表
function setMainValue(examids) {
    bool = true;
    examid = examids;
    clickexamid = examids;
    method = "loadAll";
    cleanYYunit();//清空预约信息

    $("#importPersonnel").click(function () {
        openWin('导入人员', '450', '140', 'pexamNew/importPersonnel.htm?examid=' + clickexamid + "&method=new");
    });
    var myDate = new Date();
    var stamp = myDate.getMilliseconds();
    //获取预约信息
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/getmaininfo.htm?stamp=" + stamp + "&examid=" + examid,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            if (data == 'fail') {
                alert("加载数据失败");
            } else {
                var jsons = eval('(' + data + ')');
                var Pexam_main = jsons[0];
                $('#examid').val(Pexam_main.examid);
                $('#examname').val(Pexam_main.examname);
                $('#unitname').val(Pexam_main.unitname);
                //$('#salesman').val(Pexam_main.salesman);
                $('#unitprice').val(Pexam_main.unitprice);
                $('#examqty').val(Pexam_main.examqty);
                $('#discount').val(Pexam_main.discount);
                var totalamt = changeTwoDecimal_f(Pexam_main.totalamt);
                $('#discamt').val(totalamt);
                combo_examType.setComboText(Pexam_main.examtype);
                var appoint = Pexam_main.appointtype == null ? "" : Pexam_main.appointtype;
                if (Pexam_main.appointtype == "Y") {
                    appoint = "是";
                    combo_appointType.setComboValue("Y");
                } else {
                    appoint = "否";
                    combo_appointType.setComboValue("N");
                }
                combo_appointType.setComboText(appoint);
                var tjyear = Pexam_main.tjyear == null ? "" : Pexam_main.tjyear;
                combo_tjyear.setComboText(tjyear);
                var d2 = fSecondToTime(Pexam_main.bookdate.time);
                $('#bookdate').val(d2);
            }
        }
    });

    //获取预约的套餐
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/getitemsinfo.htm?stamp=" + stamp + "&examid=" + examid,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            if (data == 'fail') {
                alert("加载数据失败！");
            } else {
                $('#examitems tr').remove();
                var jsons = eval('(' + data + ')');
                var price = 0;
                for (var i = 0; i < jsons.length; i++) {
                    var index = i + 1;
                    var sprice = jsons[i].cost;
                    price = FloatAdd(price, sprice);
                    var html = "<tr onclick='remove_item(this)' style='cursor: pointer;'>"
                        + "<td width='20' height='28'><img src='img/jiej4.jpg' /></td>"
                        + "<td width='20' >" + index + "</td>"
                        //+"<td id='"+jsons[i].itemid+"' onmouseout='removedetails(\""+jsons[i].itemid+"\")' onmouseover='showdetails(\""+jsons[i].itemid+"\")'>"+jsons[i].itemname+"</td>"
                        + "<td id='" + jsons[i].itemid + "'>" + jsons[i].itemname + "</td>"
                        + "<td width='100' align='right'>" + jsons[i].cost + "</td>"
                        + "<td width='100' align='right' style='display:none'>" + jsons[i].itemid + "</td>"
                        + "<td width='100' align='right' style='display:none'>" + jsons[i].isgroup + "</td>"
                        + "</tr>"
                    $('#examitems').append(html);
                }
                $('#exnum').text(jsons.length);
                $('#totalprice').text(price);
            }
        }
    });

    //分页显示
    loadAppPatCount(examid);
}

function fSecondToTime(iIpt) {
    var dt1 = new Date(iIpt);
    return dateToStr(dt1);
}

function dateToStr(datetime) {
    var year = datetime.getFullYear();
    var month = datetime.getMonth() + 1;//js从0开始取
    var date = datetime.getDate();
    var hour = datetime.getHours();
    var minutes = datetime.getMinutes();
    var second = datetime.getSeconds();

    if (month < 10) {
        month = "0" + month;
    }
    if (date < 10) {
        date = "0" + date;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    if (second < 10) {
        second = "0" + second;
    }
    var time = year + "-" + month + "-" + date;
    return time;
}

//移去套餐
function remove_item(obj) {
    var totalprice = $('#totalprice').text();
    var pirce = $(obj).children("td").eq(3).html()
    totalprice = FloatSub(totalprice, pirce);
    $('#totalprice').text(totalprice);
    $(obj).remove();
    $("#exnum").text($("#exnum").text() - 1);
    var trs = document.getElementById('examitems').rows;
    for (var i = 0; i < trs.length; i++) {
        $(trs[i].cells[1]).text((i + 1));
    }
}

function changetab() {
    $('#groupitems').css("display", "block");
    $('#sigelitems').css("display", "none");
    $('#tcxm').addClass("tijian_hover");
    $('#dgxm').removeClass("tijian_hover");
}

function changetab2() {
    $('#groupitems').css("display", "none");
    $('#sigelitems').css("display", "block");
    $('#tcxm').removeClass("tijian_hover");
    $('#dgxm').addClass("tijian_hover");
    clikcstatus = '2';
}

//选卡切换至“体检信息”
function changeli() {
    $('#main_info').css("display", "block");
    $('#grid_patient_appoint').css("display", "none");
    $('#tjxxspan').addClass("xxhover");
    $('#rylbspan').removeClass("xxhover");
}

//选卡切换至“人员列表”
function changeli2() {
    $('#main_info').css("display", "none");
    $('#grid_patient_appoint').css("display", "block");
    $('#tjxxspan').removeClass("xxhover");
    $('#rylbspan').addClass("xxhover");
    //分页显示
    loadAppPatCount(clickexamid);
}

//浮点数加法运算   
function FloatAdd(arg1, arg2) {
    var r1, r2, m;
    try {
        r1 = arg1.toString().split(".")[1].length
    } catch (e) {
        r1 = 0
    }
    try {
        r2 = arg2.toString().split(".")[1].length
    } catch (e) {
        r2 = 0
    }
    m = Math.pow(10, Math.max(r1, r2))
    return (arg1 * m + arg2 * m) / m
}

//浮点数减法运算
function FloatSub(arg1, arg2) {
    var r1, r2, m, n;
    try {
        r1 = arg1.toString().split(".")[1].length
    } catch (e) {
        r1 = 0
    }
    try {
        r2 = arg2.toString().split(".")[1].length
    } catch (e) {
        r2 = 0
    }
    m = Math.pow(10, Math.max(r1, r2));
    //动态控制精度长度
    n = (r1 >= r2) ? r1 : r2;
    return ((arg1 * m - arg2 * m) / m).toFixed(n);
}

function uploadPersonnel() {
    if ($('#examid').val() == "") {
        alert("请填写体检信息表并保存!");
        return
    }
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/check.htm?examid=" + $('#examid').val(),
        error: function () {
            alert('请新建体检信息表或点击保存');
        },
        success: function (data) {
            if (data == 'no') {
                alert('请点击保存');
                return;
            } else {
                openWin('上传人员文档', '420', '200', 'pexam/importPersonne2.htm?examid=' + $('#examid').val() +
                    '&examname=' + $('#examname').val() + '&unitname=' + $('#unitname').val() + '&bookdate=' + $('#bookdate').val());
            }
        }
    });
}

//预约新建
function clearScreen() {
    //删除已选列表中的套餐
    $("#leftnav li").each(
        function () {
            $(this).removeClass("tijian_11");
        }
    );
    cleanYYunit();
    $('#examid').val(getExamid());
    $('#examname').val(getExamname(new Date()));
    $('#bookdate').val((new Date()).format('yyyy-MM-dd'));

}
//生成预约的名称
function getExamname(datetime) {
    //datetime = new Date();
    var year = datetime.getFullYear();
    var month = datetime.getMonth() + 1;//js从0开始取
    var date = datetime.getDate();
    if (month < 10) {
        month = "0" + month;
    }
    if (date < 10) {
        date = "0" + date;
    }
    return year + '年' + month + '月' + date + '日预约体检';
}

var cid;
//预约保存
function saveAppointment() {
    if ($('#examid').val() == "") {
        alert("请输入体检编号!");
        return;
    }
    if ($('#examname').val() == "") {
        alert("请输入体检名称!");
        return;
    }
    if ($('#bookdate').val() == "") {
        alert("请选择预约时间!");
        return;
    }
    if (combo_examType.getComboText() == "") {
        alert("请选择体检类型!");
        return;
    }
    var basicinfo = [];//基本信息
    var itemsinfo = [];//体检项目
    cid = $('#examid').val();

    basicinfo.push({
        examid: $('#examid').val(),//体检编号
        examname: $('#examname').val(),//体检名称
        unitname: $('#unitname').val(),//体检单位
        //salesman:$('#salesman').val(),//业务员
        bookdate: $('#bookdate').val(),//预约时间
        unitprice: $('#unitprice').val(),//单价
        examqty: $('#examqty').val(),//人数
        discount: $('#discount').val(),//折扣
        discamt: $('#discamt').val(),//金额
        examtype: combo_examType.getComboText(),
        appointtype: combo_appointType.getActualValue(),
        tjyear: combo_tjyear.getComboText()
    });
    var trs = document.getElementById('examitems').rows;
    if (trs.length == 0) {
        //alert("请选择体检套餐！");
        //return
    }

    for (var i = 0; i < trs.length; i++) {
        var itname = $(trs[i].cells[2]).text();
        itemsinfo.push({
            itemname: itname,//体检项目名称
            cost: $(trs[i].cells[3]).text(),//价格
            itemid: $(trs[i].cells[4]).text(),//项目id
            isgroup: $(trs[i].cells[5]).text()//是否是套餐
        });
    }

    $.ajax({
        url: "pexam/saveappointment.htm",
        type: "post",
        data: "json1=" + toJSON(basicinfo) + "&json2=" + toJSON(itemsinfo) + "&time=" + (new Date()).valueOf(),
        success: function (reply) {
            if (reply.indexOf('fail') != -1) {
                alert(reply);
            } else {
                showMsg("数据保存成功!");
                //setTimeout('refresh()',1000);
                setTimeout('findAppointment(1)', 1000);
                $("#" + cid).click();
            }
        }
    });
}

//刷新页面
function refresh() {
    $.ajax({
        async: false,
        cache: false,
        type: 'get',
        url: "pexam/refreshnav.htm",
        error: function () {
            alert('fail');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('服务器内部错误！');
            } else {
                var jsons = eval('(' + data + ')');
                $("#leftnav li").remove();
                for (var i = 0; i < jsons.length; i++) {
                    var index = i + 1;
                    var d2 = fSecondToTime(jsons[i].bookdate.time);
                    $("#leftnav ul").append("<li style='cursor: pointer;' onclick='setMainValue(" + jsons[i].examid + ");docolor(this);' id='" + jsons[i].examid + "' style='cursor: pointer;' ><table width='240' border='0' cellspacing='0' cellpadding='0'><tr><td width='40' height='25' class='font2'><strong>(" + index + ")</strong></td><td ><strong>" + jsons[i].examname + "</strong></td></tr><tr><td>&nbsp;</td><td height='25' class='tijian_9'>预约时间：" + d2 + "</td></tr></table></li>");
                }
                $("#" + cid).click();
            }
        }
    });
}

//预约列表搜索
function findAppointment(currentPage) {
    var findname = $('#findname').val();
    $.ajax({
        url: "pexam/showmainlist.htm",
        type: "post",
        data: "method=findappointment&findname=" + findname + "&time=" + (new Date()).valueOf() + "&currentPage=" + currentPage + "&pagesize=" + pagesize,
        error: function () {
            alert("ajax请求失败！");
        },
        success: function (data) {
            var jsons = eval('(' + data + ')');
            if (jsons.length < 1) {
                alert("无此预约");
            } else {
                var currentPage = jsons[0].currentPage;
                var pageLast = jsons[0].pageLast;
                var pageUp = jsons[0].pageUp;
                var pageDown = jsons[0].pageDown;
                var info = jsons[0].list;
                $("#leftnav li").remove();
                $("#leftnav div").remove();
                for (i = 0; i < info.length; i++) {
                    var index = i + 1;
                    var d2 = fSecondToTime(info[i].bookdate.time);

                    //$("#leftnav").append("<ul class='tijian_8' style='width:100%'></ul>");
                    $("#leftnav ul").append("<li style='cursor: pointer;' onclick='setMainValue(\"" + info[i].examid + "\");docolor(this);' id='" + info[i].examid + "' style='cursor: pointer;' ><table width='240' border='0' cellspacing='0' cellpadding='0'><tr><td width='40' height='25' class='font2'><strong>(" + index + ")</strong></td><td ><strong>" + info[i].examname + "</strong></td></tr><tr><td>&nbsp;</td><td height='25' class='tijian_9'>预约时间：" + d2 + "</td></tr></table></li>");
                }
                //清除分页信息
                $("#fypage").html("");
                var html = "第" + currentPage + "/" + pageLast + "页<a href='javascript:void(0)' onclick='findAppointment(1);return false;'> 首页 </a><a href='javascript:void(0)' onclick='findAppointment(" + pageUp + ");return false;'> 上一页 </a><a href='javascript:void(0)' onclick='findAppointment(" + pageDown + ");return false;'> 下一页 </a><a href='javascript:void(0)' onclick='findAppointment(" + pageLast + ");return false;'> 尾页 </a>";
                $("#fypage").append(html);
            }
        }
    });
}

//删除预约
function delAppointment() {
    if (clickexamid == '') {
        alert("请选择要删除的预约!");
    } else {
        if (window.confirm('你确定要删除这条预约？')) {
            $.ajax({
                url: "pexam/delappointment.htm",
                type: "post",
                data: "method=delAppointment&examid=" + clickexamid + "&time=" + (new Date()).valueOf(),
                error: function () {
                    alert("ajax请求失败！");
                },
                success: function (data) {
                    if (data == "fail") {
                        alert("服务器错误！");
                    } else {
                        location.reload();
                    }
                }
            });
        }
    }
}

//样式设置
function docolor(obj) {
    $("#leftnav li").each(function () {
        $(this).removeClass("tijian_11");
    });
    $(obj).addClass("tijian_11");
}

function showdetails(id) {
    var tag = "#" + id;
    var divid = id + "div";
//	var a="<div id='"+divid+"' class='cen2' onmouseover='test()'  name='dcd'><table width='191' border='0' cellspacing='0' cellpadding='0'  name='dcd'> <tr name='dcd'><td height='28' background='img/cen1.png' valign='bottom' name='dcd'><span class='cen3' name='dcd'>此套餐包含如下体检项目</span></td></tr><tr name='dcd'><td background='img/cen2.png' name='dcd'><table width='191' border='0' cellspacing='0' cellpadding='0'  name='dcd'>"+
//	
//		
//	"<tr valign='bottom' name='dcd'><td height='25' name='dcd'><span class='cen3' name='dcd'>项目2</span></td></tr>"+
//
//	"</table></td></tr><tr name='dcd'><td name='dcd'><img src='img/cen3.png' /></td></tr></table></div>";

    //得到套餐下面的项目

    $.ajax({
        url: "pexam/getgroupitemspop.htm",
        type: "post",
        data: "method=getGroupItemspop&groupids=" + id + "&time=" + (new Date()).valueOf(),
        success: function (data) {
            var strs = new Array();
            strs = data.split(",");
            var a = "<div id='" + divid + "' class='cen2' onmouseover='prvent()' onclick='prvent()'  name='dcd'><table width='191' border='0' cellspacing='0' cellpadding='0'  name='dcd'> <tr name='dcd'><td height='28' background='img/cen1.png' valign='bottom' name='dcd'><span class='cen3' name='dcd'>此套餐包含如下体检项目</span></td></tr><tr name='dcd'><td background='img/cen2.png' name='dcd'><table width='191' border='0' cellspacing='0' cellpadding='0'  name='dcd'>";
            for (i = 0; i < strs.length; i++) {
                var index = i + 1;
                a += "<tr valign='bottom' name='dcd'><td height='25' name='dcd'><span class='cen3' name='dcd'>&nbsp;&nbsp;" + strs[i] + "</span></td></tr>";
            }
            a += "</table></td></tr><tr name='dcd'><td name='dcd'><img src='img/cen3.png' /></td></tr></table></div>";
            // alert(a);
            $(tag).append(a);
        }

    });


}
//阻止冒泡
function prvent() {
    window.event.cancelBubble = true;
}


function removedetails(id) {
    var tag = "#" + id + "div";
    var id2 = window.event.toElement.id;
    var tname = window.event.toElement.name;
    var tagName = window.event.toElement.tagName;
    if (tname == 'dcd') {

    }
    else {

        $(tag).remove();
    }


}


function delteRow() {
    var selectedIds = grid_patient_appoint.getSelectedRowId();
    if (selectedIds == null) {
        alert('请先选中一行数据！');
    }
    else {
        if (window.confirm('你确定要删除这条信息？')) {
            $.ajax({
                url: "pexam/delnewpatient.htm",
                type: "post",
                data: "examid=" + clickexamid + "&pexamid=" + selectedIds + "&time=" + (new Date()).valueOf(),
                success: function (data) {
                }
            });
            for (var i = 0; i < selectedIds.split(',').length; i++) {
                var selectedId = selectedIds.split(',')[i];
                grid_patient_appoint.deleteRow(selectedId);
            }
        }
    }
}

function getExamid() {
    var datetime = new Date();
    var year = datetime.getFullYear();
    var month = datetime.getMonth() + 1;//js从0开始取
    var date = datetime.getDate();
    var hour = datetime.getHours();
    var minutes = datetime.getMinutes();
    var second = datetime.getSeconds();
    var milliseconds = "" + datetime.getMilliseconds();
    if (month < 10) {
        month = "0" + month;
    }
    if (date < 10) {
        date = "0" + date;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    if (second < 10) {
        second = "0" + second;
    }
    while (milliseconds.lenght < 3) {
        milliseconds = "0" + milliseconds;
    }
    return parseInt("" + year + month + date + hour + minutes + second + milliseconds);
}

function setExamid() {
    if ($('#examid').val() == '') {
        var bbb = getExamid();
        $('#examid').val(bbb);
    }
    //$('#examname').focus();
    $('#examname').val(getExamname(new Date()));
    $('#bookdate').val((new Date()).format('yyyy-MM-dd'));

}

function adjustDisp() {//高度自适应，取相应的数据
    var avalibleHeight = $(window).height() - 220;
    if (avalibleHeight > 260) {
        pageSize = Math.floor((avalibleHeight - 27) / 26);
        $("#grid_patient_appoint2").css("height", avalibleHeight);
    }
}

function examPersonnelRegiste() {
    $.blockUI({
        message: "<iframe height='100%' width='100%' frameborder='0' src='pexamNew/examPersonnelRegister.htm'></iframe>",
        css: {width: "858px", height: "691px", border: "0px solid #b6cfd6", left: getLeftPos(858), top: getTopPos(691)}
    });
}

function doPrintY() {
    openWin('体检预约单打印', '980', '580', 'pexamNew/doPrintY.htm');

}

function loadGridCount() {
    selectexamid = examid;//单位id
    var urlData = "";
    if (method == "loadAll") {//加载所有
        urlData = "time=" + (new Date()).valueOf() + "&examid=" + selectexamid + "&method=" + method;
    } else if (method == "loadBySearch") {//通过搜索框查询候检人员
        urlData = "time=" + (new Date()).valueOf() + "&examid=" + selectexamid + "&searchvalue=" + searchvalue + "&method=" + method;
        //alert(selectexamid+";"+searchvalue)
    } else if (method == 'loadByPexamid') {
        urlData = "time=" + (new Date()).valueOf() + "&examid=" + selectexamid + "&loadByPexamid=" + loadByPexamid + "&method=" + method;
    } else if (method == 'loadByIdNum') {
        urlData = "time=" + (new Date()).valueOf() + "&examid=" + selectexamid + "&loadByIdNum=" + loadByIdNum + "&method=" + method;
    }
    $.ajax({
        async: true,
        cache: false,
        ifModified: true,
        type: "post",
        url: "pexam/getPatientListCount.htm",
        data: urlData,
        dataType: "json",
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            if (data.status) {
                pageIndex = 0;
                var pageCount = data.value;
                createPagination1(pageCount);
            } else {
                alert(data.message);
            }
        }
    });
}

//搜索框
function topSearch() {
    if (!bool) {
        alert("请先选择预约单位!");
        return;
    }
    searchvalue = $("#search_value").val();//病人名字或拼音码或五笔码或者身份证id
    $('#pagination1').css("display", "block");
    if (searchvalue == '' || searchvalue.length < 2) {
        $("#search_value").select();
        return false;
    }
    //慈溪卡号后4位切割
    if (searchvalue.length == 20) {
        searchvalue = searchvalue.substr(0, 16);
        $("#search_value").val(searchvalue);
    }
    method = "loadBySearch";
    loadGridCount();

}
//四舍五入
function changeTwoDecimal_f(x) {
    var f_x = parseFloat(x);
    if (isNaN(f_x)) {
        alert('小数保留两位转换出错！');
        return false;
    }
    var f_x = Math.round(x * 100) / 100;
    var s_x = f_x.toString();
    var pos_decimal = s_x.indexOf('.');
    if (pos_decimal < 0) {
        pos_decimal = s_x.length;
        s_x += '.';
    }
    while (s_x.length <= pos_decimal + 2) {
        s_x += '0';
    }
    return s_x;
}


function cleanYYunit() {
    $('#examid').val("");
    $('#examname').val("");
    $('#unitname').val("");
    $('#bookdate').val("");
    combo_examType.setComboText("");
    combo_appointType.setComboText("");
    combo_tjyear.setComboText("");
}


function ff(v) {
    var searchForm = document.getElementById("searchForm");
    document.getElementById("currentPage").value = v;
    searchForm.submit();
}