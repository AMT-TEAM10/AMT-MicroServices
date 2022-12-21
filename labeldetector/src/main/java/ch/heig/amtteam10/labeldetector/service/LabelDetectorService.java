package ch.heig.amtteam10.labeldetector.service;

import ch.heig.amtteam10.labeldetector.core.AWSLabelDetector;
import ch.heig.amtteam10.labeldetector.core.ILabelDetector;
import ch.heig.amtteam10.labeldetector.dto.ProcessDTO;
import ch.heig.amtteam10.labeldetector.core.Label;
import ch.heig.amtteam10.labeldetector.core.exceptions.FailDownloadFileException;
import org.springframework.stereotype.Service;

@Service
public class LabelDetectorService {
    private final ILabelDetector labelDetector;

    public LabelDetectorService() {
        this.labelDetector = new AWSLabelDetector();
    }

    public Label[] executeLabelDetection(ProcessDTO params) throws FailDownloadFileException {
        return labelDetector.execute(params.imageUrl(), params.maxLabels(), params.minConfidence());
    }
}
