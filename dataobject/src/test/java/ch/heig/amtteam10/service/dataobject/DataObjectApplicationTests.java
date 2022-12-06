package ch.heig.amtteam10.service.dataobject;

import ch.heig.amtteam10.core.cloud.AWSClient;
import ch.heig.amtteam10.core.cloud.AWSDataObjectHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataObjectApplicationTests {
    private static final String EXISTING_OBJECT_KEY = "existingObject";
    private static final String NON_EXISTING_OBJECT_KEY = "thisObjectDoesNotExist";

    private static final String OBJECT_CAN_BE_CREATED_KEY = "objectCanBeCreated";
    private static final String TEST_FILE_1_NAME = "main.jpeg";
    private static final String TEST_FILE_2_NAME = "test.png";
    private static final String OUTPUT_FILE_NAME = "output.jpeg";

    private static AWSClient client;

    private final static ClassLoader classLoader = AWSDataObjectHelper.class.getClassLoader();

    @BeforeAll
    public static void init() {
        client = AWSClient.getInstance();
    }

    @AfterAll
    static void cleanup() {
        final File toDelete = new File(OUTPUT_FILE_NAME);
        if (toDelete.exists()) {
            toDelete.delete();
        }
    }

    @BeforeEach
    public void setup() {
        if (!client.dataObject().doesObjectExists(EXISTING_OBJECT_KEY)) {
            client.dataObject().create(EXISTING_OBJECT_KEY, "existingObject");
        }
        if (client.dataObject().doesObjectExists(OBJECT_CAN_BE_CREATED_KEY)) {
            client.dataObject().delete(OBJECT_CAN_BE_CREATED_KEY);
        }
    }

    @Test
    public void shouldVerifyIfRootObjectExistsIfRootObjectDoesExists() {
        // Given an existing bucket
        final String bName = "amt.team10.diduno.education";

        // When I check if it exists
        boolean exists = client.dataObject().doesRootObjectExists(bName);

        // Then I should get that it exists
        assertTrue(exists);
    }

    @Test
    public void shouldVerifyIfRootObjectExistsIfRootObjectDoesntExists() {
        // Given a non-existing bucket
        final String bName = "amt.team10.diduno.education____";

        // When I check if it exists
        boolean exists = client.dataObject().doesRootObjectExists(bName);

        // Then I should get that it does not exist
        assertFalse(exists);
    }

    @Test
    public void shouldVerifyIfObjectExist() {
        // Given an existing object

        // When I check if it exists
        boolean exists = client.dataObject().doesObjectExists(EXISTING_OBJECT_KEY);

        // Then I should get that it exists
        assertTrue(exists);
    }

    @Test
    public void shouldVerifyIfObjectDoesNotExist() {
        // Given an existing object

        // When I check if it exists
        boolean exists = client.dataObject().doesObjectExists(NON_EXISTING_OBJECT_KEY);

        // Then I should get that it does not exist
        assertFalse(exists);
    }

    @Test
    public void shouldCreateObjectIfRootObjectExistAndObjectDoesntExist() throws IOException {
        // Given a local file
        File originFile = new File(classLoader.getResource(TEST_FILE_1_NAME).getFile());
        client.dataObject().create(OBJECT_CAN_BE_CREATED_KEY, originFile);

        // When I create an object on the object storage
        File outputFile = new File(OUTPUT_FILE_NAME);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(client.dataObject().get(OBJECT_CAN_BE_CREATED_KEY));
        outputStream.close();

        // Then it should have the same content if I compare the files
        assertEquals(Files.mismatch(originFile.toPath(), outputFile.toPath()), -1L);
    }

    @Test
    public void shouldCreateObjectIfRootObjectExistAndObjectExist() throws IOException {
        // Given a local file and an existing file on the storage

        // When I create an object on the object storage with the same name of the already existing object

        // Then it should replace the file and have the same content if I compare the files (local + storage)
    }

    @Test
    public void shouldGetAnObjectIfItExists() {
        // Given an existing object on the object storage

        // When I ask to get the object
        byte[] object = client.dataObject().get(EXISTING_OBJECT_KEY);

        // Then I should get the object
        assertNotNull(object);
    }

    @Test
    public void shouldThrowIfGettingAnObjectIfItDoesntExists() {
        // Given a non-existing object on the object storage

        // When I ask to get the object
        byte[] object = client.dataObject().get(NON_EXISTING_OBJECT_KEY);

        // Then I should get an exception
    }

    @Test
    public void shouldUpdateObject() throws IOException {
        // Given two files
        client.dataObject().create(OBJECT_CAN_BE_CREATED_KEY, new File(classLoader.getResource(TEST_FILE_1_NAME).getFile()));
        client.dataObject().update(OBJECT_CAN_BE_CREATED_KEY, new File(classLoader.getResource(TEST_FILE_2_NAME).getFile()));

        // When I create a fist first file, then want to replace it by another file
        File originFile = new File(classLoader.getResource(TEST_FILE_1_NAME).getFile());
        client.dataObject().create(OBJECT_CAN_BE_CREATED_KEY, originFile);
        File outputFile = new File(OUTPUT_FILE_NAME);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(client.dataObject().get(OBJECT_CAN_BE_CREATED_KEY));
        outputStream.close();

        // Then the file should be the same as the second file's content
        assertEquals(Files.mismatch(originFile.toPath(), outputFile.toPath()), -1L);
    }

    @Test
    public void shouldDeleteObject() {
        // Given having an existing object
        // When I want to delete it
        client.dataObject().delete(EXISTING_OBJECT_KEY);

        // Then it should be deleted
        assertThrows(RuntimeException.class, () -> client.dataObject().get(OBJECT_CAN_BE_CREATED_KEY));
    }

    @Test
    public void shouldThrowWhenDeleteInexistantObject() {
        // Given having a non-existing object
        // When I try to delete it
        // Then it should throw an exception
        assertThrows(RuntimeException.class, () -> client.dataObject().delete("thisObjectDoesNotExist.jpg"));
    }

    @Test
    public void shouldGetAnUrlWithPublish() throws IOException {
        // Given an existing object
        // When I want to publish it
        // Then I should get a private link to the object
        URL url = new URL(client.dataObject().publish(EXISTING_OBJECT_KEY));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        assertEquals(con.getResponseCode(), 200);
    }
}
