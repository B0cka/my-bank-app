package com.B0cka.kafka.producer;


public interface CashEventProducer {
    void sendDepositEvent(String login, Long amount);
    void sendWithdrawEvent(String login, Long amount);
}