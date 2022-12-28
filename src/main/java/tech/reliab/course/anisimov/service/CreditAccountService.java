package tech.reliab.course.anisimov.service;

import tech.reliab.course.anisimov.entity.CreditAccount;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.NotEnoughMoneyException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;

import java.util.List;

public interface CreditAccountService {
    // Создание кредитного аккаунта
    CreditAccount create(CreditAccount creditAccount) throws UnuniqeIdException;

    // Добавить кредитный аккаунт
    CreditAccount addAccount(CreditAccount account) throws UnuniqeIdException;

    // Получить кредитный аккаунт по айди
    CreditAccount getAccountById(String accountId) throws DoesNotExistException;

    // Удалить делитный аккаунт
    Boolean deleteAccount(String accountId) throws DoesNotExistException;

    // Получить кредтиный аккаунты пользователя
    List<CreditAccount> getUsersAccounts(String userId);

    // Списать оплату за месяц
    void executeMonthlyPayment(CreditAccount creditAccount) throws NotEnoughMoneyException;

    String stringRepresentation(String accountId) throws DoesNotExistException;


}
