package numble.banking.core.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import numble.banking.core.account.command.domain.TransferCompletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferCompletedEventListener {

  @Autowired
  private ObjectMapper objectMapper;

  @KafkaListener(topics = "${kafka.topic.transfer-completed}", groupId = "${spring.kafka.consumer.group-id}")
  public void consume(String message) {
    try {
      TransferCompletedEvent event = objectMapper.readValue(message, TransferCompletedEvent.class);
      log.info("=== 이체 성공 알림 전송 시작 === \n 데이터 : {}", event.toString());
      Thread.sleep(2000);
      log.info("=== 알림 전송 끝 ===");
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

}
