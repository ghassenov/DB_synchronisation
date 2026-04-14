package com.sales.bo.service;

import com.sales.bo.domain.ProductSale;
import com.sales.bo.domain.SaleSyncEvent;
import com.sales.bo.dto.SaleUpsertRequest;
import com.sales.bo.repository.ProductSaleRepository;
import com.sales.bo.repository.SaleSyncEventRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoSalesService {

    private final ProductSaleRepository productSaleRepository;
    private final SaleSyncEventRepository saleSyncEventRepository;

    public BoSalesService(ProductSaleRepository productSaleRepository, SaleSyncEventRepository saleSyncEventRepository) {
        this.productSaleRepository = productSaleRepository;
        this.saleSyncEventRepository = saleSyncEventRepository;
    }

    public List<ProductSale> getAllSales() {
        return productSaleRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public ProductSale createSale(SaleUpsertRequest request) {
        ProductSale sale = new ProductSale();
        applyRequest(sale, request);
        ProductSale saved = productSaleRepository.save(sale);
        saleSyncEventRepository.save(toUpsertEvent(saved));
        return saved;
    }

    @Transactional
    public ProductSale updateSale(Long id, SaleUpsertRequest request) {
        ProductSale sale = productSaleRepository.findById(id).orElseThrow();
        applyRequest(sale, request);
        ProductSale saved = productSaleRepository.save(sale);
        saleSyncEventRepository.save(toUpsertEvent(saved));
        return saved;
    }

    @Transactional
    public void deleteSale(Long id) {
        ProductSale sale = productSaleRepository.findById(id).orElseThrow();

        SaleSyncEvent deleteEvent = new SaleSyncEvent();
        deleteEvent.setOperation("DELETE");
        deleteEvent.setSaleId(sale.getId());
        saleSyncEventRepository.save(deleteEvent);

        productSaleRepository.delete(sale);
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

    private SaleSyncEvent toUpsertEvent(ProductSale sale) {
        SaleSyncEvent event = new SaleSyncEvent();
        event.setOperation("UPSERT");
        event.setSaleId(sale.getId());
        event.setDate(sale.getDate());
        event.setRegion(sale.getRegion());
        event.setProduct(sale.getProduct());
        event.setQty(sale.getQty());
        event.setCost(sale.getCost());
        event.setAmt(sale.getAmt());
        event.setTax(sale.getTax());
        event.setTotal(sale.getTotal());
        return event;
    }
}