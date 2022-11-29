package ch.heig.amtteam10.core.cloud;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * Helper for AWS Label Rekognition
 *
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public class AWSLabelDetectorHelper implements ILabelDetector {
    @Override
    public Label[] execute(String imageUri, int maxLabels, float minConfidence) throws IOException {
        SdkBytes sourceBytes = SdkBytes.fromInputStream(downloadImage(imageUri));
        return getLabels(sourceBytes, maxLabels, minConfidence);
    }

    @Override
    public Label[] execute(URL imageUri, int maxLabels, float minConfidence) throws IOException {
        return execute(imageUri.toString(), maxLabels, minConfidence);
    }

    @Override
    public Label[] execute(ByteBuffer base64Image, int maxLabels, float minConfidence) {
        SdkBytes sourceBytes = SdkBytes.fromByteBuffer(base64Image);
        return getLabels(sourceBytes, maxLabels, minConfidence);
    }

    private InputStream downloadImage(String imageUri) throws IOException {
        URL url = new URL(imageUri);
        return new BufferedInputStream(url.openStream());
    }

    private Label[] getLabels(SdkBytes sourceBytes, int maxLabels, float minConfidence) {
        Image image = Image.builder().bytes(sourceBytes).build();
        DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                .image(image)
                .maxLabels(maxLabels)
                .minConfidence(minConfidence * 100f)
                .build();

        DetectLabelsResponse labelsResponse = AWSClient.getInstance().getRekognitionClient().detectLabels(detectLabelsRequest);
        var awsLabels = labelsResponse.labels();

        Label[] result = new Label[awsLabels.size()];
        for (int i = 0; i < awsLabels.size(); i++) {
            result[i] = new Label(awsLabels.get(i).name(), awsLabels.get(i).confidence() / 100.0f);
        }
        return result;
    }
}
