package com.classfier.Object;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wdy on 15-4-22.
 */
public class AllAppInfo {

    public static ArrayList<AppInfo> getAllAppInfo(List<PackageInfo> packageInfos, PackageManager pm, String[] permissions, double[] Theta) {

        ArrayList<AppInfo> appInfoArrayList = new ArrayList<>();
        for (int i = 0; i < packageInfos.size(); i++) {
            PackageInfo packageInfo = packageInfos.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                AppInfo appInfo = new AppInfo();
                appInfo.setAppicon(packageInfo.applicationInfo.loadIcon(pm));
                appInfo.setAppname(packageInfo.applicationInfo.loadLabel(pm).toString());
                appInfo.setPackagename(packageInfo.packageName);
                appInfo.setVersionCode(packageInfo.versionCode);
                appInfo.setVersionName(packageInfo.versionName);
                appInfo.setPermissions(packageInfo.requestedPermissions);

                if (packageInfo.requestedPermissions == null){
                    appInfo.setRiskscore(0);
                } else {
                    int[] matrix = appInfo.getMartix(permissions);
                    appInfo.setRiskscore(Theta, matrix);
                }

                appInfoArrayList.add(appInfo);
            }
        }
        return appInfoArrayList;
    }
}
