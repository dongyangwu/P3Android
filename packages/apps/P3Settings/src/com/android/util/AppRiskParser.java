package com.android.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by wdy on 15-5-12.
 */
public class AppRiskParser {
    public static ArrayList<AppRiskInfo> getAppRiskInfo(InputStream inputStream) throws XmlPullParserException, IOException {
        ArrayList<AppRiskInfo> arrayList = null;
        AppRiskInfo appRiskInfo = null;

        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(inputStream, "UTF-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    arrayList = new ArrayList<AppRiskInfo>();
                    System.out.println("初始化arrayList");
                    break;
                case XmlPullParser.START_TAG:
                    if ("app".equals(pullParser.getName())) {
                        String name = pullParser.getAttributeValue(0);
                        appRiskInfo = new AppRiskInfo();
                        System.out.println("初始化appRiskInfo");
                        appRiskInfo.setPkgName(name);
                    } else if ("risk_level".equals(pullParser.getName())) {
                        Double level = Double.valueOf(pullParser.nextText()).doubleValue();
                        System.out.println("插入level");
                        System.out.println(level + "");
                        appRiskInfo.setRiskLevel(level);
                        arrayList.add(appRiskInfo);
                    } else {
                        break;
                    }
                case XmlPullParser.END_TAG:
                    break;
            }

            event = pullParser.next();
        }
        return arrayList;
    }
}