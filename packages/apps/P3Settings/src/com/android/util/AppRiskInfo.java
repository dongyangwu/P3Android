package com.android.util;

/**
 * Created by wdy on 15-5-12.
 */
public class AppRiskInfo {
    private String pkgName;
    private double riskLevel;

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
