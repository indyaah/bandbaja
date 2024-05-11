package pro.anuj.skunkworks.bandbaja.sample.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import pro.anuj.skunkworks.bandbaja.sqs.SqsPollingProperties;
import pro.anuj.skunkworks.bandbaja.sqs.SqsReceiverProperties;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "bandbaja.sqs")
public class SqsEventHandlerProperties {
    private String awsEndpoint;
    private String awsRegion;
    private int threadPoolSize;
    private int threadPoolQueueSize;
    @NestedConfigurationProperty
    private SqsPollingProperties polling;
    @NestedConfigurationProperty
    private Map<Integer, SqsReceiverProperties> receivers;

}
