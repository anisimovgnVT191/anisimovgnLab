package tech.reliab.course.anisimov.service;

import tech.reliab.course.anisimov.entity.PaymentAccount;
import tech.reliab.course.anisimov.exception.CannotDepositMoneyException;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.NotEnoughMoneyException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;

import java.util.List;

public interface PaymentAccountService {
    // Создание платежного аккаунта
    PaymentAccount create(PaymentAccount paymentAccount) throws UnuniqeIdException;

    // Добавление платежного аккаунта
    PaymentAccount addAccount(PaymentAccount account) throws UnuniqeIdException;

    // Удаление аккаунты
    Boolean deleteAccount(String accountId) throws DoesNotExistException;

    // Получение аккаунта по айди
    PaymentAccount getAccountById(String accountId) throws DoesNotExistException;

    // Получение всех аккаунтов польщователя
    List<PaymentAccount> getUsersAccounts(String userId);

    // Внести деньги
    void depositMoney(PaymentAccount account, double sum);

    // Снять деньги
    void withdrawMoney(PaymentAccount account, double sum) throws NotEnoughMoneyException;

    String stringRepresentation(String accountId) throws DoesNotExistException;

    PaymentAccount getBestAccountForUser(String userId) throws DoesNotExistException;
}
