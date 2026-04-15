package com.sales.bo.service;

import com.sales.bo.domain.ProductSale;
import com.sales.bo.dto.SaleMessage;
import com.sales.bo.dto.SaleUpsertRequest;
import com.sales.bo.repository.ProductSaleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoSalesService {

    private final ProductSaleRepository productSaleRepository;
    private final SalePublisherService salePublisherService;
    private final String boId;

    public BoSalesService(
        ProductSaleRepository productSaleRepository,
        SalePublisherService salePublisherService,
        @Value("${bo.id}") String boId
    ) {
        this.productSaleRepository = productSaleRepository;
        this.salePublisherService = salePublisherService;
        this.boId = boId;
    }

    public List<ProductSale> getAllSales() {
        return productSaleRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public ProductSale createSale(SaleUpsertRequest request) {
        ProductSale sale = new ProductSale();
        applyRequest(sale, request);
        ProductSale saved = productSaleRepository.save(sale);
        publishUpsert(saved);
        return saved;
    }

    @Transactional
    public ProductSale updateSale(Long id, SaleUpsertRequest request) {
        ProductSale sale = productSaleRepository.findById(id).orElseThrow();
        applyRequest(sale, request);
        ProductSale saved = productSaleRepository.save(sale);
        publishUpsert(saved);
        return saved;
    }

    @Transactional
    public void deleteSale(Long id) {
        ProductSale sale = productSaleRepository.findById(id).orElseThrow();
        productSaleRepository.delete(sale);
        publishDelete(id);
    }

    private void applyRequest(ProductSale sale, SaleUpsertRequest request) {
        sale.setDate(request.getDate());
        sale.setRegion(request.getRegion());
        sale.setProduct(request.getProduct());
        sale.setQty(request.getQty());
        sale.setCost(request.getCost());
        sale.setAmt(request.getAmt());
        sale.setTax(request.getTax());
        sale.setTotal(request.getTotal());
    }

    private void publishUpsert(ProductSale sale) {
        SaleMessage message = new SaleMessage();
        message.setOperation("UPSERT");
        message.setBoId(boId);
        message.setLocalSaleId(sale.getId());
        message.setDate(sale.getDate());
        message.setRegion(sale.getRegion());
        message.setProduct(sale.getProduct());
        message.setQty(sale.getQty());
        message.setCost(sale.getCost());
        message.setAmt(sale.getAmt());
        message.setTax(sale.getTax());
        message.setTotal(sale.getTotal());
        salePublisherService.publishEvent(message);
    }

    private void publishDelete(Long localSaleId) {
        SaleMessage message = new SaleMessage();
        message.setOperation("DELETE");
        message.setBoId(boId);
        message.setLocalSaleId(localSaleId);
        salePublisherService.publishEvent(message);
    }
}