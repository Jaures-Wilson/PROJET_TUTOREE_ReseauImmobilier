package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Integer idNotification;

    @Column(name = "date_creation",nullable = false)
    private LocalDateTime dateCreation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type",nullable = false)
    private Type type;

    @Column(name = "contenu", nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "lu", nullable = false)
    private Boolean lu = false;

    @ManyToOne
    @JoinColumn(name = "id_expediteur",referencedColumnName = "id_utilisateur", nullable = false)
    private Utilisateur expediteur;

    @ManyToOne
    @JoinColumn(name = "id_destinataire",referencedColumnName = "id_utilisateur", nullable = false)
    private Utilisateur destinataire;

    public enum Type {
        CONTRAT,
        PAIEMENT,
        VISITE,
        CONVOITISE,
        MESSAGE
    }

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (lu == null) {
            lu = false;
        }
    }
}