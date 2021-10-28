package com.cosmos.david.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KLineFetchTime {
    @EmbeddedId
    private KLineType kLineType;
    private Instant lastFetchTime;
}
