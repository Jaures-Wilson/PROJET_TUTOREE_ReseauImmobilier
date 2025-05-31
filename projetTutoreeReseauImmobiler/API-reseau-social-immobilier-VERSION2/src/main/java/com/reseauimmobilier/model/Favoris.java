package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "favoris")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FavorisId.class)
public class Favoris {

    @Id
    @Column(name = "id_acquereur")
    private Integer  idAcquereur;

    @Id
    @Column(name = "id_annonce")
    private Integer  idAnnonce;

    @Column(name = "date_ajout", nullable = false)
    private LocalDateTime dateAjout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_acquereur",referencedColumnName = "id_acquereur", insertable = false, updatable = false)
    private Acquereur acquereur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_annonce",referencedColumnName = "id_annonce", insertable = false, updatable = false)
    private Annonce annonce;

    @PrePersist
    protected void onCreate() {
        this.dateAjout = LocalDateTime.now();
    }

    // Constructeur utilitaire
    public Favoris(Integer  idAcquereur, Integer  idAnnonce) {
        this.idAcquereur = idAcquereur;
        this.idAnnonce = idAnnonce;
        this.dateAjout = LocalDateTime.now();
    }
}