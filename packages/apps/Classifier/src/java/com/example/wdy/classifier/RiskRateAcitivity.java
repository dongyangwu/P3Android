package com.example.wdy.classifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.classfier.Object.AllAppInfo;
import com.classfier.Object.AllPermissons;
import com.classfier.Object.AppInfo;
import com.classfier.Object.BayesTheta;
import com.classifier.util.FormatRiskScore;
import com.classifier.util.XMLTools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RiskRateAcitivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classifier_risk_rate_acitivity);
        BayesTheta bayesTheta = new BayesTheta();

        double[] Theta = bayesTheta.getTheta(getTrainResultInputStream(RiskBayesActivity.TRAIN_RESULT));
        String[] permissions = AllPermissons.getPermissionList(getPermissionslist(R.raw.permissionslist));
        PackageManager pm = getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        ArrayList<AppInfo> appInfoArrayList = AllAppInfo.getAllAppInfo(packageInfos, pm, permissions, Theta);

        try {
            XMLTools.save(appInfoArrayList, getOutputStream(RiskBayesActivity.APP_RISK_LEVEL));
            Toast.makeText(this, "训练数据保存成功!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> mapList = new ArrayList<>();

        for (int i = 0; i < appInfoArrayList.size(); i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("image", appInfoArrayList.get(i).getAppicon());
            listItem.put("appname", appInfoArrayList.get(i).getAppname());
            listItem.put("version", appInfoArrayList.get(i).getVersionName());

            //just output one number after .
            listItem.put("score", "Score: " + FormatRiskScore.DF.format(appInfoArrayList.get(i).getRiskscore()));
            mapList.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                mapList,
                R.layout.classifier_risk_rate_list,
                new String[] {"image", "appname", "version", "score"},
                new int[] {R.id.classifier_rate_item_image, R.id.classifier_rate_item_name, R.id.classifier_rate_item_version, R.id.classifier_rate_item_score});

        ListView listView = (ListView) findViewById(R.id.risk_rate_list);
        listView.setAdapter(simpleAdapter);
    }

    public OutputStream getOutputStream(String name) {
        try {
            FileOutputStream fileOutputStream = this.openFileOutput(name, Context.MODE_WORLD_READABLE);
            return fileOutputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputStream getPermissionslist(int id) {
        return getResources().openRawResource(id);
    }

    public InputStream getTrainResultInputStream(String name) {
        try {
            FileInputStream fileInputStream = this.openFileInput(name);
            return fileInputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(RiskRateAcitivity.this, RiskBayesActivity.class);
            RiskRateAcitivity.this.startActivity(intent);
            RiskRateAcitivity.this.finish();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_risk_rate_acitivity, menu);
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
