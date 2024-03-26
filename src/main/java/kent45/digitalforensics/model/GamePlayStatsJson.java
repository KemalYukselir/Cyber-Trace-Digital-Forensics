package kent45.digitalforensics.model;

public record GamePlayStatsJson(
        String username,
        int scenariosCorrect,
        int scenariosWrong,
        int timeTakenSeconds
) {
}
