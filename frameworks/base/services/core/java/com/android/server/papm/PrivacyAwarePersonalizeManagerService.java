/**
  *@author:Dongyang Wu
  *@date:2015-4-7
  *This service ...
  */
package com.android.server.papm;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.papm.IPrivacyAwarePersonalizeManager;
import android.content.papm.PrivacyAwarePersonalizeManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;
import android.view.WindowManager;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class PrivacyAwarePersonalizeManagerService extends IPrivacyAwarePersonalizeManager.Stub {
    private static final String TAG =  "PrivacyAwarePersonalizeManagerService";

    public static HashMap<String, String> APP_RISK_MAP = null;
    public static HashMap<String, HashMap<String, String>> APP_PERM_MANAGER = null;
    public static boolean status = false;


    private PrivacyAwarePersonalizeManagerThread mpapmThread;
    private PrivacyAwareHandler mHandler;
    private Context mContext;
    private Vector<Message> msgV;
    private Timer msgVTimer;
    private Timer showTimer;
    private int time = 15;
    private AlertDialog.Builder dialogBuild;
    private AlertDialog mDialog;


    public PrivacyAwarePersonalizeManagerService(Context context) {
        super();
        mContext = context;
        Log.i(TAG, "Starting " + PrivacyAwarePersonalizeManagerService.class.getName());
        mpapmThread = new PrivacyAwarePersonalizeManagerThread(TAG);
        mpapmThread.start();
        Log.i(TAG, "create " + TAG + " success");
    }


    /**
     * first: try to read app risk file
     * second: set status true
     * @param status
     * @param apkName
     * @return
     * @throws RemoteException
     */
    @Override
    public void setStutas(boolean status, String[] apkName, double[] riskLevel) {
        if (status == true) {
            this.status = status;

            //init notify msg vector
            msgV = new Vector<>();
            msgVTimer = new Timer(true);
            dialogBuild = new AlertDialog.Builder(mContext);

            waitforUserGrant();
            APP_RISK_MAP = new HashMap<>();
            APP_PERM_MANAGER = new HashMap<String , HashMap<String, String>>();
            Slog.v(TAG, apkName.length + " risklevel.length = " + riskLevel.length);

            for (int i = 0; i < apkName.length; i++) {
                if (riskLevel[i] < 1.0) {
                    APP_RISK_MAP.put(apkName[i], "low");
                } else if (riskLevel[i] < 3.0) {
                    APP_RISK_MAP.put(apkName[i], "medium");
                } else {
                    APP_RISK_MAP.put(apkName[i], "high");
                }

                HashMap<String, String> perm_list = new HashMap<>();
                APP_PERM_MANAGER.put(apkName[i], perm_list);
            }
        }
        else{
            msgV = null;
            msgVTimer.cancel();

            APP_RISK_MAP = null;
            APP_PERM_MANAGER = null;
            this.status = false;
        }
    }

    /**
     * @hide
     * get the perm last name
     * @param permName
     * @return
     */
    private String getPermString(String permName) {
        String[] psplit = permName.split("\\.");
        return psplit[psplit.length-1];
    }

    private boolean isMsgAlreadyInQueue(String pkgName, String permName) {
        if (status == true) {
            for (Message msg : msgV) {
                Bundle data = msg.getData();
                if (pkgName.equals(data.getString("pkgname"))) {
                    if (permName.equals(data.getString("permname"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * @hide
     * judge the permssion after the app nead to be checked;
     * @param permName
     * @return
     */
    @Override
    public boolean isPermNeedCheck(String permName, int level) {
        if (status == true) {
            String perm = getPermString(permName);
            if (level == PrivacyAwarePersonalizeManager.MEDIUM_LEVEL) {
                for(int i = 0; i < PrivacyAwarePersonalizeManager.PERM_NEED_MOCK.length; i++) {
                    if (PrivacyAwarePersonalizeManager.PERM_NEED_MOCK[i].equals(perm)) {
                        return true;
                    }
                }
            }

            if (level == PrivacyAwarePersonalizeManager.HIGH_LEVEL) {
                for (int i = 0; i < PrivacyAwarePersonalizeManager.PERM_NEED_MOCK.length; i++) {
                    if (PrivacyAwarePersonalizeManager.PERM_NEED_MOCK[i].equals(perm)) {
                        return true;
                    }
                }

                for (int j = 0; j < PrivacyAwarePersonalizeManager.PERM_NOT_NEED_MOCK.length; j++) {
                    if (PrivacyAwarePersonalizeManager.PERM_NOT_NEED_MOCK[j].equals(perm)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isPermInNeedMocked(String permName) {
        String perm = getPermString(permName);
        for (int i = 0; i < PrivacyAwarePersonalizeManager.PERM_NEED_MOCK.length; i++) {
            if (PrivacyAwarePersonalizeManager.PERM_NEED_MOCK[i].equals(perm)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @hide
     * judge an app whether need to be check;
     * @param pkgName
     * @return
     */
    @Override
    public boolean isAppNeedCheck(String pkgName) {
        if (status == true) {
            if (APP_RISK_MAP != null) {
                if (APP_RISK_MAP.containsKey(pkgName)) {
                    Slog.v(TAG, "检查" + pkgName);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * @hide
     * @param pkgName
     * @param permName
     * @return
     */
    @Override
    public int isUserAlreadyGranted(String pkgName, String permName) {
        Slog.v(TAG, "查询" + pkgName + "的权限" + permName + "是否已被授予");
        if (APP_PERM_MANAGER.get(pkgName).containsKey(permName)) {
            if (APP_PERM_MANAGER.get(pkgName).get(permName).equals("DENIED")) {
                Slog.v(TAG, "查询" + pkgName + "的权限" + permName + "已被拒绝");
                return PackageManager.PERMISSION_DENIED;
            } else if (APP_PERM_MANAGER.get(pkgName).get(permName).equals("GRANTED")) {
                Slog.v(TAG, "查询" + pkgName + "的权限" + permName + "已被授予");
                return PackageManager.PERMISSION_GRANTED;
            } else {
                Slog.v(TAG, "查询" + pkgName + "的权限" + permName + "已被仿真");
                return PackageManager.PERMISSION_MOCKED;
            }
        }
        return 10;
    }

    /**
     * @hide
     * get the risk level of an app which need to be checked;
     * @param pkgName
     * @return
     * @throws RemoteException
     */
    public int queryAppRiskLevel(String pkgName) throws RemoteException {
        if (status == true) {
            String level = APP_RISK_MAP.get(pkgName);
            if (level.equals("low")) {
                return PrivacyAwarePersonalizeManager.LOW_LEVEL;
            } else if (level.equals("medium")) {
                return PrivacyAwarePersonalizeManager.MEDIUM_LEVEL;
            } else {
                return PrivacyAwarePersonalizeManager.HIGH_LEVEL;
            }
        }
        return -1;
    }


    /**
     * @hide
     * @param pkgName
     * @param permName
     */
    public void notifyUser(String pkgName, String permName) {
        if (status == true) {
            if (!isMsgAlreadyInQueue(pkgName, permName)) {
                Slog.v(TAG, "notify User!!!!!");
                Message msg = Message.obtain();
                Bundle data = new Bundle(2);
                data.putString("pkgname", pkgName);
                data.putString("permname", permName);
                msg.setData(data);
                msg.what = PrivacyAwareHandler.NOTIFY_USER;
                msgV.add(msg);
                Slog.v(TAG, "加入msgV");
            }
        }
    }

    /**
     * @hide
     */
    public void notifyUser2(String pkgName, String permName) {
        if (status == true) {
            if (!isMsgAlreadyInQueue(pkgName, permName)) {
                Slog.v(TAG, "notify User!!!!!");
                Message msg = Message.obtain();
                Bundle data = new Bundle(2);
                data.putString("pkgname", pkgName);
                data.putString("permname", permName);
                msg.setData(data);
                msg.what = PrivacyAwareHandler.NOTIFY_USER2;
                msgV.add(msg);
                Slog.v(TAG, "加入msgV");
            }
        }
    }

    /**
     * when have many notify msgs, send one at 15s period.
     * this is a daemon.
     */
    private void waitforUserGrant() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (status == true) {
                    if (!msgV.isEmpty()) {
                        Message msg = msgV.firstElement();
                        msgV.remove(msg);
                        mHandler.sendMessage(msg);
                        Bundle data = msg.getData();
                        warningShowTime(data.getString("pkgname"), data.getString("permname"));
                    }
                }
            }
        };
        msgVTimer.schedule(task, 0, 16000);
    }

    private void warningShowTime(final String pkgName, final String permName) {
        showTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (status ==true) {
                    Message msg = Message.obtain();
                    msg.what = PrivacyAwareHandler.SHOW_TIME;
                    msg.arg1 = time--;
                    Bundle data = new Bundle(2);
                    data.putString("permname", permName);
                    msg.setData(data);
                    if (time < 1) {
                        showTimer.cancel();
                        mDialog.dismiss();
                        time = 15;
                        if (APP_PERM_MANAGER.get(pkgName).containsKey(permName)) {
                            APP_PERM_MANAGER.get(pkgName).remove(permName);
                            APP_PERM_MANAGER.get(pkgName).put(permName, "DENIED");
                        } else {
                            APP_PERM_MANAGER.get(pkgName).put(permName, "DENIED");
                            Slog.v(TAG, "保存成功");
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
        };
        showTimer.schedule(task, 0, 1000);
    }

    private void showWarningBox(final Context context, final String pkgName, final String permName) {
        dialogBuild.setTitle(getPermString(pkgName) + "权限申请");
        dialogBuild.setIcon(android.R.drawable.ic_dialog_info);

        String msg = PrivacyAwarePersonalizeManager.PERM_NEED_MOCK_HINT_MESSAGE.get(getPermString(permName));
        dialogBuild.setMessage("\t\t\t" + getPermString(permName) + "\n\t " + msg);


        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        if (APP_PERM_MANAGER.get(pkgName).containsKey(permName)) {
                            APP_PERM_MANAGER.get(pkgName).remove(permName);
                            APP_PERM_MANAGER.get(pkgName).put(permName, "DENIED");
                        } else {
                            APP_PERM_MANAGER.get(pkgName).put(permName, "DENIED");
                            Slog.v(TAG, "保存成功");
                        }
                        showTimer.cancel();
                        time = 15;
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        if (APP_PERM_MANAGER.get(pkgName).containsKey(permName)) {
                            APP_PERM_MANAGER.get(pkgName).remove(permName);
                            APP_PERM_MANAGER.get(pkgName).put(permName, "GRANTED");
                        } else {
                            APP_PERM_MANAGER.get(pkgName).put(permName, "GRANTED");
                            Slog.v(TAG, "保存成功");
                        }
                        showTimer.cancel();
                        time = 15;
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        if (APP_PERM_MANAGER.get(pkgName).containsKey(permName)) {
                            APP_PERM_MANAGER.get(pkgName).remove(permName);
                            APP_PERM_MANAGER.get(pkgName).put(permName, "MOCKED");
                        } else {
                            APP_PERM_MANAGER.get(pkgName).put(permName, "MOCKED");
                            Slog.v(TAG, "保存成功");
                        }
                        showTimer.cancel();
                        time = 15;
                        break;
                }
            }
        };
        dialogBuild.setNeutralButton("Mock", listener);
        dialogBuild.setNegativeButton("忽略", listener);
        dialogBuild.setPositiveButton("授予", listener);
        mDialog=dialogBuild.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.show();
    }

    /**
     * set system level warning;
     * @param context
     * @param pkgName
     * @param permName
     */
    private void showWarningBox2(final Context context, final String pkgName, final String permName) {
        dialogBuild.setTitle(getPermString(pkgName) + "权限申请");
        dialogBuild.setIcon(android.R.drawable.ic_dialog_info);

        String msg = PrivacyAwarePersonalizeManager.PERM_NOT_NEED_MOCK_HINT_MESSAGE.get(getPermString(permName));
        dialogBuild.setMessage("\t\t\t" + getPermString(permName) + "\n\t " + msg);


        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        if (APP_PERM_MANAGER.get(pkgName).containsKey(permName)) {
                            APP_PERM_MANAGER.get(pkgName).remove(permName);
                            APP_PERM_MANAGER.get(pkgName).put(permName, "DENIED");
                        } else {
                            APP_PERM_MANAGER.get(pkgName).put(permName, "DENIED");
                            Slog.v(TAG, "保存成功");
                        }
                        showTimer.cancel();
                        time = 15;
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        if (APP_PERM_MANAGER.get(pkgName).containsKey(permName)) {
                            APP_PERM_MANAGER.get(pkgName).remove(permName);
                            APP_PERM_MANAGER.get(pkgName).put(permName, "GRANTED");
                        } else {
                            APP_PERM_MANAGER.get(pkgName).put(permName, "GRANTED");
                            Slog.v(TAG, "保存成功");
                        }
                        showTimer.cancel();
                        time = 15;
                        break;
                }
            }
        };
        dialogBuild.setNegativeButton("忽略", listener);
        dialogBuild.setPositiveButton("授予", listener);
        mDialog=dialogBuild.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.show();
    }



    public class PrivacyAwarePersonalizeManagerThread extends Thread {

        public PrivacyAwarePersonalizeManagerThread(String name) {
            super(name);
        }

        public void run() {
            Looper.prepare();
            mHandler = new PrivacyAwareHandler();
            Looper.loop();
        }
    }

    private class PrivacyAwareHandler extends Handler {
        static final int RISK_LEVEL = 1;
        static final int NOTIFY_USER = 2;
        static final int NOTIFY_USER2 = 3;
        static final int SHOW_TIME = 4;
        Bundle data;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RISK_LEVEL:
                    break;
                case NOTIFY_USER:
                    //缺少一个带有三个按钮的AlertDialog
                    data = msg.getData();
                    showWarningBox(mContext, data.getString("pkgname"), data.getString("permname"));
                    break;
                case NOTIFY_USER2:
                    data = msg.getData();
                    showWarningBox2(mContext, data.getString("pkgname"), data.getString("permname"));
                    break;
                case SHOW_TIME:
                    int timetmp = msg.arg1;
                    data = msg.getData();
                    String message = PrivacyAwarePersonalizeManager.PERM_NEED_MOCK_HINT_MESSAGE.get(getPermString(data.getString("permname")));
                    if (message == null) {
                        message = PrivacyAwarePersonalizeManager.PERM_NOT_NEED_MOCK_HINT_MESSAGE.get(getPermString(data.getString("permname")));
                    }
                    mDialog.setMessage(getPermString(data.getString("permname")) + ": " + message + "\n\t\t\t\t\t(" + timetmp + ")");
            }
        }
    }

}
