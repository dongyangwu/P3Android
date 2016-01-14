package com.example.wdy.classifier;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.example.wdy.predictservice.PersonaePredictService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClassifierMainActivity extends Activity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classifier_main);

        final String[] classifiers = { "应用风险值评估", "Profile预测服务管理", "ccc" };
        String[] cdesc = { "评估已安装应用的风险值", "预测用户的兴趣爱好等", "vv"};
        int[] cimages = new int[] {
                R.drawable.classifier_item_risk,
                R.drawable.classifier_item_risk1,
                R.drawable.classifier_item_risk1
        };
        String symbol = ">";

        List<Map<String, Object>> mapList = new ArrayList<>();
        for(int i = 0; i < classifiers.length; i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("image", cimages[i]);
            listItem.put("classifier", classifiers[i]);
            listItem.put("desc", cdesc[i]);
            listItem.put("symbol", symbol);
            mapList.add(listItem);

        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                mapList,
                R.layout.classifier_main_list,
                new String[] {"image", "classifier", "desc", "symbol"},
                new int[] {R.id.classifier_list_item_image, R.id.classifier_list_item_name, R.id.classifier_list_item_desc, R.id.classifier_list_item_symbol});

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(classifiers[position].equals("应用风险值评估")) {
                    Intent intent = new Intent();
                    intent.setClass(ClassifierMainActivity.this, RiskBayesActivity.class);
                    ClassifierMainActivity.this.startActivity(intent);
                    ClassifierMainActivity.this.finish();
                } else if (classifiers[position].equals("Profile预测服务管理")) {
                    Intent intent = new Intent();
                    intent.setClass(ClassifierMainActivity.this, ProfilePredictActivity.class);
                    ClassifierMainActivity.this.startActivity(intent);
                    ClassifierMainActivity.this.finish();
                }
            }
        });

    }

        public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle("提示");
            isExit.setMessage("确定要退出吗？");
            isExit.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
            isExit.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", listener);
            isExit.show();
        }
        return false;
    }

    DialogInterface.OnClickListener listener = new  DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    System.out.println("按下了确定键");

                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    System.out.println("按下了取消键");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_demo, menu);
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
