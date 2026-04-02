package ru.complaints.pair.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "refresh_sessions")
public class RefreshSession {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private UserCredential userCredential;

    @Column(name = "user_id")
    private UUID userId;

    private String refreshTokenHash;

    private String fingerprint;

    private Date expirationDate;
}
