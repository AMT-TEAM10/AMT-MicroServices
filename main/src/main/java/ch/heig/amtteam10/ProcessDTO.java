package ch.heig.amtteam10;

public record ProcessDTO(
        String imageUrl,
        int maxLabels,
        float minConfidence
) {
}
