package br.com.food.ordering.system.payment.service.domain.valueobject;

import br.com.food.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
