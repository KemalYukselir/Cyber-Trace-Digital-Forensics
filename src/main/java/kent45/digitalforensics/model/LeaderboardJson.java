package kent45.digitalforensics.model;

public record LeaderboardJson (
        int rank,
        String username,
        int highScore
) {
}