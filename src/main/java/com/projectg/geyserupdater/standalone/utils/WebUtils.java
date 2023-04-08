package com.projectg.geyserupdater.standalone.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebUtils {
    public WebUtils() {
    }

    private static final String AGENT = "GeyserUpdater";

    public static String getBody(String reqURL) {
        try {
            URL url = new URL(reqURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", AGENT);
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            return connectionToString(con);
        } catch (Exception var3) {
            return var3.getMessage();
        }
    }


    private static String connectionToString(HttpURLConnection con) throws IOException {
        con.getResponseCode();
        InputStream inputStream = con.getErrorStream();
        if (inputStream == null) {
            inputStream = con.getInputStream();
        }

        StringBuilder content = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
                content.append("\n");
            }

            con.disconnect();
        } catch (Throwable var7) {
            try {
                in.close();
            } catch (Throwable var6) {
                var7.addSuppressed(var6);
            }

            throw var7;
        }

        in.close();
        return content.toString();
    }
}
