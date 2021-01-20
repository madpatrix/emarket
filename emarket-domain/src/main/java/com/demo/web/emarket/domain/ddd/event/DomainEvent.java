package com.demo.web.emarket.domain.ddd.event;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class DomainEvent {

    private String eventVersion;
    private LocalDateTime eventOccuredOn;
    private String idUtilisateur;
    private String idObjet;
    private int numVersionObjet;
    private String typeObjet;

    public DomainEvent(String eventVersion, LocalDateTime eventOccuredOn, String idUtilisateur, String idObjet, int numVersionObjet, String typeObjet) {
        this.eventVersion = eventVersion;
        this.eventOccuredOn = eventOccuredOn;
        this.idUtilisateur = idUtilisateur;
        this.idObjet = idObjet;
        this.numVersionObjet = numVersionObjet;
        this.typeObjet = typeObjet;
    }

    public DomainEvent(String eventVersion, LocalDateTime eventOccuredOn, String idUtilisateur) {
        this.eventVersion = eventVersion;
        this.eventOccuredOn = eventOccuredOn;
        this.idUtilisateur = idUtilisateur;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public LocalDateTime getEventOccuredOn() {
        return eventOccuredOn;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public String getIdObjet() {
        return idObjet;
    }


    public int getNumVersionObjet() {
        return numVersionObjet;
    }

    public String getTypeObjet() {
        return typeObjet;
    }
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof DomainEvent)) return false;

        DomainEvent that = (DomainEvent) o;
        return Objects.equals(eventVersion, that.eventVersion) &&
                Objects.equals(eventOccuredOn, that.eventOccuredOn) &&
                Objects.equals(idUtilisateur, that.idUtilisateur) &&
                Objects.equals(idObjet, that.idObjet) &&
                numVersionObjet == that.numVersionObjet &&
                Objects.equals(typeObjet, that.typeObjet);
    }
}
