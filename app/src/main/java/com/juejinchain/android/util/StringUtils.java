package com.juejinchain.android.util;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final String TAG = "StringUtils";

    /**
     * 返回具体的格式
     * @param pattern
     * @param input
     * @return
     */
    public static String getSpecificFormat(String pattern,double input){
        DecimalFormat df = new DecimalFormat(pattern);

        return df.format(input);
    }

    /**
     * 字符串是否为空，null也视为空
     */
    public static boolean isEmpty(String string) {
        return null == string || "".equals(string.replace(" ", "")) || "null".equals(string.trim());
    }

    /**
     * 解析double类型字符串
     *
     * @param strNum
     * @return
     */
    public static double parseDouble(String strNum) {
        if (isEmpty(strNum)) {
            return 0;
        }//有的金额带格式符‘,’固先去除此符号。
        else if (NumberUtils.isNumber(strNum.replace(",", "").replace(" ", ""))) {
            return Double.parseDouble(strNum);
        } else {
            return 0;
        }
    }

    public static int parseInteger(String strNum) {
        return (int) parseDouble(strNum);
    }

    /**
     * 字符串长度
     *
     * @param str
     * @return
     */
    public static int length(CharSequence str) {
        return str == null ? 0 : str.length();
    }

    /**
     * null转换成空字符串
     *
     * @param str
     * @return
     */
    public static String nullStrToEmpty(Object str) {
        return (str == null ? "" : (str instanceof String ? (String) str : str.toString()));
    }

    /**
     * 设置金额，添加元
     *
     * @return
     */
    public static String setAmount(Object amount) {

        if (amount instanceof Double) {
            //格式化double为两位小数
            return formatDouble((Double) amount) + "元";
        }
        return amount + "元";
    }

    /**
     * 格式化为两位小数
     *
     * @param num
     * @return
     */
    public static String formatDouble(double num) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(num);
    }

    /**
     * 格式化为一位小数
     *
     * @param num
     * @return
     */
    public static String formatDouble2(double num) {
        DecimalFormat df = new DecimalFormat("#0.0");
        return df.format(num);
    }

    /**
     * 将将所有的数字、字母及标点全部转为全角字符避免TextView自动换行出现的排版混乱问题
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static String getSpecialText(String t1, String t2,String t3) {
        return "<font color=\"#999999\">" + t1 + "</font>"
                + "<font color=\"#FF6633\">" + t2 + "</font>"
                + "<font color=\"#999999\">" + t3 + "</font>"
                ;
    }

    public static String getSpecialText2(String t1, String t2,String t3) {
        return "<font color=\"#333333\">" + t1 + "</font>"
                + "<font color=\"#FF6633\">" + t2 + "</font>"
                + "<font color=\"#333333\">" + t3 + "</font>"
                ;
    }


    /**
     * 格式化金额 千分位逗号隔开 //299,792,458.00
     * */
    public static String parseAmount(double s){
        DecimalFormat df = new DecimalFormat(",##0.0");

        return  df.format(s);
    }



    /**
     *规则：至少包含大写字母，小写字母及数字中的两种
     * */
    public static boolean isCorrectLoginPwd(String str){
       // (?=.*?[0-9])
      /*  Pattern z1_ = Pattern.compile("^(?=.*?[a-zA-Z])[a-zA-Z0-9]{6,15}$");
        //判断是否都成立，都包含返回true
        boolean isPwd = z1_.matcher(string).matches();
        return isPwd;*/

        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLowerCase = false;//定义一个boolean值，用来表示是否包含字母
        boolean isUpperCase = false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            } else if (Character.isLowerCase(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLowerCase = true;
            } else if (Character.isUpperCase(str.charAt(i))) {
                isUpperCase = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        boolean isRight;
        if(isDigit && (isLowerCase||isUpperCase)&&str.matches(regex)){
            isRight = true;
        }else if(isLowerCase && isUpperCase && str.matches(regex)){
            isRight = true;
        }else{
            isRight = false;
        }

        return isRight;

    }

    /**
     * 保留前1位，其他的以*号替代
     * @param text
     * @return
     */
    public static String parseHint(String text,int startPosition,int endPosition) {
        if (text == null || text.length() < startPosition+1 ||text.length() < endPosition+1) {
            return text;
        }
        final int length = text.length();
        StringBuffer sb = new StringBuffer();
        sb.append(text.substring(0,startPosition));
        for (int i = startPosition; i <endPosition; i++) {
            sb.append("*");
        }
        sb.append(text.substring(endPosition,length));
        return sb.toString();
    }

    /**
     * listView的用这个方法, 正则太耗时间了，20条要1.5秒左右
     * @param htmlStr
     * @return
     */
    public static String filterHtmlList(String htmlStr) {

        String regEx_html = "<[^>]+>|\\&[a-zA-Z]{1,10};"; //定义HTML标签的正则表达式
        // 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;
//        String regEx_special = "\\&[a-zA-Z]{1,10};";

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

    public static String filterHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
        // 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;
        String regEx_special = "\\&[a-zA-Z]{1,10};";

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        Pattern p_htmlSpec = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
        Matcher m_htmlSpec = p_htmlSpec.matcher(htmlStr);
        htmlStr = m_htmlSpec.replaceAll(""); //过滤html转义符号
        // 去除字符串中的空格 回车 换行符 制表符 等
//        htmlStr = htmlStr.replaceAll("\\s*|\t|\r|\n", "");

        return htmlStr.trim(); //返回文本字符串
    }

    /**
     * 设置阿里云图片清晰度直接替换最后面的高度
     * @param {String} imgUrl 阿里云图片地址
     * @param {Number} h 图片高度
     */
    public static String getImgUrlHeight(String imgUrl, int h){
        //http://jjlmobile.oss-cn-shenzhen.aliyuncs.com/goods_car/gid_34567/pic_list_h/f4ec1b5d4e7c6f10bd062fbe0d175050.jpg?x-oss-process=image/resize,h_50
        int has = imgUrl.indexOf("h_");

        if (has != -1 && has+5 > imgUrl.length()){
            imgUrl = imgUrl.substring(0, has+2) + h+"00";
        }
        return imgUrl;
    }

}
