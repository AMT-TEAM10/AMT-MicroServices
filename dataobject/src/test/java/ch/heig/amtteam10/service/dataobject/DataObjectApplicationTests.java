package ch.heig.amtteam10.service.dataobject;

import ch.heig.amtteam10.core.cloud.AWSClient;
import ch.heig.amtteam10.core.cloud.AWSDataObjectHelper;
import ch.heig.amtteam10.core.exceptions.NoObjectFoundException;
import org.junit.jupiter.api.*;
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
    public void setup() throws NoObjectFoundException {
        if (!client.dataObject().objectExists(EXISTING_OBJECT_KEY)) {
            client.dataObject().create(EXISTING_OBJECT_KEY, "existingObject");
        }
        if (client.dataObject().objectExists(OBJECT_CAN_BE_CREATED_KEY)) {
            client.dataObject().delete(OBJECT_CAN_BE_CREATED_KEY);
        }
    }

    @Test
    public void shouldVerifyIfObjectExist() {
        assertTrue(client.dataObject().objectExists(EXISTING_OBJECT_KEY));
    }

    @Test
    public void shouldVerifyIfObjectDoesNotExist() {
        assertFalse(client.dataObject().objectExists("thisObjectDoesNotExist"));
    }

    @Test
    public void shouldCreateObject() throws IOException, NoObjectFoundException {
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
    public void shouldUpdateObject() throws IOException, NoObjectFoundException {
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
    public void shouldDeleteObject() throws NoObjectFoundException {
        // Given having an existing object
        // When I want to delete it
        client.dataObject().delete(EXISTING_OBJECT_KEY);

        // Then it should be deleted
        assertThrows(NoObjectFoundException.class, () -> client.dataObject().get(OBJECT_CAN_BE_CREATED_KEY));
    }

    @Test
    public void shouldThrowWhenDeleteInexistantObject() {
        // Given having a non-existing object
        // When I try to delete it
        // Then it should throw an exception
        assertThrows(NoObjectFoundException.class, () -> client.dataObject().delete("thisObjectDoesNotExist.jpg"));
    }

    @Test
    public void shouldGetAnUrlWithPublish() throws IOException, NoObjectFoundException {
        // Given an existing object
        // When I want to publish it
        // Then I should get a private link to the object
        URL url = new URL(client.dataObject().publish(EXISTING_OBJECT_KEY));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        assertEquals(con.getResponseCode(), 200);
    }

    @Nested
    class TestWithListObject{
        static String folderName = "testFolder";
        static String[] objectNames = {
                folderName + "/test1.jpg",
                folderName + "/test2.jpg",
                folderName + "/dir1/test3.jpg",
                folderName + "/dir1/test4.jpg",
        };

        @BeforeAll
        public static void init() {
            // create N objects
            for (String objectName : objectNames) {
                client.dataObject().create(objectName, "test");
            }
        }

        @Test
        public void shouldListObjects() {
            // Given having N objects
            // When I want to list them
            // Then I should get N objects
            var list = client.dataObject().listObjects(folderName);
            assertEquals(list.size(), objectNames.length);
        }

        @Test
        public void shouldDeleteFolder() throws NoObjectFoundException {
            // Given having N objects
            // When I want to delete them
            // Then I should get 0 objects
            client.dataObject().deleteFolder(folderName);
            var list = client.dataObject().listObjects(folderName);
            assertEquals(list.size(), 0);
        }

        @Test
        public void shouldThrowWhenDeleteInexistantFolder() {
            // Given having a non-existing folder
            // When I try to delete it
            // Then it should throw an exception
            assertThrows(NoObjectFoundException.class, () -> client.dataObject().deleteFolder("thisFolderDoesNotExist"));
        }

        @AfterAll
        public static void cleanup() {
            // delete N objects
            for (String objectName : objectNames) {
                try{
                    client.dataObject().delete(objectName);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }

}
