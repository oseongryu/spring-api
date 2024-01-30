package io.github.oseongryu.api.common.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class RedisEntity implements Serializable{

    @CreatedBy
    @Column(updatable = false)
    private String instId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime instDtm;

    @LastModifiedBy
    private String mdfId;

    @LastModifiedDate
    private LocalDateTime mdfDtm;
}
