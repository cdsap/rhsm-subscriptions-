/*
 * Copyright (c) 2020 Red Hat, Inc.
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
package org.candlepin.subscriptions.db.model;

import org.candlepin.subscriptions.json.Event;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;

/**
 * DB entity for an event record.
 *
 * An event record consists of an ID and a JSON document with the event data.
 */
@Entity
@Table(name = "events")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRecord {

    /**
     * Create a new EventRecord from a given Event.
     *
     * Generates a new random UUID if the Event does not have a UUID.
     *
     * @param event the event object
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public EventRecord(Event event) {
        Objects.requireNonNull(event, "event must not be null");
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }
        this.id = event.getEventId();
        this.event = event;
        this.accountNumber = event.getAccountNumber();
        this.eventType = event.getEventType();
        this.eventSource = event.getEventSource();
        this.instanceId = event.getInstanceId();
        this.timestamp = event.getTimestamp();
    }

    @Id
    private UUID id;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "event_source")
    private String eventSource;

    @Column(name = "instance_id")
    private String instanceId;

    private OffsetDateTime timestamp;

    @Valid
    @Column(name = "data")
    @Convert(converter = EventRecordConverter.class)
    private Event event;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventRecord that = (EventRecord) o;
        return Objects.equals(accountNumber, that.accountNumber) &&
            Objects.equals(eventType, that.eventType) &&
            Objects.equals(eventSource, that.eventSource) &&
            Objects.equals(instanceId, that.instanceId) &&
            Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, eventType, eventSource, instanceId, timestamp);
    }
}
