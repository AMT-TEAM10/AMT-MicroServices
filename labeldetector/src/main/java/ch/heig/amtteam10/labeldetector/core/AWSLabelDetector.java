package ch.heig.amtteam10.labeldetector.core;

import ch.heig.amtteam10.labeldetector.core.exceptions.FailDownloadFileException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * Helper for AWS Label Rekognition
 *
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public class AWSLabelDetector implements ILabelDetector {
    private final RekognitionClient client;

    public AWSLabelDetector(){
        Region region = Region.of(Env.get("AWS_REGION"));
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider
                .create(AwsBasicCredentials
                        .create(
                                Env.get("AWS_ACCESS_KEY_ID"),
                                Env.get("AWS_SECRET_ACCESS_KEY"
                                )
                        ));
        this.client = RekognitionClient.builder().region(region).credentialsProvider(credentialsProvider).build();
    }

    @Override
    public Label[] execute(String imageUri, int maxLabels, float minConfidence) throws FailDownloadFileException {
        SdkBytes sourceBytes = SdkBytes.fromInputStream(downloadImage(imageUri));
        return getLabels(sourceBytes, maxLabels, minConfidence);
    }

    @Override
    public Label[] execute(URL imageUri, int maxLabels, float minConfidence) throws FailDownloadFileException {
        return execute(imageUri.toString(), maxLabels, minConfidence);
    }

    @Override
    public Label[] execute(ByteBuffer base64Image, int maxLabels, float minConfidence) {
        SdkBytes sourceBytes = SdkBytes.fromByteBuffer(base64Image);
        return getLabels(sourceBytes, maxLabels, minConfidence);
    }

    private InputStream downloadImage(String imageUri) throws FailDownloadFileException {
        try {
            URL url = new URL(imageUri);
            return new BufferedInputStream(url.openStream());
        } catch (MalformedURLException e) {
            throw new FailDownloadFileException("Failed to download file, because url is malformed");
        } catch (IOException e) {
            throw new FailDownloadFileException("Failed to download file, cannot open url");
        }
    }

    private Label[] getLabels(SdkBytes sourceBytes, int maxLabels, float minConfidence) {
        Image image = Image.builder().bytes(sourceBytes).build();
        DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                .image(image)
                .maxLabels(maxLabels)
                .minConfidence(minConfidence * 100f)
                .build();

        DetectLabelsResponse labelsResponse = client.detectLabels(detectLabelsRequest);
        var awsLabels = labelsResponse.labels();

        Label[] result = new Label[awsLabels.size()];
        for (int i = 0; i < awsLabels.size(); i++) {
            result[i] = new Label(awsLabels.get(i).name(), awsLabels.get(i).confidence() / 100.0f);
        }
        return result;
    }
}
