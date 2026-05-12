package com.b0cka.service;

import com.b0cka.dto.TransferRequest;

public interface TransferService {

    String transferMoney(TransferRequest transferRequest);

}
