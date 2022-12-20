package ch.heig.amtteam10.labeldetector;

import ch.heig.amtteam10.labeldetector.controller.LabelDetectorController;
import ch.heig.amtteam10.labeldetector.DTO.ProcessDTO;
import ch.heig.amtteam10.labeldetector.service.LabelDetectorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class LabelDetectorControllerTests {

    private static LabelDetectorController labelController;

    @BeforeAll
    static void init() {
        labelController = new LabelDetectorController(new LabelDetectorService());
    }

    @Test
    void should_get_a_valid_response_with_valid_data() {
        //GIVEN a valid data
        ProcessDTO validData = new ProcessDTO(
                "https://www.rts.ch/2018/07/15/11/28/9715654.image",
                5,
                0.5f);

        //WHEN send the data to process
        var response = labelController.processImage(validData);

        //THEN Got a success response.
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(Objects.requireNonNull(response.getBody()).length, 5);
    }

    @Test
    void should_get_an_error_when_sending_invalid_url() {
        //GIVEN an invalid url
        ProcessDTO invalidData = new ProcessDTO(
                "Invalid",
                5,
                0.5f);
        //WHEN send the data to process
        //THEN Got a error response.
        assertThrows(ResponseStatusException.class, () -> labelController.processImage(invalidData));
    }

    @Test
    void should_get_an_error_when_sending_inexistant_url() {
        //GIVEN an invalid url
        ProcessDTO invalidData = new ProcessDTO(
                "http://inexistant.nope",
                5,
                0.5f);
        //WHEN send the data to process
        //THEN Got a error response.
        assertThrows(ResponseStatusException.class, () -> labelController.processImage(invalidData));
    }
}
