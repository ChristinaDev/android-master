package com.daniel.alexa;

import com.daniel.alexa.baker.exceptions.ReCaptchaException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;



public class Downloader implements com.daniel.alexa.baker.Downloader {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0";
    private static String mCookies = "";

    private static Downloader instance = null;

    private Downloader() {
    }

    public static Downloader getInstance() {
        if (instance == null) {
            synchronized (Downloader.class) {
                if (instance == null) {
                    instance = new Downloader();
                }
            }
        }
        return instance;
    }

    public static synchronized void setCookies(String cookies) {
        Downloader.mCookies = cookies;
    }

    public static synchronized String getCookies() {
        return Downloader.mCookies;
    }

    /**
     * @param siteUrl  the URL of the text file to return the contents of
     * @param language the language (usually a 2-character code) to set as the preferred language
     * @return the contents of the specified text file
     */
    public String download(String siteUrl, String language) throws IOException, ReCaptchaException {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Accept-Language", language);
        return download(siteUrl, requestProperties);
    }


    /**
     * @param siteUrl          the URL of the text file to return the contents of
     * @param customProperties set request header properties
     * @return the contents of the specified text file
     * @throws IOException
     */
    public String download(String siteUrl, Map<String, String> customProperties) throws IOException, ReCaptchaException {
        URL url = new URL(siteUrl);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        for (Map.Entry<String, String> pair: customProperties.entrySet()) {
            con.setRequestProperty(pair.getKey(), pair.getValue());
        }
        return dl(con);
    }

    /**
     * Common functionality between download(String url) and download(String url, String language)
     */
    private static String dl(HttpsURLConnection con) throws IOException, ReCaptchaException {
        StringBuilder response = new StringBuilder();
        BufferedReader in = null;

        try {
            con.setConnectTimeout(30 * 1000);// 30s
            con.setReadTimeout(30 * 1000);// 30s
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            if (getCookies().length() > 0) {
                con.setRequestProperty("Cookie", getCookies());
            }

            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (UnknownHostException uhe) {
            throw new IOException("unknown host or no network", uhe);
         
        } catch (Exception e) {
        
            if (con.getResponseCode() == 429) {
                throw new ReCaptchaException("reCaptcha Challenge requested");
            }

            throw new IOException(con.getResponseCode() + " " + con.getResponseMessage(), e);
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return response.toString();
    }

    /**
     * @param siteUrl the URL of the text file to download
     * @return the contents of the specified text file
     */
    public String download(String siteUrl) throws IOException, ReCaptchaException {
        URL url = new URL(siteUrl);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
    
        return dl(con);
    }
}
