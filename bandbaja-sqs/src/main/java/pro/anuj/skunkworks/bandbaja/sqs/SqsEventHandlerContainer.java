package pro.anuj.skunkworks.bandbaja.sqs;

import static pro.anuj.skunkworks.bandbaja.core.contracts.ExceptionHandler.ErrorHandlerResult.ACKNOWLEDGE;
import static pro.anuj.skunkworks.bandbaja.core.contracts.ExceptionHandler.ErrorHandlerResult.IGNORE;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.ThreadContext;

import pro.anuj.skunkworks.bandbaja.core.contracts.EventHandlerContainer;
import pro.anuj.skunkworks.bandbaja.core.contracts.ExceptionHandler;
import pro.anuj.skunkworks.bandbaja.core.contracts.ExceptionHandler.ErrorHandlerResult;
import pro.anuj.skunkworks.bandbaja.core.domain.ErrorOrResult;
import pro.anuj.skunkworks.bandbaja.core.execution.TaskExecutionResult;
import software.amazon.awssdk.services.sqs.model.Message;

@Log4j2
@AllArgsConstructor
public class SqsEventHandlerContainer implements EventHandlerContainer {

    private final List<SqsEventReceiver> receivers;
    private final ScheduledThreadPoolExecutor poller;
    private final ThreadPoolExecutor executor;
    private final SqsEventHandler handler;
    private final long initialDelayInMillis;
    private final long pollingIntervalInMillis;

    private final ExceptionHandler<Message> exceptionHandler =
            (m, e) -> {
                if (e instanceof SQLException) {
                    return IGNORE;
                } else {
                    return ACKNOWLEDGE;
                }
            };

    public void start() {
        for (int i = 0; i < poller.getCorePoolSize(); i++) {
            log.info("starting sqs poller - thread {}", i);
            poller.scheduleWithFixedDelay(
                    this::poll,
                    initialDelayInMillis,
                    pollingIntervalInMillis,
                    TimeUnit.MILLISECONDS);
        }
    }

    void poll() {
        final Iterator<SqsEventReceiver> iterator = receivers.iterator();
        while (executor.getQueue().remainingCapacity() > 0 && iterator.hasNext()) {
            int capacity = executor.getQueue().remainingCapacity();
            final SqsEventReceiver receiver = iterator.next();
            List<Message> receive = receiver.receive(capacity);
            if (receive.isEmpty()) {
                continue;
            }
            for (Message message : receive) {
                Runnable runnable =
                        () -> {
                            try {
                                ThreadContext.put("Message-Id", message.messageId());
                                ThreadContext.putAll(message.attributesAsStrings());
                                ErrorOrResult<TaskExecutionResult> handle = handler.handle(message);
                                ErrorOrResult<Boolean> acknowledge = receiver.acknowledge(message);
                                if (log.isDebugEnabled()) {
                                    log.debug("Task result : {}, acknowledge results : {}", handle, acknowledge);
                                }
                            } catch (Exception e) {
                                log.error("Exception while process message.", e);
                                ErrorHandlerResult result = exceptionHandler.handle(message, e);
                                if (result.equals(ACKNOWLEDGE)) {
                                    ErrorOrResult<Boolean> acknowledge = receiver.acknowledge(message);
                                    if (log.isDebugEnabled()) {
                                        log.debug("acknowledge results : {}", acknowledge);
                                    }
                                }
                            } finally {
                                ThreadContext.clearAll();
                            }
                        };
                executor.submit(runnable);
            }
        }
    }

    @Override
    public void stop() {
        try {
            poller.shutdown();
            //noinspection ResultOfMethodCallIgnored
            poller.awaitTermination(30, TimeUnit.SECONDS);
            executor.shutdown();
            //noinspection ResultOfMethodCallIgnored
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Exception while stopping sqs handler lifecyle", e);
        }
    }
}
