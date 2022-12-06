package ch.heig.amtteam10.labeldetector.core;

import ch.heig.amtteam10.core.Env;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS Cloud Client
 *
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public class AWSClient {
    private final AWSDataObjectHelper dataObjectHelper;
    private static AWSClient instance;
    private final Region region;
    private final S3Client s3Client;

    private final RekognitionClient rekognitionClient;
    private final AwsCredentialsProvider credentialsProvider;

    private AWSClient() {
        this.region = Region.of(Env.get("AWS_REGION"));
        this.credentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(Env.get("AWS_ACCESS_KEY_ID"), Env.get("AWS_SECRET_ACCESS_KEY")));
        this.s3Client = S3Client.builder().region(region).credentialsProvider(this.getCredentials()).build();
        this.rekognitionClient = RekognitionClient.builder().credentialsProvider(this.getCredentials()).region(this.getRegion()).build();
        dataObjectHelper = new AWSDataObjectHelper();
    }

    public static AWSClient getInstance() {
        if (instance == null)
            instance = new AWSClient();
        return instance;
    }

    public AWSDataObjectHelper dataObject() {
        return dataObjectHelper;
    }

    S3Client getS3Client() {
        return s3Client;
    }

    RekognitionClient getRekognitionClient() {
        return rekognitionClient;
    }

    Region getRegion() {
        return region;
    }

    AwsCredentialsProvider getCredentials() {
        return credentialsProvider;
    }
}
