var reader;
/*引入该文件前必须引入JQuery文件*/
$(document).ready(function () {
    $("body").prepend('<div style="display: none;"><OBJECT classid="clsid:36BB0FFA-0598-4B70-A293-2F502B78987C" id="card_ykt" width="0" height="0"></OBJECT></div>');
    //this.innerHTML+='<div style="display: none;"><OBJECT classid="clsid:36BB0FFA-0598-4B70-A293-2F502B78987C" id="card_ykt" width="0" height="0"></OBJECT></div>';
    //getCardDate();
});
/*返回数据为：数组[市民卡识别码,外卡号,姓名,身份证,性别代码,出生日期,民族代码] 失败则为空*/
function getCardData() {
    try {
        var reader = document.getElementById("card_ykt");
        var brea = reader.read_icc_readcard();
        //var cardno = reader.read_icc_cardid();/*调用方法：read_icc_readcard,read_icc_cardid*/
        if (brea != null || brea != '') {
            if (brea.indexOf("错误") >= 0 && brea.indexOf("信息：") >= 0) {
                alert(brea);
                return;
            }
            //console.log(brea);
            //市民卡识别码
            var Cid = brea.substr(0, 32);
            //外卡号
            var Wid = brea.substr(32, 9);
            //姓名
            var name = brea.substr(41, 27).replace(/(^\s*)|(\s*$)/g, "");
            //身份证
            var id = brea.substr(68, 18);
            //性别代码
            var sex = brea.substr(86, 1);// 1男，2女
            if (parseInt(sex) == 1) {
                sex = "男";
            } else if (parseInt(sex) == 2) {
                sex = "女";
            } else {
                sex = "未知性别";
            }
            //出生日期
            var birth = brea.substr(87, 10);//格式YYYY-MM-DD
            //民族代码
            var nation = brea.substr(97, 2);//0->58
            /*console.log("市民卡识别码:"+Cid);
             console.log("外卡号:"+Wid);
             console.log("姓名:"+name);
             console.log("身份证:"+id);
             console.log("性别代码:"+sex);
             console.log("出生日期:"+birth);
             console.log("民族代码:"+nation);*/
            array = ['',
                '汉族', '蒙古族', '回族', '藏族', '维吾尔族',
                '苗族', '彝族', '壮族', '布依族', '朝鲜族',
                '满族', '侗族', '瑶族', '白族', '土家族',
                '哈尼族', '哈萨克族', '傣族', '黎族', '傈僳族',
                '佤族', '畲族', '高山族', '拉祜族', '水族',
                '东乡族', '纳西族', '景颇族', '柯尔克孜族', '土族族',
                '达斡尔族', '仫佬族', '羌族', '布朗族', '撒拉族',
                '毛南族', '仡佬族', '锡伯族', '阿昌族', '普米族',
                '塔吉克族', '怒族', '乌孜别克族', '俄罗斯族', '鄂温克族',
                '德昂族', '保安族', '裕固族', '京族', '塔塔尔族',
                '独龙族', '鄂伦春族', '赫哲族', '门巴族', '珞巴族',
                '基诺族', '其他', '外国血统'];
            nation = array[parseInt(nation)];
            array = [Cid, Wid, name, id, sex, birth, nation];
            return array;
        }
        alert("读取数据为空");
        return;
    }
    catch (e) {
        alert("读取数据为空,请重新插入卡片");
    }
}