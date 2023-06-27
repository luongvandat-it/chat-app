package com.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.ResourceBundle;

@Controller
public class ChatAppController implements Initializable {

    @FXML
    private Button btnSend;

    @FXML
    private ListView<String> listMessage;

    @FXML
    private TextArea txtMessage;

    private Producer<String, String> kafkaProducer;
    private Consumer<String, String> kafkaConsumer;
    private String kafkaTopic = "luongvandat";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Replace with your Kafka broker address
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "chat-app-consumer");

        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Collections.singleton(kafkaTopic));

        // Start a separate thread to continuously poll and process messages
        Thread messageConsumerThread = new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String message = record.value();
                    // Display the message in the ListView
                    listMessage.getItems().add(message);
                }
            }
        });

        messageConsumerThread.start();

        Properties props2 = new Properties();
        props2.put("bootstrap.servers", "localhost:9092"); // Replace with your Kafka broker address
        props2.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props2.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer<>(props2);

        btnSend.setOnAction(e -> {
            String message = txtMessage.getText();
            listMessage.getItems().add(message);
            txtMessage.clear();

            // Send message to Kafka
            ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTopic, message);
            kafkaProducer.send(record);
        });

        txtMessage.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER:
                    btnSend.fire();
            }
        });
    }
}
