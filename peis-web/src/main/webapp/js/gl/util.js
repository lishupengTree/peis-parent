function getUUID() {
    //
    // Loose interpretation of the specification DCE 1.1: Remote Procedure Call
    // described at http://www.opengroup.org/onlinepubs/009629399/apdxa.htm#tagtcjh_37
    // since JavaScript doesn't allow access to internal systems, the last 48 bits
    // of the node section is made up using a series of random numbers (6 octets long).
    //
    var dg = new Date(1582, 10, 15, 0, 0, 0, 0);
    var dc = new Date();
    var t = dc.getTime() - dg.getTime();
    var h = '-';
    var tl = getIntegerBits(t, 0, 31);
    var tm = getIntegerBits(t, 32, 47);
    var thv = getIntegerBits(t, 48, 59) + '1'; // version 1, security version is 2
    var csar = getIntegerBits(rand(4095), 0, 7);
    var csl = getIntegerBits(rand(4095), 0, 7);

    // since detection of anything about the machine/browser is far to buggy,
    // include some more random numbers here
    // if NIC or an IP can be obtained reliably, that should be put in
    // here instead.
    var n = getIntegerBits(rand(8191), 0, 7) +
        getIntegerBits(rand(8191), 8, 15) +
        getIntegerBits(rand(8191), 0, 7) +
        getIntegerBits(rand(8191), 8, 15) +
        getIntegerBits(rand(8191), 0, 15); // this last number is two octets long
    return tl + h + tm + h + thv + h + csar + csl + h + n;
}


//
// GENERAL METHODS (Not instance specific)
//


// Pull out only certain bits from a very large integer, used to get the time
// code information for the first part of a UUID. Will return zero's if there 
// aren't enough bits to shift where it needs to.
function getIntegerBits(val, start, end) {
    var base16 = returnBase(val, 16);
    var quadArray = new Array();
    var quadString = '';
    var i = 0;
    for (i = 0; i < base16.length; i++) {
        quadArray.push(base16.substring(i, i + 1));
    }
    for (i = Math.floor(start / 4); i <= Math.floor(end / 4); i++) {
        if (!quadArray[i] || quadArray[i] == '') quadString += '0';
        else quadString += quadArray[i];
    }
    return quadString;
}

// Numeric Base Conversion algorithm from irt.org
// In base 16: 0=0, 5=5, 10=A, 15=F
function returnBase(number, base) {
    //
    // Copyright 1996-2006 irt.org, All Rights Reserved.
    //
    // Downloaded from: http://www.irt.org/script/146.htm
    // modified to work in this class by Erik Giberti
    var convert = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
    if (number < base) var output = convert[number];
    else {
        var MSD = '' + Math.floor(number / base);
        var LSD = number - MSD * base;
        if (MSD >= base) var output = this.returnBase(MSD, base) + convert[LSD];
        else var output = convert[MSD] + convert[LSD];
    }
    return output;
}

// pick a random number within a range of numbers
// int b rand(int a); where 0 <= b <= a
function rand(max) {
    return Math.floor(Math.random() * max);
}
// end of UUID class file

//转换成json形式
function toJSON(txtOrObj, hasIndent) {
    var data = txtOrObj;
    if (typeof data == 'string')
        try {
            data = eval('(' + data + ')')
        } catch (e) {
            return ""
        }
    ;
    var draw = [], last = false, isLast = true, indent = 0;

    function notify(name, value, isLast, formObj) {
        if (value && value.constructor == Array) {
            draw.push((formObj ? ('"' + name + '":') : '') + '[');
            for (var i = 0; i < value.length; i++)notify(i, value[i], i == value.length - 1, false);
            draw.push(']' + (isLast ? '' : (',')));
        } else if (value && typeof value == 'object') {
            draw.push((formObj ? ('"' + name + '":') : '') + '{');
            var len = 0, i = 0;
            for (var key in value)len++;
            for (var key in value)notify(key, value[key], ++i == len, true);
            draw.push('}' + (isLast ? '' : (',')));
        } else {
            if (typeof value == 'string') value = '"' + value + '"';
            draw.push((formObj ? ('"' + name + '":') : '') + value + (isLast ? '' : ','));
        }
        ;
    };
    notify('', data, isLast, false);
    return draw.join('');
};

function getTop(node) {
    if (node.tagName == "BODY") {
        return 0;
    } else {
        return node.offsetTop + getTop(node.parentNode);
    }
}

function getLeft(node) {
    if (node.tagName == "BODY") {
        return 0;
    } else {
        return node.offsetLeft + getLeft(node.parentNode);
    }
}

function getLeftPos(width) {
    return (document.documentElement.clientWidth - width) / 2 + "px";
}

function getTopPos(height) {
    return (document.documentElement.clientHeight - height) / 2 + "px";
}

function isGridOrCombo(node) {
    if (node.tagName == "BODY") {
        return false;
    } else if (node.className.replace(/ /g, "") == "objbox" || node.className.replace(/ /g, "") == "dhx_combo_list" || node.tagName == "HTML") {
        return true;
    } else {
        return isGridOrCombo(node.parentNode);
    }
}
function isSymptomsDiv(node) {
    if (node.tagName == "BODY") {
        return false;
    } else if (node.id == "symptomsDiv" || node.tagName == "HTML") {
        return true;
    } else {
        return isSymptomsDiv(node.parentNode);
    }
}
function doBoxCheck(obj) {
    obj.src = obj.src.indexOf("item_chk0.gif") != -1 ? "imgs/item_chk1.gif" : "imgs/item_chk0.gif";
}
function showMsg(msg) {
    $.blockUI({
        message: "<table width='100%' height='100%' style='text-align:center;vertical-align:middle'><tr><td>" + msg + "</td></tr></table>",
        css: {
            width: "350px",
            height: "50px",
            border: "2px solid #b6cfd6",
            color: "#8eafbb",
            fontSize: "30px",
            top: getTopPos(50),
            left: getLeftPos(350)
        }
    });
    window.setTimeout(function () {
        $.unblockUI();
    }, 1000);
}
function showMsg2(msg) {
    $.blockUI({
        message: "<table width='100%' height='100%' style='text-align:center;vertical-align:middle'><tr><td>" + msg + "</td></tr></table>",
        css: {
            width: "350px",
            height: "50px",
            border: "2px solid #b6cfd6",
            color: "#8eafbb",
            fontSize: "30px",
            top: getTopPos(50),
            left: getLeftPos(350)
        }
    });
}
function showMsg_zzj(msg) {
    $.blockUI({
        message: "<table width='100%' height='100%' style='text-align:center;vertical-align:middle'><tr><td style='color:#8eafbb;font-size:40px'>" + msg + "</td></tr></table>",
        css: {
            width: "400px",
            height: "60px",
            border: "2px solid #b6cfd6",
            color: "#8eafbb",
            font: "50px",
            top: getTopPos(60),
            left: getLeftPos(400)
        }
    });
}
function showMsg3(msg) {
    $.blockUI({
        message: "<table width='100%' height='100%' style='text-align:center;vertical-align:middle'><tr><td>" + msg + "</td></tr></table>",
        css: {
            width: "400px",
            height: "50px",
            border: "2px solid #b6cfd6",
            color: "#8eafbb",
            fontSize: "20px",
            top: getTopPos(50),
            left: getLeftPos(400)
        }
    });
    window.setTimeout(function () {
        $.unblockUI();
    }, 2000);
}
function showConfirm(msg, isParent, isNotFocus) {
    $.blockUI({
        message: "<div style='width:400px;height:162px;background-color:#d9eaee;padding:8px;'>" +
        "<div style='height:20px;padding-bottom:5px;font-size:13px; color:#6ba3b6; font-family:微软雅黑; font-weight:bold;text-align:left'>" +
        "<span style='float:left'>操作提示</span>" +
        "<span style='float:right;'><img id='close_iframe_img' src='img/close.gif' align='absmiddle' style='cursor: pointer;'/></span>" +
        "</div>" +
        "<div style='background-color:#fff;height:134px;border:1px solid #b6cfd6'>" +
        "<table width='100%' height='100%' border='0' align='center'>" +
        "<tr style='font-size:20px; color:#6ba3b6; font-family:微软雅黑; font-weight:bold;text-align:middle'>" +
        "<td height='50%'>" + msg + "</td>" +
        "</tr>" +
        "<tr>" +
        "<td><input id='confirmButton' class='btn01' type='button' value='确 定'/>&nbsp;&nbsp;&nbsp;<input id='abolishButton' class='btn01' type='button' value='取 消'/></td>" +
        "</tr>" +
        "</table>" +
        "</div>" +
        "</div>",
        css: {
            width: "416px",
            height: "178px",
            border: "1px solid #b6cfd6",
            padding: "1px",
            top: isParent ? window.parent.getTopPos(178) : getTopPos(178),
            left: isParent ? window.parent.getLeftPos(416) : getLeftPos(416)
        }
    });
    if (!isNotFocus) {
        window.setTimeout(function () {
            if ($("#confirmButton").length != 0) {
                $("#confirmButton")[0].focus();
            }
        }, 300);
    }
}

function showConfirm2(msg, isParent, isNotFocus) {
    $.blockUI({
        message: "<div style='width:400px;height:162px;background-color:#d9eaee;padding:8px;'>" +
        "<div style='height:20px;padding-bottom:5px;font-size:13px; color:#6ba3b6; font-family:微软雅黑; font-weight:bold;text-align:left'>" +
        "<span style='float:left'>操作提示</span>" +
        "<span style='float:right;'><img id='close_iframe_img' src='img/close.gif' align='absmiddle' style='cursor: pointer;'/></span>" +
        "</div>" +
        "<div style='background-color:#fff;height:134px;border:1px solid #b6cfd6'>" +
        "<table width='100%' height='100%' border='0' align='center'>" +
        "<tr style='font-size:20px; color:#6ba3b6; font-family:微软雅黑; font-weight:bold;text-align:middle'>" +
        "<td height='50%'>" + msg + "</td>" +
        "</tr>" +
        "<tr>" +
        "<td><input id='confirmButton' class='btn01' type='button' value='确 定'/></td>" +
        "</tr>" +
        "</table>" +
        "</div>" +
        "</div>",
        css: {
            width: "416px",
            height: "178px",
            border: "1px solid #b6cfd6",
            padding: "1px",
            top: isParent ? window.parent.getTopPos(178) : getTopPos(178),
            left: isParent ? window.parent.getLeftPos(416) : getLeftPos(416)
        }
    });
    if (!isNotFocus) {
        window.setTimeout(function () {
            if ($("#confirmButton").length != 0) {
                $("#confirmButton")[0].focus();
            }
        }, 300);
    }
}

function showConfirm3(msg, isParent, isNotFocus) {
    $.blockUI({
        message: "<div style='width:420px;height:162px;background-color:#d9eaee;padding:8px;'>" +
        "<div style='height:20px;padding-bottom:5px;font-size:13px; color:#6ba3b6; font-family:微软雅黑; font-weight:bold;text-align:left'>" +
        "<span style='float:left'>操作提示</span>" +
        "<span style='float:right;'><img id='close_iframe_img' src='img/close.gif' align='absmiddle' style='cursor: pointer;'/></span>" +
        "</div>" +
        "<div style='background-color:#fff;height:134px;border:1px solid #b6cfd6'>" +
        "<table width='100%' height='100%' border='0' align='center'>" +
        "<tr style='font-size:20px; color:#6ba3b6; font-family:微软雅黑; font-weight:bold;text-align:middle'>" +
        "<td height='50%'>" + msg + "</td>" +
        "</tr>" +
        "<tr>" +
        "<td><input id='confirmButton' class='btn01' type='button' value='确 定'/>&nbsp;&nbsp;&nbsp;<input id='abolishButton' class='btn01' type='button' value='取 消'/></td>" +
        "</tr>" +
        "</table>" +
        "</div>" +
        "</div>",
        css: {
            width: "416px",
            height: "178px",
            border: "1px solid #b6cfd6",
            padding: "1px",
            top: isParent ? window.parent.getTopPos(178) : getTopPos(178),
            left: isParent ? window.parent.getLeftPos(416) : getLeftPos(416)
        }
    });
    if (!isNotFocus) {
        window.setTimeout(function () {
            if ($("#confirmButton").length != 0) {
                $("#confirmButton")[0].focus();
            }
        }, 300);
    }
}

var errorOpacity;
var errorInterval;
var errorTimeout;
function hideError() {
    errorOpacity = errorOpacity - 0.01;
    $("#error").css({opacity: errorOpacity});
    if (errorOpacity == 0 && errorInterval != null) {
        window.clearInterval(errorInterval);
        errorInterval = null;
    }
}
function errorIntervalStart() {
    if (errorInterval == null) {
        errorInterval = window.setInterval(hideError, 10);
    }
}
function showError(error) {
    if (errorInterval != null) {
        window.clearInterval(errorInterval);
        errorInterval = null;
    }
    if (errorTimeout != null) {
        window.clearTimeout(errorTimeout);
        errorTimeout = null;
    }
    errorOpacity = 1;
    $("#error").css({opacity: errorOpacity});
    $("#error").html(error);
    if (errorTimeout == null) {
        errorTimeout = window.setTimeout(errorIntervalStart, 2500);
    }
}
function checkBrowser() {
    var cb = "Unknown";
    if (window.ActiveXObject) {
        cb = "IE";
    } else if (navigator.userAgent.toLowerCase().indexOf("firefox") != -1) {
        cb = "FireFox";
    } else if ((typeof document.implementation != "undefined") && (typeof document.implementation.createDocument != "undefined") && (typeof HTMLDocument != "undefined")) {
        cb = "Mozilla";
    } else if (navigator.userAgent.toLowerCase().indexOf("opera") != -1) {
        cb = "Opera";
    }
    return cb;
}

var F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, ESC, ENTER, SHIFT, CTRL, CTRL_S;
window.document.onkeydown = function (ev) {
    var browser = checkBrowser();
    var event = ev || window.event;
    var keyCode = browser == "FireFox" ? event.which : event.keyCode;
    var node = browser == "FireFox" ? event.target : event.srcElement;
    if (keyCode == 8) {//backspace
        if ((node.tagName.toLowerCase() != "input" && node.tagName.toLowerCase() != "textarea" && node.tagName.toLowerCase() != "text") || node.readOnly) {
            if (browser == "IE") {
                event.keyCode = 0;
                event.returnValue = false;
            } else {
                event.preventDefault();
            }
        }
    } else if (keyCode == 13) {//enter
        eval(ENTER);
    } else if (keyCode == 27) {//esc
        eval(ESC);
    } else if (keyCode == 113) {//F2
        if (browser == "IE") {
            event.keyCode = 0;
            event.returnValue = false;
        } else {
            event.preventDefault();
        }
        eval(F2);
    } else if (keyCode == 114) {//F3
        if (browser == "IE") {
            event.keyCode = 0;
            event.returnValue = false;
        } else {
            event.preventDefault();
        }
        eval(F3);
    } else if (keyCode == 115) {//F4
        if (browser == "IE") {
            event.keyCode = 0;
            event.returnValue = false;
        } else {
            event.preventDefault();
        }
        eval(F4);
    } else if (keyCode == 116) {//F5
        if (browser == "IE") {
            event.keyCode = 0;
            event.returnValue = false;
        } else {
            event.preventDefault();
        }
        eval(F5);
    } else if (keyCode == 117) {//F6
        if (browser == "IE") {
            event.keyCode = 0;
            event.returnValue = false;
        } else {
            event.preventDefault();
        }
        eval(F6);
    } else if (keyCode == 118) {//F7
        if (browser == "IE") {
            event.keyCode = 0;
            event.returnValue = false;
        } else {
            event.preventDefault();
        }
        eval(F7);
    } else if (keyCode == 119) {//F8
        if (browser == "IE") {
            event.keyCode = 0;
            event.returnValue = false;
        } else {
            event.preventDefault();
        }
        eval(F8);
    } else if (keyCode == 120) {//F9
        if (browser == "IE") {
            event.keyCode = 0;
            event.returnValue = false;
        } else {
            event.preventDefault();
        }
        eval(F9);
    } else if (keyCode == 121) {//F10
        if (browser == "IE") {
            event.keyCode = 0;
            event.returnValue = false;
        } else {
            event.preventDefault();
        }
        eval(F10);
    } else if (keyCode == 122) {//F11
        if (browser == "IE") {
            event.keyCode = 0;
            event.returnValue = false;
        } else {
            event.preventDefault();
        }
        eval(F11);
    }
}
function LTrim(str) {
    var i;
    for (i = 0; i < str.length; i++) {
        if (str.charAt(i) != " " && str.charAt(i) != " ")break;
    }
    str = str.substring(i, str.length);
    return str;
}
function RTrim(str) {
    var i;
    for (i = str.length - 1; i >= 0; i--) {
        if (str.charAt(i) != " " && str.charAt(i) != " ")break;
    }
    str = str.substring(0, i + 1);
    return str;
}
function Trim(str) {
    return LTrim(RTrim(str));
}
/**
 *
 * @param {} 要分割的串
 * @param {} 分割字符
 * @param {} 返回格式   num
 * lsp
 */
function strToArrBy(str, sp, retpat) {
    if (str == '' || str == null) {
        return '';
    }
    if (str.indexOf(',') < 0) {
        return str;
    }
    var temp = '';
    var strs = new Array(); //定义一数组
    strs = str.split(",");

    if (retpat == 'num') {
        for (i = 0; i < strs.length; i++) {
            temp += strs[i] + ',';
        }
    }
    if (temp != '') {
        temp = temp.substring(0, temp.length - 1);
    }
    return temp;
}