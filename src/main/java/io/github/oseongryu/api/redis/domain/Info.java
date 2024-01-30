package io.github.oseongryu.api.redis.domain;

import io.github.oseongryu.api.common.entity.RedisEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="INFO")
public class Info extends RedisEntity {
    @Id
    @Column(length = 4000)
    private String key;
    @Column(precision = 15)
    private Long keyLength;
    private String keyType;
}

