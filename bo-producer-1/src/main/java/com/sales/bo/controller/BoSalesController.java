package com.sales.bo.controller;

import com.sales.bo.domain.ProductSale;
import com.sales.bo.dto.SaleUpsertRequest;
import com.sales.bo.service.BoSalesService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
public class BoSalesController {

    private final BoSalesService boSalesService;

    public BoSalesController(BoSalesService boSalesService) {
        this.boSalesService = boSalesService;
    }

    @GetMapping
    public List<ProductSale> getAllSales() {
        return boSalesService.getAllSales();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductSale createSale(@RequestBody SaleUpsertRequest request) {
        return boSalesService.createSale(request);
    }

    @PutMapping("/{id}")
    public ProductSale updateSale(@PathVariable Long id, @RequestBody SaleUpsertRequest request) {
        return boSalesService.updateSale(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSale(@PathVariable Long id) {
        boSalesService.deleteSale(id);
    }
}