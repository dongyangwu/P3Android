package com.classfier.personalization.util;

import android.util.Log;

import com.chenlb.mmseg4j.example.Complex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by wdy on 15-6-11.
 */
public class PersonaeSignals2Matrix {

    public HashMap<String, Integer> readWordTable(InputStream inputStream) {
        HashMap<String, Integer> word_Num = new HashMap<>();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader wordReader = new BufferedReader(inputStreamReader);

        String wordTable = null;
        try {
            while ((wordTable = wordReader.readLine()) != null) {
                String[] t = wordTable.split(" ");
                for (String t1 : t) {
                    String t2 = t1.substring(0, t1.indexOf(":"));
                    int n = Integer.parseInt(t1.substring(t1.indexOf(":") + 1));
                    word_Num.put(t2, n);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return word_Num;
    }



    public void word2Matrix(List<String> signals, OutputStream outputStream, HashMap<String, Integer> word_Num) {
        HashMap<Integer, Double> wordmap = new HashMap<>();
        String word;
        try {
            for (int k = 0; k < signals.size(); k++) {
                Log.i("PersonaeSignals2Matrix", signals.get(k));
                double time = 0;
                wordmap.clear();

                Complex complex = new Complex();
                String splitedString = complex.segWords(signals.get(k), ",");
                String[] signals_seg = splitedString.split(",");

                for (String item : signals_seg) {
                    Log.i("PersonaeSignals2Matrix", item);
                    String st = item.replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]", "");
                    if (!st.equals("")) {
                        int num;
                        if (word_Num.get(st) != null) {
                            num = word_Num.get(st);
                            if (!wordmap.containsKey(num)) {
                                wordmap.put(num, 1.0);
                                time++;
                            } else {
                                wordmap.put(num, wordmap.get(num) + 1);
                                time++;
                            }
                        }
                    }
                }


                for (Entry<Integer, Double> enty : wordmap.entrySet()) {
                    wordmap.put(enty.getKey(), enty.getValue() / time);
                }
                List<Entry<Integer, Double>> lis = new ArrayList<>(wordmap.entrySet());
                Collections.sort(lis, new Comparator<Entry<Integer, Double>>() {
                    public int compare(Map.Entry<Integer, Double> a0, Map.Entry<Integer, Double> a1) {
                        return a1.getKey().compareTo(a0.getKey());
                    }
                });

                String matrixtmp = "1 ";
                for (int i = lis.size() - 1, log = 1; i >= 0; i--) {
                    matrixtmp = matrixtmp + lis.get(i).getKey() + ":" + lis.get(i).getValue() + " ";
                }
                outputStream.write(matrixtmp.getBytes());
                outputStream.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteFile(File file){
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }
}
