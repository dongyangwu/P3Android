package com.android.server.papm;

import android.content.Context;
import android.content.papm.IPersonalizationSupportManager;
import android.content.papm.PersonalizationSupportManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Slog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by wdy on 15-6-8.
 */
public class PersonalizationSupportManagerService extends IPersonalizationSupportManager.Stub {

    public static HashMap<String, Integer> profile = new HashMap<>();

    private static final String TAG = "PersonalizationSupportManagerService";
    private Context mContext;
    private PersonalizationSupportHandler mHandler;
    private PersonalizationSupportManagerThread psmThread;
    private static final String SIGNALS_STORE = "/data/personalization/signals";
    private static final String USER_PROFILE = "/data/personalization/user_profile";
    private String topPersonae;
    private int topPersonaeValue;
    private String[] personae;
    private int[] personaeValue;
    private Vector<String> cacheV;

    public PersonalizationSupportManagerService(Context context) {
        super();
        mContext = context;
        cacheV = new Vector<>(10);
        Slog.v(TAG, "Starting " + PersonalizationSupportManagerService.class.getName());
        psmThread = new PersonalizationSupportManagerThread(TAG);
        psmThread.start();
        Slog.v(TAG, "create " + TAG + " success");
    }

    @Override
    public void setStatus(boolean status) {
        if (PersonalizationSupportManager.status != status) {
            PersonalizationSupportManager.status = status;
        }

    }

    @Override
    public boolean getStatus() {
        return PersonalizationSupportManager.status;
    }

    @Override
    public boolean logPersonalizationSignals(String signals, int category) {
        if (PersonalizationSupportManager.status) {
            synchronized (this) {
                if (cacheV.size() < 10) {
                    if (!cacheV.contains(signals)) {
                        Slog.v(TAG, signals);
                        cacheV.add(signals);
                    }
                } else {
                    if (!cacheV.contains(signals)) {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(SIGNALS_STORE, true);
                            for (int i = 0; i < cacheV.size(); i++) {
                                fileOutputStream.write(cacheV.get(i).getBytes());
                                fileOutputStream.write('\n');
                            }
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        } catch (FileNotFoundException e) {
                            Slog.v(TAG, "Failed to create file!");
                            e.printStackTrace();
                        } catch (IOException e) {
                            Slog.v(TAG, "Failed to write to file!");
                            e.printStackTrace();
                        }
                        cacheV.clear();
                        Slog.v(TAG, signals);
                        cacheV.add(signals);
                    }
                }
            }
        }
        return true;
    }


    @Override
    public List<String> getPersonalizationSignals() {
        List<String> list = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(SIGNALS_STORE);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                list.add(tmp);
            }
            inputStreamReader.close();
            bufferedReader.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Slog.v(TAG, "Failed to write to file!");
            e.printStackTrace();
        }
        return list;
    }

    public void setPersonaeProfile(String[] personae, int[] value) {
        profile.clear();

        for (int k = 0; k < value.length; k++) {
            for (int m = k + 1; m < value.length; m++) {
                if (value[k] < value[m]) {
                    int tmp = value[k];
                    value[k] = value[m];
                    value[m] = tmp;

                    String tmp2 = personae[k];
                    personae[k] = personae[m];
                    personae[m] = tmp2;
                }
            }
        }

        topPersonaeValue = value[0];
        topPersonae = personae[0];

        this.personae = personae;
        this.personaeValue = value;

        File profilefile = new File(USER_PROFILE);
        if (profilefile.exists()) {
            profilefile.delete();
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(USER_PROFILE, true);
            for (int i = 0; i < personae.length; i++) {
                profile.put(personae[i], value[i]);
                fileOutputStream.write((personae[i] + '\t' + value[i]).getBytes());
                fileOutputStream.write('\n');
            }
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            Slog.v(TAG, "Failed to create file!");
            e.printStackTrace();
        } catch (IOException e) {
            Slog.v(TAG, "Failed to write to file!");
            e.printStackTrace();
        }

    }

    @Override
    public int getTopPersonaeValue() {
        return topPersonaeValue;
    }

    @Override
    public String getTopPersonae() {
        return topPersonae;
    }

    @Override
    public List<String> getPersonaeByDescOrder() {
        List<String> tmp = new ArrayList<>(personae.length);
        for (int i = 0; i < personae.length; i++) {
            tmp.add(personae[i]);
        }
        return tmp;
    }

    @Override
    public int[] getPersonaeValueByDescOrder() {
        return personaeValue;
    }

    @Override
    public int getUserProbablyAge() {
        if (topPersonae.equals("retiree")) {
            return PersonalizationSupportManager.OLD;
        } else if (topPersonae.equals("medical_staff") || topPersonae.equals("business_executive") || topPersonae.equals("homemaker")
                || topPersonae.equals("technophile") || topPersonae.equals("travel_stuff")) {
            return PersonalizationSupportManager.MIDLIFE;
        } else
            return PersonalizationSupportManager.YOUTH;
    }


    @Override
    public boolean cleanOldPersonalizationSignals() {
        File signalfile = new File(SIGNALS_STORE);
        if (signalfile.exists()) {
            if (signalfile.isFile()) {
                signalfile.delete();
                return true;
            }
        }

        return false;
    }

    private class PersonalizationSupportManagerThread extends Thread {
        public PersonalizationSupportManagerThread(String name) {
            super(name);
        }

        public void run() {
            Looper.prepare();
            mHandler = new PersonalizationSupportHandler();
            Looper.loop();
        }
    }

    private class PersonalizationSupportHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            }
        }
    }
}
