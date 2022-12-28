package tech.reliab.course.anisimov.service;

import tech.reliab.course.anisimov.entity.CreditAccount;
import tech.reliab.course.anisimov.entity.PaymentAccount;
import tech.reliab.course.anisimov.entity.User;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;

import java.util.List;

public interface UserService {
    // Создание пользователя
    User create(User user) throws UnuniqeIdException;

    // Добавить пользователя
    User addUser(User user) throws UnuniqeIdException;

    // Получение всех пользователей
    List<User> getAllUsers();

    // Получение пользователя по айди
    User getUserById(String userId) throws DoesNotExistException;

    // Удаление пользователя
    Boolean deleteUser(String userId) throws DoesNotExistException;

    // Получение всех пользователей банка
    List<User> getAllUsersByBankId(String bankId);

    // Добавить кредитный аккаунт
    Boolean addCreditAccount(String userId, CreditAccount creditAccount) throws DoesNotExistException;

    // Удалить кредитный аккаунт
    Boolean removeCreditAccount(String userId, String accountId) throws DoesNotExistException;

    // Добави платежный аккаунт
    Boolean addPaymentAccount(String userId, PaymentAccount paymentAccount) throws DoesNotExistException;

    // Удалить платежный аккаунт
    Boolean removePaymentAccount(String userId, String paymentId) throws DoesNotExistException;

    // Установить мето работы
    void registerJob(User user, String jobAddress, double monthlyIncome);

    // Посчитать кредитный рейтинг пользователя
    void calculateRating(User user);

    String stringRepresentation(String userId) throws DoesNotExistException;
}
