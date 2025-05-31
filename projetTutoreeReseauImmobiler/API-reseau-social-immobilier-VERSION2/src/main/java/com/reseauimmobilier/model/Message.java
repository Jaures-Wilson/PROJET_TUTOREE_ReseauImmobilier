package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_message")
    private Integer  idMessage;

    @Column(name = "contenu", nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "lu", nullable = false)
    private Boolean lu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_expediteur", referencedColumnName = "id_utilisateur", nullable = false)
    private Utilisateur expediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destinataire", referencedColumnName = "id_utilisateur", nullable = false)
    private Utilisateur destinataire;

    // Constructeur personnalisé pour la création
    public Message(String contenu, Utilisateur expediteur, Utilisateur destinataire) {
        this.contenu = contenu;
        this.expediteur = expediteur;
        this.destinataire = destinataire;
        this.dateEnvoi = LocalDateTime.now();
        this.lu = false;
    }

    @PrePersist
    protected void onCreate() {
        if (dateEnvoi == null) {
            dateEnvoi = LocalDateTime.now();
        }
        if (lu == null) {
            lu = false;
        }
    }
}