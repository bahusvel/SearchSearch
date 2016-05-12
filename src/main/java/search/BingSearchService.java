package search;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Created by denislavrov on 12/05/16.
 */
public class BingSearchService {
    public class PrimaryResult{
        public String id;
        public String title;
        public String description;
        public String displayURL;
        public String url;

        public PrimaryResult(String id, String title, String description, String displayURL, String url) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.displayURL = displayURL;
            this.url = url;
        }

        @Override
        public String toString() {
            return "PrimaryResult{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", displayURL='" + displayURL + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    private static final String API_ROOT = "https://api.datamarket.azure.com/Bing/Search/Web?$format=json&Query=";

    private static final String API_KEY = "HIDDEN";

    public static String executeRequest(String targetURL, String authKey) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            String encoded = Base64.getEncoder().encodeToString((authKey + ":" + authKey).getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encoded);

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

    public ArrayList<PrimaryResult> searchBing(String query){
        try {
            String bingQuery = URLEncoder.encode("'" + query + "'", "UTF-8");
            String output = executeRequest(API_ROOT+bingQuery, API_KEY);
            if (output.equals("")){
                System.out.println("Search Failed");
                return null;
            }
            ArrayList<PrimaryResult> primaryResults = new ArrayList<>();
            JSONObject results = new JSONObject(output);
            JSONArray resultsArray = results.getJSONObject("d").getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject result = resultsArray.getJSONObject(i);
                PrimaryResult primaryResult = new PrimaryResult(result.getString("ID"), result.getString("Title"), result.getString("Description"), result.getString("DisplayUrl"), result.getString("Url"));
                primaryResults.add(primaryResult);
            }
            System.out.println(output);
            return primaryResults;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
