package ch.heig.amtteam10.dataobject.core.cloud;

import ch.heig.amtteam10.core.Env;
import ch.heig.amtteam10.core.exceptions.BucketAlreadyCreatedException;
import ch.heig.amtteam10.core.exceptions.NoObjectFoundException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper for AWS S3 object storage
 *
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public class AWSDataObjectHelper implements IDataObjectHelper {
    private final static int PUBLIC_LINK_VALIDITY_DURATION = Integer.parseInt(Env.get("PUBLIC_LINK_VALIDITY_DURATION"));

    @Override
    public void createRootObject(String bucketName) throws BucketAlreadyCreatedException {
        AWSClient client = AWSClient.getInstance();

        try {
            CreateBucketRequest req = CreateBucketRequest.builder().bucket(bucketName).build();
            client.getS3Client().createBucket(req);
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException e) {
            throw new BucketAlreadyCreatedException(bucketName);
        }
    }

    @Override
    public boolean doesRootObjectExists(String bucketName) {
        try {
            AWSClient.getInstance().getS3Client()
                    .headBucket(HeadBucketRequest.builder()
                            .bucket(bucketName)
                            .build());
            return true;
        } catch (NoSuchBucketException e) {
            Logger.getLogger(AWSDataObjectHelper.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    @Override
    public byte[] get(String objectName) throws NoObjectFoundException {
        if (!doesRootObjectExists(Env.get("AWS_BUCKET_NAME"))) {
            throw new NoObjectFoundException("Bucket not found");
        }

        if (!doesObjectExists(objectName)) {
            throw new NoObjectFoundException("Object not found");
        }

        GetObjectRequest objectRequestGet = GetObjectRequest
                .builder()
                .bucket(Env.get("AWS_BUCKET_NAME"))
                .key(objectName)
                .build();

        ResponseBytes<GetObjectResponse> result;

        try {
            result = AWSClient.getInstance().getS3Client().getObjectAsBytes(objectRequestGet);
        } catch (NoSuchKeyException e) {
            throw new NoObjectFoundException(objectName);
        }
        return result.asByteArray();
    }

    @Override
    public void create(String objectName, File file) {
        if (!doesRootObjectExists(Env.get("AWS_BUCKET_NAME"))) {
            throw new RuntimeException("Bucket not found");
        }

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(Env.get("AWS_BUCKET_NAME"))
                .key(objectName)
                .build();
        AWSClient.getInstance().getS3Client().putObject(objectRequest, RequestBody.fromFile(file));
    }

    @Override
    public void create(String objectName, byte[] bytes) {
        if (!doesRootObjectExists(Env.get("AWS_BUCKET_NAME"))) {
            throw new RuntimeException("Bucket not found");
        }

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(Env.get("AWS_BUCKET_NAME"))
                .key(objectName)
                .build();
        AWSClient.getInstance().getS3Client().putObject(objectRequest, RequestBody.fromBytes(bytes));
    }

    @Override
    public void update(String objectName, File newFile) {
        create(objectName, newFile);
    }

    @Override
    public void delete(String objectName) throws NoObjectFoundException {
        if (!doesObjectExists(objectName)) {
            throw new NoObjectFoundException(objectName);
        }
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(Env.get("AWS_BUCKET_NAME"))
                .key(objectName)
                .build();

        AWSClient.getInstance().getS3Client().deleteObject(request);
    }

    @Override
    public void deleteFolder(String folderName) throws NoObjectFoundException {
        List<String> list = listObjects(folderName);
        if (list.isEmpty()) {
            throw new NoObjectFoundException(folderName);
        }

        for (String objectName : list) {
            delete(objectName);
        }
    }

    @Override
    public List<String> listObjects(String prefix) {
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(Env.get("AWS_BUCKET_NAME"))
                .prefix(prefix)
                .build();

        ListObjectsResponse listObjectsResponse = AWSClient.getInstance().getS3Client().listObjects(listObjectsRequest);
        return listObjectsResponse.contents().stream().map(S3Object::key).toList();
    }

    @Override
    public String publish(String objectName) throws NoObjectFoundException {
        if (!doesObjectExists(objectName)) {
            throw new NoObjectFoundException(objectName);
        }

        S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(AWSClient.getInstance().getCredentials())
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(Env.get("AWS_BUCKET_NAME"))
                .key(objectName)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PUBLIC_LINK_VALIDITY_DURATION))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    @Override
    public boolean doesObjectExists(String objectName) {
        try {
            AWSClient.getInstance().getS3Client()
                    .headObject(HeadObjectRequest.builder()
                            .bucket(Env.get("AWS_BUCKET_NAME"))
                            .key(objectName)
                            .build());
            return true;
        } catch (NoSuchKeyException e) {
            Logger.getLogger(AWSDataObjectHelper.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }
}
