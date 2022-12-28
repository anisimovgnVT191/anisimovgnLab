package tech.reliab.course.anisimov.service.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.reliab.course.anisimov.entity.Bank;
import tech.reliab.course.anisimov.entity.CreditAccount;
import tech.reliab.course.anisimov.entity.PaymentAccount;
import tech.reliab.course.anisimov.entity.User;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;
import tech.reliab.course.anisimov.service.BankService;
import tech.reliab.course.anisimov.service.CreditAccountService;
import tech.reliab.course.anisimov.service.PaymentAccountService;
import tech.reliab.course.anisimov.service.UserService;

import java.math.BigDecimal;
import java.util.*;

final public class UserServiceImpl implements UserService {
    //region ===================== Properties ======================
    private final Map<String, User> userMap = new HashMap<>();

    //region ===================== DI ======================
    @NotNull public CreditAccountService creditAccountService;
    @NotNull public PaymentAccountService paymentAccountService;
    @NotNull public BankService bankService;

    //region ===================== UserServiceOverrides ======================
    @Override
    public @Nullable User create(@NotNull User user) throws UnuniqeIdException {
        this.calculateRating(user);
        return this.addUser(new User(user));
    }

    @Override
    public @Nullable User addUser(@NotNull User user) throws UnuniqeIdException {
        if (this.userMap.containsKey(user.getId())) { throw new UnuniqeIdException(user.getId()); }

        Bank bank = user.getBank();
        try {
            if (bank != null && this.bankService.addClient(bank.getId(), user)) {
                this.userMap.put(user.getId(), user);
                return user;
            }
        } catch (DoesNotExistException e) {
            return null;
        }

        return null;
    }

    @Override
    public @NotNull List<User> getAllUsers() {
        return new ArrayList<>(this.userMap.values());
    }

    @Override
    public @NotNull User getUserById(@NotNull String userId) throws DoesNotExistException {
        User user = this.userMap.get(userId);
        if (user == null) { throw new DoesNotExistException(userId); }

        return user;
    }

    @Override
    public @NotNull Boolean deleteUser(@NotNull String userId) throws DoesNotExistException {
        User user = this.getUserById(userId);

        for (PaymentAccount account : this.paymentAccountService.getUsersAccounts(userId)) {
            this.paymentAccountService.deleteAccount(account.getId());
        }
        for (CreditAccount account : this.creditAccountService.getUsersAccounts(userId)) {
            this.creditAccountService.deleteAccount(account.getId());
        }

        this.bankService.removeClient(user.getBank().getId(), userId);

        return true;
    }

    @Override
    public @NotNull List<User> getAllUsersByBankId(@NotNull String bankId) {
        return this.userMap.values()
                .stream()
                .filter(user -> Objects.equals(user.getBank().getId(), bankId))
                .toList();
    }

    @Override
    public @NotNull Boolean addCreditAccount(@NotNull String userId, @NotNull CreditAccount creditAccount) throws DoesNotExistException {
        User user = this.getUserById(userId);

        user.setCreditAccountsCount(user.getCreditAccountsCount() + 1);

        return true;
    }

    @Override
    public @NotNull Boolean removeCreditAccount(@NotNull String userId, @NotNull String accountId) throws DoesNotExistException {
        User user = this.getUserById(userId);

        if (user.getCreditAccountsCount() > 0) {
            user.setCreditAccountsCount(user.getCreditAccountsCount() - 1);
            return true;
        }

        return false;
    }

    @Override
    public @NotNull Boolean addPaymentAccount(@NotNull String userId, @NotNull PaymentAccount paymentAccount) throws DoesNotExistException {
        User user = this.getUserById(userId);

        user.setPaymentAccountsCount(user.getPaymentAccountsCount() + 1);

        return true;
    }

    @Override
    public @NotNull Boolean removePaymentAccount(@NotNull String userId, @NotNull String paymentId) throws DoesNotExistException {
        User user = this.getUserById(userId);

        if (user.getPaymentAccountsCount() > 0) {
            user.setCreditAccountsCount(user.getPaymentAccountsCount() - 1);
            return true;
        }

        return false;
    }

    @Override
    public void registerJob(@NotNull User user, @NotNull String jobAddress, double monthlyIncome) {
        user.setPlaceOfWork(jobAddress);
        user.setMonthlyIncome(monthlyIncome);
        calculateRating(user);
    }

    @Override
    public void calculateRating(User user) {
        int rate = 100;
        BigDecimal value = BigDecimal.valueOf(1000L);

        while (BigDecimal.valueOf(user.getMonthlyIncome()).compareTo(value) > 0) {
            rate += 100;
            value = value.add(BigDecimal.valueOf(1000L));
        }

        user.setLoanRating(rate);
    }

    @Override
    public @NotNull String stringRepresentation(@NotNull String userId) throws DoesNotExistException {
        User user = this.getUserById(userId);

        StringBuilder builder = new StringBuilder(user.toString());

        builder.append("Кредитные аккаунты:\n");
        for (CreditAccount account : this.creditAccountService.getUsersAccounts(userId)) {
            builder.append(creditAccountService.stringRepresentation(account.getId())).append("\n");
        }

        builder.append("Платежные аккаунты:\n");
        for (PaymentAccount account : this.paymentAccountService.getUsersAccounts(userId)) {
            builder.append(this.paymentAccountService.stringRepresentation(account.getId())).append("\n");
        }

        builder.append("\n");
        return builder.toString();
    }
}
