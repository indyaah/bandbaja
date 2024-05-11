package pro.anuj.skunkworks.bandbaja.sqs;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class SqsReceiverProperties {
    private String queueUrl;
    private int waitTimeInSeconds = 10;
    private List<String> attributesToFetch = List.of("ALL");
}
