package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.ZonedDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractPojo {
    @CreationTimestamp
    private ZonedDateTime createdAt;
    @UpdateTimestamp
    private ZonedDateTime updatedAt;
    @Version
    private int version;
}
