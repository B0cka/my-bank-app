package com.b0cka.service;

import com.b0cka.dto.CashAction;

public interface CashService {

    String performCashOperation(CashAction cashAction, long sum);

}
