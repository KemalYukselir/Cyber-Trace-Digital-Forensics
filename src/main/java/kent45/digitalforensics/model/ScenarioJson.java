package kent45.digitalforensics.model;

import java.util.List;

public record ScenarioJson(
        int id,
        String subject,
        String overview,
        List<EmailJson> emails,
        List<PaymentJson> payments,
        List<TextJson> texts,
        List<RecordJson> records,
        int awardScore,
        boolean isGuilty
) {
}
