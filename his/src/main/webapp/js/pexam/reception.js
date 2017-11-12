var grid_patient_recption;//体检名单grid
var selectexamid = 'default';//选中的体检单位编号--初始化为“default”
var selectpexamid = '';//选中的体检者
var selectrowid;//体检名单选中的行的id
var grid_items_details;//指标grid
var status;//选中的人是否已经开始体检的标志
var pageIndex = 0;//页面索引初始值
var pageSize = 7;//每页显示条数初始化，修改显示条数，修改这里即可
var method = "";
var searchvalue = "";

var loadByPexamid = "";//读卡后赋值的
var loadByIdNum = "";//读卡后赋值的
var myCalendar, myCalendar1, myCalendar2;
$(document).ready(function () {
    adjustDisp();
    var autoheight = ($(window).height() - 350);
    $("#dleft").css("height", autoheight + 2);
    $("#dsigle0").css("height", autoheight - 65);
    $("#dsigle").css("height", autoheight - 65);

    $("#dxm2").css("height", autoheight - 35);
    $("#dxm").css("height", autoheight + 10);
    //$("#trh2").css("height", autoheight-30);
    $("#grid_patient_recption").css("height", autoheight - 10);
    $("#grid_items_details").css("height", autoheight + 10);

    //体检名单
    grid_patient_recption = new dhtmlXGridObject('grid_patient_recption');
    grid_patient_recption.setImagePath("dhtmlxGrid/codebase/imgs/");
    grid_patient_recption.setHeader("序号,姓名,状态,完成情况,pexamid,状态");
    grid_patient_recption.setInitWidths("30,70,50,*,60,50");
    grid_patient_recption.setColAlign("center,center,center,center,center,center");
    grid_patient_recption.setColTypes("ro,ro,ro,ro,ro,ro");
    grid_patient_recption.setSkin("dhx_custom");
    grid_patient_recption.setColumnHidden(4, true);
//	grid_patient_recption.attachEvent("onRowSelect",dispalyPatientInfo);
    grid_patient_recption.attachEvent("onRowDblClicked", dispalyPatientInfo);   //修改单击事件为 双击事件
    grid_patient_recption.init();

    //项目明细
    grid_items_details = new dhtmlXGridObject('grid_items_details');
    grid_items_details.setImagePath("dhtmlxGrid/codebase/imgs/");
    grid_items_details.setHeader("类型,套餐/项目名称,执行科室,完成情况,价格,完成时间");
    grid_items_details.setInitWidths("73,200,120,60,80,*");
    grid_items_details.setColAlign("center,center,center,center,right,right");
    grid_items_details.setColTypes("ro,ro,ro,ro,ro,ro");
    grid_items_details.setSkin("dhx_custom");
    grid_items_details.attachEvent("onRowDblClicked", function (rId, cInd) {
        var firstrowid = grid_items_details.getRowId(0);
        if (rId == firstrowid)return;  //第一行不能点击
        //var v_status = grid_patient_recption.cells(grid_patient_recption.getSelectedRowId(),2).getValue();
        if ('未检' == status)return;//状态是未检 不能点击
        var url = 'pexamNew/toUpdateStatus.htm?pexamid=' + $('#pexamid').val() + '&patname=' + encodeURI(encodeURI($('#patname').val())) + '&itemuuid=' + rId;
        if (cInd == 3) {
            openWin('修改项目状态', 300, 250, url);
        }
    });
    grid_items_details.init();

    combo_village = new dhtmlXCombo("shequ", "alfa3", 143);
    comboInit();//初始化社区combo
    myCalendar1 = new dhtmlXCalendarObject(["findtime"]);
    myCalendar1.attachEvent("onClick", function (date) {
        //alert("Date is set to "+date)
        //findAppointment(1);
        var startdate = $('#findtime').val();
        if (startdate != '') {
            findYYXX($('#findtime').val(), $('#enddate').val()); //获取 预约列表信息。

        }
    })
    myCalendar2 = new dhtmlXCalendarObject(["enddate"]);
    myCalendar2.attachEvent("onClick", function (date) {
        //alert("Date is set to "+date)
        //findAppointment(1);
        var startdate = $('#findtime').val();
        if (startdate != '') {
            findYYXX($('#findtime').val(), $('#enddate').val()); //获取 预约列表信息。

        }
    })
    $('#findtime').val((new Date()).format('yyyy-MM-dd'));
    $('#enddate').val((new Date()).format('yyyy-MM-dd'));
    findYYXX((new Date()).format('yyyy-MM-dd'), $('#enddate').val());
});
function findYYXX(findtime, enddate) {
    $.ajax({
        async: true,
        type: "post",
        url: "pexam/findYYXX.htm?findtime=" + findtime + "&enddate=" + enddate,
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            var jsons = eval('(' + data + ')');
            if (jsons.length == 0) {
                $("#tjxm option").remove();
                $("#tjxm select").append("<option value='default'>--------请选择体检项目---------</option>");
            } else {
                $("#tjxm option").remove();
                $("#tjxm select").append("<option value='default'>--------请选择体检项目---------</option>");
                for (var i = 0; i < jsons.length; i++) {
                    $("#tjxm select").append("<option value='" + jsons[i].examid + "'>" + jsons[i].examname + "</option>");
                }
            }
            if (data.indexOf('fail') < 0) {
            } else {
                alert(data);
            }
        }
    });

}


//加载该单位体检人数中--在检和未检总人数
function loadgrid(examid) {
    disabledButton(true);
    selectexamid = examid;//单位id
    method = "loadAll";
    $('#pagination').css("display", "block");
    loadGridCount();

    getNum(selectexamid);

}

function loadGridCount() {
    var urlData = "";
    if (method == "loadAll") {//加载所有
        urlData = "time=" + (new Date()).valueOf() + "&examid=" + selectexamid + "&method=" + method;
    } else if (method == "loadBySearch") {//通过搜索框查询候检人员
        urlData = "time=" + (new Date()).valueOf() + "&examid=" + selectexamid + "&searchvalue=" + searchvalue + "&method=" + method;
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
        url: "pexamNew/getPatientListCountNew.htm",
        data: urlData,
        dataType: "json",
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            if (data.status) {
                pageIndex = 0;
                var pageCount = data.value;
                if (method == "loadBySearch" && pageCount > 1) {
                    alert('存在同名病人！');
                }
                createPagination(pageCount);
            } else {
                alert(data.message);
            }
        }
    });
}
//刷卡 调用加载数据方法
function getCard() {

    var url = "selectbyins.htm?time=" + new Date().getMilliseconds();
    //window.open(url);
    var reVal = window.showModalDialog(url, "", "dialogHeight: 255px; dialogWidth: 352px; dialogHide: yes; help: no; resizable: no; status: no; scroll: no");
    if (reVal) {
        //		 卡类型,          卡号,
        var bp_json = eval('(' + reVal + ')');//调用：bp_json.type,bp_json.id
        autoCreatePexamInfo(bp_json[0].id, bp_json[0].type);
    }
}

//获取该单位下参检总人数、在检人数、未检人数--及单位信息
function getNum(examid) {
    $.ajax({
        async: true,
        cache: false,
        type: "post",
        url: "pexamNew/getPatNameNumNew.htm",
        data: "time=" + (new Date()).valueOf() + "&examid=" + examid,
        error: function () {
            alert("服务器内部错误！");
        },
        success: function (data) {
            if (data == 'fail') {
                alert("获取数据失败！");
            } else {
                var json = eval("(" + data + ")");
                $('#tjrs').text("（" + json.tjrs + "）");//体检人数
                $('#zjrs').text("（" + json.zjrs + "）");//在检人数
                $('#wjrs').text("（" + json.wjrs + "）");//未开始体检人数
            }
        }
    });
    if (examid != '0000') {
        $.ajax({
            url: "pexam/getPexamMainInfo.htm",
            type: "post",
            data: "examid=" + examid + "&time=" + (new Date()).valueOf(),
            success: function (data) {
                var json = eval('(' + data + ')');
                var bookdateObj = new Date(json.bookdate.time);
                var bookdateString = bookdateObj.getFullYear() + "-" + (bookdateObj.getMonth() + 1) + "-" + bookdateObj.getDate();
                $('#unitname').text(json.unitname);
                $('#examtype').text(json.examtype);
                $('#bookdate').text(bookdateString);
            }
        });
    }
}

function createPagination(pageCount) {//创建分页标签
    if (pageCount == 0) {
        pageCount = 0;
    }
    //分页，pageCount是总条目数，这是必选参数，其它参数都是可选
    $("#pagination").pagination(pageCount, {
        callback: pageCallback,
        prev_text: '上一页',       //上一页按钮里text
        next_text: '下一页',       //下一页按钮里text
        items_per_page: pageSize,  //显示条数
        num_display_entries: 2,    //连续分页主体部分分页条目数
        current_page: pageIndex,   //当前页索引
        num_edge_entries: 1        //两侧首尾分页条目数
    });
}

//分页回调函数
function pageCallback(index, jq) {//翻页回调
    pageIndex = index;
    loadgrid2();
    return false;
}

//加载体检名单
function loadgrid2() {
    var urlData = "";
    if ("loadAll" == method) {
        urlData = "examid=" + selectexamid + "&method=" + method + "&time=" + (new Date()).valueOf() + "&index=" + pageIndex + "&size=" + pageSize;
    } else if ("loadBySearch" == method) {
        urlData = "examid=" + selectexamid + "&method=" + method + "&searchvalue=" + searchvalue + "&time=" + (new Date()).valueOf() + "&index=" + pageIndex + "&size=" + pageSize;
    } else if (method == 'loadByPexamid') {
        urlData = "time=" + (new Date()).valueOf() + "&examid=" + selectexamid + "&loadByPexamid=" + loadByPexamid + "&method=" + method + "&index=" + pageIndex + "&size=" + pageSize;
    } else if (method == 'loadByIdNum') {
        urlData = "time=" + (new Date()).valueOf() + "&examid=" + selectexamid + "&loadByIdNum=" + loadByIdNum + "&method=" + method + "&index=" + pageIndex + "&size=" + pageSize;
    }
    //加载体检名单
    $.ajax({
        cache: false,
        type: "post",
        url: "pexamNew/getPatientListNew.htm",
        data: urlData,
        error: function () {
            alert("ajax请求失败！");
        },
        success: function (data) {
            if (data == 'fail') {
                alert("获取数据失败！");
            } else {
                var json = eval("(" + data + ")");
                grid_patient_recption.clearAll();
                var status = "";
                var finish = "";//完成情况
                var count = 0;
                var index = pageIndex * pageSize + 1;
                for (var i = 0; i < json.length; i++) {
                    if (json[i].bdate == null) {
                        status = "未检";
                        finish = "--";
                    } else {
                        if (json[i].isover != '完成') {
                            status = "在检";
                            finish = json[i].fin + "/" + json[i].total;
                            count++;
                        } else {
                            status = json[i].isover;
                            finish = json[i].fin + "/" + json[i].total;
                            count++;
                        }
                    }
                    var hs_status = json[i].hs_status == null ? "" : json[i].hs_status;
                    if (hs_status == '完成') {
                        hs_status = '<img src="img/yhtj/yhtj_wc.gif"></img>';
                    } else {
                        hs_status = '<img src="img/yhtj/yhtj_wj.gif"></img>';
                    }
                    grid_patient_recption.addRow(getUUID().replace(/-/g, ""), [
                        index++,
                        json[i].patname,
                        status,
                        finish,
                        json[i].pexamid,
                        hs_status
                    ]);
                }
                if ("loadAll" != method) {
                    if (json.length > 0) {
                        grid_patient_recption.selectRow(0);
                        dispalyPatientInfo(grid_patient_recption.getRowId(0), 1);
                        if ($("#startExamButton").attr("disabled") != "disabled") {
                            $("#startExamButton").css("background-image", "url(img/btn_black.jpg)");
                            $("#startExamButton").css("font-weight", "bolder");
                            $("#startExamButton").focus();
                        }
                    } else {
                        alert("无此人信息！");
                        $("#search_value").select();
                    }
                }
            }
        }
    });
    //grid_patient_recption.clearAll();
    grid_patient_recption.selectRow(0);
    setTimeout("dispalyPatientInfo(grid_patient_recption.getRowId(0),1)", 300);
}
//撤销开始体检操作
function backoutExam() {
    if (!window.confirm("撤销体检将删除该人已录入的体检结果，确定？")) {
        return;
    }
    $.ajax({
        cache: false,
        async: false,
        type: 'get',
        url: 'pexamNew/wipeExamData.htm?pexamid=' + $('#pexamid').val(),
        error: function () {
            alert('fail');
        },
        success: function (reply) {
            if (reply == 'success') {
                $('#startExamButton').removeAttr('disabled');
                $('#backoutExamButton').attr('disabled', 'true');
                $('#AgainButton').attr('disabled', 'true');
                $('#AgainCode').attr('disabled', 'true');
                grid_patient_recption.cells(selectrowid, 2).setValue('未检');
                grid_patient_recption.cells(selectrowid, 3).setValue("--");
            }
            if (reply == 'fail') {
                alert('服务器内部出错');
            }
            if (reply == 'isintj') {
                alert('体检项目已经出结果或者已经收费，不能撤销！');
            }
        }
    });
}
//选中体检名单行所触发的事件
function dispalyPatientInfo(rowId, colIndex) {
    if (colIndex == '5') {
        //alert(colIndex);
        var pexamid = grid_patient_recption.cells(rowId, 4).getValue();
        status = grid_patient_recption.cells(rowId, 5).getValue();
        $.ajax({
            url: "pexamNew/savehs_status.htm",
            type: "post",
            async: false,
            data: {time: (new Date()).valueOf(), pexamid: pexamid, status: status},
            error: function () {
                alert("获取数据失败");
            },
            success: function (reply) {
                if (reply == 'success') {
                    if (status.indexOf('wc') > -1) {
                        grid_patient_recption.cells(rowId, 5).setValue('<img src="img/yhtj/yhtj_wj.gif"></img>');
                    } else {
                        grid_patient_recption.cells(rowId, 5).setValue('<img src="img/yhtj/yhtj_wc.gif"></img>');
                    }
                } else {
                    alert(reply);
                }
            }
        });
    } else {
        //alert(colIndex);
        $('#sstarttime_').text('开始时间：');
        var stamp = new Date().getMilliseconds();
        var pexamid = grid_patient_recption.cells(rowId, 4).getValue();
        status = grid_patient_recption.cells(rowId, 2).getValue();
        if (status == '在检') {
            disabledButton(true);
            $("#backoutExamButton").removeAttr("disabled");
        } else if (status == '完成') {
            $("#backoutExamButton").attr("disabled", "true");
            disabledButton(true);
        } else {
            $("#backoutExamButton").attr("disabled", "true");
            disabledButton(false);
        }
        $('#AgainButton').removeAttr("disabled");  //让重打登记表 的按钮可以点击（不管未开始 还是已经开始都可点击。）
        $('#AgainCode').removeAttr("disabled");
        selectpexamid = pexamid;
        selectrowid = rowId;
        $('#examitems tr').remove();//已选项目
        //获取选择人的信息
        $.ajax({
            async: false,
            cache: false,
            type: 'get',
            url: "pexamNew/getPatientInfoNew.htm?stamp=" + stamp + "&pexamid=" + pexamid,
            error: function () {
                alert('fail');
            },
            success: function (data) {
                var jsons = eval('(' + data + ')');
                /*
                 var jsons=json[0].psinfolist;
                 var codePath=json[0].codePath;
                 */

                //体检人员信息
                var Pexam_mans = jsons[0];
                //人员表中条形码路径
                var codePath = Pexam_mans.codepath;
                $("#codePath").val(codePath);
                $('#patname').val(Pexam_mans.patname);
                $('#sex').val(Pexam_mans.sex);
                $('#dateofbirth').val(fSecondToTime(Pexam_mans.dateofbirth.time));
                try {
                    $('#bdate').val(fSecondToTime(Pexam_mans.bdate.time));
                } catch (e) {
                    $('#bdate').val((new Date()).format("yyyy-MM-dd"));
                }
                $('#pexamtype').val(Pexam_mans.examtype);
                if (Pexam_mans.examtype == "退休人员体检") {
                    $("#isInsurRig").attr("disabled", false);

                    //$("#isInsurRig").attr("checked",true);	社保登记暂时不可用，默认不勾选
                    $("#isInsurRig").attr("checked", false);
                } else {
                    $("#isInsurRig").attr("disabled", true);
                    $("#isInsurRig").attr("checked", false);
                }
                $('#phonecall').val(Pexam_mans.phonecall);
                $('#pexamid').val(Pexam_mans.pexamid);
                $('#comments').val(Pexam_mans.comments);
                $('#idnum').val(Pexam_mans.idnum);
                $('#age').val(Pexam_mans.age);
                var village = Pexam_mans.village == null ? "" : Pexam_mans.village;
                combo_village.setComboText(village);//村社区
                $("#address").val(Pexam_mans.address);//家庭地址
                //体检人员地址
                $('#village').val(Pexam_mans.village);
                $('#sstarttime_').text((function () {
                    if (Pexam_mans.bdate1 == null || Pexam_mans.bdate1 == '' || Pexam_mans.bdate1 == 'null') {
                        return "开始时间：";
                    } else {
                        return "开始时间：" + Pexam_mans.bdate1;
                    }
                })());
            }
        });

        //获取选择人的体检项目
        $.ajax({
            async: false,
            cache: false,
            type: 'get',
            url: "pexamNew/getPatientExanInfoNew.htm?stamp=" + stamp + "&pexamid=" + pexamid + "&examid=" + selectexamid,
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
                    //var examHtml = "<tr style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20' >"+index+"</td><td id='"+jsons[i].itemid+"' onmouseout='removedetails(\""+jsons[i].itemid+"\")' onmouseover='showdetails(\""+jsons[i].itemid+"\")'>"+jsons[i].itemname+"</td><td width='100' align='right'>"+jsons[i].cost+"</td><td width='100' align='right' style='display:none'>"+jsons[i].itemid+"</td><td width='100' align='right' style='display:none'>"+jsons[i].isgroup+"</td></tr>"
                    //注释掉 点击事件 remove_item
                    var itemsHtml = "<tr onclick='remove_item--(this)' style='cursor: pointer;'>"
                        + "<td width='20' height='28'><img src='img/jiej4.jpg' /></td>"
                        + "<td width='20' >" + index + "</td>"
                        //+"<td id='"+jsons[i].itemid+"' onmouseout='removedetails(\""+jsons[i].itemid+"\")' onmouseover='showdetails(\""+jsons[i].itemid+"\")'>"+jsons[i].itemname+"</td>"
                        + "<td id='" + jsons[i].itemid + "'>" + jsons[i].itemname + "</td>"
                        + "<td width='100' align='right'>" + jsons[i].cost + "</td>"
                        + "<td width='100' align='right' style='display:none'>" + jsons[i].itemid + "</td>"
                        + "<td width='100' align='right' style='display:none'>" + jsons[i].isgroup + "</td>"//是否是套餐
                        + "<td width='100' align='right' style='display:none'>" + (jsons[i].pexamid == null ? '' : jsons[i].pexamid) + "</td>"//是否是个人附加体检项目--为空则是团体预约的套餐
                        + "<td width='100' align='right' style='display:none'>" + jsons[i].startflag + "</td>"//是否已开始体检标志
                        + "</tr>";
                    $('#examitems').append(itemsHtml);
                }
                $('#exnum').text(jsons.length);
                $('#totalprice').text(price);
            }
        });
        //获取体检项目明细
        showgriditemdetails();
    }
}

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
                $('#examitems').append("<tr onclick='remove_item(this)' style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20'>" + index + "</td><td>" + jsons[i].itemname + "</td><td width='100' align='right'>" + jsons[i].cost + "</td><td width='100' align='right' style='display:none'>" + jsons[i].itemcode + "</td></tr>");
            }
            $('#exnum').text(jsons.length + trs.length);
            $('#totalprice').text(price);
        }
    });
}

//已被startExam2取代
function startExam() {
    var trs = document.getElementById('examitems').rows;
    if (trs.length == 0) {
        alert("必须有体检项！");
        return
    }
    var itemsinfo = [];
    for (var i = 0; i < trs.length; i++) {
        var itname = $(trs[i].cells[2]).text();
        itemsinfo.push({
            itemname: itname,
            cost: $(trs[i].cells[3]).text(),
            itemid: $(trs[i].cells[4]).text()
        });
    }
    grid_patient_recption.cells(selectrowid, 2).setValue('在检');
    var stamp = new Date().getMilliseconds();
    $.ajax({
        async: false,
        cache: false,
        type: 'post',
        url: "pexam/startexam.htm",
        data: "method=newAppointment&pexamid=" + selectpexamid + "&examid=" + selectexamid + "&json2=" + toJSON(itemsinfo) + "&time=" + (new Date()).valueOf(),
        error: function () {
            alert('fail');
        },
        success: function (data) {
            alert("体检开始");
        }
    });
}

//获取套餐中的明细
function showgriditemdetails() {
    grid_items_details.clearAll();//体检明细指标
    var trs = document.getElementById('examitems').rows;//已选项目
    //循环已选项目
    for (var i = 0; i < trs.length; i++) {
        var itname = $(trs[i].cells[2]).text();
        var cost = $(trs[i].cells[3]).text();
        cost = parseFloat(cost);
        cost = cost.toFixed(2);
        var itemcode = $(trs[i].cells[4]).text();
        var isgroup = $(trs[i].cells[5]).text();
        var isPerson = $(trs[i].cells[6]).text();//是否是个人附加项---为空则是团体的预约套餐
        var index = grid_items_details.getRowsNum() + 1;
        var rowId = getUUID().replace(/-/g, "");
        if (isgroup == 'y') {
            if (isPerson != '') {
                grid_items_details.addRow(rowId, ["个人项目", "<img src='img/yellowstar.png'/>" + itname + "&nbsp;&nbsp;&nbsp;&nbsp;", '/', "/", cost]);
                var stamp = new Date().getMilliseconds();
                getinfo(itemcode, rowId);
            } else {
                grid_items_details.addRow(rowId, ["团体项目", "<img src='img/yellowstar.png'/>" + itname + "&nbsp;&nbsp;&nbsp;&nbsp;", '/', "/", cost]);
                var stamp = new Date().getMilliseconds();
                getinfo(itemcode, rowId);
            }
        } else {//目前去掉了附加单个项目的功能，所以不会出现下面的情况
            getinfo(itemcode, rowId, '1');
        }
    }
}

//取得体检套餐下面的体检项目
function getinfo(itemcode, rowId, flag) {
    var stamp = new Date().getMilliseconds();
    $.ajax({
        async: false,
        cache: false,
        type: 'post',
        url: "pexamNew/getGroupItemsNew.htm",
        data: "stamp=" + stamp + "&groupids=" + itemcode + "&examid=" + selectexamid + "&pexamid=" + selectpexamid + "&status=" + status + "&flag=" + flag,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            var jsons = eval('(' + data + ')');
            if (flag == "1") {
                if (status == "未检") {
                    for (i = 0; i < jsons.length; i++) {
                        var a = jsons[i].cost;
                        a = parseFloat(a);
                        a = a.toFixed(2);         //getUUID().replace(/-/g,"")
                        grid_items_details.addRow(jsons[i].itemuuid, ['加项', jsons[i].bookname == null ? jsons[i].comname : jsons[i].comname + '<' + jsons[i].bookname + '>', jsons[i].excdeptname, '未检', a, jsons[i].excdate]);
                    }
                } else if (status == "在检" || status == "完成") {
                    var count = 0;
                    for (i = 0; i < jsons.length; i++) {
                        var a = jsons[i].price;
                        a = parseFloat(a);
                        a = a.toFixed(2);
                        if (jsons[i].excdate == null) {
                            count = count + 1;
                        }
                        grid_items_details.addRow(jsons[i].itemuuid, [
                            '加项',
                            jsons[i].bookname == null ? jsons[i].itemname : jsons[i].itemname + '<' + jsons[i].bookname + '>',
                            jsons[i].excdeptname,
                            getstatus(jsons[i].xmstatus),
                            //jsons[i].excdate==null?"未检":"<font color='green'>完成</font>",
                            a,
                            jsons[i].excdate
                        ]);
                    }
                }
            } else {
                if (status == "未检") {
                    for (i = 0; i < jsons.length; i++) {
                        var a = jsons[i].cost;
                        a = parseFloat(a);
                        a = a.toFixed(2);
                        grid_items_details.addRow(jsons[i].itemuuid, [i + 1, jsons[i].bookname == null ? jsons[i].comname : jsons[i].comname + '<' + jsons[i].bookname + '>', jsons[i].excdeptname, '未检', a, jsons[i].excdate]);
                    }
                } else if (status == "在检" || status == "完成") {
                    var count = 0;
                    for (i = 0; i < jsons.length; i++) {
                        var a = jsons[i].price;
                        a = parseFloat(a);
                        a = a.toFixed(2);
                        if (jsons[i].excdate == null) {
                            count = count + 1;
                        }
                        grid_items_details.addRow(jsons[i].itemuuid, [
                            i + 1,
                            jsons[i].bookname == null ? jsons[i].itemname : jsons[i].itemname + '<' + jsons[i].bookname + '>',
                            jsons[i].excdeptname,
                            getstatus(jsons[i].xmstatus),
                            //jsons[i].excdate==null?"未检":"<font color='green'>完成</font>",
                            a,
                            jsons[i].excdate
                        ]);
                    }
                    if (count == 0) {
                        grid_items_details.cells(rowId, 3).setValue("<font color='green'>完成</font>");
                    } else if (count < jsons.length && count > 0) {
                        grid_items_details.cells(rowId, 3).setValue("<font color='green'>在检</font>");
                    }
                }
            }
        }
    });
}

/**
 * 判断状态
 * @param {} status
 * @return {}
 */
function getstatus(status) {
    if (status == '未检') {
        return status;
    }
    if (status == '在检' || status == '待检' || status == '完成') {
        return "<font color='green'>" + status + "</font>";
    }
    if (status == '弃检') {
        return "<font color='red'>" + status + "</font>";
    }
}


var gprice = 0;
//不需拆分项目添加套餐
function addgroup2(groupid, groupname, cost) {
    //$('#examitems tr').remove();
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
//	$('#examitems').append("<tr onclick='remove_item(this)' style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20'>"+groupindex+"</td><td>"+gpname+"</td><td width='100' align='right'>"+cost+"</td><td width='100' align='right' style='display:none'>"+groupid+"</td><td width='100' align='right' style='display:none'>y</td></tr>");
    $('#examitems').append("<tr onclick='remove_item(this)' style='cursor: pointer;'><td width='20' height='28'><img src='img/jiej4.jpg' /></td><td width='20'>" + groupindex + "</td><td id='" + groupid + "' onmouseout='removedetails(\"" + groupid + "\")' onmouseover='showdetails(\"" + groupid + "\")'>" + gpname + "</td><td width='100' align='right'>" + cost + "</td><td width='100' align='right' style='display:none'>" + groupid + "</td><td width='100' align='right' style='display:none'>y</td></tr>");
    gprice = $('#totalprice').text();
    gprice = FloatAdd(gprice, cost);
    $('#totalprice').text(gprice);
    var trs = document.getElementById('examitems').rows;
    $('#exnum').text(trs.length);
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
                        for (var i = 0; i < jsons.length; i++) {
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

//修改过的开始体检，以套餐为单位
function startExam2() {
    //doPrint();
    var idnum = $("#idnum").val();
    var flag = false;
    if ("" == selectpexamid) {
        return;
    }
    var isInsurRig = $("#isInsurRig").attr("checked") ? "Y" : "N";
    /*  老版本社保登记流程
     if(isInsurRig=="Y"){
     $.ajax({//上传体检信息
     async: false,
     cache: false,
     type: "POST",
     url: "clc/checkidnum.htm",
     data: "idnum="+idnum+"&time="+(new Date()).valueOf(),
     error:function(){
     alert("检查是否收费失败");
     flag=true;
     },
     success: function(data){
     if(data.indexOf("错误")>-1){
     alert("上传医保失败"+data);
     flag=true;
     }else if(data=="Y"){
     alert("该体检人今年医保明细已上传");
     flag=true;
     }else{
     flag=false;
     }
     }
     });
     }
     if(flag){//如果上传过
     return;
     }*/
    var trs = document.getElementById('examitems').rows;
    if (trs.length == 0) {
        alert("必须有体检项！");
        return
    }

    if (isInsurRig == "Y") {
        alert("社保登记尚不可用!");
        return;
        /*  14年社保登记方案，已停用
         var url = "clc/readcard.htm?patientid="+$("#patientid").val()+"&reflag=N"+"&now="+new Date().getMilliseconds();
         url= encodeURI(encodeURI(url));
         //openWin("费用明细",width,height,url);
         var width = 414;
         var height = 224;

         var num = 2;
         var temp_datas = new Array();
         var temp = '';
         var detailid_r = '';
         var invoiceitems_r = '';

         height = height + 33*num; //2012.4.12收费明细页面，不再需要各项发票科目的金额信息
         //height = 224 - 33*2;
         openWin("费用明细",width,height,url);
         */
    } else {
        if (!window.confirm("确定开始体检吗？")) {
            return;
        }
        statrtj();
        //savePersonInfo();//开始体检前先保存个人信息
        disabledButton(true);
    }
}
function statrtj() {
    var hosnum = $("#hosnum").val();
    if ("" == selectpexamid) {
        return;
    }
    var trs = document.getElementById('examitems').rows;
    if (trs.length == 0) {
        alert("必须有体检项！");
        return
    }

    //savePersonInfo();//开始体检前先保存个人信息
    disabledButton(true);
    //获取体检项目信息---以下没用处(现在数据是从后台表里查询)
    var itemsinfo = [];
    for (var i = 0; i < trs.length; i++) {
        var itname = $(trs[i].cells[2]).text();
        itemsinfo.push({
            itemname: itname,//套餐名称
            cost: $(trs[i].cells[3]).text(),//套餐价格
            itemid: $(trs[i].cells[4]).text(),//套餐id
            isgroup: $(trs[i].cells[5]).text()//是否套餐
        });
    }

    var stamp = new Date().getMilliseconds();
    var pexamtype = $('#pexamtype').val();
    $.ajax({
        async: false,
        cache: false,
        type: 'post',
        url: "pexamNew/startExamNew2.htm",
        data: "pexamid=" + selectpexamid + "&examid=" + selectexamid + "&json2=" + toJSON(itemsinfo) + "&time=" + (new Date()).valueOf() + "&pexamtype=" + pexamtype,
        error: function () {
            alert('fail');
        },
        success: function (data) {
            if (data == 'fail') {
                alert('失败！');
                disabledButton(false)
            } else {
                var obj = eval("(" + data + ")");
                grid_patient_recption.cells(selectrowid, 2).setValue('在检');
                grid_patient_recption.cells(selectrowid, 3).setValue("0/" + obj.itemsNum);
                //$('#sstarttime_').text('开始时间：'+obj.staTime);
                //var patInfo = obj.pexamList[0];
                //alert("参数："+patInfo);

                //if($("#pexamtype").val()=="职工体检" || $("#pexamtype").val()=="农民体检" ||$("#pexamtype").val()=="退休人员体检"){
                if ($("#isPrintDGT").val() != "N") {
                    doPrintABC(obj);
                }
                var json = obj.barCodeList;//检验条码数据
                //alert("条码数量："+json.length);
                if (json.length > 0) {
                    //LODOP_barcode(json);
                }
                //	}
                $("#backoutExamButton").removeAttr("disabled");
                $("#search_value").select();
            }
        }
    });
}
//--------------2013-01-28------ 徐闯  体检重打-----------------
function doAgainPrint() {
    if ("" == selectpexamid) {
        return;
    }
    var trs = document.getElementById('examitems').rows;
    if (trs.length == 0) {
        alert("必须有体检项！");
        return
    }
    /*
     var patInfo = [];
     patInfo.push({
     patname:$("#patname").val(),//姓名
     sex:$("#sex").val(),//性别
     pexamid:$("#pexamid").val()//编号
     });
     */

    savePersonInfo();//重打前先保存个人信息
    var patInfo = {
        age: $("#age").val(),
        address: $("#address").val(),
        idnum: $("#idnum").val(),
        patname: $("#patname").val(),
        sex: $("#sex").val(),
        pexamid: $("#pexamid").val(),
        phonecall: $("#phonecall").val(),
        village: $("#village").val(),
        codepath: $("#codePath").val()
    }
    //LODOP_examSheet(patInfo[0]);
    // var codePath=$("#codePath").val();
    //  LODOP_examSheet(patInfo,codePath);
    if (status == '未检') {
        //先插入体检项目到临时表（只是为了打印导检单）
        $.ajax({
            async: false,
            cache: false,
            type: 'post',
            url: "pexamNew/insertTitle_print_temp.htm?pexamid=" + $("#pexamid").val() + "&examid=" + selectexamid + "&time=" + new Date(),
            error: function () {
                alert('fail');
            },
            success: function (data) {
                if (data.indexOf('fail') > -1) {
                    alert(data);
                } else {
                    LODOP_examSheet1(patInfo);
                }
            }
        });
    } else {
        LODOP_examSheet1(patInfo);
    }


}


function disabledButton(flag) {
    if (!flag) {
        $("#startExamButton").removeAttr("disabled");
        $("#AgainButton").attr("disabled", "true");
        $("#AgainCode").attr("disabled", "true");
        $("#BackButton").attr("disabled", "true");
        $("#sbbq").attr("disabled", "true");
    } else {
        $("#startExamButton").attr("disabled", "true");
        $("#AgainButton").removeAttr("disabled");
        $("#AgainCode").removeAttr("disabled");
        $("#BackButton").removeAttr("disabled");
        $("#sbbq").removeAttr("disabled");
    }
}

//项目选项卡
function changeli() {
    $('#dxm').css("display", "block");
    $('#dmx').css("display", "none");
    $('#tabxm').addClass("xxhover");
    $('#tabmx').removeClass("xxhover");
}

//明细选项卡
function changeli2() {
    $('#dxm').css("display", "none");
    $('#dmx').css("display", "block");
    $('#tabxm').removeClass("xxhover");
    $('#tabmx').addClass("xxhover");
    //showgriditemdetails();
}

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
    return (arg1 * m + arg2 * m) / m;
}

function fSecondToTime(iIpt) {
    var dt1 = new Date(iIpt);
    return dateToStr(dt1);
    // return dt1.toLocaleTimeString();
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
    //var time = year+"-"+month+"-"+date+" "+hour+":"+minutes+":"+second; //2009-06-12 17:18:05
    var time = year + "-" + month + "-" + date;
    return time;
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
}

//已被addgroup2取代
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
            for (var i = 0; i < jsons.length; i++) {
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

//删除已选项目
function remove_item(obj) {
    var isPerson = $(obj).children("td").eq(6).html();//不为空说明是个人附加项
    var startFlag = $(obj).children("td").eq(7).html();//为“Y”说一开始体检
    if (startFlag == 'Y') {
        alert('该套餐项目已开始体检，不能删除！');
        return;
    }
    if (isPerson == '') {
        alert("不是个人附加套餐不能删除!");
        return;
    }
    var totalprice = $('#totalprice').text();
    var pirce = $(obj).children("td").eq(3).html();
    totalprice = FloatSub(totalprice, pirce);
    $('#totalprice').text(totalprice);
    $(obj).remove();
    $("#exnum").text($("#exnum").text() - 1);
    var trs = document.getElementById('examitems').rows;
    for (var i = 0; i < trs.length; i++) {
        $(trs[i].cells[1]).text((i + 1));
    }
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
    return (arg1 * m + arg2 * m) / m;
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

function showdetails(id) {
//	alert(document.getElementById(id).parentNode.lastChild.innerHTML);
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

//删除
function removedetails(id) {
    var tag = "#" + id + "div";
    var id2 = window.event.toElement.id;
    var tname = window.event.toElement.name;
    var tagName = window.event.toElement.tagName;
    if (tname == 'dcd') {

    } else {
        $(tag).remove();
    }
}

//新建人员
function createPersonInfo() {
    openWin('人员录入', '790', '400', 'phyexam/personInfoEntry.htm');
}

Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(), //day
        "h+": this.getHours(), //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    }

    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
}

function adjustDisp() {//高度自适应，取相应的数据
    var avalibleHeight = $(window).height() - 380;
    if (avalibleHeight > 260) {
        pageSize = Math.floor((avalibleHeight - 27) / 26);
    }
}

//搜索框
function topSearch() {
    var hosnum = $("#hosnum").val();
    searchvalue = $("#search_value").val();//病人名字或拼音码或五笔码或者身份证id
    if (searchvalue == '' || searchvalue.length < 2) {
        $("#search_value").select();
        return false;
    }
    if (hosnum != '7007') {
        if (selectexamid == 'default') {
            alert("请先选择体检名称");
            $("#select").focus();
            return;
        }
        method = "loadBySearch";
        loadGridCount();
    } else {
        if (selectexamid == 'default') {
            searchExamid();
        } else {
            method = "loadBySearch";
            loadGridCount();
        }
    }

}

function searchExamid() {

    $.ajax({
        async: false,//同步
        cache: false,
        url: "pexamNew/searchExamidBySearch.htm",
        type: "post",
        data: "searchvalue=" + searchvalue,
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            var jsons = eval('(' + data + ')');
            var examnames = '';
            if (jsons.length > 1) {
                for (var i = 0; i < jsons.length; i++) {
                    examnames += jsons[i].examname + ' ';
                }
                examnames += '以上预约单位查有多条记录,请选择准确的预约单位！'
                alert(examnames);
            } else if (jsons.length == 1) {
                var selectedExamid = jsons[0].examid;
                $("#select").find("option[value='" + selectedExamid + "']").attr("selected", true);
                method = "loadBySearch";
                selectexamid = selectedExamid;
                loadGridCount();
                getNum(selectexamid);
            } else {
                alert('查无此人！');
            }
        }
    });
}
//新建体检名单
function newcreat() {
    var str = "jiejian";
    var clickexamid = selectexamid;
    var value = $("#select").val();
    var unit = $("#select").find("option:selected").text();
    var examtype = $("#examtype").text();
    openWin('新建体检人员', '760', '625', 'phyexam/personInfoEntry.htm?examid=' + clickexamid + '&unit=' + encodeURI(encodeURI(unit)) + "&examtype=" + encodeURI(encodeURI(examtype)) + "&str=" + str);


}


//修改或保存体检名单
function saveupdate() {
    var village = combo_village.getComboText();//村（社区）
    var pexamid = $("#pexamid").val();//体检编号
    var address = $("#address").val();//家庭住址
    var phonecall = $("#phonecall").val();//联系电话
    if (phonecall != null && phonecall != "") {
        if (isNaN(phonecall)) {
            alert("联系电话必须位数字");
            return;
        }
    }
    if (!pexamid) {
        return;
    }

    $.ajax({
        url: "pexam/saveOrUpdate.htm",
        type: "post",
        data: "pexamid=" + pexamid + "&village=" + village + "&address=" + address + "&phonecall=" + phonecall + "&time=" + (new Date()).valueOf(),
        success: function (reply) {
            if (reply == "false") {
                showMsg("数据保存失败!");
            } else {
                showMsg("数据保存成功!");
                setTimeout('refresh()', 1000);
            }
        }
    });


}

function tishi() {
    var patname = $("#patname").val();
    var idnum = $("#idnum").val();
    if (patname != "" && patname != null) {
        showMsg("需在预约服务中修改!");
    }
    if (idnum != "" && idnum != null) {
        showMsg("需在预约服务中修改!");
    }
}

//刷卡自动添加体检人员并对人员信息及体检记录进行验证
function autoCreatePexamInfo(cardNo, cardType) {
    var obj = {
        examId: selectexamid,//预约编号
        cardNo: cardNo,//卡号
        cardType: cardType//卡类型
    };
    $.ajax({
        async: false,//同步
        cache: false,
        url: "pexamNew/autoCreatePexamInfo.htm",
        type: "post",
        data: "jsonStr=" + toJSON(obj) + "&time=" + (new Date()).valueOf(),
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            if (data == "fail") {
                alert("服务器错误");
            } else {
                var json = eval("(" + data + ")");
                var status = json.status;
                var loadFlag = false;
                if (status == "N") {//未体检过
                    if (json.msg == "") {
                        method = "loadByPexamid";//通过体检编号加载
                        loadByPexamid = json.pexamId;
                        loadFlag = true;
                    } else {
                        if (window.confirm(json.msg)) {//此人参保类型是不农保，是否允许参加农民体检？
                            createPexamInfo(obj);
                        }
                    }
                } else {
                    if (window.confirm(json.msg)) {//本年度此人已参加过体检，是否再次参加体检
                        createPexamInfo(obj);
                    } else {//通过身份证加载体检过的数据
                        method = "loadByIdNum";//通过身份证加载
                        loadByIdNum = json.idNum;
                        loadFlag = true;
                    }
                }
                if (loadFlag) {
                    loadGridCount();
                    getNum(selectexamid);
                }
            }
        }
    });
}

//检体检人员信息插入体检人员表
function createPexamInfo(obj) {
    $.ajax({
        async: false,//同步
        cache: false,
        url: "pexamNew/createPexamInfo.htm",
        type: "post",
        data: "jsonStr=" + toJSON(obj) + "&time=" + (new Date()).valueOf(),
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            if (data == "fail") {
                alert("人员信息录入出错");
            } else {
                method = "loadByPexamid";//通过体检编号加载
                loadByPexamid = data;
                loadGridCount();
                getNum(selectexamid);
            }
        }
    });
}
//初始化combo
function comboInit() {
    $.ajax({
        async: false,//同步
        cache: false,
        url: "pexamNew/shequcombo.htm",
        type: "post",
        data: "time=" + (new Date()).valueOf(),
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            if (data == "fail") {
                alert("ajax请求失败");
            } else {
                var jsons = eval('(' + data + ')');
                for (var i = 0; i < jsons.length; i++) {
                    combo_village.addOption(jsons[i].nevalue, jsons[i].contents);
                }
            }
        }
    });
}
//开始体检和重打前先保存个人信息
function savePersonInfo() {
    var pexamid = selectpexamid;//选中的人
    var address = $("#address").val();//地址
    var phonecall = $("#phonecall").val();//电话
    var village = combo_village.getComboText();//村（社区）
    $.ajax({
        async: false,//同步
        cache: false,
        url: "pexamNew/savepersoninfo.htm",
        type: "post",
        data: "time=" + (new Date()).valueOf() + "&pexamid=" + pexamid + "&address=" + address + "&phonecall=" + phonecall + "&village=" + village,
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {

        }

    });
}
//----社保补签功能-----
function doSbbq() {
    alert("社保补签功能尚不可用！");
    return;
    var idnum = $("#idnum").val();
    var flag;
    $.ajax({//上传体检信息
        async: false,
        cache: false,
        type: "POST",
        url: "clc/checkidnum.htm",
        data: "idnum=" + idnum + "&time=" + (new Date()).valueOf(),
        error: function () {
            alert("检查是否收费失败");
            flag = true;
        },
        success: function (data) {
            if (data.indexOf("错误") > -1) {
                alert("上传医保失败" + data);
                flag = true;
            } else if (data == "Y") {
                alert("该体检人今年医保明细已上传");
                flag = true;
            } else {
                flag = false;
            }
        }
    });


    if ($("#examtype").text() == "退休人员体检" && flag == false) {
        var url = "clc/readcard.htm?patientid=" + $("#patientid").val() + "&reflag=Y" + "&now=" + new Date().getMilliseconds();
        url = encodeURI(encodeURI(url));
        //openWin("费用明细",width,height,url);
        var width = 414;
        var height = 224;

        var num = 2;
        var temp_datas = new Array();
        var temp = '';
        var detailid_r = '';
        var invoiceitems_r = '';

        height = height + 33 * num; //2012.4.12收费明细页面，不再需要各项发票科目的金额信息
        //height = 224 - 33*2;
        openWin("费用明细", width, height, url);
    }

}
//动态生成重打条码选项表
var json;
function rebarcode() {
    $.ajax({
        async: false,//同步
        cache: false,
        url: "pexamNew/getItems.htm",
        type: "post",
        data: "pexamid=" + selectpexamid + "&examid=" + selectexamid,
        error: function () {
            alert('ajax请求失败');
        },
        success: function (data) {
            var tr = document.getElementById("choose")
            if (tr.style.display == "none") {
                $("#choose").show();
            } else {
                $("#choose").hide();
            }
            json = eval("(" + data + ")");
            createTable();
        }
    });
}
function createTable() {
    var data = new Array();
    for (var i = 0; i < json.length; i++) {
        data.push('<input type="checkbox" name="infos"  itemuuid="' + json[i].itemuuid + '"  value="' + json[i].itemname + '" />');
        data.push(json[i].itemname);
        data.push('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
        if (i > 0 && (i + 1) % 4 == 0) {
            data.push('<br>');
        }
    }
    document.getElementById('items').innerHTML = data.join('');
}
function selectAll() {
    var judged = document.getElementById('judge');
    var inf = document.getElementsByName('infos');
    for (i = 0; i < inf.length; i++) {
        inf[i].checked = judged.checked;
    }
}


var demo = {};
demo.prototype = function () {


}

