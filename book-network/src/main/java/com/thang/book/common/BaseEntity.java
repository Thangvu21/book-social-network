package com.thang.book.common;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @LastModifiedBy
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    // tacs gia de noi bang
    // theo dõi ai đã cập nhật
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Integer createBy;

    //
    @LastModifiedBy
    @Column(insertable = false)
    private Integer lastModifiedBy;
}
