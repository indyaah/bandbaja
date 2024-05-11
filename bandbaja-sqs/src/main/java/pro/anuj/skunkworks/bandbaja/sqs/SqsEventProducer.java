package pro.anuj.skunkworks.bandbaja.sqs;

import static software.amazon.awssdk.utils.StringUtils.isBlank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.utils.ImmutableMap;
import software.amazon.awssdk.utils.ImmutableMap.Builder;

@Log4j2
@RequiredArgsConstructor
public class SqsEventProducer {

  private final Map<String, String> queueNameToQueueUrlMap = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper;
  private final SqsClient sqsClient;

    public <T> SendMessageResponse convertAndSend(
      final T message,
      final String groupId,
      final ImmutableMap<String, MessageAttributeValue> attributes,
      final String queueName)
      throws JsonProcessingException {

    Builder<String, MessageAttributeValue> builder = ImmutableMap.builder();
    if (attributes != null && !attributes.isEmpty()) {
      attributes.forEach(builder::put);
    }
    builder.put("QUEUE", SqsMessageAttributeUtil.string(queueName));

    final ImmutableMap<String, MessageAttributeValue> updatedAttributes = builder.build();

    final Function<String, String> queueUrlResolver =
        name ->
            sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(name).build()).queueUrl();
    String url = queueNameToQueueUrlMap.computeIfAbsent(queueName, queueUrlResolver);

    return sendMessageInternal(message, groupId, updatedAttributes, url);
  }

  private <T> SendMessageResponse sendMessageInternal(
      final T message,
      final String groupId,
      final ImmutableMap<String, MessageAttributeValue> attributes,
      final String url)
      throws JsonProcessingException {

    final String sanitisedGroupId = sanitize(groupId);
    final String messageBody =
        message instanceof String ? message.toString() : objectMapper.writeValueAsString(message);
    final SendMessageRequest sendMessageRequest =
        SendMessageRequest.builder()
            .messageGroupId(sanitisedGroupId)
            .queueUrl(url)
            .messageBody(messageBody)
            .messageAttributes(attributes)
            .build();
    return this.sqsClient.sendMessage(sendMessageRequest);
  }

  private static String sanitize(String groupId) {
    if (isBlank(groupId)) {
      return UUID.randomUUID().toString();
    }
    return groupId.trim().replaceAll(" ", "");
  }
}
