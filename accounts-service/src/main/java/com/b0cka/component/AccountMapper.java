package com.b0cka.component;

import com.b0cka.dto.AccountDto;
import com.b0cka.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public static AccountDto toDto(Account account){
        return AccountDto.builder()
                .name(account.getName())
                .balance(account.getBalance())
                .birthday(account.getBirthday())
                .build();
    }

}
