package com.lsp.his.utils;

import java.util.UUID;

public class UuidUtil {

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }


    public static String getUuid() {
        int randomIndex = -1;
        int i = -1;
        String randomID = "";
        char[] randomElement = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z' }; // 定义一个一维密码字典，用来产生随机代码
        for (i = 0; i < 32; i++) {
			/* 利用random()方法（函数）产生一个随机的整型数，用来确定字典数组的对应元素 */
            randomIndex = ((new Double(Math.random() * 998)).intValue()) % 36;
            randomID = String.valueOf(randomElement[randomIndex]) + randomID;
        }

        return randomID;
    }

    public static void main(String[] args) {
        System.out.println(getUUID());
    }

}
