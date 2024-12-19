package org.richardstallman.dvback.domain.user.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 1)
  private Long userId;

  @Column(nullable = false, unique = true)
  private String socialId;

  @Column(nullable = false)
  private String email;

  private String username;

  private String name;

  @Column(unique = true)
  private String nickname;

  @Column(name = "s3_profile_image_object_key")
  private String s3ProfileImageObjectKey;

  @Column private Boolean leave = false;

  @Enumerated(EnumType.STRING)
  private CommonConstants.Gender gender;

  @Column private LocalDate birthdate;

  //  @OneToOne(fetch = FetchType.EAGER)
  //  @JoinColumn(name = "job_id", nullable = false)
  //  private JobEntity job;

  public UserEntity(String socialId, String email) {
    this.socialId = socialId;
    this.email = email;
  }

  public UserEntity updatedUserEntity(
      String username,
      String name,
      String nickname,
      String s3ProfileImageObjectKey,
      LocalDate birthdate,
      CommonConstants.Gender gender) {
    return new UserEntity(
        this.userId,
        this.socialId,
        this.email,
        username,
        name,
        nickname,
        s3ProfileImageObjectKey,
        this.leave,
        gender,
        birthdate
        //            this.job
        );
  }
}
