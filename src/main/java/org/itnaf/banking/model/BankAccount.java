package org.itnaf.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Bank Account entity representing a user's financial account.
 * Supports checking, savings, and investment account types.
 */
@Entity
@Table(name = "bank_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "account_number")
    private String accountNumber;

    private BigDecimal balance;

    private BigDecimal available;

    private String institution;

    @Column(name = "apy")
    private Double apy; // Annual Percentage Yield for savings accounts

    @Column(name = "change_percent")
    private Double changePercent; // Percentage change for investment accounts

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    public enum AccountType {
        CHECKING,
        SAVINGS,
        INVESTMENT
    }

    public BankAccount(User user, String name, AccountType type, String accountNumber,
                       BigDecimal balance, BigDecimal available, String institution) {
        this.user = user;
        this.name = name;
        this.type = type;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.available = available;
        this.institution = institution;
    }
}
