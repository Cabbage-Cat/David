package com.cosmos.david.repository;

import com.cosmos.david.model.KLineFetchTime;
import com.cosmos.david.model.KLineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface KLineFetchTimeRepository extends JpaRepository<KLineFetchTime, KLineType> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<KLineFetchTime> findById(KLineType kLineType);
}
