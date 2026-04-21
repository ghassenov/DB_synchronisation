package com.sales.ho.service;

import com.sales.ho.domain.ConsolidatedSale;
import com.sales.ho.dto.SaleMessage;
import com.sales.ho.repository.ConsolidatedSaleRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesMessageListener {

    private static final Logger log = LoggerFactory.getLogger(SalesMessageListener.class);

    private final ConsolidatedSaleRepository consolidatedSaleRepository;

    public SalesMessageListener(ConsolidatedSaleRepository consolidatedSaleRepository) {
        this.consolidatedSaleRepository = consolidatedSaleRepository;
    }

    @RabbitListener(queues = "${rabbitmq.queue}")
    @Transactional
    public void consumeSale(SaleMessage message) {
        if ("DELETE".equalsIgnoreCase(message.getOperation())) {
            consolidatedSaleRepository.deleteByBoIdAndLocalSaleId(message.getBoId(), message.getLocalSaleId());
            log.info("Deleted consolidated sale: boId={}, localSaleId={}", message.getBoId(), message.getLocalSaleId());
            return;
        }

        ConsolidatedSale consolidatedSale = consolidatedSaleRepository
            .findByBoIdAndLocalSaleId(message.getBoId(), message.getLocalSaleId())
            .orElseGet(ConsolidatedSale::new);

        if (consolidatedSale.getId() == null) {
            consolidatedSale.setBoId(message.getBoId());
            consolidatedSale.setLocalSaleId(message.getLocalSaleId());
        }

        mapFields(consolidatedSale, message);
        consolidatedSaleRepository.save(consolidatedSale);
        log.info("Upserted sale: boId={}, localSaleId={}", message.getBoId(), message.getLocalSaleId());
    }

    private void mapFields(ConsolidatedSale sale, SaleMessage message) {
        sale.setDate(message.getDate());
        sale.setRegion(message.getRegion());
        sale.setProduct(message.getProduct());
        sale.setQty(message.getQty());
        sale.setCost(message.getCost());
        sale.setAmt(message.getAmt());
        sale.setTax(message.getTax());
        sale.setTotal(message.getTotal());
        sale.setReceivedAt(LocalDateTime.now());
    }
}
