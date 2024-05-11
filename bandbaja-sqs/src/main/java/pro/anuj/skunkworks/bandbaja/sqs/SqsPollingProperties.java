package pro.anuj.skunkworks.bandbaja.sqs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class SqsPollingProperties {
    private long initialDelayInMillis;
    private long pollingIntervalInMillis;
    private int pollingThreads;
}
