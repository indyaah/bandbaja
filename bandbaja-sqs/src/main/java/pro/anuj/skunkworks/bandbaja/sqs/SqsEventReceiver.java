package pro.anuj.skunkworks.bandbaja.sqs;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import pro.anuj.skunkworks.bandbaja.core.contracts.EventReceiver;
import pro.anuj.skunkworks.bandbaja.core.domain.ErrorOrResult;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.utils.StringUtils;

@Log4j2
public class SqsEventReceiver implements EventReceiver<Message> {

  private final SqsClient sqsClient;
  private final String queueUrl;
  private final Integer waitTimeInSeconds;
  private final List<String> attributesToFetch;

  public SqsEventReceiver(SqsClient sqsClient, String queueUrl, Integer waitTimeInSeconds, List<String> attributesToFetch) {
    this.sqsClient = sqsClient;
      this.queueUrl = queueUrl;
      this.waitTimeInSeconds = waitTimeInSeconds;
      this.attributesToFetch = attributesToFetch;
  }

  public List<Message> receive(Integer batchSize) {
    if (batchSize <= 0) {
      return Collections.emptyList();
    }
    log.trace("fetching messages from SQS queue {}", queueUrl);
      ReceiveMessageRequest request =
        ReceiveMessageRequest.builder()
            .maxNumberOfMessages(Math.min(batchSize, 10))
            .queueUrl(queueUrl)
            .waitTimeSeconds(waitTimeInSeconds)
            .messageAttributeNames(attributesToFetch)
            .build();

    ReceiveMessageResponse response = sqsClient.receiveMessage(request);
    if (response.sdkHttpResponse() != null && response.sdkHttpResponse().isSuccessful()) {
      return response.messages();
    }
    return Collections.emptyList();
  }

  public ErrorOrResult<Boolean> acknowledge(Message message) {
    DeleteMessageRequest build =
        DeleteMessageRequest.builder()
            .queueUrl(queueUrl)
            .receiptHandle(message.receiptHandle())
            .build();
    try {
      DeleteMessageResponse response = sqsClient.deleteMessage(build);
      if (response.sdkHttpResponse() != null && response.sdkHttpResponse().isSuccessful()) {
        log.debug("Deleted message successfully");
        return ErrorOrResult.result(true);
      }
      return ErrorOrResult.result(false);
    } catch (Exception e) {
      log.error("Exception while deleting message", e);
      return ErrorOrResult.error(e);
    }
  }
}
