package numble.banking.core.config;

import java.util.HashMap;
import java.util.Map;
import numble.banking.core.account.command.domain.TransferCompletedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

  @Value("${spring.kafka.producer.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${kafka.topic.transfer-completed}")
  private  String TRANSFER_COMPLETE_TOPIC_NAME;

  @Bean
  public ProducerFactory<String, TransferCompletedEvent> transferCompletedEventProducerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, TransferCompletedEvent> transferKafkaTemplate() {
    return new KafkaTemplate<>(transferCompletedEventProducerFactory());
  }

  @Bean
  public NewTopic transferCompleteTopic() {
    return new NewTopic(TRANSFER_COMPLETE_TOPIC_NAME, 1, (short) 1);
  }

}
