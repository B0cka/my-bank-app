package com.B0cka.service;

import com.B0cka.dto.CashAction;

public interface CashService {

    String performCashOperation(CashAction cashAction, Long sum);

}
