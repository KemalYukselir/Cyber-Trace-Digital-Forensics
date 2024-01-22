package kent45.digitalforensics.model;

public record Scenario(
        int id,
        String subject,
        String overview,
        int awardScore,
        boolean isGuilty
) {
}
