package com.classfier.Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by wdy on 15-4-22.
 */
public class AllPermissons {

    public static String[] getPermissionList(InputStream inputStream) {
        String[] permissions = new String[151];

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;
        int i = 0;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                permissions[i] = line;
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return permissions;
    }
}
