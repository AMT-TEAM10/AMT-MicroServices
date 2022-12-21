package ch.heig.amtteam10;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.InputStream;

public class App {
    private static final String LABEL_DETECTOR_URL = "http://localhost:8080/v1";
    private static final String DATA_OBJECT_URL = "http://localhost:8081/v1";

    public static void main(String[] args) throws JsonProcessingException, UnirestException {
        App app = new App();
        System.out.println("Scenario 1");
        app.scenario1();
        System.out.println("Scenario 2");
        app.scenario2();
        System.out.println("Scenario 3");
        app.scenario3();
    }

    private JsonNode analyseImage(String imageUrl, int maxLabels, float minConfidence) throws UnirestException, JsonProcessingException {
        String url = LABEL_DETECTOR_URL + "/labels";
        String body = new ObjectMapper().writeValueAsString(new ProcessDTO(imageUrl, maxLabels, minConfidence));
        var response = Unirest.post(url)
                .header("Content-Type", "application/json")
                .body(body)
                .asJson();
        return response.getBody();
    }

    private JsonNode createBucket() throws UnirestException {
        String url = DATA_OBJECT_URL + "/root-objects";
        var response = Unirest.post(url)
                .asJson();
        return response.getBody();
    }

    private JsonNode uploadObject(String objectName, InputStream stream) throws UnirestException {
        String url = DATA_OBJECT_URL + "/objects/" + objectName;
        var response = Unirest.post(url)
                .field("file", stream, objectName)
                .asJson();
        return response.getBody();
    }

    private String publishObject(String objectName) throws UnirestException {
        String url = DATA_OBJECT_URL + "/objects/" + objectName + "/link";
        var response = Unirest.get(url).asString();
        return response.getBody();
    }

    public void scenario1() throws UnirestException, JsonProcessingException {
        System.out.println("Creating bucket...");
        var bucketResult = createBucket();
        System.out.println(bucketResult);

        System.out.println("Uploading object...");
        var uplaodResult = uploadObject("mysuperimage.jpg", App.class.getClassLoader().getResourceAsStream("main.jpeg"));
        System.out.println(uplaodResult);

        System.out.println("Publishing object...");
        String url = publishObject("mysuperimage.jpg");
        System.out.println(url);

        System.out.println("Analyzing image...");
        var result = analyseImage(url, 10, 0.5f);
        System.out.println(result);
    }

    public void scenario2() throws UnirestException, JsonProcessingException {
        System.out.println("Uploading object...");
        var uplaodResult = uploadObject("mysuperimage2.jpg", App.class.getClassLoader().getResourceAsStream("main.jpeg"));
        System.out.println(uplaodResult);

        System.out.println("Publishing object...");
        String url = publishObject("mysuperimage2.jpg");
        System.out.println(url);

        System.out.println("Analyzing image...");
        var result = analyseImage(url, 10, 0.5f);
        System.out.println(result);
    }

    public void scenario3() throws UnirestException, JsonProcessingException {
        System.out.println("Publishing object...");
        String url = publishObject("mysuperimage.jpg");
        System.out.println(url);

        System.out.println("Analyzing image...");
        var result = analyseImage(url, 10, 0.5f);
        System.out.println(result);
    }
}
