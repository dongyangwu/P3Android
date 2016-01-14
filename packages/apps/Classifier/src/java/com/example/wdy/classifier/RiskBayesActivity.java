package com.example.wdy.classifier;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.Jama.Matrix;
import com.classfier.Object.AllAppInfo;
import com.classfier.Object.AllPermissons;
import com.classfier.Object.AppInfo;
import com.classifier.util.String2double;
import com.classifier.util.XMLTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;



public class RiskBayesActivity extends Activity {
    private android.app.ProgressDialog progressDialog;
    private int PROGRESS_MAX = 0;
    private InputStream inputStream;
    private TrainProgress trainProgress;
    public static String TRAIN_RESULT = "trainResult.txt";
    public static String APP_RISK_LEVEL = "app_risk.xml";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classifier_risk_bayes);

        final String[] items= {"查看训练结果", "重新训练"};
        String[] decs = {"训练后每个app的风险值", "提供训练数据后可重新训练"};
        String symbol = ">";

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("item", items[i]);
            map.put("decs", decs[i]);
            map.put("symbol", symbol);
            mapList.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                mapList,
                R.layout.classifier_risk_bayes_list,
                new String[]{"item", "decs", "symbol"},
                new int[] {R.id.classifier_risk_item_name, R.id.classifier_risk_item_desc, R.id.classifier_risk_item_symbol}
                );
        ListView lv = (ListView) findViewById(R.id.risk_bayes_list);
        lv.setAdapter(simpleAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (items[position].equals("重新训练")) {
                    progressDialog = new android.app.ProgressDialog(RiskBayesActivity.this);
                    inputStream = getResources().openRawResource(R.raw.traindataset_bayes);
                    getProgressMax(inputStream);
                    progressDialog.setMax(getPROGRESS_MAX());
                    progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                if (trainProgress != null && trainProgress.getStatus() == AsyncTask.Status.RUNNING) {
                                    trainProgress.cancel(true);
                                }
                            }
                        }
                    });
                    InputStream inputStream2 = getResources().openRawResource(R.raw.traindataset_bayes);
                    trainProgress= new TrainProgress();
                    trainProgress.execute(inputStream2);
                }
                else if (items[position].equals("查看训练结果")){
                    Intent intent = new Intent();
                    intent.setClass(RiskBayesActivity.this, RiskRateAcitivity.class);
                    RiskBayesActivity.this.startActivity(intent);
                    RiskBayesActivity.this.finish();
                }
            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(RiskBayesActivity.this, ClassifierMainActivity.class);
            RiskBayesActivity.this.startActivity(intent);
            RiskBayesActivity.this.finish();
        }
        return false;
    }

    private void getProgressMax(InputStream inputStream) {
        PROGRESS_MAX = 0;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                PROGRESS_MAX++;
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public int getPROGRESS_MAX(){
        return PROGRESS_MAX * 2;
    }

    class TrainProgress extends AsyncTask<InputStream, Integer, double[]> {
        int trainDataSize = 0;
        int progressvalue = 0;
        protected double[] doInBackground(InputStream... params) {

            System.out.println(params[0]);
            Vector<double[]> vector = new Vector<>();
            InputStreamReader inputStreamReader = new InputStreamReader(params[0]);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    if(isCancelled())
                        return null;
                    String[] tmp = line.split("\t");
//                    Log.i("info", line);
                    double[] data = String2double.String2double(tmp);
                    vector.add(data);
                    progressvalue++;
                    publishProgress(progressvalue);
                }

            }catch (IOException e){
                e.printStackTrace();
            }

            trainDataSize = vector.size();
            int row = vector.get(0).length;
            Matrix statistics = new Matrix(row, 1);
            for (int i = 0; i < vector.size(); i++) {
                if(isCancelled())
                    return null;
                Matrix tmp = new Matrix(vector.get(i), vector.get(i).length);
                statistics = statistics.plus(tmp);
                progressvalue++;
                publishProgress(progressvalue);
            }
            double[] result = statistics.getColumnPackedCopy();
            return result;
        }

        public void onPostExecute(double[] trainresult) {
            String tmp = "";
            double[] Theta = new double[trainresult.length];
            for (int i = 0; i < trainresult.length; i++) {
                if (i < 9) {
                    Theta[i] = (trainresult[i] + 1) / (double)(trainDataSize * 3 + 1);
                }
                else if (i >= 9 && i < 26) {
                    Theta[i] = (trainresult[i] + 1) / (double)(trainDataSize * 2 + 1);
                }
                else {
                    Theta[i] = (trainresult[i] + 1) / (double)(trainDataSize + 1);
                    if (Theta[i] > 0.5) {
                        Theta[i] = (double)0.5;
                    }
                }

//                System.out.println(Theta[i]);
                tmp = tmp + Theta[i] + "\t";
            }
            try {
                inputStream.close();
                FileOutputStream outputStream = openFileOutput(TRAIN_RESULT,
                        Activity.MODE_PRIVATE);
                outputStream.write(tmp.getBytes());
                outputStream.flush();
                outputStream.close();


                String[] permissions = AllPermissons.getPermissionList(getPermissionslist(R.raw.permissionslist));
                PackageManager pm = getPackageManager();
                List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
                ArrayList<AppInfo> appInfoArrayList = AllAppInfo.getAllAppInfo(packageInfos, pm, permissions, Theta);

                XMLTools.save(appInfoArrayList, getOutputStream(APP_RISK_LEVEL));


                Toast.makeText(RiskBayesActivity.this, "训练数据保存成功!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void onPreExecute() {
            progressDialog.setMessage("训练数据...");
            progressDialog.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("训练risk分类器");
            progressDialog.show();
        }

        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_risk_bayes, menu);
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
