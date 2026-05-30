package io.github.emersondll.transactions.service.impl;

import io.github.emersondll.transactions.config.constants.RabbitMqConstants;
import io.github.emersondll.transactions.document.AccountDocument;
import io.github.emersondll.transactions.mapper.AccountMapper;
import io.github.emersondll.transactions.model.request.AccountRequest;
import io.github.emersondll.transactions.model.response.AccountResponse;
import io.github.emersondll.transactions.repository.AccountRepository;
import io.github.emersondll.transactions.service.AccountService;
import io.github.emersondll.transactions.service.RabbitMqService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.SQLDataException;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private AccountRepository repository;
    private AccountMapper mapper;
    @Autowired
    private RabbitMqService mqService;

    public AccountResponse createAccount(final AccountRequest request) {
        log.info("Start Access method createAccount in Service ");
        AccountDocument document = findByDocumentNumber(request.getDocumentNumber());
        if (ObjectUtils.isEmpty(document)) {
            log.info("New Account ");
            AccountResponse response = mapper.convertDocumentToResponse(repository.save(mapper.convertRequestToDocument(request)));
            log.info("Finished method createAccount in Service ");
            mqService.send(RabbitMqConstants.ACCOUNT, "New AccountID Created: ".concat(response.getAccountId()));
            return response;
        }
        log.info("Finished method createAccount in Service returning Old Account ");
        return mapper.convertDocumentToResponse(document);

    }

    public AccountDocument findByDocumentNumber(String documentNumber) {
        return repository.findByDocumentNumber(documentNumber);
    }


    @Override
    public AccountResponse findById(String accountId) throws Exception {
        log.info("Start find account by id");
        Optional<AccountDocument> response = repository.findById(accountId);

        //if (response.isEmpty()) {
        if (response.isEmpty()) {
            log.error("find account by id");
            throw new SQLDataException("Id Not Found");
        }
        log.info("Finished find account by id");
        return mapper.convertDocumentToResponseComplete(response.get());


    }
}
