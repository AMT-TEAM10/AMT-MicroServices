package ch.heig.amtteam10.labeldetector.service;

import ch.heig.amtteam10.labeldetector.DTO.ProcessDTO;
import ch.heig.amtteam10.labeldetector.core.AWSClient;
import ch.heig.amtteam10.labeldetector.core.Label;
import ch.heig.amtteam10.labeldetector.core.exceptions.FailDownloadFileException;
import org.springframework.stereotype.Service;

@Service
public class LabelDetectorService {
  public Label[] executeLabelDetection(ProcessDTO params) throws FailDownloadFileException {
    return AWSClient.getInstance().labelDetector().execute(params.imageUrl(), params.maxLabels(), params.minConfidence());
  }
}