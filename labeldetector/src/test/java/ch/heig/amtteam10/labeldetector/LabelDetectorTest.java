package ch.heig.amtteam10.labeldetector;

import ch.heig.amtteam10.labeldetector.core.AWSClient;
import ch.heig.amtteam10.labeldetector.core.ICloudClient;
import ch.heig.amtteam10.labeldetector.core.ILabelDetector;
import ch.heig.amtteam10.labeldetector.core.Label;
import ch.heig.amtteam10.labeldetector.core.exceptions.FailDownloadFileException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class LabelDetectorTest {

    private static ICloudClient client;
    private static ILabelDetector detector;

    @BeforeAll
    public static void init() {
        client = AWSClient.getInstance();
        detector = client.labelDetector();
    }

    @Test
    public void shouldGetLabelDetector() {
        assertNotNull(detector);
    }

    @Test
    public void shouldDetectLabelsFromURLString() throws FailDownloadFileException, IOException {
        // Given a valid URL string
        String exampleUrl = "https://upload.wikimedia.org/wikipedia/commons/9/9d/NYC_Montage_2014_4_-_Jleon.jpg";

        // When I ask for image analyse with some criteria
        var labels = detector.execute(exampleUrl, 5, 0.7f);

        // Then I should get labels respecting those criteria
        assertEquals(labels.length, 5);
        for (var label : labels) {
            assertTrue(label.confidence() >= 0.7f);
        }
    }

    @Test
    public void shouldDetectLabelsFromURL() throws IOException, FailDownloadFileException {
        // Given a valid URL object
        URL exampleUrl = new URL("https://upload.wikimedia.org/wikipedia/commons/9/9d/NYC_Montage_2014_4_-_Jleon.jpg");

        // When I ask for image analyse with some criteria
        var labels = detector.execute(exampleUrl, 5, 0.7f);

        // Then I should get labels respecting those criteria
        assertEquals(labels.length, 5);
        for (var label : labels) {
            assertTrue(label.confidence() >= 0.7f);
        }
    }

    @Test
    public void shouldDetectLabelsFromBase64() throws IOException {
        // Given a valid image (as bytes)
        ByteBuffer imageBytes;

        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream("main.jpeg")) {
            imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        }

        // When I ask for image analyse with some criteria
        var labels = detector.execute(imageBytes, 5, 0.7f);

        // Then I should get labels respecting those criteria
        assertEquals(labels.length, 5);
        for (Label label : labels) {
            assertTrue(label.confidence() >= 0.7f);
        }
    }
}
