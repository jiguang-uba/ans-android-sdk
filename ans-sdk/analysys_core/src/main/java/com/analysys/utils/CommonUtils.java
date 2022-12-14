package com.analysys.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.analysys.AnalysysAgent;
import com.analysys.process.AgentProcess;
import com.analysys.thread.AnsLogicThread;
import com.analysys.userinfo.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * @Copyright ?? 2018 EGuan Inc. All rights reserved.
 * @Description: ???????????????
 * @Version: 1.0
 * @Create: 2018/3/7
 * @Author: Wang-X-C
 */

public class CommonUtils {

    /**
     * ??????????????????
     */
    public static String getManifestData(Context context, String type) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (!TextUtils.isEmpty(type)) {
                if (appInfo == null || appInfo.metaData == null) {
                    return null;
                }
                Object o = appInfo.metaData.get(type);
                if (o == null) {
                    return null;
                }
                if (o instanceof String) {
                    return (String) o;
                } else if (o instanceof Number) {
                    return String.valueOf(o);
                } else if (o instanceof Boolean) {
                    boolean bool = (boolean) o;
                    return String.valueOf(bool);
                } else {
                    return o.toString();
                }
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return null;
    }

    /**
     * ???????????? ???????????? mac imei
     */
    public static boolean isAutoCollect(Context context, String type) {
        try {
            if (TextUtils.isEmpty(type)) {
                return false;
            }
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo == null) {
                return true;
            }
            Bundle metaData = appInfo.metaData;
            if (metaData == null) {
                return true;
            }
            return metaData.getBoolean(type);
        } catch (Throwable e) {
            ExceptionUtil.exceptionThrow(e);
        }
        return true;
    }

    /**
     * ??????????????????,?????? yyyy-MM-dd hh:mm:ss.SSS
     */
    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Date date = new Date(getCalibrationTimeMillis(AnalysysUtil.getContext()));
        return simpleDateFormat.format(date);
    }

    public static Set<String> toSet(String names) {
        Set<String> set = null;
        if (!isEmpty(names)) {
            set = new HashSet<>();
            if (names.contains("$$")) {
                String[] array = names.split("\\$\\$");
                Collections.addAll(set, array);
            } else {
                set.add(names);
            }
        }
        return set;
    }

    public static String toString(Set<String> set) {
        String names = "";
        if (set != null && !set.isEmpty()) {
            for (String name : set) {
                if (isEmpty(names)) {
                    names = name;
                } else {
                    names += "$$" + name;
                }
            }
        }
        return names;
    }

    /**
     * ??????????????????
     */
    public static List<String> toList(String names) {
        List<String> list = new ArrayList<>();
        if (!isEmpty(names)) {
            if (names.contains("$$")) {
                String[] array = names.split("\\$\\$");
                Collections.addAll(list, array);
            } else {
                list.add(names);
            }
        }
        return list;
    }

    /**
     * ?????????????????????
     */
    public static String toString(List<String> list) {
        String names = "";
        if (list != null && !list.isEmpty()) {
            for (String name : list) {
                if (isEmpty(names)) {
                    names = name;
                } else {
                    names += "$$" + name;
                }
            }
        }
        return names;
    }

    /**
     * Json ??? Map
     */
    public static Map<String, Object> jsonToMap(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            Object key = keys.next();
            if (key == null) {
                continue;
            }
            String sKey = key.toString();
            map.put(sKey, jsonObject.opt(sKey));
        }
        return map;
    }

    /**
     * gzip?????? base64??????
     */
    public static String messageZip(String message) throws IOException {
        String baseMessage = null;
        if (!isEmpty(message)) {
            byte[] gzipMessage = ZipUtils.compressForGzip(message);
            byte[] base64 = Base64.encode(gzipMessage, Base64.DEFAULT);
            baseMessage = new String(base64);
            if (isEmpty(baseMessage)) {
                return "";
            }
        }
        return baseMessage;
    }

    /**
     * base64?????? gzip?????????
     */
    public static String messageUnzip(String message) {
        if (isEmpty(message)) {
            return null;
        }
        byte[] base64Message = Base64.decode(message, Base64.DEFAULT);
        String gzipMessage = ZipUtils.decompressForGzip(base64Message);
        if (isEmpty(gzipMessage)) {
            return message;
        }
        return gzipMessage;
    }

    /**
     * ???????????????
     */
    public static String getSpvInfo(Context context) {
        try {
            String appId = getAppKey(context);
            String sdkVersion = Constants.DEV_SDK_VERSION;
            String policyVersion = SharedUtil.getString(context, Constants.SP_SERVICE_HASH,
                    null);

            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String appVersion = info.versionName;
            String spv =
                    Constants.PLATFORM + "|" + appId + "|" + sdkVersion + "|" + policyVersion +
                            "|" + appVersion;
//            return new String(Base64.encode(spv.getBytes(), Base64.DEFAULT));
            return new String(Base64.encodeToString(spv.getBytes(),Base64.NO_WRAP));
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return null;
    }

    /**
     * ??????????????????
     */
    public static boolean isFirst(Context context) {
        if (!isEmpty(context)) {
            String first = SharedUtil.getString(context, Constants.SP_FIRST_START_TIME,
                    null);
            return isEmpty(first);
        }
        return false;
    }

    /**
     * ??????????????????
     */
    public static boolean isFirstStart(Context context) {
        if (!isEmpty(context)) {
            String first = SharedUtil.getString(context, Constants.SP_FIRST_START_TIME,
                    null);
            ANSLog.d("trackFirstInstall3" + first);
            if(isEmpty(first)){
                getFirstStartTime(context);
                return true;
            }
        }
        return false;
    }

    public static String timeConversion(long timeStamp) {
        if (timeStamp == 0) {
            return "";
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
        return sdf.format(new Date(timeStamp));
    }

    /**
     * ????????????????????????
     */
    public static String getFirstStartTime(Context context) {
        String firstTime = SharedUtil.getString(context,
                Constants.SP_FIRST_START_TIME, Constants.EMPTY);
        if (isEmpty(firstTime)) {
            firstTime = getTime();
            SharedUtil.setString(context, Constants.SP_FIRST_START_TIME, firstTime);
        }
        return firstTime;
    }

    /**
     * ??????????????????,?????? yyyy/MM/dd
     */
    public static String getDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String checkUrl(String url) {
        if (isEmpty(url)) {
            return null;
        }
        url = url.trim();
        int lastDex = url.lastIndexOf("/");
        if (lastDex != -1 && lastDex == (url.length() - 1)) {
            return checkUrl(url.substring(0, url.length() - 1));
        }
        return url;
    }

    /**
     * ???????????????????????????
     */
    public static boolean isEmpty(Object object) {
        try {
            if (object == null) {
                return true;
            } else if (object instanceof String) {
                String val = object.toString();
                return (TextUtils.isEmpty(val)
                        || TextUtils.isEmpty(val.trim()));
            } else if (object instanceof JSONObject) {
                return ((JSONObject) object).length() < 1;
            } else if (object instanceof JSONArray) {
                return ((JSONArray) object).length() < 1;
            } else if (object instanceof Map) {
                return ((Map) object).isEmpty();
            } else if (object instanceof List) {
                return ((List) object).isEmpty();
            } else if (object instanceof Set) {
                return ((Set) object).isEmpty();
            } else {
                return false;
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return true;
    }

    private static Boolean sIsMainProcess = null;

    /**
     * ????????????????????????
     */

    public static boolean isMainProcess(Context context) {
        ANSLog.d("======isMainProcess=======");
        new Exception().printStackTrace();
        if (sIsMainProcess != null) {
            return sIsMainProcess;
        }
        if (context == null) {
            return false;
        }

        if (!AgentProcess.getInstance().isInited()) {
            //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????false,hybird??????????????????
            return false;
        }
        ANSLog.d("======isMainProcess getRunningAppProcesses =======");
        sIsMainProcess = true;
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = null;
        if (activityManager != null) {
            runningApps = activityManager.getRunningAppProcesses();
        }

        if (runningApps == null) {
            return false;
        }
        String process = "";
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    process = proInfo.processName;
                }
            }
        }
        String mainProcessName = null;
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        if (applicationInfo != null) {
            mainProcessName = context.getApplicationInfo().processName;
        }
        if (mainProcessName == null) {
            mainProcessName = context.getPackageName();
        }
        sIsMainProcess = mainProcessName.equals(process);
        ANSLog.d("======isMainProcess getRunningAppProcesses DONE=======");

        return sIsMainProcess;
    }


    /**
     * ????????????
     *
     * @param context    Context
     * @param permission ????????????
     * @return true:??????????????????; false:?????????????????????
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                Object invoke = method.invoke(context, permission);
                if (invoke instanceof Integer) {
                    int rest = (Integer) invoke;
                    result = rest == PackageManager.PERMISSION_GRANTED;
                }
            } catch (Throwable ignore) {
                ExceptionUtil.exceptionThrow(ignore);
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission,
                    context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    /**
     * ?????? AppKey
     */
    public static String getAppKey(Context context) {
        try {
            return SharedUtil.getString(context, Constants.SP_APP_KEY, null);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return null;
    }


    /**
     * ??????json??????,source ??????????????? dest??????
     */
    public static void mergeJson(final JSONObject source, JSONObject dest) throws JSONException {
        if (isEmpty(source)) {
            return;
        } else if (dest == null) {
            return;
        }
        Iterator<String> keys = source.keys();
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            dest.put(key, source.opt(key));
        }
    }

    /**
     * ?????????
     */
    public static String readStream(InputStream is) {
        StringBuffer sb = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        } finally {
            CloseUtils.closeItQuietly(is);
        }
        return String.valueOf(sb);
    }

    /**
     * ????????? ??????
     */
    public static Object reflexUtils(String classPath, String methodName, Class[] classes,
                                     Object... objects) {
        try {
            Class<?> cl = Class.forName(classPath);
            Method method = cl.getDeclaredMethod(methodName, classes);
            //????????????????????????private,?????????????????????
            method.setAccessible(true);
            Object object = cl.newInstance();
            return method.invoke(object, objects);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return null;
    }

    /**
     * ????????? static ??????
     */
    public static Object reflexStaticMethod(String classPath, String methodName, Class[] classes,
                                            Object... objects) {
        try {
            Class<?> cl = Class.forName(classPath);
            Method method = cl.getDeclaredMethod(methodName, classes);
            //????????????????????????private,?????????????????????
            method.setAccessible(true);
            return method.invoke(null, objects);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return null;
    }

    /**
     * ?????? ??????
     */
    public static Object reflexUtils(String classPath, String methodName) {
        try {
            Class<?> cl = Class.forName(classPath);
            Method method = cl.getDeclaredMethod(methodName);
            //????????????????????????private,?????????????????????
            method.setAccessible(true);
            Object object = cl.newInstance();
            return method.invoke(object);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return null;
    }

//    /**
//     * ?????? originalId????????????distinctId,????????????UUID
//     */
//    public static String getDistinctId(Context context) {
//        String id = getIdFile(context, Constants.SP_DISTINCT_ID);
//        if (isEmpty(id)) {
//            id = getIdFile(context, Constants.SP_UUID);
//        }
//        return id;
//    }

    /**
     * ?????????value???????????????
     */
    public static void pushToJSON(JSONObject json, String key, Object value) {
        try {
            if (value == null) {
                return;
            }
            if (value instanceof String) {
                String v = String.valueOf(value);
                if (!TextUtils.isEmpty(v) && !"unknown".equalsIgnoreCase(v)) {
                    if (!json.has(key)) {
                        json.put(key, value);
                    }
                }
            } else {
                json.put(key, value);
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
    }

    /**
     * ??????????????????
     * ??????????????? > ???????????? > ????????????
     */
    public static String getUrl(Context context) {
        String url = SharedUtil.getString(context, Constants.SP_SERVICE_URL, null);
        if (!isEmpty(url)) {
            return url;
        }
        url = SharedUtil.getString(context, Constants.SP_USER_URL, null);
        if (!isEmpty(url)) {
            return url;
        }
        return null;
    }

    /**
     * ????????????
     */
    public static String getMould(Context context, String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            InputStream inputStream = null;
            try {
                inputStream = context.getResources().getAssets().open(fileName);
                int size = inputStream.available();
                byte[] bytes = new byte[size];
                inputStream.read(bytes);
                return new String(bytes);
            } catch (Throwable ignore) {
                //??????????????????????????????
                ExceptionUtil.exceptionPrint(ignore);
            } finally {
                CloseUtils.closeItQuietly(inputStream);
            }
        }
        return "";
    }

    /**
     * ??????????????????????????????????????????
     */
    public static boolean checkClass(String packageName, String className) {
        boolean result = true;
        try {
            Class.forName(packageName + "." + className);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
            result = false;
        }
        return result;
    }

    /**
     *?????? id ?????????????????????????????????????????????????????????????????????
     * @param context
     * @param key
     * @param value
     */
    @Deprecated
    public static void setIdFile(Context context, String key, String value) {
        try {
            if (context != null && !TextUtils.isEmpty(key)) {
                String filePath = context.getFilesDir().getPath() + Constants.FILE_NAME;
                String info = readFile(filePath);
                JSONObject job;
                if (!TextUtils.isEmpty(info)) {
                    job = new JSONObject(info);
                } else {
                    job = new JSONObject();
                }
                if (TextUtils.isEmpty(value)) {
                    job.remove(key);
                } else {
                    job.put(key, value);
                }
                writeFile(filePath, String.valueOf(job));
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);

        }
    }

    /**
     * ?????? id ?????????????????????????????????????????????????????????????????????
     */
    @Deprecated
    public static String getIdFile(Context context, String key) {
        try {
            if (context == null || context.getFilesDir() == null) {
                return null;
            }
            String filePath = context.getFilesDir().getPath() + Constants.FILE_NAME;
            String info = readFile(filePath);
            if (!TextUtils.isEmpty(info)) {
                JSONObject job = new JSONObject(info);
                return job.optString(key);
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
//            writeFile(filePath, null);
        }
        return null;
    }

    /**
     * ??????????????????
     */
    private synchronized static void writeFile(String filePath, String content) {
        RandomAccessFile randomAccessFile = null;
        FileChannel fileChannel = null;
        if (isEmpty(filePath)) {
            return;
        }
        try {
            File file = new File(filePath);

            if (TextUtils.isEmpty(content)) {
                file.delete();
                return;
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            randomAccessFile = new RandomAccessFile(file, "rw");
            fileChannel = randomAccessFile.getChannel();
            final FileLock fileLock = fileChannel.lock(0L, Long.MAX_VALUE, false);
            if (fileLock != null) {
                if (fileLock.isValid()) {
                    fileChannel.truncate(0);
                    fileChannel.write(ByteBuffer.wrap(content.getBytes()));
                    fileLock.release();
                }
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        } finally {
            CloseUtils.closeItQuietly(randomAccessFile);
            CloseUtils.closeItQuietly(fileChannel);
        }
    }

    /**
     * ??????????????????
     */
    private synchronized static String readFile(String filePath) {
        FileChannel fileChannel = null;
        RandomAccessFile randomAccessFile = null;
        if (isEmpty(filePath)) {
            return null;
        }
        try {
            File file = new File(filePath);
            if (!file.exists()) {
//                file.createNewFile();
                return null;
            }
            randomAccessFile = new RandomAccessFile(file, "rw");
            fileChannel = randomAccessFile.getChannel();
            FileLock fileLock = fileChannel.lock(0, Long.MAX_VALUE, true);
            if (null != fileLock) {
                if (fileLock.isValid()) {
                    randomAccessFile.seek(0);
                    byte[] buf = new byte[(int) randomAccessFile.length()];
                    randomAccessFile.read(buf);
                    return new String(buf, "utf-8");
                }
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        } finally {
            CloseUtils.closeItQuietly(fileChannel);
            CloseUtils.closeItQuietly(randomAccessFile);
        }
        return null;
    }

    /**
     * ???????????????path
     */
    public static String getClassPath(String path) {
        int index = path.lastIndexOf(".");
        return path.substring(0, index);
    }

    /**
     * ??????????????????
     */
    public static String getMethod(String path) {
        int index = path.lastIndexOf(".");
        return path.substring(index + 1);
    }

    //.............................................................

    /**
     * ?????? Channel
     */
    public static String getChannel(Context context) {
        String channel = "";
        try {
            channel = SharedUtil.getString(context, Constants.SP_CHANNEL, null);
            if (isEmpty(channel)) {
                channel = getManifestData(context, Constants.DEV_CHANNEL);
                if (!isEmpty(channel)) {
                    SharedUtil.setString(context, Constants.SP_CHANNEL, channel);
                }
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return channel;
    }

    /**
     * ??????distinct id ?????????????????????????????????androidId
     */
    public static String getUserId(Context context) {
//        String id = getIdFile(context, Constants.SP_ALIAS_ID);
//        if (!isEmpty(id)) {
//            return id;
//        }
//        id = getIdFile(context, Constants.SP_DISTINCT_ID);
//        if (!isEmpty(id)) {
//            return id;
//        }
//        id = getIdFile(context, Constants.SP_UUID);
//        if (!isEmpty(id)) {
//            return id;
//        } else {
//            String uuid = String.valueOf(java.util.UUID.randomUUID());
//            setIdFile(context, Constants.SP_UUID, uuid);
//        }
//        if (TextUtils.isEmpty(id)) {
//            id = transSaveId(context);
//        }
        return UserInfo.getXho();
    }

    /**
     * ??????android id
     */
    public static String getAndroidID(Context context) {
        try {


            String androidId = SharedUtil.getString(AnalysysUtil.getContext(), Constants.SP_ANDID, null);
            if (!TextUtils.isEmpty(androidId)) {
                return androidId;
            }
            String cacheAndroidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedUtil.setString(context,Constants.SP_ANDID,cacheAndroidId);
            return cacheAndroidId;

        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
            return null;
        }
    }

//    /**
//     * ??????id
//     */
//    private static String transSaveId(Context context) {
//        String aliasId = SharedUtil.getString(context, Constants.SP_ALIAS_ID, null);
//        if (!TextUtils.isEmpty(aliasId)) {
//            setIdFile(context, Constants.SP_ALIAS_ID, aliasId);
//        }
//        String distinctId = SharedUtil.getString(context, Constants.SP_DISTINCT_ID, null);
//        if (!TextUtils.isEmpty(distinctId)) {
//            setIdFile(context, Constants.SP_DISTINCT_ID, distinctId);
//        }
//        String uuid = SharedUtil.getString(context, Constants.SP_UUID, null);
//        if (TextUtils.isEmpty(uuid)) {
//            uuid = String.valueOf(java.util.UUID.randomUUID());
//        }
//        setIdFile(context, Constants.SP_UUID, uuid);
//
//        if (!TextUtils.isEmpty(aliasId)) {
//            return aliasId;
//        }
//        if (!TextUtils.isEmpty(distinctId)) {
//            return distinctId;
//        }
//        return uuid;
//    }

    /**
     * ????????????????????????
     */
    public static String getVersionName(Context context) {
        try {
            final PackageManager packageManager = context.getPackageManager();
            final PackageInfo packageInfo = packageManager.
                    getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
            return Constants.EMPTY;
        }
    }

    private static String sCarrierNameCache = null;

    /**
     * ????????????????????????
     */
    public static String getCarrierName(Context context) {


        if (sCarrierNameCache != null) {
            return sCarrierNameCache;
        }
        sCarrierNameCache = "";
        try {
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                TelephonyManager mTelephonyMgr = (TelephonyManager)
                        context.getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephonyMgr != null) {
                    String imsi = mTelephonyMgr.getSubscriberId();
                    if (!isEmpty(imsi)) {
                        if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
                            sCarrierNameCache = "????????????";
                        } else if (imsi.startsWith("46001")) {
                            sCarrierNameCache = "????????????";
                        } else if (imsi.startsWith("46003")) {
                            sCarrierNameCache = "????????????";
                        }
                    }
                }
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionPrint(ignore);
        }
        if (TextUtils.isEmpty(sCarrierNameCache)) {
            return null;
        } else {
            return sCarrierNameCache;
        }
    }

    /**
     * ??????????????????
     */
    public static Object getScreenWidth(Context context) {
        int width = -1;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            width = metrics.widthPixels;
        } else {
            WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
            if (wm != null) {
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);
                width = dm.widthPixels;
            }
        }
        if (width == -1) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            width = dm.widthPixels;
        }
        return width;
    }

    /**
     * ?????? ????????????
     * ??????context???Activity????????????????????????????????? ????????????????????????Activity?????????
     */
    public static Object getScreenHeight(Context context) {
        int height = -1;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            height = metrics.heightPixels;
        } else {
            WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
            DisplayMetrics dm = new DisplayMetrics();
            if (wm != null) {
                wm.getDefaultDisplay().getMetrics(dm);
                height = dm.heightPixels;
            }
        }
        if (height == -1) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            height = dm.heightPixels;
        }
        return height;
    }

    /**
     * ??????????????????
     */
    public static Object isFirstDay(Context context) {
        String firstTimeSP = SharedUtil.getString(context, Constants.SP_FIRST_START_TIME, Constants.EMPTY);
        if (!isEmpty(firstTimeSP)) {
            String firstDay = getDay();
            String firstDaySP = firstTimeSP.substring(0, firstTimeSP.indexOf(" "));
            return firstDay.equals(firstDaySP);
        } else {
            return true;
        }
    }

    /**
     * ??????debug?????? ??????????????? > ???????????? > ????????????
     */
    public static Object getDebugMode(Context context) {
        int debug = SharedUtil.getInt(context, Constants.SP_SERVICE_DEBUG, -1);
        if (debug != -1) {
            return debug;
        }
        debug = SharedUtil.getInt(context, Constants.SP_USER_DEBUG, -1);
        if (debug != -1) {
            return debug;
        }
        return 0;
    }

    private static String sIMEICache = null;

    /**
     * ?????? IMEI
     */
    public static String getIMEI(Context context) {

        String imeiCache = SharedUtil.getString(AnalysysUtil.getContext(), Constants.SP_IMEI, null);
        if (!TextUtils.isEmpty(imeiCache)) {
            ANSLog.d("get imei cache");
            return imeiCache;
        }
        imeiCache = "";
        try {
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                TelephonyManager telephonyMgr =
                        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyMgr != null) {
                    imeiCache = telephonyMgr.getDeviceId();
                    SharedUtil.setString(context,Constants.SP_IMEI,imeiCache);
                }
                ANSLog.d("get imei");
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionPrint(ignore);
        }
        return imeiCache;

//        if (sIMEICache != null) {
//            return sIMEICache;
//        }
//        sIMEICache = "";
//        try {
//            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
//                TelephonyManager telephonyMgr =
//                        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//                if (telephonyMgr != null) {
//                    sIMEICache = telephonyMgr.getDeviceId();
//                }
//            }
//        } catch (Throwable ignore) {
//            ExceptionUtil.exceptionPrint(ignore);
//        }
//        return sIMEICache;
    }

    private static String sMacCache = null;

    /**
     * ??????mac??????
     */
    public static Object getMac(Context context) {
        String macCache = SharedUtil.getString(AnalysysUtil.getContext(), Constants.SP_MAC, null);
        if (!TextUtils.isEmpty(macCache)) {
            ANSLog.d("get mac address cache");
            return macCache;
        }
        macCache = "";
        try {
            if (Build.VERSION.SDK_INT < 23) {
                macCache = getMacBySystemInterface(context);
            } else if (Build.VERSION.SDK_INT == 23) {
                macCache = getMacByFileAndJavaAPI(context);
            } else {
                macCache = getMacByJavaAPI();
            }
            ANSLog.d("get mac address");
            SharedUtil.setString(context,Constants.SP_MAC,macCache);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return macCache;


//        if (sMacCache != null) {
//            return sMacCache;
//        }
//        sMacCache = "";
//        try {
//            if (Build.VERSION.SDK_INT < 23) {
//                sMacCache = getMacBySystemInterface(context);
//            } else if (Build.VERSION.SDK_INT == 23) {
//                sMacCache = getMacByFileAndJavaAPI(context);
//            } else {
//                sMacCache = getMacByJavaAPI();
//            }
//
//            SharedUtil.setString(context,Constants.SP_ANDID,sMacCache);
//        } catch (Throwable ignore) {
//            ExceptionUtil.exceptionThrow(ignore);
//        }
        //return sMacCache;
    }

    private static String getMacBySystemInterface(Context context) {
        if (context != null && checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) {
                    return info.getMacAddress();
                }
            }
        }
        return Constants.EMPTY;
    }

    private static String getMacByFileAndJavaAPI(Context context) throws Exception {
        String mac = getMacShell();
        return !isEmpty(mac) ? mac : getMacByJavaAPI();
    }

    private static String getMacByJavaAPI() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netInterface = interfaces.nextElement();
            if ("wlan0".equals(netInterface.getName()) || "eth0".equals(netInterface.getName())) {
                byte[] addr = netInterface.getHardwareAddress();
                if (addr == null || addr.length == 0) {
                    continue;
                }
                StringBuilder buf = new StringBuilder();
                for (byte b : addr) {
                    buf.append(String.format("%02X:", b));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString().toLowerCase(Locale.getDefault());
            }
        }
        return Constants.EMPTY;
    }

    private static String getMacShell() throws IOException {

        String[] urls = new String[]{
                "/sys/class/net/wlan0/address",
                "/sys/class/net/eth0/address",
                "/sys/devices/virtual/net/wlan0/address"
        };
        String mc;
        for (String url : urls) {
            mc = reaMac(url);
            if (mc != null) {
                return mc;
            }
        }
        return Constants.EMPTY;
    }

    private static String reaMac(String url) throws IOException {
        String macInfo;
        FileReader fstream = new FileReader(url);
        BufferedReader in = null;
        try {
            in = new BufferedReader(fstream, 1024);
            macInfo = in.readLine();
        } finally {
            CloseUtils.closeItQuietly(fstream, in);
        }
        return macInfo;
    }

//    /**
//     * ??????original id
//     */
//    public static Object getOriginalId(Context context) {
//        String originalId = SharedUtil.getString(context, Constants.SP_ORIGINAL_ID, null);
//        if (!isEmpty(originalId)) {
//            return originalId;
//        } else {
//            return getDistinctId(context);
//        }
//    }

    /**
     * ??????????????????
     */
    public static String dbEncrypt(String data) {
        if (!TextUtils.isEmpty(data)) {
            byte[] bytes = Base64.encode(data.getBytes(), Base64.NO_WRAP);
            String baseData = String.valueOf(new StringBuffer(new String(bytes)).reverse());
            int length = baseData.length();
            String subA = baseData.substring(0, length / 10);
            String subB = baseData.substring(length / 10, length);
            return subB + subA;
        }
        return null;
    }

    /**
     * ??????????????????
     */
    public static String dbDecrypt(String data) {
        try {
            if (!TextUtils.isEmpty(data)) {
                // ????????????????????????????????????????????????
                try {
                    String baseData = new String(Base64.decode(data.getBytes(), Base64.DEFAULT));
                    if (!TextUtils.isEmpty(baseData)
                            && !isEmpty(new JSONObject(baseData))) {
                        return baseData;
                    }
                } catch (Throwable ignored) {
                }
                // ??????????????????
                int length = data.length();
                int l = length - (length / 10);
                String subA = data.substring(0, l);
                String subB = data.substring(l, length);
                String dd = String.valueOf(new StringBuffer(subB + subA).reverse());
                return new String(Base64.decode(dd, Base64.NO_WRAP));
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return null;
    }

    /**
     * ??????Application
     */
    public static Application getApplication() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object at = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(at);
            if (app != null) {
                return (Application) app;
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return null;
    }

    /**
     * ??????key/value???????????????
     */
    public static void clearEmptyValue(Map<String, Object> map) {
        if (!isEmpty(map)) {
            Set<String> keys = map.keySet();
            String key;
            Object value;
            for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
                key = iterator.next();
                value = map.get(key);
                if (isEmpty(key)) {
                    iterator.remove();
                    continue;
                }
                if (isInvalidValue(value)) {
                    iterator.remove();
                }
            }
        }
    }

    private static boolean isInvalidValue(Object value) {
        return isEmpty(value)
                || "unknown".equalsIgnoreCase(value.toString())
                || "null".equalsIgnoreCase(value.toString());
    }

    /**
     * map ??????
     */
    public static <String, T> Map<String, T> deepCopy(Map<String, T> map) {
        try {
            if (map == null) {
                return new HashMap<>();
            }
            return new HashMap<>(map);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
            return new HashMap<>();
        }
    }

    /**
     * ?????? ??????
     */
    private static String certName = null;

    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        if (TextUtils.isEmpty(certName)) {
            certName = getManifestData(context, Constants.DEV_KEYSTONE);
        }
        if (TextUtils.isEmpty(certName)) {
            return null;
        } else {
            return getUserSSLSocketFactory(context, certName);
        }
    }


    /**
     * ??????????????????
     */
    private static SSLSocketFactory getUserSSLSocketFactory(Context context, String certName) {
        try {
            //????????????
            AssetManager am = context.getAssets();
            if (am != null) {
                // ??????????????????
                InputStream is = am.open(certName);
                InputStream input = new BufferedInputStream(is);
                CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
                Certificate cer = cerFactory.generateCertificate(input);
                // ??????keystore????????????????????????
                String keySoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keySoreType);
                keyStore.load(null);
                keyStore.setCertificateEntry("cert", cer);
                // ???????????? trustManager????????? keystore ???????????? ?????????????????????
                String algorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory trustManagerFactory =
                        TrustManagerFactory.getInstance(algorithm);
                trustManagerFactory.init(keyStore);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                // ??? TrustManager ???????????????SSLContext
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagers, null);
                return sslContext.getSocketFactory();
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return null;
    }

    /**
     * ??????????????????????????????
     */
    public static long getCalibrationTimeMillis(Context context) {
        if (Constants.isTimeCheck) {
            if (!CommonUtils.isMainProcess(context)) {
                String diff = SharedUtil.getString(context, Constants.SP_DIFF_TIME,"");
                if (!TextUtils.isEmpty(diff)) {
                    Constants.diffTime = CommonUtils.parseLong(diff, 0);
                }
            }
            return System.currentTimeMillis() + Constants.diffTime;
        }
        return System.currentTimeMillis();
    }

    /**
     * ????????????????????????
     */
    public static String getLaunchSource() {
        switch (Constants.sourceNum) {
            case 1:
                return "icon";
            case 2:
                return "msg";
            case 3:
                return "url";
            default:
                return "0";
        }
    }

    public static int parseInt(String value, int defaultValue) {
        try {
            if(TextUtils.isEmpty(value)){
                return defaultValue;
            }
            return Integer.parseInt(value);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
            return defaultValue;
        }
    }

    public static long parseLong(String value, long defaultValue) {
        try {
            if(TextUtils.isEmpty(value)){
                return defaultValue;
            }
            return Long.parseLong(value);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
            return defaultValue;
        }
    }

    public static float parseFloat(String value, float defaultValue) {
        try {
            if(TextUtils.isEmpty(value)){
                return defaultValue;
            }
            return Float.parseFloat(value);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
            return defaultValue;
        }
    }

    public static String[] getViewTypeAndText(View view, boolean isClickable) {
        String viewType = "";
        CharSequence viewText = "";
        if (view instanceof CheckBox) {
            // CheckBox
//            viewType = "CheckBox";
            CheckBox checkBox = (CheckBox) view;
            viewText = checkBox.getText();
        } else if (view instanceof RadioButton) {
            // RadioButton
//            viewType = "RadioButton";
            RadioButton radioButton = (RadioButton) view;
            viewText = radioButton.getText();
        } else if (view instanceof ToggleButton) {
            // ToggleButton
//            viewType = "ToggleButton";
            viewText = getCompoundButtonText(view);
        } else if (view instanceof CompoundButton) {
//            viewType = getViewTypeByReflect(view);
            viewText = getCompoundButtonText(view);
        } else if (view instanceof Button) {
            // Button
//            viewType = "Button";
            Button button = (Button) view;
            viewText = button.getText();
        } else if (view instanceof CheckedTextView) {
            // CheckedTextView
//            viewType = "CheckedTextView";
            CheckedTextView textView = (CheckedTextView) view;
            viewText = textView.getText();
        } else if (view instanceof TextView) {
            // TextView
//            viewType = "TextView";
            TextView textView = (TextView) view;
            viewText = textView.getText();
        } else if (view instanceof ImageView) {
            // ImageView
//            viewType = "ImageView";
            ImageView imageView = (ImageView) view;
            if (!TextUtils.isEmpty(imageView.getContentDescription())) {
                viewText = imageView.getContentDescription().toString();
            }
        } else if (view instanceof RatingBar) {
//            viewType = "RatingBar";
            RatingBar ratingBar = (RatingBar) view;
            viewText = String.valueOf(ratingBar.getRating());
        } else if (view instanceof SeekBar) {
//            viewType = "SeekBar";
            SeekBar seekBar = (SeekBar) view;
            viewText = String.valueOf(seekBar.getProgress());
        } else if (view instanceof ExpandableListView) {
//            viewType = "ExpandableListView";
            viewText = "";
        } else if (view instanceof ListView) {
//            viewType = "ListView";
            viewText = "";
        } else if (view instanceof GridView) {
//            viewType = "GridView";
            viewText = "";
        } else if (view instanceof Spinner) {
//            viewType = "Spinner";
            StringBuilder stringBuilder = new StringBuilder();
            viewText = traverseView(stringBuilder, (ViewGroup) view);
            if (!TextUtils.isEmpty(viewText)) {
                viewText = viewText.toString().substring(0, viewText.length() - 1);
            }
        } else if (view instanceof ViewGroup) {
//            viewType = getViewGroupTypeByReflect(view);
            viewText = view.getContentDescription();
            if (TextUtils.isEmpty(viewText) && isClickable) {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    viewText = traverseView(stringBuilder, (ViewGroup) view);
                    if (!TextUtils.isEmpty(viewText)) {
                        viewText = viewText.toString().substring(0, viewText.length() - 1);
                    }
                } catch (Throwable ignore) {
                    ExceptionUtil.exceptionThrow(ignore);
                }
            }
        }

//        if (TextUtils.isEmpty(viewType)) {
        viewType = view.getClass().getName();
//        }

        if (TextUtils.isEmpty(viewText)) {
            viewText = "";
            if (!TextUtils.isEmpty(view.getContentDescription())) {
                viewText = view.getContentDescription().toString();
            }
        }
        return new String[]{viewType, viewText.toString()};
    }

    /**
     * ?????? CompoundButton text
     *
     * @param view view
     * @return CompoundButton ???????????????
     */
    private static String getCompoundButtonText(View view) {
        try {
            CompoundButton switchButton = (CompoundButton) view;
            Method method;
            if (switchButton.isChecked()) {
                method = view.getClass().getMethod("getTextOn");
            } else {
                method = view.getClass().getMethod("getTextOff");
            }
            return (String) method.invoke(view);
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
            return "UNKNOWN";
        }
    }

    private static String traverseView(StringBuilder stringBuilder, ViewGroup root) {
        try {
            if (stringBuilder == null) {
                stringBuilder = new StringBuilder();
            }

            if (root == null) {
                return stringBuilder.toString();
            }

            final int childCount = root.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                final View child = root.getChildAt(i);

                if (child != null) {
                    if (child.getVisibility() != View.VISIBLE) {
                        continue;
                    }

                    if (child instanceof ViewGroup) {
                        traverseView(stringBuilder, (ViewGroup) child);
                    } else {
                        String viewText = getViewText(child);
                        if (!TextUtils.isEmpty(viewText)) {
                            stringBuilder.append(viewText);
                            stringBuilder.append("-");
                        }
                    }
                }
            }
            return stringBuilder.toString();
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
            return stringBuilder != null ? stringBuilder.toString() : "";
        }
    }

    private static String getViewText(View child) {
        if (child == null) {
            return "";
        }
        if (child instanceof EditText) {
            return "";
        }
        try {
            Class<?> switchCompatClass = null;
            switchCompatClass = AnsReflectUtils.getClassByName("android.support.v7.widget.SwitchCompat");

            if (switchCompatClass == null) {
                switchCompatClass = AnsReflectUtils.getClassByName("androidx.appcompat.widget.SwitchCompat");
            }

            CharSequence viewText = null;

            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;
                viewText = checkBox.getText();
            } else if (switchCompatClass != null && switchCompatClass.isInstance(child)) {
                CompoundButton switchCompat = (CompoundButton) child;
                Method method;
                if (switchCompat.isChecked()) {
                    method = child.getClass().getMethod("getTextOn");
                } else {
                    method = child.getClass().getMethod("getTextOff");
                }
                viewText = (String) method.invoke(child);
            } else if (child instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) child;
                viewText = radioButton.getText();
            } else if (child instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) child;
                boolean isChecked = toggleButton.isChecked();
                if (isChecked) {
                    viewText = toggleButton.getTextOn();
                } else {
                    viewText = toggleButton.getTextOff();
                }
            } else if (child instanceof Button) {
                Button button = (Button) child;
                viewText = button.getText();
            } else if (child instanceof CheckedTextView) {
                CheckedTextView textView = (CheckedTextView) child;
                viewText = textView.getText();
            } else if (child instanceof TextView) {
                TextView textView = (TextView) child;
                viewText = textView.getText();
            } else if (child instanceof ImageView) {
                ImageView imageView = (ImageView) child;
                if (!TextUtils.isEmpty(imageView.getContentDescription())) {
                    viewText = imageView.getContentDescription().toString();
                }
            } else {
                viewText = child.getContentDescription();
            }
            if (TextUtils.isEmpty(viewText) && child instanceof TextView) {
                viewText = ((TextView) child).getHint();
            }
            if (!TextUtils.isEmpty(viewText)) {
                return viewText.toString();
            }
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionThrow(ignore);
        }
        return "";
    }

    public static boolean verifyHost(String hostname, String url) {
        String uploadHost = null;
        try {
            if (TextUtils.isEmpty(hostname)) {
                return false;
            }
            if(TextUtils.isEmpty(url)) {
                url = CommonUtils.getUrl(AnalysysUtil.getContext());
            }
            ANSLog.i("verify url: " + url);
            uploadHost = URI.create(url).getHost();
        } catch (Throwable ignore) {
            ExceptionUtil.exceptionPrint(ignore);
        }
        ANSLog.i("verify " + hostname + ", " + uploadHost);
        return TextUtils.equals(hostname, uploadHost);
    }
}