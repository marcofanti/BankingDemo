package org.itnaf.banking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Recipient entity representing a saved transfer recipient.
 * Users can save frequently used recipients for quick transfers.
 */
@Entity
@Table(name = "recipients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Recipient name is required")
    private String name;

    @NotBlank(message = "Account number is required")
    @Column(name = "account_number")
    private String accountNumber;

    private String nickname; // Optional friendly name

    public Recipient(User user, String name, String accountNumber, String nickname) {
        this.user = user;
        this.name = name;
        this.accountNumber = accountNumber;
        this.nickname = nickname;
    }
}
