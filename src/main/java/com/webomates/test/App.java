package com.webomates.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.JSONObject;

/**
 * Hello world!
 *
 */
public class App 
{
	private static String cycleId = null;
	
    public static void main( String[] args )
    {
    	String requestUrl = "https://cq.webomates.com/ci-cd/v1/cycle/";
        String username = "suhani";
        String password = "suhanij@1n";
        final String POST_PARAMS = "{\r\n" +
                "\"productId\": 29,\r\n" +
                "\"scope\": {\r\n" +
                "\"suiteName\": \"Mini\",\r\n" +
                "\"suiteType\": \"MINI\"\r\n" +
                "},\r\n" +
                "\"environment\": \"Prod\",\r\n" +
                "\"executionFocus\": \"ACCURACY\",\r\n" +
                "\"browserMode\": \"HEADLESS_EXECUTION\"\r\n" +
                "}";
        try {
			postCall(requestUrl, POST_PARAMS, username, password);
			getCall(requestUrl + "2025662"+ "/results", username, password);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void postCall(String requestUrl,String POST_PARAMS,String username,String password) throws IOException {
        System.out.println("Request Body >> " +  POST_PARAMS);
        URL obj = new URL(requestUrl);
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Accept", "application/json");
        postConnection.setRequestProperty("Content-Type", "application/json");
        String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        postConnection.setRequestProperty("Authorization", "Basic "+encoded);
        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        int responseCode = postConnection.getResponseCode();
        System.out.println("POST Response Code >> " + responseCode);
        System.out.println("POST Response Message >> " + postConnection.getResponseMessage());
        if (responseCode == HttpURLConnection.HTTP_CREATED) { // If success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    postConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();
            System.out.println("POST Response Body >> " + response.toString());
            JSONObject json = new JSONObject(response.toString());
            cycleId = String.valueOf(json.getLong("cycleId"));
            System.out.println("Cycle is created and cycle id is: " + cycleId);
        } else {                                        // If Failed
            System.out.println("POST API Request Not Working");
            System.out.println("POST Response Code >> " + responseCode);
            System.out.println("POST Response Message >> " + postConnection.getResponseMessage());
        }
    }
    
    public static void getCall(String requestUrl, String username,String password) throws IOException {
        URL obj = new URL(requestUrl);
        System.out.println(requestUrl);
        HttpURLConnection getConnection = (HttpURLConnection) obj.openConnection();
        getConnection.setRequestMethod("GET");
        getConnection.setRequestProperty("Accept", "application/json");
        getConnection.setRequestProperty("Content-Type", "application/json");
        String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        getConnection.setRequestProperty("Authorization", "Basic "+encoded);
        getConnection.setDoOutput(true);
        int responseCode = getConnection.getResponseCode();
        System.out.println("POST Response Code >> " + responseCode);
        System.out.println("POST Response Message >> " + getConnection.getResponseMessage());
        if (responseCode == HttpURLConnection.HTTP_OK) { // If success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    getConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();
            System.out.println("POST Response Body >> " + response.toString());
        } else {                                        // If Failed
            System.out.println("POST API Request Not Working");
            System.out.println("POST Response Code >> " + responseCode);
            System.out.println("POST Response Message >> " + getConnection.getResponseMessage());
        }
    }
}
