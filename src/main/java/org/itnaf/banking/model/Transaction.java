package org.itnaf.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Transaction entity representing a financial transaction.
 * Tracks all account activity including purchases, deposits, and transfers.
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount account;

    private String description;

    private String category;

    private LocalDate date;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    public enum TransactionStatus {
        COMPLETED,
        PENDING
    }

    public Transaction(BankAccount account, String description, String category,
                       LocalDate date, BigDecimal amount, TransactionStatus status) {
        this.account = account;
        this.description = description;
        this.category = category;
        this.date = date;
        this.amount = amount;
        this.status = status;
    }
}
