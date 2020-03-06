package com.sisucon.loopdaily.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.ImageView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static Utils utils;
    private DisplayImageOptions options;
    public  static Utils getInstance(Context context){
        if (utils==null){
            utils = new Utils(context);
        }
        return utils;
    }
    public Utils(Context context){
        this.context = context;
        initImageLoader();
    }
    static DateFormat LESSIONDF = new SimpleDateFormat("hh:mm a, dd-MMM-yyyy");
    public static String DateToLessionType(Date date){return LESSIONDF.format(date);}
    /***
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(2000, 2000)
                .memoryCacheSize(2 * 1024 * 1024).threadPoolSize(3).threadPriority(1000).diskCache(new UnlimitedDiskCache(context.getExternalCacheDir())).diskCacheFileCount(100).diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024).build();
        ImageLoader.getInstance().init(configuration);
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
    }

    /***
     * 获取到指定url图片,设置到imageview
     * @param
     * @param imageView
     */
    public void GetImg(String url, final ImageView imageView) {
        try {
            url = url.trim();
            ImageLoader.getInstance().displayImage(url, imageView, options);
        } catch (Exception e) {
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static boolean isToday(Date date1,Date date2){
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar.setTime(date1);
        calendar1.setTime(date2);
        return calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == calendar1
                .get(Calendar.DAY_OF_MONTH);
    }

    public static Date getStartTime(){
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH,0);

        //一天的开始时间 yyyy:MM:dd 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }
    public static Date getEndTime(){
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH,0);
        //一天的结束时间 yyyy:MM:dd 23:59:59
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTime();
    }

    public static String loopTimeToString(long looptime){
        int second = (int) looptime/1000/60;
        int hour = second/60;
        int day = hour/24;
        return day>0?day+"天"+(hour>0?hour+"小时一次":""):hour>0?hour+"小时"+(second>0?second+"分钟一次":""):second+"分钟一次";
    }

    public static Boolean checkEditNotNUll(EditText editText){
        return !editText.getText().toString().equals("");
    }
}
