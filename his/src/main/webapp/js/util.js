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

//通过词语获取拼音码和五笔码   cnWord : 中文词; async:true (异步),false(同步)
//json 格式  {'py':'xxxx','wb':'xxxx'}
//
function getPyAndWb(cnWord) {
    var myDate = new Date();
    var stamp = myDate.getMilliseconds();
    var data;
    $.ajax({
        url: "character_code.htm",
        type: "post",
        async: false,
        data: "method=getpyandwb&stamp=" + stamp + "&cnword=" + encodeURI(encodeURI(cnWord)),
        success: function (json) {
            data = eval('(' + json + ')');
        },
        error: function () {
            alert("获取拼音五笔码失败！");
        }
    });

    /*$.get("character_code.htm?method=getpyandwb&stamp="+stamp +"&cnword="+encodeURI(encodeURI(cnWord)),function(json){
     data = eval('(' + json + ')');
     alert(data.py);

     });*/
    return data;
}


//原生函数，日期格式华
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


var date = new Date();
var nowdate = date.format("yyyy-MM-dd");


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

//浮点数乘法运算
function FloatMul(arg1, arg2) {
    var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
    try {
        m += s1.split(".")[1].length
    } catch (e) {
    }
    try {
        m += s2.split(".")[1].length
    } catch (e) {
    }
    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m)
}


//浮点数除法运算   
function FloatDiv(arg1, arg2) {
    var t1 = 0, t2 = 0, r1, r2;
    try {
        t1 = arg1.toString().split(".")[1].length
    } catch (e) {
    }
    try {
        t2 = arg2.toString().split(".")[1].length
    } catch (e) {
    }
    with (Math) {
        r1 = Number(arg1.toString().replace(".", ""))
        r2 = Number(arg2.toString().replace(".", ""))
        return (r1 / r2) * pow(10, t2 - t1);
    }
}


function doClose() {
    window.parent.$.unblockUI();
}