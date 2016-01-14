package com.classfier.Object;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.Jama.Matrix;

/**
 * Created by wdy on 15-3-18.
 */
public class AppInfo {
    private int versionCode = 0;
    //名称
    private String appname = "";
    //包
    private String packagename = "";
    //版本
    private String versionName = "";
    //图标
    private Drawable appicon = null;
    //风险值
    private double riskscore = 0;
    //种类
    private String type = "";

    private String[] permissions;


    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        if (permissions != null){
            int len = permissions.length;
            String[] permtmp = new String[len];
            String[] strtmp;
            for (int i = 0; i < len; i++) {
                strtmp = permissions[i].split("\\.");
                permtmp[i] = strtmp[strtmp.length - 1];
            }
            this.permissions = permtmp;
        } else {
            this.permissions = null;
        }
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }

    public double getRiskscore() {
        return riskscore;
    }

    public int[] getMartix(String[] permissions_list) {
        int[] matrix;
        if (permissions == null) {
            return null;
        }
        else {
            matrix = new int[151];
            List<String> permlist = Arrays.asList(permissions);

            for (int i = 0; i < permissions_list.length; i++) {
                if (permlist.contains(permissions_list[i])) {
                    matrix[i] = 1;
                } else {
                    matrix[i] = 0;
                }
            }
            return matrix;
        }
    }

    public void setRiskscore(double[] Theta, int[] matrix) {

        if (matrix != null) {
            double[] matrixtmp = new double[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                matrixtmp[i] = (double)matrix[i];
            }
            Matrix theta_matrix = new Matrix(Theta, 1);
            Matrix app_matrix = new Matrix(matrixtmp, matrix.length);
            Matrix score_matrix = theta_matrix.times(app_matrix);
            double[] score_array = score_matrix.getColumnPackedCopy();
            this.riskscore = score_array[0];
        }
        else {
            this.riskscore = 0;
        }
    }

    public void setRiskscore(int riskscore) {
        this.riskscore = riskscore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
