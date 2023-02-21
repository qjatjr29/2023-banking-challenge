package numble.banking.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import numble.banking.core.account.command.domain.Money;
import numble.banking.core.account.command.domain.TransferCompletedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"test-topic"})
@ActiveProfiles("test")
public class KafkaProducerTest {

  private String TEST_TOPIC_NAME = "test-topic";

  @Autowired
  KafkaTemplate<String, TransferCompletedEvent> transferKafkaTemplate;

  @Value("${spring.kafka.producer.bootstrap-servers}")
  private String bootstrapServers;

  @Test
  @DisplayName("kafka producer 로 계좌 이체 성공이벤트 발행 테스트")
  @DirtiesContext
  public void testSendTransferCompletedEvent() {

    // given
    String groupId = "test-group";

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

    Consumer<String, TransferCompletedEvent> consumer = new KafkaConsumer<>(props);

    TransferCompletedEvent event = new TransferCompletedEvent("testUser",
        "0000-000-000000",
        new Money(1000L),
        new Money(1000L),
        true,
        LocalDateTime.now());

    // when

    consumer.subscribe(Collections.singleton(TEST_TOPIC_NAME));
    transferKafkaTemplate.send(TEST_TOPIC_NAME, event);

    // then
    ConsumerRecords<String, TransferCompletedEvent> consumerRecords = KafkaTestUtils.getRecords(consumer, 10000);
    ConsumerRecord<String, TransferCompletedEvent> consumerRecord = consumerRecords.iterator().next();
    TransferCompletedEvent received = consumerRecord.value();

    assertThat(received.getToUserName()).isEqualTo("testUser");
    assertThat(received.getAmount().getMoney()).isEqualTo(1000L);
    assertThat(received.getAccountNumber()).isEqualTo("0000-000-000000");
  }

}
