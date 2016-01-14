package com.classfier.Object;

import com.classifier.util.String2double;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by wdy on 15-4-22.
 */
public class BayesTheta {

    public double[] getTheta(InputStream inputStream) {
        int score = 0;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        double[] Theta = null;
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                Theta = String2double.doubleString2double(line.split("\t"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Theta;
    }


}
