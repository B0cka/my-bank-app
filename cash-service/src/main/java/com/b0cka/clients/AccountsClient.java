package com.b0cka.clients;

public interface AccountsClient {

    void deposit(String login, Long amount);

    void withdraw(String login, Long amount);

}
