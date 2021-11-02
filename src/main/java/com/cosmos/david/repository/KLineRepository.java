package com.cosmos.david.repository;

import com.cosmos.david.model.KLine;
import com.cosmos.david.model.KLineId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KLineRepository extends JpaRepository<KLine, KLineId> {
    @Query("select k from KLine k where k.kLineId.symbol = ?1 and k.kLineId.interval = ?2 order by k.kLineId.startTime asc")
    List<KLine> findAllKLinesBySymbolAndInterval(String symbol, String interval);

    List<KLine> findAllKLinesBySymbolAndIntervalWithEMANotInit(String symbol, String interval);
}
