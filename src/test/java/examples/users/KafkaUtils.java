package examples.users;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;


public class KafkaUtils {
    private static final Logger logger = LoggerFactory.getLogger(KafkaUtils.class);
    private final String bootstrapServers;
    private final String username;
    private final String password;

    public KafkaUtils(String bootstrapServers, String username, String password) {
        this.bootstrapServers = bootstrapServers;
        this.username = username;
        this.password = password;
    }

    // Produce a message to Kafka 
    
    public Map<String, Object> produceMessage(String topic, String key, String value) {
    	
    	
    	//To Logs failures and returns human-readable error message
    	
    	
    	Map<String, Object> result = new HashMap<>();
        Properties props = new Properties();
        
        //Broker settings
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        // Add security if credentials are provided
        if (username != null && password != null) {
            props.put("security.protocol", "SASL_SSL");
            props.put("sasl.mechanism", "PLAIN");
            props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + username + "\" password=\"" + password + "\";");
        }

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        /*Waits 10 sec*/    RecordMetadata metadata = producer.send(record).get(10, TimeUnit.SECONDS); //Timeout handling
           
        // Returns status map with topic, partition, offset, timestamp
            
            result.put("status", "success");
            result.put("topic", metadata.topic());
            result.put("partition", metadata.partition());
            result.put("offset", metadata.offset());
            result.put("timestamp", metadata.timestamp());
           
            logger.info("Message produced to Kafka: topic={}, partition={}, offset={}",
                       metadata.topic(), metadata.partition(), metadata.offset());
            
          /*Logs failures and returns human-readable error message*/
        } catch (Exception e) {
            result.put("status", "failed");
            result.put("error", e.getMessage());
            logger.error("Failed to produce message to Kafka", e);
        }

        return result;
    }

    /*Consume messages from Kafka
    
    
    - Reads messages from Kafka from start (earliest), without auto committing
    - Filters and returns structured list of messages
    - Sets limit on how many messages (idempotency protection)
    - Supports username/password if needed
    - Safe with try-with-resources

    Also logs how many were consumed
    */
    
      
    
        
    public List<Map<String, Object>> consumeMessages(String topic, String groupId, int maxMessages, int timeoutSeconds) {
        List<Map<String, Object>> messages = new ArrayList<>();
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // Add security if credentials are provided
        if (username != null && password != null) {
            props.put("security.protocol", "SASL_SSL");
            props.put("sasl.mechanism", "PLAIN");
            props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + username + "\" password=\"" + password + "\";");
        }

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);

            while (System.currentTimeMillis() < endTime && messages.size() < maxMessages) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    Map<String, Object> message = new HashMap<>();
                    message.put("key", record.key());
                    message.put("value", record.value());
                    message.put("partition", record.partition());
                    message.put("offset", record.offset());
                    message.put("timestamp", record.timestamp());
                    messages.add(message);

                    if (messages.size() >= maxMessages) {
                        break;
                    }
                }
            }

            // Commit offsets
            consumer.commitSync();
            logger.info("Consumed {} messages from topic {}", messages.size(), topic);
        } catch (Exception e) {
            logger.error("Failed to consume messages from Kafka", e);
        }

        return messages;
    }

    // Wait for a specific message
    public Map<String, Object> waitForMessage(String topic, String key, int timeoutSeconds) {
        List<Map<String, Object>> messages = consumeMessages(topic, "wait-group", 100, timeoutSeconds);
        return messages.stream()
                .filter(m -> key.equals(m.get("key")))
                .findFirst()
                .orElse(null);
    }
}