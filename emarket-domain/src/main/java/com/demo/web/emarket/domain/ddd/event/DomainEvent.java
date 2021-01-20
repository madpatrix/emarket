package com.demo.web.emarket.domain.ddd.event;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class DomainEvent {

    private String eventVersion;
    private LocalDateTime eventOccuredOn;

    public DomainEvent(String eventVersion, LocalDateTime eventOccuredOn) {
        this.eventVersion = eventVersion;
        this.eventOccuredOn = eventOccuredOn;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public LocalDateTime getEventOccuredOn() {
        return eventOccuredOn;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof DomainEvent)) return false;

        DomainEvent that = (DomainEvent) o;
        return Objects.equals(eventVersion, that.eventVersion) &&
                Objects.equals(eventOccuredOn, that.eventOccuredOn);
    }
}
