package org.richardstallman.dvback.domain.point.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.PointTransactionType;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "point_transactions")
public class PointTransactionEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "point_transaction_seq")
  @SequenceGenerator(
      name = "point_transaction_seq",
      sequenceName = "point_transaction_id_seq",
      allocationSize = 1)
  private Long pointTransactionId;

  // 유저 구현 시 연결 필요
  @JoinColumn(name = "user_id", nullable = false)
  private Long userId;

  @NotNull(message = "Point Amount is required") private int amount;

  @NotNull(message = "Point Transaction Type is required") @Enumerated(EnumType.STRING)
  private PointTransactionType pointTransactionType;

  private String description;
}
