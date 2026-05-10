package com.luzonni.cashflow.features.settings.domain;

import com.luzonni.cashflow.features.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "user_settings")
public class Settings {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 16)
    private String theme = "system";

    @Column(nullable = false, length = 16)
    private String locale = "en-US";

    @Column(nullable = false, length = 8)
    private String currency = "USD";

}
