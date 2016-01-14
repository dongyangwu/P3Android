package com.classifier.util;

import android.util.Xml;

import com.classfier.Object.AppInfo;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by wdy on 15-4-22.
 */
public class XMLTools {

    /**
     * save app risk into xml file.
     * @param appInfoArrayList
     * @param outputStream
     * @throws IOException
     */
    public static void save(ArrayList<AppInfo> appInfoArrayList, OutputStream outputStream) throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(outputStream, "UTF-8");
        serializer.startDocument("UTF-8", true);
        serializer.startTag(null, "apps");
        for (AppInfo appInfo: appInfoArrayList) {
            serializer.startTag(null, "app");
            serializer.attribute(null, "name", appInfo.getPackagename());
            serializer.startTag(null, "risk_level");
            serializer.text(FormatRiskScore.DF.format(appInfo.getRiskscore()));
            serializer.endTag(null, "risk_level");
            serializer.endTag(null, "app");
        }
        serializer.endTag(null, "apps");
        serializer.endDocument();
        outputStream.flush();
        outputStream.close();
    }

}
