package ch.heig.amtteam10.labeldetector.core;

import ch.heig.amtteam10.labeldetector.core.exceptions.FailDownloadFileException;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * Defines a label detector
 *
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public interface ILabelDetector {
    /**
     * Execute Rekognition analysis on image
     *
     * @param imageUri      The URI of the image to analyze
     * @param maxLabels     The maximum number of labels to return
     * @param minConfidence The minimum confidence required to return a label (0-1)
     * @return An array of labels
     * @throws IOException If the image cannot be downloaded
     */
    Label[] execute(String imageUri, int maxLabels, float minConfidence) throws IOException, FailDownloadFileException;

    /**
     * Execute Rekognition analysis on image
     *
     * @param imageUri      The URI of the image to analyze
     * @param maxLabels     The maximum number of labels to return
     * @param minConfidence The minimum confidence required to return a label (0-1)
     * @return An array of labels
     * @throws IOException If the image cannot be downloaded
     */
    Label[] execute(URL imageUri, int maxLabels, float minConfidence) throws IOException, FailDownloadFileException;

    /**
     * Execute Rekognition analysis on image
     *
     * @param base64Image   The byte encoded representation of image
     * @param maxLabels     The maximum number of labels to return
     * @param minConfidence The minimum confidence required to return a label (0-1)
     * @return An array of labels
     * @throws IOException If the image cannot be downloaded
     */
    Label[] execute(ByteBuffer base64Image, int maxLabels, float minConfidence) throws IOException;
}
