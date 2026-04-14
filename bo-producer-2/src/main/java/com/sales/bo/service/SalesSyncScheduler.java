package com.sales.bo.service;

import com.sales.bo.domain.SaleSyncEvent;
import com.sales.bo.dto.SaleMessage;
import com.sales.bo.repository.SaleSyncEventRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SalesSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(SalesSyncScheduler.class);

    private final SaleSyncEventRepository saleSyncEventRepository;
    private final SalePublisherService salePublisherService;
    private final String boId;

    public SalesSyncScheduler(
        SaleSyncEventRepository saleSyncEventRepository,
        SalePublisherService salePublisherService,
        @Value("${bo.id}") String boId
    ) {
        this.saleSyncEventRepository = saleSyncEventRepository;
        this.salePublisherService = salePublisherService;
        this.boId = boId;
    }

    @Scheduled(fixedDelayString = "${sync.interval.ms}")
    public void syncPendingEvents() {
        List<SaleSyncEvent> pendingEvents = saleSyncEventRepository.findTop100BySyncedFalseOrderByIdAsc();
        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Found {} pending sync event(s) for {}", pendingEvents.size(), boId);

        for (SaleSyncEvent event : pendingEvents) {
            try {
                SaleMessage message = toMessage(event);
                salePublisherService.publishEvent(message);
                event.setSynced(true);
                saleSyncEventRepository.save(event);
                log.info("Published {} event id={} for sale id={} ({})", event.getOperation(), event.getId(), event.getSaleId(), boId);
            } catch (Exception ex) {
                log.warn("Failed to publish event id={} for {}. It will be retried.", event.getId(), boId, ex);
            }
        }
    }

    private SaleMessage toMessage(SaleSyncEvent event) {
        SaleMessage message = new SaleMessage();
        message.setOperation(event.getOperation());
        message.setBoId(boId);
        message.setLocalSaleId(event.getSaleId());
        message.setDate(event.getDate());
        message.setRegion(event.getRegion());
        message.setProduct(event.getProduct());
        message.setQty(event.getQty());
        message.setCost(event.getCost());
        message.setAmt(event.getAmt());
        message.setTax(event.getTax());
        message.setTotal(event.getTotal());
        return message;
    }
}
