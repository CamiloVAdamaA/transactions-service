package com.bankx.transactions.repository.mongo;

import com.bankx.transactions.model.mongo.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
    Mono<Account>findByNumber(String number);
}
