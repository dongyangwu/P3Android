/*
** @author:Dongyang Wu
*/
package android.content.papm;
/**
 * See {@link PrivacyAwarePersonalizeManager} for documentation on most of the APIs
 * her
 * {@hide}
 */

interface IPrivacyAwarePersonalizeManager {
    void notifyUser(String pkgName, String permName);
    void notifyUser2(String pkgName, String permName);
    int queryAppRiskLevel(String pkgName);
    int isUserAlreadyGranted(String pkgName, String permName);
    void setStutas(boolean status, inout String[] apkName, inout double[] riskLevel);
    boolean isPermNeedCheck(String permName, int level);
    boolean isAppNeedCheck(String pkgName);
    boolean isPermInNeedMocked(String permName);

}
