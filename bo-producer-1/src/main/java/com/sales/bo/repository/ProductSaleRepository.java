package com.sales.bo.repository;

import com.sales.bo.domain.ProductSale;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSaleRepository extends JpaRepository<ProductSale, Long> {

    List<ProductSale> findAllByOrderByIdDesc();
}
