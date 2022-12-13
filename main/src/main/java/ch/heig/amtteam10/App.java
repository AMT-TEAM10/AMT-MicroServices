package ch.heig.amtteam10;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
/**
 * Hello world!
 *
 */
public class App
{

    private JsonNode processData(String imageUrl, int maxLabels, float minConfidence) throws UnirestException, JsonProcessingException {
        String url = "http://localhost:8080/process";
        String body = new ObjectMapper().writeValueAsString(new ProcessDTO(imageUrl, maxLabels, minConfidence));
        var response = Unirest.post(url)
                .header("Content-Type", "application/json")
                .body(body)
                .asJson();
        return response.getBody();
    }



    public static void main( String[] args ) throws JsonProcessingException, UnirestException {
        var app = new App();
        var response = app.processData(
                "https://www.rts.ch/2018/07/15/11/28/9715654.image",
                10,
                0.5f
        );
        System.out.println(response);
    }
}
