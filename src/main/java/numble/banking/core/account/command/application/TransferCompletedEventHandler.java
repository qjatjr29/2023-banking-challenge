package numble.banking.core.account.command.application;

import numble.banking.core.account.command.domain.TransferCompletedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;


public class TransferCompletedEventHandler {

  @Value("${kafka.topic.transfer-completed}")
  private  String TRANSFER_COMPLETE_TOPIC_NAME;

  private final KafkaTemplate<String, TransferCompletedEvent> transferKafkaTemplate;

  public TransferCompletedEventHandler(
      KafkaTemplate<String, TransferCompletedEvent> transferKafkaTemplate) {
    this.transferKafkaTemplate = transferKafkaTemplate;
  }

  @EventListener(TransferCompletedEvent.class)
  public void handle(TransferCompletedEvent event) {
    transferKafkaTemplate.send(TRANSFER_COMPLETE_TOPIC_NAME, event);
  }
}
