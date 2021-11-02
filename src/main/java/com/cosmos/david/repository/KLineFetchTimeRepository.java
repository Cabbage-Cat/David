package com.cosmos.david.repository;

import com.cosmos.david.model.KLineFetchTime;
import com.cosmos.david.model.KLineType;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;



@Repository
public interface KLineFetchTimeRepository extends JpaRepository<KLineFetchTime, KLineType> {
}
