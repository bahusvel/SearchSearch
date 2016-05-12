package crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * Created by denislavrov on 12/05/16.
 */
public class Crawler {

    public static String executeRequest(String targetURL) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            return responseStrBuilder.toString();
        } catch (Exception ignored) {

        }
        return "";
    }


    public String crawlURL(String url){
        return executeRequest(url);
    }

}
