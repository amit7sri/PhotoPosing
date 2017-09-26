package photoposing.com.amko0l.backend;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by amko0l on 4/23/2017.
 */

public class CallVisionServer {
    private static final String TARGET_URL =
            "https://vision.googleapis.com/v1/images:annotate?";
    private static final String API_KEY =
            "key=AIzaSyCBj7r40J8AyQHak9QmIt3oGKZsS8gzN4g";


    public String detectFaces(String filePath) throws IOException {
        URL serverUrl = new URL(TARGET_URL + API_KEY);
        URLConnection urlConnection = serverUrl.openConnection();
        HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/json");

        httpConnection.setDoOutput(true);

        BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
                OutputStreamWriter(httpConnection.getOutputStream()));
        httpRequestBodyWriter.write
                ("{\"requests\":  [{ \"features\":  [ {\"type\": \"FACE_DETECTION\""
                        + "}], \"image\": {\"source\": { \"imageUri\":"
                        + " \"" + filePath + "\"}}}]}");
        httpRequestBodyWriter.close();
        String response = httpConnection.getResponseMessage();
        if (httpConnection.getInputStream() == null) {
            System.out.println("No stream");
            return response;
        }

        Scanner httpResponseScanner = new Scanner(httpConnection.getInputStream());
        String resp = "";
        while (httpResponseScanner.hasNext()) {
            String line = httpResponseScanner.nextLine();
            if (line.contains("detectionConfidence") || line.contains("joyLikelihood") || line.contains("sorrowLikelihood")
                    || line.contains("angerLikelihood") || line.contains("surpriseLikelihood")) {
                resp += line;
                //System.out.println("lines are  " + line);  //  alternatively, print the line of response
            }
        }
        httpResponseScanner.close();

        return resp;
    }


    public String detectLandMarks(String filePath) throws IOException {
        URL serverUrl = new URL(TARGET_URL + API_KEY);
        URLConnection urlConnection = serverUrl.openConnection();
        HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/json");

        httpConnection.setDoOutput(true);

        BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
                OutputStreamWriter(httpConnection.getOutputStream()));
        httpRequestBodyWriter.write
                ("{\"requests\":  [{ \"features\":  [ {\"type\": \"LANDMARK_DETECTION\""
                        + "}], \"image\": {\"source\": { \"imageUri\":"
                        + " \"" + filePath + "\"}}}]}");
        httpRequestBodyWriter.close();
        String response = httpConnection.getResponseMessage();
        if (httpConnection.getInputStream() == null) {
            System.out.println("No stream");
            return response;
        }

        Scanner httpResponseScanner = new Scanner(httpConnection.getInputStream());
        String resp = "";
        while (httpResponseScanner.hasNext()) {
            String line = httpResponseScanner.nextLine();
                resp += line;
        }
        httpResponseScanner.close();

        return resp;
    }

    public String detectSimilar(String filePath) throws IOException {
        URL serverUrl = new URL(TARGET_URL + API_KEY);
        URLConnection urlConnection = serverUrl.openConnection();
        HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/json");

        httpConnection.setDoOutput(true);

        BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
                OutputStreamWriter(httpConnection.getOutputStream()));
        httpRequestBodyWriter.write
                ("{\"requests\":  [{ \"features\":  [ {\"type\": \"WEB_DETECTION\""
                        + "}], \"image\": {\"source\": { \"imageUri\":"
                        + " \"" + filePath + "\"}}}]}");
        httpRequestBodyWriter.close();
        String response = httpConnection.getResponseMessage();
        if (httpConnection.getInputStream() == null) {
            System.out.println("No stream");
            return response;
        }

        Scanner httpResponseScanner = new Scanner(httpConnection.getInputStream());
        String resp = "";
        while (httpResponseScanner.hasNext()) {
            String line = httpResponseScanner.nextLine();
            resp += line;
            System.out.println("lines are  " + line);  //  alternatively, print the line of response
        }
        httpResponseScanner.close();

        if (resp != null && resp.length() > 0) {
            JSONObject jsonObj = new JSONObject(resp);
            JSONArray resparray = jsonObj.getJSONArray("responses");
            JSONObject webdetection = resparray.getJSONObject(0);
            JSONObject webdeJsonObject = webdetection.getJSONObject("webDetection");
            JSONArray similariImages = webdeJsonObject.getJSONArray("partialMatchingImages");
            JSONArray visuallySimilarImages = webdeJsonObject.getJSONArray("visuallySimilarImages");
            //System.out.println("similariImages  " +" size is " +similariImages.length());
            String respresult = "";
            for (int i = 0; i < similariImages.length(); i++) {
                //System.out.println(similariImages.getJSONObject(i).get("url"));
                respresult += "amko0lsimilarimageURL:" + similariImages.getJSONObject(i).get("url") + ", ";
            }

            for (int i = 0; i < visuallySimilarImages.length(); i++) {
                //System.out.println(similariImages.getJSONObject(i).get("url"));
                respresult += "amko0lsimilarimageURL:" + visuallySimilarImages.getJSONObject(i).get("url") + ", ";
            }
            return respresult;
        }

        return "";
    }
}
