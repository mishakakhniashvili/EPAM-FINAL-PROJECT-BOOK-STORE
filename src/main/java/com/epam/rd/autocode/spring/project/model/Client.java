package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CLIENTS")
public class Client extends User {
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean blocked = false;
    public Client() {
        super();
    }

    public Client(Long id, String email, String password, String name, BigDecimal balance) {
        super(id, email, password, name);
        this.balance = balance;
    }


    @Column(name = "BALANCE", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public boolean isBlocked() {
        return blocked;
    }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}
