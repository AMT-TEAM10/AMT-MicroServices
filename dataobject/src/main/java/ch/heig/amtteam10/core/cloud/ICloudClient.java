package ch.heig.amtteam10.core.cloud;

/**
 * Defines a cloud cli
 *
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public interface ICloudClient {
    /**
     * Return an IDataObjectHelper that allows operations to object storage functionalities
     * @return IDataObjectHelper
     * @see IDataObjectHelper
     */
    IDataObjectHelper dataObject();

    /**
     * Return an IDataObjectHelper that allows operations to object storage functionalities
     * @return ILabelDetector
     * @see ILabelDetector
     */
    ILabelDetector labelDetector();
}
