package com.cosmos.david.repository;

import com.cosmos.david.model.KLine;
import com.cosmos.david.model.KLineId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface KLineRepository extends JpaRepository<KLine, KLineId> {
    @Query("select k from KLine k where k.kLineId.symbol = ?1 and k.kLineId.interval = ?2")
    List<KLine> findAllKLinesBySymbolAndInterval(String symbol, String interval);

    @Query("select k.kLineId from KLine as k where k.kLineId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    KLineId getWriteLockByKLineId(KLineId kLineId);

}
