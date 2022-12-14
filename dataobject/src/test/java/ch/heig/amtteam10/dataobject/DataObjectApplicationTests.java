package ch.heig.amtteam10.dataobject;

import ch.heig.amtteam10.dataobject.core.IDataObject;
import ch.heig.amtteam10.dataobject.core.exceptions.NoObjectFoundException;
import ch.heig.amtteam10.dataobject.core.AWSDataObject;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataObjectApplicationTests {
    private static final String EXISTING_OBJECT_KEY = "existingObject";
    private static final String NON_EXISTING_OBJECT_KEY = "thisObjectDoesNotExist";
    private static final String OBJECT_CAN_BE_CREATED_KEY = "objectCanBeCreated";
    private static final String TEST_FILE_1_NAME = "main.jpeg";
    private static final String TEST_FILE_2_NAME = "test.png";
    private static final String OUTPUT_FILE_NAME = "output.jpeg";
    private static final String RAW_CONTENT_TEST = "test";
    private final static ClassLoader classLoader = AWSDataObject.class.getClassLoader();
    private static IDataObject dataObjectHelper;

    @BeforeAll
    public static void init() {
        dataObjectHelper = new AWSDataObject();
    }

    @AfterAll
    static void cleanup() {
        final File toDelete = new File(OUTPUT_FILE_NAME);
        if (toDelete.exists() && !toDelete.delete()) {
            System.err.println("Could not delete file " + OUTPUT_FILE_NAME);
        }
    }

    @BeforeEach
    public void setup() throws NoObjectFoundException {
        if (!dataObjectHelper.doesObjectExists(EXISTING_OBJECT_KEY)) {
            dataObjectHelper.create(EXISTING_OBJECT_KEY, RAW_CONTENT_TEST.getBytes(), "text/plain");
        }
        if (dataObjectHelper.doesObjectExists(OBJECT_CAN_BE_CREATED_KEY)) {
            dataObjectHelper.delete(OBJECT_CAN_BE_CREATED_KEY);
        }
    }

    // object exists
    @Test
    public void DoesObjectExist_RootObjectExists_Exists() {
        // Given an existing bucket
        final String bName = "amt.team10.diduno.education";
        assertTrue(dataObjectHelper.doesRootObjectExists(bName));

        // When I check if it exists
        boolean exists = dataObjectHelper.doesRootObjectExists(bName);

        // Then I should get that it exists
        assertTrue(exists);
    }

    @Test
    public void DoesObjectExist_RootObjectDoesntExist_DoesntExist() {
        // Given a non-existing bucket
        final String bName = "amt.team10.diduno.education____";
        assertFalse(dataObjectHelper.doesRootObjectExists(bName));

        // When I check if it exists
        boolean exists = dataObjectHelper.doesRootObjectExists(bName);

        // Then I should get that it does not exist
        assertFalse(exists);
    }

    @Test
    public void DoesObjectExist_RootObjectAndObjectExist_Exists() {
        // Given an existing object
        assertTrue(dataObjectHelper.doesObjectExists(EXISTING_OBJECT_KEY));

        // When I check if it exists
        boolean exists = dataObjectHelper.doesObjectExists(EXISTING_OBJECT_KEY);

        // Then I should get that it exists
        assertTrue(exists);
    }

    @Test
    public void DoesObjectExist_RootObjectExistObjectDoesntExist_DoesntExist() {
        // Given a not existing object
        assertFalse(dataObjectHelper.doesObjectExists(NON_EXISTING_OBJECT_KEY));

        // When I check if it exists
        boolean exists = dataObjectHelper.doesObjectExists(NON_EXISTING_OBJECT_KEY);

        // Then I should get that it does not exist
        assertFalse(exists);
    }
    // object exists end

    // create object
    @Test
    public void UploadObject_RootObjectExistsNewObject_Uploaded() throws IOException, NoObjectFoundException {
        // Given a local file
        File originFile = new File(Objects.requireNonNull(classLoader.getResource(TEST_FILE_1_NAME)).getFile());
        assertNotNull(originFile);

        // When I create an object on the object storage
        dataObjectHelper.create(OBJECT_CAN_BE_CREATED_KEY, originFile);
        File outputFile = new File(OUTPUT_FILE_NAME);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(dataObjectHelper.get(OBJECT_CAN_BE_CREATED_KEY));
        outputStream.close();

        // Then it should have the same content if I compare the files
        assertEquals(Files.mismatch(originFile.toPath(), outputFile.toPath()), -1L);
    }

    @Test
    public void UploadObject_RootObjectExistsObjectExists_Uploaded() throws IOException, NoObjectFoundException {
        // Given two files
        File originFile = new File(Objects.requireNonNull(classLoader.getResource(TEST_FILE_1_NAME)).getFile());
        File replacingFile = new File(Objects.requireNonNull(classLoader.getResource(TEST_FILE_2_NAME)).getFile());
        assertNotNull(replacingFile);
        assertNotNull(replacingFile);

        // When I create a first file, then want to replace it by another file
        dataObjectHelper.create(OBJECT_CAN_BE_CREATED_KEY, originFile);
        dataObjectHelper.update(OBJECT_CAN_BE_CREATED_KEY, replacingFile);

        File outputFile = new File(OUTPUT_FILE_NAME);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(dataObjectHelper.get(OBJECT_CAN_BE_CREATED_KEY));
        outputStream.close();

        // Then the file should be the same as the second file's content
        assertEquals(Files.mismatch(replacingFile.toPath(), outputFile.toPath()), -1L);
    }
    // create object end

    // get object
    @Test
    public void DownloadObject_ObjectExists_Downloaded() throws NoObjectFoundException {
        // Given an existing object on the object storage
        assertTrue(dataObjectHelper.doesObjectExists(EXISTING_OBJECT_KEY));

        // When I ask to get the object
        byte[] object = dataObjectHelper.get(EXISTING_OBJECT_KEY);

        // Then I should get the object
        assertNotNull(object);
    }

    @Test
    public void DownloadObject_ObjectDoesntExist_ThrowException() {
        // Given a non-existing object on the object storage
        assertFalse(dataObjectHelper.doesObjectExists(NON_EXISTING_OBJECT_KEY));
        // When I ask to get the object
        // Then I should get an exception
        assertThrows(NoObjectFoundException.class, () -> dataObjectHelper.get(NON_EXISTING_OBJECT_KEY));
    }
    // get object end

    // remove object
    @Test
    public void RemoveObject_SingleObjectExists_Removed() throws NoObjectFoundException {
        // Given an existing object on the storage
        assertTrue(dataObjectHelper.doesObjectExists(EXISTING_OBJECT_KEY));

        // When I want to delete it
        dataObjectHelper.delete(EXISTING_OBJECT_KEY);

        // Then it should be deleted
        assertFalse(dataObjectHelper.doesObjectExists(EXISTING_OBJECT_KEY));
    }

    @Test
    public void RemoveObject_SingleObjectDoesntExist_ThrowException() {
        // Given having a non-existing object
        assertFalse(dataObjectHelper.doesObjectExists(NON_EXISTING_OBJECT_KEY));
        // When I try to delete it
        // Then it should throw an exception
        assertThrows(NoObjectFoundException.class, () -> dataObjectHelper.delete(NON_EXISTING_OBJECT_KEY));
    }
    // remove object end

    // publish object
    @Test
    public void PublishObject_ObjectExists_Published() throws IOException, NoObjectFoundException {
        // Given an existing object on the storage
        assertTrue(dataObjectHelper.doesObjectExists(EXISTING_OBJECT_KEY));
        // When I want to publish it
        // Then I should get a private link to the object
        URL url = new URL(dataObjectHelper.publish(EXISTING_OBJECT_KEY));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        assertEquals(con.getResponseCode(), 200);
    }

    @Test
    public void PublishObject_ObjectDoesntExist_ThrowException() {
        // Given a non exiting file on the storage
        assertFalse(dataObjectHelper.doesObjectExists(NON_EXISTING_OBJECT_KEY));
        // When I want to publish it
        // Then it should throw an exception
        assertThrows(NoObjectFoundException.class, () -> dataObjectHelper.publish(NON_EXISTING_OBJECT_KEY));
    }

    // publish object end
    @Nested
    class TestWithListObject {
        static String folderName = "testFolder";

        static int nbObjects = 4;
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
                dataObjectHelper.create(objectName, RAW_CONTENT_TEST.getBytes(), "text/plain");
            }
        }

        @AfterAll
        public static void cleanup() {
            // delete N objects
            for (String objectName : objectNames) {
                try {
                    dataObjectHelper.delete(objectName);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }

        @Test
        public void ListObject_FolderObjectExists_Listed() {
            // Given having N objects
            assertEquals(objectNames.length, nbObjects);
            // When I want to list them
            // Then I should get N objects
            var list = dataObjectHelper.listObjects(folderName);
            assertEquals(list.size(), objectNames.length);
        }

        @Test
        public void RemoveObject_FolderObjectExistWithRecursiveOption_Removed() throws NoObjectFoundException {
            // Given having N objects
            assertEquals(objectNames.length, nbObjects);
            // When I want to delete them
            // Then I should get 0 objects
            dataObjectHelper.deleteFolder(folderName);
            var list = dataObjectHelper.listObjects(folderName);
            assertEquals(list.size(), 0);
        }

        @Test
        public void RemoveObject_FolderObjectNotExist_ThrowException() {
            // Given having a non-existing folder
            assertFalse(dataObjectHelper.doesObjectExists("thisFolderDoesNotExist"));
            // When I try to delete it
            // Then it should throw an exception
            assertThrows(NoObjectFoundException.class, () -> dataObjectHelper.deleteFolder("thisFolderDoesNotExist"));
        }
    }

}
