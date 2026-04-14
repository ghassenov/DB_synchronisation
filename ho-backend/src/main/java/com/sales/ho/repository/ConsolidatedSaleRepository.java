package com.sales.ho.repository;

import com.sales.ho.domain.ConsolidatedSale;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsolidatedSaleRepository extends JpaRepository<ConsolidatedSale, Long> {

    boolean existsByBoIdAndLocalSaleId(String boId, Long localSaleId);

    Optional<ConsolidatedSale> findByBoIdAndLocalSaleId(String boId, Long localSaleId);

    void deleteByBoIdAndLocalSaleId(String boId, Long localSaleId);

    List<ConsolidatedSale> findAllByOrderByReceivedAtDescIdDesc();
}
