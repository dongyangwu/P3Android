package com.classifier.util;

/**
 * Created by wdy on 15-3-17.
 */
public class String2double {

    /**
     * convert String[] to double[]
     * String is a int type
     *
     * @param arr the String[] to convert
     * @return converted double[]
     */
    public static double[] String2double(String... arr) {
        double[] tmp = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            tmp[i] = (double)Integer.parseInt(arr[i]);
        }
        return tmp;
    }

    /**
     * string is a double type
     * @param arr
     * @return
     */
    public static double[] doubleString2double(String... arr) {
        double[] tmp = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            tmp[i] = Double.parseDouble(arr[i]);
        }
        return tmp;
    }

}
