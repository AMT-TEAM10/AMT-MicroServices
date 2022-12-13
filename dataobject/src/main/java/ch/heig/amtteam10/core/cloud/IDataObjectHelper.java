package ch.heig.amtteam10.core.cloud;

import ch.heig.amtteam10.core.exceptions.BucketAlreadyCreatedException;
import ch.heig.amtteam10.core.exceptions.NoObjectFoundException;

import java.io.File;
import java.util.List;

/**
 * Defines a object storage manager
 *
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public interface IDataObjectHelper {

    /**
     * Create root object with unique name
     *
     * @param rootObjectName root object unique name
     */
    void createRootObject(String rootObjectName) throws BucketAlreadyCreatedException;

    /**
     * Get an object stored on an object storage manager
     *
     * @param objectName name of object
     * @return object as byte array
     */
    byte[] get(String objectName) throws NoObjectFoundException;

    /**
     * Create object (file) on an object storage manager
     *
     * @param objectName name of object
     * @param file       file to upload
     */
    void create(String objectName, File file);

    /**
     * Create object (string) on an object storage manager
     *
     * @param objectName name of object
     * @param rawContent content to upload
     */
    void create(String objectName, String rawContent);

    /**
     * Update object on an object storage manager
     *
     * @param objectName name of object
     * @param newFile    file to upload
     */
    void update(String objectName, File newFile);

    /**
     * Delete object on an object storage manager
     *
     * @param objectName name of object
     */
    void delete(String objectName) throws NoObjectFoundException;

    /**
     * Delete all objects starting with prefix.
     * Because there are no folders in object storage, we use prefix to simulate folders.
     * @param folderName name of folder
     */
    void deleteFolder(String folderName) throws NoObjectFoundException;

    /**
     * Get a private URL to an object
     *
     * @param objectName name of object
     * @return URL to access object
     */
    String publish(String objectName) throws NoObjectFoundException;

    /**
     * Check if a root object
     *
     * @param rootObjectName root object unique name
     */
    boolean doesRootObjectExists(String rootObjectName);

    /**
     * Check if object exists on the object storage
     *
     * @param objectName object to check
     * @return true if object exist, else false (will log exception)
     */
    boolean doesObjectExists(String objectName);

    /**
     * Get all objects starting with prefix.
     * Because there are no folders in object storage, we use prefix to simulate folders.
     * @param prefix prefix of objects
     * @return list of objects name
     */
    List<String> listObjects(String prefix);
}