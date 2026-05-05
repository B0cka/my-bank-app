package com.B0cka.events;

public sealed interface BankEvent permits MoneyTransferredEvent, MoneyDepositedEvent, MoneyWithdrawnEvent, ProfileUpdatedEvent{
}
