package com.android.server.papm;

import java.util.HashMap;

/**
 * Created by wdy on 15-4-24.
 */
public class AppRiskInfo {
    private String pkgName;
    private double riskLevel;

    public HashMap<String, String> pM;

    public AppRiskInfo() {
        pM = new HashMap<>();
    }


    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public double getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(double riskLevel) {
        this.riskLevel = riskLevel;
    }
}
