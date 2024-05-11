package pro.anuj.skunkworks.bandbaja.sample.sqs;

import java.net.URI;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import pro.anuj.skunkworks.bandbaja.core.contracts.EventHandlerContainer;
import pro.anuj.skunkworks.bandbaja.core.execution.DefaultTaskExecutor;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutor;
import pro.anuj.skunkworks.bandbaja.sample.config.SqsEventHandlerProperties;
import pro.anuj.skunkworks.bandbaja.spring.SpringEventHandlerLifecycle;
import pro.anuj.skunkworks.bandbaja.spring.SpringTaskProvider;
import pro.anuj.skunkworks.bandbaja.sqs.SqsEventHandler;
import pro.anuj.skunkworks.bandbaja.sqs.SqsEventHandlerContainer;
import pro.anuj.skunkworks.bandbaja.sqs.SqsEventReceiver;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.utils.NamedThreadFactory;
import software.amazon.awssdk.utils.StringUtils;

@Configuration
@AllArgsConstructor
public class SqsEventHandlerConfig {

    public static final ThreadFactory THREAD_FACTORY = Thread::new;
    private final SqsEventHandlerProperties sqsEventHandlerProperties;

    @Bean
    public SqsClient sqsClient() {
        SqsClientBuilder builder = SqsClient.builder();
        String awsEndpoint = sqsEventHandlerProperties.getAwsEndpoint();
        if (StringUtils.isNotBlank(awsEndpoint)) {
            builder.endpointOverride(URI.create(awsEndpoint));
        }
        builder.region(Region.of(sqsEventHandlerProperties.getAwsRegion()));
        return builder.build();
    }

    @Bean
    public List<SqsEventReceiver> sqsEventReceivers(SqsClient sqsClient) {
        return new TreeMap<>(sqsEventHandlerProperties.getReceivers())
                .values()
                .stream()
                .map(v -> new SqsEventReceiver(sqsClient, v.getQueueUrl(), v.getWaitTimeInSeconds(), v.getAttributesToFetch()))
                .toList();
    }

    @Bean
    public SpringTaskProvider springTaskProvider(ApplicationContext applicationContext) {
        return new SpringTaskProvider(applicationContext);
    }

    @Bean
    public TaskExecutor taskExecutor(SpringTaskProvider springTaskProvider) {
        return new DefaultTaskExecutor(springTaskProvider);
    }

    @Bean
    public SqsEventHandler handler(ObjectMapper objectMapper, TaskExecutor taskExecutor) {
        return new SqsEventHandler(objectMapper, taskExecutor);
    }

    @Bean
    public ScheduledThreadPoolExecutor pollerPool() {
        return new ScheduledThreadPoolExecutor(
                sqsEventHandlerProperties.getPolling().getPollingThreads(), new NamedThreadFactory(THREAD_FACTORY, "poller"));
    }

    @Bean
    public ThreadPoolExecutor executor() {
        return new ThreadPoolExecutor(
                sqsEventHandlerProperties.getThreadPoolSize(),
                sqsEventHandlerProperties.getThreadPoolSize(),
                0L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(sqsEventHandlerProperties.getThreadPoolQueueSize()),
                new NamedThreadFactory(THREAD_FACTORY, "sqs-executor"));
    }


    @Bean
    public SpringEventHandlerLifecycle springEventHandlerLifecycle(List<EventHandlerContainer> eventHandlerContainers) {
        return new SpringEventHandlerLifecycle(eventHandlerContainers);
    }

    @Bean
    public SqsEventHandlerContainer sqsEventHandlerContainer(List<SqsEventReceiver> sqsEventReceivers,
                                                             @Qualifier("pollerPool") ScheduledThreadPoolExecutor pollerPool,
                                                             @Qualifier("executor") ThreadPoolExecutor executor, SqsEventHandler handler) {
        long initialDelayInMillis = sqsEventHandlerProperties.getPolling().getInitialDelayInMillis();
        long pollingIntervalInMillis = sqsEventHandlerProperties.getPolling().getPollingIntervalInMillis();
        return new SqsEventHandlerContainer(sqsEventReceivers, pollerPool, executor, handler, initialDelayInMillis, pollingIntervalInMillis);
    }
}
