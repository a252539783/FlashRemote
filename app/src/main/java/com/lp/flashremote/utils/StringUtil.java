package com.lp.flashremote.utils;


import com.lp.flashremote.beans.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;

public class StringUtil {
    /**
     * 上线的下划线拼接
     * @param str1
     * @param str2
     * @param str3
     * @param str4
     * @return
     */
    public static String stringAddUnderline(String str1,String str2,String str3,String str4){
        return str1+"_"+str2+"_"+str3+"_"+str4;
    }

    /**
     * 返回字符串是否以  ServerProtocol.ONLINE_SUCCESS 开始
     *               以   ServerProtocol.END_FLAG      结尾
     * @param string
     * @return
     */
    public static boolean startAndEnd(String string){
        return  (string.startsWith(ServerProtocol.ONLINE_SUCCESS) && string.endsWith(ServerProtocol.END_FLAG));
    }

    /**
     * 返回字符串是否以  ServerProtocol.CONNECTED_SUCCESS 开始
     *               以   ServerProtocol.END_FLAG      结尾
     * @param s
     * @return
     */
    public static boolean isBind(String s){
        return s.startsWith(ServerProtocol.CONNECTED_SUCCESS)&&s.endsWith(ServerProtocol.END_FLAG);
    }

    /**
     *  读取流中的字符
     * @param reader
     * @return
     */
    public static String readLine(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        String temp = "";
        try {
            while (!(temp = reader.readLine()).endsWith(ServerProtocol.END_FLAG)) {
                sb.append(temp);
            }
            sb.append(temp);
        } catch (IOException e) {
            System.out.println("读取数据失败。。。");
            e.printStackTrace();
        }
        return sb.toString();
    }
}
