package ch.heig.amtteam10;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.InputStream;

/**
 * Test class for testing the API
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 * Use flag -l to edit the label detector url
 * Use flag -d to edit the data object url
 */
public class App {
    private static String LABEL_DETECTOR_URL = "http://localhost:8080";
    private static String DATA_OBJECT_URL = "http://localhost:8081";

    public static void main(String[] args) throws JsonProcessingException, UnirestException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("l", "label-detector-url", true, "LabelDetector URL");
        options.addOption("d", "data-object-url", true, "DataObject URL");
        try {
            var cmd = parser.parse(options, args);
            if (cmd.hasOption("l")) {
                LABEL_DETECTOR_URL = cmd.getOptionValue("l");
            }
            if (cmd.hasOption("d")) {
                DATA_OBJECT_URL = cmd.getOptionValue("d");
            }
        } catch (Exception e) {
            System.out.println("Error while parsing arguments");
            System.exit(1);
        }

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

    private JsonNode uploadJson(String objectName, String json) throws UnirestException {
        String url = DATA_OBJECT_URL + "/objects/" + objectName;
        var response = Unirest.post(url)
                .header("Content-Type", "application/json")
                .body(json)
                .asJson();
        return response.getBody();
    }

    private JsonNode publishObject(String objectName) throws UnirestException {
        String url = DATA_OBJECT_URL + "/objects/" + objectName + "/link";
        var response = Unirest.get(url).asJson();
        return response.getBody();
    }

    public void scenario1() throws UnirestException, JsonProcessingException {
        var objectName = "scenario1";
        System.out.println("Creating bucket...");
        var bucketResult = createBucket();
        System.out.println(bucketResult);

        System.out.println("Uploading object...");
        var uploadResult = uploadObject(objectName+".jpg", App.class.getClassLoader().getResourceAsStream("main.jpeg"));
        System.out.println(uploadResult);

        System.out.println("Publishing object...");
        var result = publishObject(objectName+".jpg").getObject().get("url");;
        System.out.println(result);

        System.out.println("Analyzing image...");
        var analyse = analyseImage(result.toString(), 10, 0.5f);
        System.out.println(analyse.getObject().get("results").toString());

        System.out.println("Uploading result...");
        var uploadResult2 = uploadJson(objectName+".json", analyse.getObject().get("results").toString());
        System.out.println(uploadResult2);

        System.out.println("Publishing result...");
        var result2 = publishObject(objectName+".json").getObject().get("url");;
        System.out.println(result2);
    }

    public void scenario2() throws UnirestException, JsonProcessingException {
        var objectName = "scenario2";

        System.out.println("Uploading object...");
        var uploadResult = uploadObject(objectName+".jpg", App.class.getClassLoader().getResourceAsStream("main.jpeg"));
        System.out.println(uploadResult);

        System.out.println("Publishing object...");
        var result = publishObject(objectName+".jpg").getObject().get("url");;
        System.out.println(result);

        System.out.println("Analyzing image...");
        var analyse = analyseImage(result.toString(), 10, 0.5f);
        System.out.println(analyse.getObject().get("results").toString());

        System.out.println("Uploading result...");
        var uploadResult2 = uploadJson(objectName+".json", analyse.getObject().get("results").toString());
        System.out.println(uploadResult2);

        System.out.println("Publishing result...");
        var result2 = publishObject(objectName+".json").getObject().get("url");;
        System.out.println(result2);
    }

    public void scenario3() throws UnirestException, JsonProcessingException {
        var objectName = "scenario3";

        System.out.println("Publishing object...");
        var result = publishObject(objectName+".jpg").getObject().get("url");;
        System.out.println(result);

        System.out.println("Analyzing image...");
        var analyse = analyseImage(result.toString(), 10, 0.5f);
        System.out.println(analyse.getObject().get("results").toString());

        System.out.println("Uploading result...");
        var uploadResult2 = uploadJson(objectName+".json", analyse.getObject().get("results").toString());
        System.out.println(uploadResult2);

        System.out.println("Publishing result...");
        var result2 = publishObject(objectName+".json").getObject().get("url");;
        System.out.println(result2);
    }
}
