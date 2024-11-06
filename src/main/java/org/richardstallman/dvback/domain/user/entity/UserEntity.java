package org.richardstallman.dvback.domain.user.entity;

import jakarta.persistence.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants;
import org.richardstallman.dvback.domain.job.entity.JobEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 1)
  private Long id;

  @Column(unique = true)
  private String socialId;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String nickname;

  @Column(name = "s3_profile_image_url")
  private String s3ProfileImageUrl;

  @Column private Boolean leave = false;

  @Enumerated(EnumType.STRING)
  private CommonConstants.Gender gender;

  @Column private Date birthdate;

  //  @OneToOne(fetch = FetchType.EAGER)
  //  @JoinColumn(name = "job_id", nullable = false)
  //  private JobEntity job;

  public UserEntity(
      String socialId,
      String email,
      String name,
      String nickname,
      String s3ProfileImageUrl,
      CommonConstants.Gender gender,
      JobEntity job) {
    this.socialId = socialId;
    this.email = email;
    this.name = name;
    this.nickname = nickname;
    this.s3ProfileImageUrl = s3ProfileImageUrl;
    this.leave = false;
    this.gender = gender;
    this.birthdate = new Date();
    //    this.job = job;
  }

  public UserEntity updatedUserEntity(
      String nickname, Date birthdate, CommonConstants.Gender gender) {
    return new UserEntity(
        this.id,
        this.socialId,
        this.email,
        this.name,
        nickname,
        this.s3ProfileImageUrl,
        this.leave,
        gender,
        birthdate
        //            this.job
        );
  }

  public UserEntity deactivate() {
    return new UserEntity(
        this.id, null, "withdrawn@example.com", "탈퇴한 회원", "탈퇴한 회원", null, true, null, null);
  }
}
