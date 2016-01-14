/*
 * @author: Dongyang Wu
 *
 */

package android.content.papm;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Slog;

import com.android.internal.content.NativeLibraryHelper;

import java.util.HashMap;
import java.util.Map;

public class PrivacyAwarePersonalizeManager {
    private static final String TAG = "PrivacyAwarePersonalizeManager"; 

    //app'risk is high
    public static final int HIGH_LEVEL = 2;

    public static final int MEDIUM_LEVEL = 1;

    public static final int LOW_LEVEL = 0;


    /**
     *
     */
    public static String[] HIGH_RISK_PERM = {
            //location
            "ACCESS_COARSE_LOCATION",
            "ACCESS_FINE_LOCATION",

            //make calls
            "PROCESS_OUTGOING_CALLS",
            "CALL_PHONE",

//            //contacts
            "READ_CONTACTS",
            "WRITE_CONTACTS",

//            //SMS
            "READ_SMS",
            "SEND_SMS",

            //install packages
            "INSTALL_PACKAGES"
    };

    public static String[] MEDIUM_RISK_PERM = { 
            "READ_CALENDAR",
            "READ_HISTORY_BOOKMARKS",
            "READ_PHONE_STATE",
            "RECEIVE_MMS",
            "RECEIVE_SMS",
            "RECORD_AUDIO",
            "RECEIVE_WAP_PUSH",
            "READ_LOGS",
            "MOUNT_UNMOUNT_FILESYSTEMS",
            "WRITE_CALENDAR",
            "WRITE_HISTORY_BOOKMARKS",
            "WRITE_SMS",
            "WRITE_EXTERNAL_STORAGE",
            "NFC",
            "GET_ACCOUNTS",
            "BLUETOOTH",
            "BLUETOOTH_ADMIN"
    };

    public static String[] PERM_NEED_MOCK = {
            "ACCESS_COARSE_LOCATION",
            "ACCESS_FINE_LOCATION",
            "READ_CONTACTS",
            "READ_SMS",
            "READ_CALENDAR",
            "READ_HISTORY_BOOKMARKS",
            "READ_PHONE_STATE",
            "READ_LOGS",
            "GET_ACCOUNTS",
    };

    public static String[] PERM_NOT_NEED_MOCK = {
            "PROCESS_OUTGOING_CALLS",
            "CALL_PHONE",
            "WRITE_CONTACTS",
            "SEND_SMS",
            "INSTALL_PACKAGES",
            "RECEIVE_MMS",
            "RECEIVE_SMS",
            "RECORD_AUDIO",
            "RECEIVE_WAP_PUSH",
            "MOUNT_UNMOUNT_FILESYSTEMS",
            "WRITE_CALENDAR",
            "WRITE_HISTORY_BOOKMARKS",
            "WRITE_SMS",
            "WRITE_EXTERNAL_STORAGE",
            "NFC",
            "BLUETOOTH",
            "BLUETOOTH_ADMIN"
    };

    public static HashMap<String, String> PERM_NEED_MOCK_HINT_MESSAGE = new HashMap<String, String>() {
        {
            put("ACCESS_COARSE_LOCATION", "允许访问粗粒度的地理位置. ");
            put("ACCESS_FINE_LOCATION", "允许访问细粒度的地理位置. ");
            put("READ_CONTACTS", "允许读取通讯录");
            put("READ_SMS", "允许读取SMS消息. ");
            put("READ_CALENDAR", "允许读日历数据.");
            put("READ_HISTORY_BOOKMARKS", "允许读浏览器历史和书签. ");
            put("READ_PHONE_STATE", "允许访问设备状态. ");
            put("READ_LOGS", "允许读取low-level系统日志文件. ");
            put("GET_ACCOUNTS", "允许访问账号管理服务中的账号列表. ");
        }
    };

    public static Map<String, String> PERM_NOT_NEED_MOCK_HINT_MESSAGE = new HashMap<String, String>(){
        {
            put("PROCESS_OUTGOING_CALLS", "允许查看被叫号码,转接或放弃通话. ");
            put("CALL_PHONE", "允许后台拨打电话. ");
            put("WRITE_CONTACTS", "允许写入通讯录");
            put("SEND_SMS", "允许后台发送SMS消息. ");
            put("INSTALL_PACKAGES", "允许应用安装其它应用. ");
            put("RECEIVE_MMS", "允许监控处理MMS. ");
            put("RECEIVE_SMS", "允许监控处理SMS. ");
            put("RECORD_AUDIO", "允许应用录音 ");
            put("RECEIVE_WAP_PUSH", "允许监控WAP推送信息. ");
            put("MOUNT_UNMOUNT_FILESYSTEMS", "允许挂载和卸载可移动存储文件系统. ");
            put("WRITE_CALENDAR", "允许写日历数据. ");
            put("WRITE_HISTORY_BOOKMARKS", "允许写浏览器历史和书签. ");
            put("WRITE_SMS", "允许写SMS消息. ");
            put("WRITE_EXTERNAL_STORAGE", "允许写入外部存储. ");
            put("NFC", "允许使用NFC完成I/O操作. ");
            put("BLUETOOTH", "允许访问已配对的蓝牙设备. ");
            put("BLUETOOTH_ADMIN", "允许应用发现和配对蓝牙设备. ");
        }

    };


    /**
     * is user agreed to grant this permission
     *
     * -1: let checkPermission wait User to authorization
     *  0: indicate user deny this permission(PERMISSION_DENIED)
     *  1: indicate user agree this permission(PERMISSION_GRANTED)
     */
    public static int isUserGranted = -1;

    final Context mContext;
    final IPrivacyAwarePersonalizeManager mService;
    final Handler mHandler;


    /**
     * {@hide}
     * @param context
     * @param service
     * @param handler
     */
    public PrivacyAwarePersonalizeManager(Context context, IPrivacyAwarePersonalizeManager service, Handler handler) {
        mContext = context;
        mService = service;
        mHandler = handler;
    }

    /**
     * @hide
     * @param pkgName
     * @return
     */
    public boolean isAppNeedCheck(String pkgName) {
        try {
            return mService.isAppNeedCheck(pkgName);
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * @hide
     * @param permName
     * @return
     */
    public boolean isPermNeedCheck(String permName, int level) {
        try {
            return mService.isPermNeedCheck(permName, level);
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * @hide
     * @param pkgName
     * @param permName
     */
    public void notifyUser(String pkgName, String permName) {
        try {
            mService.notifyUser(pkgName, permName);
        } catch (RemoteException e) {
        }
    }

    /**
     * @hide
     * @param pkgName
     * @return
     */
    public int queryAppRiskLevel(String pkgName) {
        try {
            return mService.queryAppRiskLevel(pkgName);
        } catch (RemoteException e) {
            Slog.e(TAG, "query failed", e);
            throw new RuntimeException("privacy awareness manager has died", e);
        }
    }

    public void setStutas(boolean status, String[] apkName, double[] riskLevel) {
        try {
            mService.setStutas(status, apkName, riskLevel);
        } catch (RemoteException e) {
        }
    }
}
