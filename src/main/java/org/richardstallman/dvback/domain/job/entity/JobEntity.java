package org.richardstallman.dvback.domain.job.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jobs")
public class JobEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_seq")
  @SequenceGenerator(name = "job_seq", sequenceName = "job_id_seq", allocationSize = 1)
  private Long jobId;

  @NotNull(message = "Job Name is required") private String jobName;

  @NotNull(message = "Job Name Korean is required") private String jobNameKorean;

  @Column(columnDefinition = "TEXT")
  @NotNull(message = "Job Description is required") private String jobDescription;
}
