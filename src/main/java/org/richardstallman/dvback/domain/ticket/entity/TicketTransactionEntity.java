package org.richardstallman.dvback.domain.ticket.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket_transactions")
public class TicketTransactionEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_transaction_seq")
  @SequenceGenerator(
      name = "ticket_transaction_seq",
      sequenceName = "ticket_transaction_id_seq",
      allocationSize = 1)
  private Long ticketTransactionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  private int amount;

  @Enumerated(EnumType.STRING)
  private TicketTransactionType ticketTransactionType;

  @Enumerated(EnumType.STRING)
  private TicketTransactionMethod ticketTransactionMethod;

  @Enumerated(EnumType.STRING)
  private InterviewMode interviewMode;

  @Enumerated(EnumType.STRING)
  private InterviewAssetType interviewAssetType;

  private String description;
  private LocalDateTime generatedAt;
}
