package com.classfier.personalization.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.StringTokenizer;

import libsvm.*;

/**
 * Created by wdy on 15-6-11.
 */
public class PersonaeClassifier {

    private static String TAG = "PersonaeClassifier";

    private static svm_print_interface svm_print_null = new svm_print_interface() {
        public void print(String s) {
        }
    };

    private static svm_print_interface svm_print_stdout = new svm_print_interface() {
        public void print(String s) {
            System.out.print(s);
        }
    };

    private static svm_print_interface svm_print_string = svm_print_stdout;

    static void info(String s) {
        svm_print_string.print(s);
    }

    private static double atof(String s) {
        return Double.valueOf(s).doubleValue();
    }

    private static int atoi(String s) {
        return Integer.parseInt(s);
    }

    private static int predict(BufferedReader input, DataOutputStream output, svm_model model, int predict_probability) throws IOException {
        int correct = 0;
        int total = 0;
        double error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

        int svm_type = svm.svm_get_svm_type(model);
        int nr_class = svm.svm_get_nr_class(model);
        double[] prob_estimates = null;

        while (true) {
            String line = input.readLine();
            if (line == null) break;

            StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

            double target = atof(st.nextToken());
            int m = st.countTokens() / 2;
            svm_node[] x = new svm_node[m];
            for (int j = 0; j < m; j++) {
                x[j] = new svm_node();
                x[j].index = atoi(st.nextToken());
                x[j].value = atof(st.nextToken());
            }

            double v;

            v = svm.svm_predict(model, x);
            output.writeBytes(v + "\n");

            if (v == target)
                ++correct;
            error += (v - target) * (v - target);
            sumv += v;
            sumy += target;
            sumvv += v * v;
            sumyy += target * target;
            sumvy += v * target;
            ++total;
        }

        Log.i(TAG, "Accuracy = " + (double) correct / total * 100 +
                "% (" + correct + "/" + total + ") (classification)\n");
        return correct / total * 100;
    }

    public static int personaePredict(InputStream signalMatrixInput, InputStream modelInput, OutputStream probabilityOutput) {
        int predict_probability = 0;

        InputStreamReader wordisr = new InputStreamReader(signalMatrixInput);
        BufferedReader wordbr = new BufferedReader(wordisr);

        InputStreamReader modelisr = new InputStreamReader(modelInput);
        BufferedReader modelbr = new BufferedReader(modelisr);

        DataOutputStream probabilityDOS = new DataOutputStream(probabilityOutput);
        try {
            svm_model model = svm.svm_load_model(modelbr);
            if (model == null) {
                return -1;
            }

            return predict(wordbr, probabilityDOS, model, predict_probability);

        } catch (IOException e) {
            Log.i(TAG, "Failed to read model file");
            e.printStackTrace();
        }
        return -1;
    }

    private static void exit_with_help() {
        return;
    }


}
