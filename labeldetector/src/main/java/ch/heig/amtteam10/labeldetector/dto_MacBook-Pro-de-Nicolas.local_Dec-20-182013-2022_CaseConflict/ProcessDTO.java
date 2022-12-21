package ch.heig.amtteam10.labeldetector.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProcessDTO(
        @NotBlank(message = "imageUrl is mandatory")
        String imageUrl,
        @Min(value = 1, message = "maxLabels must be greater than 0")
        int maxLabels,
        @Min(value = 0, message = "minConfidence must be greater than 0")
        @Max(value = 1, message = "minConfidence must be lower than 1")
        float minConfidence
) {
}
