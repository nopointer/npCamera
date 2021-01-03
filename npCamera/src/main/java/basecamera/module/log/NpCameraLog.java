package basecamera.module.log;

import android.util.Log;

public class NpCameraLog {

    /**
     * 是否允许log
     */
    public static boolean enableLog = true;


    public static void logE(String message) {
        if (!enableLog) return;
        StackTraceElement caller = getCallerStackTraceElement();
        message = "[" + getCallPathAndLineNumber(caller) + "]：" + message;
        Log.e("NpCameraLog", message);
    }

    public static void logI(String message) {
        if (!enableLog) return;
        StackTraceElement caller = getCallerStackTraceElement();
        message = "[" + getCallPathAndLineNumber(caller) + "]：" + message;
        Log.i("NpCameraLog", message);
    }


    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    /**
     * 获取调用路径和行号
     *
     * @return
     */
    private static String getCallPathAndLineNumber(StackTraceElement caller) {
        String result = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        result = String.format(result, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        return result;
    }


}
