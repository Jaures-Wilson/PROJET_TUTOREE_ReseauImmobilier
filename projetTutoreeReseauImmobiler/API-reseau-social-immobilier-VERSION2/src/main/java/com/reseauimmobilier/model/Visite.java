package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "visite")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Visite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_visite")
    private Integer  idVisite;

    @Column(name = "date_visite", nullable = false)
    private LocalDate dateVisite;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutVisite statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_annonce",referencedColumnName = "id_annonce", nullable = false)
    private Annonce annonce;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_acquereur",referencedColumnName = "id_acquereur", nullable = false)
    private Acquereur acquereur;

    public enum StatutVisite {
        DEMANDEE,
        CONFIRMEE,
        ANNULEE,
        EFFECTUEE
    }

    // Constructeur pour créer une nouvelle visite
    public Visite(LocalDate dateVisite, Annonce annonce, Acquereur acquereur) {
        this.dateVisite = dateVisite;
        this.annonce = annonce;
        this.acquereur = acquereur;
        this.statut = StatutVisite.DEMANDEE;
    }
}