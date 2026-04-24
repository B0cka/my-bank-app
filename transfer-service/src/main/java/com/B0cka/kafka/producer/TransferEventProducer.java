package com.B0cka.kafka.producer;

public interface TransferEventProducer {
    void sendTransferEvent(String from, String to, Long amount);
}