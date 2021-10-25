package com.cosmos.david.repository;

import com.cosmos.david.model.KLine;
import com.cosmos.david.model.KLineId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KLineRepository extends JpaRepository<KLine, KLineId> {
}
