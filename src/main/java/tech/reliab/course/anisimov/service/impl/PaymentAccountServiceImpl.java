package tech.reliab.course.anisimov.service.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.reliab.course.anisimov.entity.PaymentAccount;
import tech.reliab.course.anisimov.entity.User;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.NotEnoughMoneyException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;
import tech.reliab.course.anisimov.service.PaymentAccountService;
import tech.reliab.course.anisimov.service.UserService;

import java.util.*;

final public class PaymentAccountServiceImpl implements PaymentAccountService {
    //region ===================== Properties ======================
    private final Map<String, PaymentAccount> paymentAccountMap = new HashMap<>();

    //region ===================== DI ======================
    @NotNull public UserService userService;

    //region ===================== PaymentService implementation ======================
    @Override
    public @Nullable PaymentAccount create(@NotNull PaymentAccount paymentAccount) throws UnuniqeIdException {
        return this.addAccount(new PaymentAccount(paymentAccount));
    }

    @Override
    public @Nullable PaymentAccount addAccount(@NotNull PaymentAccount account) throws UnuniqeIdException {
        if (this.paymentAccountMap.containsKey(account.getId())) { throw new UnuniqeIdException(account.getId()); };

        User user = account.getUser();
        if (user != null) {
            this.paymentAccountMap.put(account.getId(), account);
            return account;
        }

        return null;
    }

    @Override
    public @NotNull Boolean deleteAccount(@NotNull String accountId) throws DoesNotExistException {
        PaymentAccount account = this.paymentAccountMap.get(accountId);
        if (account == null) { throw new DoesNotExistException(accountId); }

        this.userService.removePaymentAccount(account.getUser().getId(), accountId);
        return this.paymentAccountMap.remove(accountId) != null;
    }

    @Override
    public @NotNull PaymentAccount getAccountById(@NotNull String accountId) throws DoesNotExistException {
        PaymentAccount account = this.paymentAccountMap.get(accountId);
        if (account == null) { throw new DoesNotExistException(accountId); }

        return account;
    }

    @Override
    public @NotNull List<PaymentAccount> getUsersAccounts(@NotNull String userId) {
        return this.paymentAccountMap.values()
                .stream()
                .filter(account -> Objects.equals(account.getUser().getId(), userId))
                .toList();
    }

    @Override
    public void depositMoney(@NotNull PaymentAccount account, double sum) {
        account.setTotalCash(account.getTotalCash() + sum);
    }

    @Override
    public void withdrawMoney(@NotNull PaymentAccount account, double sum) throws NotEnoughMoneyException {
        if (account.getTotalCash() > sum) {
            account.setTotalCash(account.getTotalCash() - sum);
        } else {
            throw new NotEnoughMoneyException();
        }
    }

    @Override
    public @NotNull String stringRepresentation(@NotNull String accountId) throws DoesNotExistException {
        PaymentAccount paymentAccount = this.getAccountById(accountId);

        return paymentAccount.toString();
    }

    @Override
    public @NotNull PaymentAccount getBestAccountForUser(@NotNull String userId) throws DoesNotExistException {
        return this.getUsersAccounts(userId)
                .stream()
                .max(Comparator.comparing(PaymentAccount::getTotalCash))
                .orElseThrow(NoSuchElementException::new);
    }
}
