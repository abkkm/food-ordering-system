package br.com.food.ordering.system.order.service.messaging.publisher.kafka;

import br.com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import br.com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import br.com.food.ordering.system.kafka.producer.service.KafkaProducer;
import br.com.food.ordering.system.domain.config.OrderServiceConfigData;
import br.com.food.ordering.system.domain.event.OrderPaidEvent;
import br.com.food.ordering.system.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import br.com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final KafkaMessageHelper orderKafkaMessageHelper;

    public PayOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                         OrderServiceConfigData orderServiceConfigData,
                                         KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer,
                                         KafkaMessageHelper orderKafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderKafkaMessageHelper = orderKafkaMessageHelper;
    }

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        try {
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel
                    = orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(domainEvent);

            kafkaProducer.send(
                    orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                    orderId,
                    restaurantApprovalRequestAvroModel,
                    orderKafkaMessageHelper
                            .getKafkaCallback(
                                    orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                                    restaurantApprovalRequestAvroModel,
                                    orderId,
                                    "RestaurantApprovalRequestAvroModel")
            );
            log.info("RestaurantApprovalRequestAvroModel sent to Kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalRequestAvroModel message to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
