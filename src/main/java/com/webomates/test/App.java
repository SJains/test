package com.webomates.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

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
    	String username = "suhani";
    	String password = "suhanij@1n";
        try {
			String cycleID = postCall(requestUrl, createRequestJson(), username, password);
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
        HttpURLConnection postConnection = createHttpConnection(requestUrl, username, password, "POST");
        OutputStream os = postConnection.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        int responseCode = postConnection.getResponseCode();
        System.out.println("Status for cycle request is: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_CREATED) { // If success
            BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
            String response = readAllLinesWithStream(in);
            in .close();
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
     * Return response as a string
     * @param reader
     * @return
     */
    public static String readAllLinesWithStream(BufferedReader reader) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
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
        JSONObject json = null;
        HttpURLConnection getConnection = createHttpConnection(requestUrl, username, password, "GET");
        int responseCode = getConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(getConnection.getInputStream()));
            String response = readAllLinesWithStream(in);
            in .close();
            json = new JSONObject(response.toString());
        } else {
            System.out.println("POST API Request Not Working");
            System.out.println("POST Response Code >> " + responseCode);
            System.out.println("POST Response Message >> " + getConnection.getResponseMessage());
        }
        return json;
    }
    
    /**
     * To create HTTP connection
     * @param requestUrl
     * @param username
     * @param password
     * @param requestMethod
     * @return
     * @throws IOException
     */
    public static HttpURLConnection createHttpConnection(String requestUrl, String username, String password, String requestMethod) throws IOException {
    	URL obj = new URL(requestUrl);
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod(requestMethod);
        postConnection.setRequestProperty("Accept", "application/json");
        postConnection.setRequestProperty("Content-Type", "application/json");
        String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        postConnection.setRequestProperty("Authorization", "Basic "+encoded);
        postConnection.setDoOutput(true);
        return postConnection;
    }
    
    /**
     * Create request json
     * @return request body
     */
    public static String createRequestJson() {
    	String productId = "95";
    	String suiteName = "Mini";
    	String suiteType = "MINI";
    	String environment = "Staging";
    	String executionFocus = "ACCURACY_WITH_NO_REVIEW";
    	String browserMode = "HEADLESS_EXECUTION";
    	JSONObject scopeJson = new JSONObject();
    	scopeJson.put("suiteName", suiteName);
    	scopeJson.put("suiteType", suiteType);
    	JSONObject requestJson = new JSONObject();
    	requestJson.put("productId", productId);
    	requestJson.put("environment", environment);
    	requestJson.put("executionFocus", executionFocus);
    	requestJson.put("browserMode", browserMode);
    	requestJson.put("scope", scopeJson);
        return requestJson.toString();
    }
}
