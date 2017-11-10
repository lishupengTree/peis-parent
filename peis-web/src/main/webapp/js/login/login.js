var combo_dept;
var combo_shayne;
function getDepts(key) {
    if (key == '') return;
    $.ajax({
        cache: false,   //是否使用缓存
        url: "getDepts.htm",
        async: false,   //是否异步，false为同步
        type: "get",
        data: "key=" + key + "&time=" + new Date().getMilliseconds(),
        error: function () {
            Log('获取数据失败')
        },
        success: function (reply) {
            if (reply == '[]') {
                Log('用户信息验证不通过')
                $('#key')[0].focus()
                var e = event.srcElement;
                var r = e.createTextRange();
                r.moveStart('character ', e.value.length);
                r.collapse(true);
                r.select();
                return;
            }
            var depts = eval('(' + reply + ')');
            combo_dept.clearAll();

            var opts = new Array();
            for (var i = 0; i < depts.length; i++) {
                var opt = new Array();
                opt[0] = depts[i].deptcode;
                opt[1] = depts[i].deptname;
                opts.push(opt);
            }
            combo_dept.addOption(opts);
            combo_dept.selectOption(0, false, true);
            //getShaynes();
        }
    });
}

function getShaynes() {
    var userkey = $('#key').val();
    var deptcode = combo_dept.getActualValue();
    $.ajax({
        cache: true,   //是否使用缓存
        url: "getShaynes.htm",
        async: false,   //是否异步，false为同步
        type: "get",
        data: "deptcode=" + deptcode + "&time=" + new Date().getMilliseconds(),
        error: function () {
            Log('获取部门失败')
        },
        success: function (reply) {
            if (reply == '[]') {
                $("#shayne_div").css('display', 'none');
                combo_dept.setSize(202);
                return;
            }
            $("#shayne_div").css('display', '');
            combo_dept.setSize(100);
            combo_shayne.setSize(100);
            var depts = eval('(' + reply + ')');
            combo_shayne.clearAll();
            var opts = new Array();
            for (var i = 0; i < depts.length; i++) {
                var opt = new Array();
                opt[0] = depts[i].deptcode;
                opt[1] = depts[i].deptname;
                opts.push(opt);
            }
            combo_shayne.addOption(opts);
            combo_shayne.selectOption(0, false, true);
            //return ;
        }
    });
}

function strToJson(str) {
    var json = (new Function("return " + str))();
    return json;
}

function checkUser() {
    var key = $('#key').val();
    var password = $('#password').val();
    if (key == '') {
        alert("请输入登录信息!");
        $('#key')[0].focus()
        return;
    }
    if (password == '') {
        alert("请输入登录信息!");
        document.getElementById("password").focus();
        return;
    }
    var dept = combo_dept.getActualValue();
    //alert(dept);
    var shayne = combo_shayne.getActualValue();

    var mac = $("#mac").val();
    var ip = $("#ip").val();
    $.ajax({
        cache: false,   //是否使用缓存
        url: "checkUser.htm",
        async: false,   //是否异步，false为同步
        type: "get",
        data: "key=" + key + "&password=" + password + "&dept=" + dept + "&shayne=" + shayne + "&mac=" + mac + "&ip=" + ip + "&time=" + new Date().getMilliseconds(),
        error: function () {
            Log('用户信息验证不通过')
        },
        success: function (reply) {
            if (reply == 'success') {
                $.cookie("job_no", $('#key').val());

                document.location.href = "platform.htm";
            } else {
                alert("用户信息验证不通过!");
            }
        }
    });
}
function set() {
    //查看是否全屏,取消全屏
    doFullScreen();
    $('#key')[0].focus();
    if ($.cookie('job_no') != null) {
        $('#key').val($.cookie('job_no'));
        $('#password')[0].focus();
    }
    if (parent.document.location.href.indexOf('login.htm') == -1) {
        parent.document.location.href = 'login.htm';
    }

    combo_dept = dhtmlXComboFromSelect("dept");
    combo_dept.enableFilteringMode(false);
    combo_dept.readonly(true);
    combo_dept.attachEvent("onChange", getShaynes);
    combo_dept.attachEvent("onKeyPressed", function (keyCode) {
        if (keyCode == 13) {
            if ($("#shayne_div").css('display') == 'none') {
                checkUser();
            } else {
                $(combo_shayne.DOMelem_input).focus();
            }
        }
    });

    combo_shayne = dhtmlXComboFromSelect("shayne");
    combo_shayne.enableFilteringMode(false);
    combo_shayne.readonly(true);
    combo_shayne.attachEvent("onKeyPressed", function (keyCode) {
        if (keyCode == 13) {
            checkUser();
        }
    });
    // var opts =  new Array();
    // var opt = new Array();
    // opt[0] ="1001";
    // opt[1] ="内科";
    // opts.push(opt);
    // combo_dept.addOption(opts);
    // combo_dept.selectOption(0,true,true);

    // $("#mac").val(LODOP.GET_SYSTEM_INFO("NetworkAdapter.1.PhysicalAddress"));
    // $("#ip").val(LODOP.GET_SYSTEM_INFO("NetworkAdapter.1.IPAddress"));
    return;
}

function enter(name) {
    if (event.keyCode == 13) {
        if (name == 'password') {
            window.setTimeout(
                function () {
                    $(combo_dept.DOMelem_input).focus();
                }
                , 0)
        } else {
            $('#password')[0].focus();
        }
    }
}

function doFullScreen() {
    if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth) {
        winHeight = document.documentElement.clientHeight;
        winWidth = document.documentElement.clientWidth;
    }
    if (winHeight == screen.height || winWidth == screen.width) {

        window.focus();
        var WshShell = new ActiveXObject('WScript.Shell');
        WshShell.SendKeys('{F11}');
    }
}