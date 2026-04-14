package com.sales.ho.controller;

import com.sales.ho.domain.ConsolidatedSale;
import com.sales.ho.repository.ConsolidatedSaleRepository;
import java.time.LocalDateTime;
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
public class SalesController {

    private final ConsolidatedSaleRepository consolidatedSaleRepository;

    public SalesController(ConsolidatedSaleRepository consolidatedSaleRepository) {
        this.consolidatedSaleRepository = consolidatedSaleRepository;
    }

    @GetMapping
    public List<ConsolidatedSale> getAllSales() {
        return consolidatedSaleRepository.findAllByOrderByReceivedAtDescIdDesc();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsolidatedSale createSale(@RequestBody ConsolidatedSale request) {
        request.setId(null);
        request.setReceivedAt(LocalDateTime.now());
        return consolidatedSaleRepository.save(request);
    }

    @PutMapping("/{id}")
    public ConsolidatedSale updateSale(@PathVariable Long id, @RequestBody ConsolidatedSale request) {
        ConsolidatedSale existing = consolidatedSaleRepository.findById(id).orElseThrow();
        existing.setBoId(request.getBoId());
        existing.setLocalSaleId(request.getLocalSaleId());
        existing.setDate(request.getDate());
        existing.setRegion(request.getRegion());
        existing.setProduct(request.getProduct());
        existing.setQty(request.getQty());
        existing.setCost(request.getCost());
        existing.setAmt(request.getAmt());
        existing.setTax(request.getTax());
        existing.setTotal(request.getTotal());
        existing.setReceivedAt(LocalDateTime.now());
        return consolidatedSaleRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSale(@PathVariable Long id) {
        consolidatedSaleRepository.deleteById(id);
    }
}
