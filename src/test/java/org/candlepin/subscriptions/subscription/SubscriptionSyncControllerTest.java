/*
 * Copyright (c) 2021 Red Hat, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Red Hat trademarks are not licensed under GPLv3. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package org.candlepin.subscriptions.subscription;

import org.candlepin.subscriptions.db.SubscriptionRepository;
import org.candlepin.subscriptions.db.model.Subscription;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles({"worker", "test"})
class SubscriptionSyncControllerTest {

    private static final OffsetDateTime NOW = OffsetDateTime.now();

    @Autowired
    SubscriptionSyncController subject;

    @MockBean
    SubscriptionRepository subscriptionRepository;

    @Test
    void shouldCreateNewRecordOnQuantityChange() {
        Mockito.when(subscriptionRepository.findActiveSubscription(Mockito.anyString()))
            .thenReturn(Optional.of(createSubscription("123", "testsku", "456")));
        final org.candlepin.subscriptions.subscription.api.model.Subscription dto = createDto("456",
            10);
        subject.syncSubscription(dto);
        Mockito.verify(subscriptionRepository, Mockito.times(2))
            .save(Mockito.any(Subscription.class));
    }

    @Test
    void shouldUpdateRecordOnNoQuantityChange() {
        Mockito.when(subscriptionRepository.findActiveSubscription(Mockito.anyString()))
            .thenReturn(Optional.of(createSubscription("123", "testsku", "456")));
        final org.candlepin.subscriptions.subscription.api.model.Subscription dto = createDto("456",
            4);
        subject.syncSubscription(dto);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
            .save(Mockito.any(Subscription.class));
    }

    @Test
    void shouldCreateNewRecordOnNotFound() {
        Mockito.when(subscriptionRepository.findActiveSubscription(Mockito.anyString()))
            .thenReturn(Optional.empty());
        final org.candlepin.subscriptions.subscription.api.model.Subscription dto = createDto("456",
            10);
        subject.syncSubscription(dto);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
            .save(Mockito.any(Subscription.class));
    }

    private Subscription createSubscription(String orgId, String sku, String subId) {
        final Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subId);
        subscription.setOwnerId(orgId);
        subscription.setQuantity(4L);
        subscription.setSku(sku);
        subscription.setStartDate(NOW);
        subscription.setEndDate(NOW.plusDays(30));

        return subscription;
    }

    private org.candlepin.subscriptions.subscription.api.model.Subscription createDto(String subId,
        int quantity) {
        final org.candlepin.subscriptions.subscription.api.model.Subscription dto =
            new org.candlepin.subscriptions.subscription.api.model.Subscription();
        dto.setQuantity(quantity);
        dto.setId(Integer.valueOf(subId));
        dto.setSubscriptionNumber("123");
        dto.setEffectiveStartDate(NOW.toEpochSecond());
        dto.setEffectiveEndDate(NOW.plusDays(30).toEpochSecond());

        return dto;
    }
}