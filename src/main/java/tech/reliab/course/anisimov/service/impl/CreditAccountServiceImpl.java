package tech.reliab.course.anisimov.service.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.reliab.course.anisimov.entity.CreditAccount;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.IssuingCreditException;
import tech.reliab.course.anisimov.exception.NotEnoughMoneyException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;
import tech.reliab.course.anisimov.service.BankService;
import tech.reliab.course.anisimov.service.CreditAccountService;
import tech.reliab.course.anisimov.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final public class CreditAccountServiceImpl implements CreditAccountService {
    //region ===================== Properties ======================
    private final Map<String, CreditAccount> creditAccountMap = new HashMap<>();

    //region ===================== DI ======================
    @NotNull public BankService bankService;
    @NotNull public UserService userService;

    //region ===================== CreditAccountService implementation ======================
    @Override
    public @Nullable CreditAccount create(@NotNull CreditAccount creditAccount) throws UnuniqeIdException {
        try {
            if (this.bankService.isCreditAllowed(creditAccount.getBank().getId(), creditAccount, creditAccount.getEmployee())) {
                return this.addAccount(new CreditAccount(creditAccount));
            } else {
                return null;
            }
        } catch (DoesNotExistException | IssuingCreditException e) {
            return null;
        }
    }

    @Override
    public @Nullable CreditAccount addAccount(@NotNull CreditAccount account) throws UnuniqeIdException {
        if (this.creditAccountMap.containsKey(account.getId())) {
            throw new UnuniqeIdException(account.getId());
        }
        try {
            if (this.userService.addCreditAccount(account.getCustomer().getId(), account)) {
                this.creditAccountMap.put(account.getId(), account);
                return account;
            }
        } catch (DoesNotExistException e) {
            return null;
        }

        return null;
    }

    @Override
    public @NotNull CreditAccount getAccountById(@NotNull String accountId) throws DoesNotExistException {
        CreditAccount account = this.creditAccountMap.get(accountId);
        if (account == null) { throw new DoesNotExistException(accountId); }

        return account;
    }

    @Override
    public @NotNull Boolean deleteAccount(@NotNull String accountId) throws DoesNotExistException {
        CreditAccount account = this.getAccountById(accountId);

        if (this.userService.removeCreditAccount(account.getCustomer().getId(), accountId)) {
            return this.creditAccountMap.remove(accountId) != null;
        }

        return false;
    }

    @Override
    public @NotNull List<CreditAccount> getUsersAccounts(@NotNull String userId) {
        return this.creditAccountMap.values()
                .stream()
                .filter(creditAccount -> Objects.equals(creditAccount.getCustomer().getId(), userId))
                .toList();
    }

    @Override
    public void executeMonthlyPayment(@NotNull CreditAccount creditAccount) throws NotEnoughMoneyException {
        if (creditAccount.getLoanAmount() > 0) {
            double monthlyPayment = creditAccount.getMonthlyPayment();
            double paymentAccountCash = creditAccount.getPaymentAccount().getTotalCash();


            if (paymentAccountCash >= monthlyPayment) {
                creditAccount.getPaymentAccount().setTotalCash(paymentAccountCash - monthlyPayment);
                creditAccount.setLoanAmount(creditAccount.getLoanAmount() - monthlyPayment);
            } else {
                throw new NotEnoughMoneyException();
            }
        }
    }

    @Override
    public @NotNull String stringRepresentation(@NotNull String accountId) throws DoesNotExistException {
        CreditAccount creditAccount = this.getAccountById(accountId);

        return creditAccount.toString();
    }
}
