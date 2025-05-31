package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "plainte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plainte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plainte")
    private Integer  idPlainte;

    @Column(name = "contenu", nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_plainte", nullable = false)
    private LocalDateTime datePlainte;

    @Column(name = "statut", nullable = false)
    private String statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_acquereur",referencedColumnName = "id_acquereur", nullable = false)
    private Acquereur acquereur;

    // Constructeur personnalisé pour la création
    public Plainte(String contenu, Acquereur acquereur) {
        this.contenu = contenu;
        this.acquereur = acquereur;
        this.datePlainte = LocalDateTime.now();
        this.statut = "NOUVELLE";
    }

    @PrePersist
    protected void onCreate() {
        if (datePlainte == null) {
            datePlainte = LocalDateTime.now();
        }
        if (statut == null) {
            statut = "NOUVELLE";
        }
    }
}