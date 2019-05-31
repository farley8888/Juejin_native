package com.juejinchain.android.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeUtils
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yy/MM/dd");
    public static final SimpleDateFormat DATE_FORMAT_DATE    = new SimpleDateFormat("yy/MM/dd HH:mm:ss");

    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    public static String getTime(Date date, String format){
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static String getTimeLine(long time){
        Date dt = new Date(time);
        return getTimeLine(dt);
    }

    /**
     * 判断date距离当前多长时间
     * @param date
     * @return
     */
    public static String getTimeLine(Date date){
        long now = new Date().getTime()/1000;
        long da1 = date.getTime();
//        System.out.println("now="+now + ", da1="+da1);
        String timeline = "";
        if(now>da1){//之前
            long a = now-da1;
            if(a/1000==0){
                timeline = "刚刚";
            }else {
                long a1 = a/1000;
                if(a1<60){
                    timeline = a1+"秒前";
                } else{
                    long b = a1/60;
                    if(b<60){
                        if(b>30){
                            timeline = "半小时前";
                        }else{
                            timeline = b+"分钟前";
                        }
                    }else{
                        long c = b/60;
                        if(c<24){
                            timeline = c+"小时前";
                        }else {
                            long d = c/24;
                            if(d<30){
                                if(d>7){
                                    timeline = (d/7)+"周前";
                                }else{
                                    timeline = d+"天前";
                                }
                            } else{
                                long e = d/30;
                                if(e<12){
                                    timeline = e+"月前";
                                } else{
                                    timeline = getTime(date, "yy/MM/dd");
                                }
                            }
                        }
                    }
                }
            }
        } else {
            long a = da1-now;
            if(a/1000==0){
                timeline = "刚刚";
            }else {


                long a1 = a/1000;{
                    if(a1<60){
                        timeline = a1+"秒后";
                    } else{
                        long b = a1/60;

                        if(b<60){
                            if(b==30){
                                timeline = "半小时后";
                            }else{
                                timeline = b+"分钟后";
                            }
                        }else{
                            long c = b/60;
                            if(c<24){
                                timeline = c+"小时后";
                            }else {
                                long d = c/24;
                                if(d<30){
                                    if(d%7==0){
                                        timeline = (d/7)+"周后";
                                    }else{
                                        timeline = d+"天后";
                                    }
                                } else{
                                    long e = d/30;
                                    if(e<12){
                                        timeline = e+"月后";
                                    } else{
                                        timeline = getTime(date,"yy/MM/dd");
                                    }
                                }

                            }

                        }
                    }
                }

            }
        }
        return timeline;
    }

    /**
     * 返回指定格式的time
     * @param pattern
     * @param time
     * @return
     */
    public static String getTimeString(String pattern, long time){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date(time));
    }

    /**
     * 时间转日期
     * @param pattern
     * @param input
     * @return
     */
    public static Date parseTime2Date(String pattern, String input){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}