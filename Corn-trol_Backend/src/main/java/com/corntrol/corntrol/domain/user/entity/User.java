package com.corntrol.corntrol.domain.user.entity;

import com.corntrol.corntrol.domain.record.entity.Record;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String refreshToken;

    @Column(nullable = false)
    private String nickname;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ProfileImage profileImage = ProfileImage.SPROUT;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Record> records = new ArrayList<>();

    public void updateProfile(String nickname, String profileImage) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (profileImage != null && !profileImage.isBlank()) {
            this.profileImage = ProfileImage.valueOf(profileImage);
        }
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
