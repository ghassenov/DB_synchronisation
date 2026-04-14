package com.sales.bo.repository;

import com.sales.bo.domain.SaleSyncEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleSyncEventRepository extends JpaRepository<SaleSyncEvent, Long> {

    List<SaleSyncEvent> findTop100BySyncedFalseOrderByIdAsc();
}