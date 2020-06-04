package basecamera.module.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference工具类，提供更简单的get和put方法操作数据
 */
public class PreferencesUtils {

    public static String PREFERENCE_NAME = "FIT_PREFERENCE";

    private static PreferencesUtils instance;


    private PreferencesUtils() {
    }

    public static PreferencesUtils getInstance() {
        if (instance == null) {
            instance = new PreferencesUtils();
        }
        return instance;
    }

    /**
     * put一个String类型数据
     *
     * @param key   存储的键
     * @param value 要存储的值
     * @return
     */
    public boolean putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 根据key获取String类型的值，默认值为""
     *
     * @param key
     * @return
     */
    public String getString(Context context, String key) {
        return getString(context,key, "");
    }

    /**
     * 根据key获取String类型的值,可自定义缺省值(默认值)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    /**
     * put一个Int类型的数据
     *
     * @param key
     * @param value
     * @return
     */
    public boolean putInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * 根据key获取int类型的值，默认值为-1
     *
     * @param key
     * @return
     */
    public int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * 根据key获取int类型的值,可自定义缺省值(默认值)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInt(Context context, String key, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }


    /**
     * 删除指定键值
     *
     * @param key
     */
    public boolean deleteObject(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        return editor.commit();
    }

    /**
     * 清除所有数据
     *
     * @return
     */
    public boolean clear(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        return true;
    }
}

