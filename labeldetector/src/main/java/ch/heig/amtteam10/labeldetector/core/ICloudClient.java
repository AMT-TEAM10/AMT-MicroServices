package ch.heig.amtteam10.labeldetector.core;

/**
 * Defines a cloud cli
 *
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public interface ICloudClient {
    /**
     * Return an IDataObjectHelper that allows operations to object storage functionalities
     *
     * @return ILabelDetector
     * @see ILabelDetector
     */
    ILabelDetector labelDetector();
}
