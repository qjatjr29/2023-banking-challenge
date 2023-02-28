package numble.banking.core.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import numble.banking.core.account.command.domain.TransferCompletedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

  @Value("${spring.kafka.consumer.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, TransferCompletedEvent>> transferCompletedContainerFactory() {

    Map<String, Object> props = new HashMap<>();

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

    DefaultKafkaConsumerFactory<String, TransferCompletedEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(props);

    ConcurrentKafkaListenerContainerFactory<String, TransferCompletedEvent> listenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();

    listenerContainerFactory.getContainerProperties().setConsumerRebalanceListener(
        new ConsumerAwareRebalanceListener() {
          @Override
          public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer,
              Collection<TopicPartition> partitions) {
          }

          @Override
          public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer,
              Collection<TopicPartition> partitions) {
          }

          @Override
          public void onPartitionsLost(Consumer<?, ?> consumer,
              Collection<TopicPartition> partitions) {
          }

          @Override
          public void onPartitionsAssigned(Consumer<?, ?> consumer,
              Collection<TopicPartition> partitions) {
          }
        });

    listenerContainerFactory.setBatchListener(false);
    listenerContainerFactory.getContainerProperties().setAckMode(AckMode.RECORD);
    listenerContainerFactory.setConsumerFactory(consumerFactory);
    return listenerContainerFactory;
  }

}
