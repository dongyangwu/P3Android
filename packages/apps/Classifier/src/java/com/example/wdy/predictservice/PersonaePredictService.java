package com.example.wdy.predictservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.papm.IPersonalizationSupportManager;
import android.content.papm.PersonalizationSupportManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.classfier.personalization.util.PersonaeClassifier;
import com.classfier.personalization.util.PersonaeSignals2Matrix;
import com.example.wdy.classifier.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.*;

public class PersonaePredictService extends Service {

    public static final String SIGNALS_MATRIX = "signals_matrix";
    public static final String PROFILE_FILE = "profile_file";

    private static final String TAG = "PersonaePredictService";
    private String path = "/data/data/com.example.wdy.classifier/files/";
    private Timer timer;
    private TimerTask timerTask;

    public PersonaePredictService() {
    }

    public void onCreate() {
        super.onCreate();
        timer = new Timer(true);

        final List<Integer> wordlist = new ArrayList<Integer>(8);
        wordlist.add(R.raw.wordtabledocs);
        wordlist.add(R.raw.wordtableentertainment);
        wordlist.add(R.raw.wordtablefinance);
        wordlist.add(R.raw.wordtablehomemaker);
        wordlist.add(R.raw.wordtableretire);
        wordlist.add(R.raw.wordtablesports);
        wordlist.add(R.raw.wordtabletech);
        wordlist.add(R.raw.wordtabletravel);

        final List<Integer> modelist = new ArrayList<Integer>(8);
        modelist.add(R.raw.modedocs);
        modelist.add(R.raw.modeentertainment);
        modelist.add(R.raw.modefinance);
        modelist.add(R.raw.modehomemaker);
        modelist.add(R.raw.moderetire);
        modelist.add(R.raw.modesports);
        modelist.add(R.raw.modetech);
        modelist.add(R.raw.modetravel);

        final List<String> extraStr = new ArrayList<String>(8);
        extraStr.add("_docs");
        extraStr.add("_entertainment");
        extraStr.add("_finance");
        extraStr.add("_homemaker");
        extraStr.add("_retire");
        extraStr.add("_sports");
        extraStr.add("_tech");
        extraStr.add("_travel");

        timerTask = new TimerTask() {
            @Override
            public void run() {
                PersonalizationSupportManager psm = (PersonalizationSupportManager) getSystemService(PSM_SERVICE);
                PersonaeSignals2Matrix ps2m = new PersonaeSignals2Matrix();
                String[] personae = {"medical_staff", "enthusiasts", "business_executive", "homemaker", "retiree", "sports_stuff", "technophile", "travel_stuff"};
                int[] value = new int[8];
                try {
                    List<String> signals = psm.getPersonalizationSignals();

                    for (int j = 0; j < 8; j++) {
                        File file1 = new File(path + PROFILE_FILE + extraStr.get(j));
                        if (file1.exists()) {
                            ps2m.deleteFile(file1);
                        }
                    }

                    for (int i = 0; i < wordlist.size(); i++) {
                        HashMap<String, Integer> word_Num = ps2m.readWordTable(getResources().openRawResource(wordlist.get(i)));
                        Log.i(TAG, "Read word table Over!!!!!!!!");

                        File file = new File(path + SIGNALS_MATRIX);
                        if (file.exists()) {
                            ps2m.deleteFile(file);
                        }


                        FileOutputStream fileOutputStream = openFileOutput(SIGNALS_MATRIX, Context.MODE_WORLD_READABLE);
                        ps2m.word2Matrix(signals, fileOutputStream, word_Num);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        Log.i(TAG, "Word to Matrix suceess!!!!!!!!");
                        System.gc();

                        InputStream signalsIn = openFileInput(SIGNALS_MATRIX);
                        InputStream modelInput = getResources().openRawResource(modelist.get(i));
                        FileOutputStream probabilityOutput = openFileOutput(PROFILE_FILE + extraStr.get(i), Context.MODE_WORLD_READABLE);
                        Log.i(TAG, "Begin to predict!!!!!!!!");
                        value[i] = PersonaeClassifier.personaePredict(signalsIn, modelInput, probabilityOutput);

                        signalsIn.close();
                        modelInput.close();
                        probabilityOutput.flush();
                        probabilityOutput.close();

                        System.gc();
                    }

                    psm.setPersonaeProfile(personae, value);
                    Toast.makeText(null, "Profile updated success!", Toast.LENGTH_LONG).show();

                } catch (FileNotFoundException e) {
                    Log.i(TAG, "Failed to create signals_matrix file");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i(TAG, "Failed to write signals_matrix file");
                    e.printStackTrace();
                }
            }
        };


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        //predict every three hours
        timer.schedule(timerTask, 0, 10800000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestory");
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
