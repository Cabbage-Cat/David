package com.cosmos.david.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@MappedSuperclass
@EqualsAndHashCode
public class KLineType implements Serializable {
    private String baseAsset;
    private String quoteAsset;
    private String interval;
}
