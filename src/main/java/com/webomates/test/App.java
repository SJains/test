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
	private static String cycleId;
	
    public static void main( String[] args ) throws InterruptedException
    {
    	String requestUrl = "https://cq.webomates.com/ci-cd/v1/cycle/";
    	String username = System.getProperty("username");
    	String password = System.getProperty("password");
        String productId = "29";
    	String suiteName = "Mini";
    	String suiteType = "MINI";
    	String environment = "Prod";
    	String executionFocus = "ACCURACY";
        final String POST_PARAMS = "{\r\n" + "\"productId\":" + productId + ",\r\n" + "\"scope\": {\r\n" +
                "\"suiteName\": \"" + suiteName + "\",\r\n" + "\"suiteType\": \"" + suiteType + "\"\r\n" +
                "},\r\n" + "\"environment\": \"" + environment + "\",\r\n" + "\"executionFocus\": \"" + executionFocus + "\",\r\n" +
                "\"browserMode\": \"HEADLESS_EXECUTION\"\r\n}";
        try {
			String cycleID = postCall(requestUrl, POST_PARAMS, username, password);
			System.out.println(requestUrl + cycleID + "/status");
			while(getCall(requestUrl + cycleID + "/status", username, password).getString("cycleStatus").equals("IN_PROGRESS")) {
				Thread.sleep(60000);
				System.out.println("Waiting for 60 seconds");
				JSONObject json = getCall(requestUrl + cycleID + "/status", username, password);
				JSONObject completion = json.getJSONObject("completionPercentage");
				System.out.println(json.getLong("cycleId") + " Cycle is In-Progress");
				System.out.println("Cycle is " + completion.getLong("execute") + "% completed");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
    }

    /**
     * To hit the POST call
     * @param requestUrl
     * @param POST_PARAMS
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public static String postCall(String requestUrl,String POST_PARAMS,String username,String password) throws IOException {
        System.out.println("Request for CI/CD is: " +  POST_PARAMS);
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
        System.out.println("Status for cycle request is: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_CREATED) { // If success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    postConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();
            JSONObject json = new JSONObject(response.toString());
            System.out.println("Cycle is successfully created with cycle id: " + json.getLong("cycleId"));
            cycleId = String.valueOf(json.getLong("cycleId"));
        } else {
            System.out.println("POST API Request Not Working");
            System.out.println("POST Response Code >> " + responseCode);
            System.out.println("POST Response Message >> " + postConnection.getResponseMessage());
        }
        return cycleId;
    }
    
    /**
     * To hit GET API call
     * @param requestUrl
     * @param username
     * @param password
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static JSONObject getCall(String requestUrl, String username,String password) throws IOException, InterruptedException {
        URL obj = new URL(requestUrl);
        JSONObject json = null;
        HttpURLConnection getConnection = (HttpURLConnection) obj.openConnection();
        getConnection.setRequestMethod("GET");
        getConnection.setRequestProperty("Accept", "application/json");
        getConnection.setRequestProperty("Content-Type", "application/json");
        String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        getConnection.setRequestProperty("Authorization", "Basic "+encoded);
        getConnection.setDoOutput(true);
        int responseCode = getConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    getConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();
            json = new JSONObject(response.toString());
        } else {                                        // If Failed
            System.out.println("POST API Request Not Working");
            System.out.println("POST Response Code >> " + responseCode);
            System.out.println("POST Response Message >> " + getConnection.getResponseMessage());
        }
        return json;
    }
}
