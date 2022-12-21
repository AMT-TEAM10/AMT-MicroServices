package ch.heig.amtteam10.labeldetector.core;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

/**
 * AWS Cloud Client
 *
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public class AWSClient implements ICloudClient {
    private static AWSClient instance;
    private final AWSLabelDetectorHelper labelDetectorHelper;
    private final Region region;
    private final RekognitionClient rekognitionClient;
    private final AwsCredentialsProvider credentialsProvider;

    private AWSClient() {
        this.region = Region.of(Env.get("AWS_REGION"));
        this.credentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(Env.get("AWS_ACCESS_KEY_ID"), Env.get("AWS_SECRET_ACCESS_KEY")));
        this.rekognitionClient = RekognitionClient.builder().credentialsProvider(this.getCredentials()).region(this.getRegion()).build();
        labelDetectorHelper = new AWSLabelDetectorHelper();
    }

    public static AWSClient getInstance() {
        if (instance == null)
            instance = new AWSClient();
        return instance;
    }

    @Override
    public AWSLabelDetectorHelper labelDetector() {
        return labelDetectorHelper;
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
