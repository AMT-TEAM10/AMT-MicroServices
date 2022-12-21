package ch.heig.amtteam10.labeldetector.dto;

import ch.heig.amtteam10.labeldetector.core.Label;

public record LabelResultDTO(
        int count,
        String sourceUrl,
        Label[] results
) {
}
