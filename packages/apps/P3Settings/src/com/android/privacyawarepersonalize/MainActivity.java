package com.android.privacyawarepersonalize;

import android.app.Activity;
import android.content.Context;
import android.content.papm.PersonalizationSupportManager;
import android.content.papm.PrivacyAwarePersonalizeManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.android.util.AppRiskInfo;
import com.android.util.AppRiskParser;
import com.android.util.MySwitch;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.content.Context.PAPM_SERVICE;


public class MainActivity extends Activity {

    private final String CLASSIFIER_SAVE_FILE = "/data/data/com.example.wdy.classifier/files/app_risk.xml";
    private static ArrayList<AppRiskInfo> APP_RISK_STORE = null;
    static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pap_main);

        ArrayList<AppRiskInfo> a = new ArrayList<>();
        final PrivacyAwarePersonalizeManager pps = (PrivacyAwarePersonalizeManager) this.getSystemService(Context.PAPM_SERVICE);
        final PersonalizationSupportManager pss = (PersonalizationSupportManager) this.getSystemService(Context.PSM_SERVICE);

        MySwitch ppsStatus = (MySwitch) findViewById(R.id.pps_switch);
        MySwitch pssStatus = (MySwitch) findViewById(R.id.pss_switch);
        ppsStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println("开着");
                    getAppRiskStore(CLASSIFIER_SAVE_FILE);
                    final String[] apkName = new String[APP_RISK_STORE.size()];
                    final double[] risklevel = new double[APP_RISK_STORE.size()];
                    for (int i = 0; i < APP_RISK_STORE.size(); i++) {
                        apkName[i] = APP_RISK_STORE.get(i).getPkgName();
                        System.out.println(apkName[i]);
                        risklevel[i] = APP_RISK_STORE.get(i).getRiskLevel();
                    }
                    pps.setStutas(true, apkName, risklevel);
                } else {


                    System.out.println("关着");
                    getAppRiskStore(CLASSIFIER_SAVE_FILE);
                    final String[] apkName = new String[APP_RISK_STORE.size()];
                    final double[] risklevel = new double[APP_RISK_STORE.size()];
                    for (int i = 0; i < APP_RISK_STORE.size(); i++) {
                        apkName[i] = APP_RISK_STORE.get(i).getPkgName();
                        risklevel[i] = APP_RISK_STORE.get(i).getRiskLevel();
                    }
                    pps.setStutas(false, apkName, risklevel);
                }
            }
        });

        pssStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println("开着");
                    pss.setStutas(true);
                } else {
                    System.out.println("关着");
                    pss.setStutas(false);
                }

            }
        });
    }

    private boolean getAppRiskStore(String appRiskXml) {
        try {
            InputStream in = new FileInputStream(new File(appRiskXml));
            APP_RISK_STORE = AppRiskParser.getAppRiskInfo(in);
//            System.out.println(APP_RISK_STORE.size() + "");
            return true;
        } catch (XmlPullParserException e) {
            APP_RISK_STORE = null;
            Log.e(TAG, "Failure to parser AppRisk XML file", e);
            return false;
        } catch (IOException e) {
            APP_RISK_STORE = null;
            Log.v(TAG, "Failure to open AppRisk xml file", e);
            return false;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
