package com.juejinchain.android.network;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
	
	public static String encode(String text){
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] result = digest.digest(text.getBytes());
			StringBuilder sb = new StringBuilder();
			for(byte b : result){
				int number = b&0xff; //加密
				String hex = Integer.toHexString(number);
				if(hex.length()==1){
					sb.append("0");
				}
				sb.append(hex);
			}
			return sb.toString();
			//return result.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	//和上面方法结果一样的
    public static String md5(String str) {
        try {
                MessageDigest md = MessageDigest.getInstance("MD5");//返回实现制定摘要算法的MessageDigest，有异常需要捕获下异常
                byte[] bytes = md.digest(str.getBytes());//定义一个变量，把str转换成byte类型，在调用Java提供的方法进行加密，并赋值给bytes
                String s = "" ;         //定义一个空字符串接收加密后的字符串
                for(byte b:bytes) {      //遍历数组
                    s += Integer.toHexString(b >>> 4 & 0xF);//这里右移的原因是byte有效位8位所以要将16进制拆分成2个值；
                    //把数组里的每个值都无符号右移4，然后& 0xF(这是16进制)，最后把结果装换成16进制并赋值给s
                    s += Integer.toHexString(b & 0xF);//byte有效位8位所以要将16进制拆分成2个值；
                }
                return s;//把加密的字符串返回
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }
        
}
