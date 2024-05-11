package pro.anuj.skunkworks.bandbaja.sqs;

import java.util.List;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

public class SqsMessageAttributeUtil {

  public static MessageAttributeValue string(String value) {
    return MessageAttributeValue.builder().dataType("String").stringValue(value).build();
  }

  public static MessageAttributeValue string(List<String> value) {
    return MessageAttributeValue.builder().dataType("String").stringListValues(value).build();
  }

  public static MessageAttributeValue number(Number value) {
    return MessageAttributeValue.builder()
        .dataType("Number")
        .stringValue(String.valueOf(value))
        .build();
  }
}
