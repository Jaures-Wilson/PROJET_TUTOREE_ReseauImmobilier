package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "annonce")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Annonce {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_annonce")
    private Integer  idAnnonce;
    
    @Column(name = "titre", nullable = false, columnDefinition = "TEXT")
    private String titre;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "prix_mensuel", nullable = false)
    private Integer prixMensuel;
    
    @Column(name = "localisation", nullable = false, columnDefinition = "TEXT")
    private String localisation;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.DISPONIBLE;
    
    @Column(name = "date_publication", nullable = false)
    private LocalDateTime datePublication = LocalDateTime.now();
    
    @Column(name = "tarif_reservation")
    private Integer tarifReservation;
    
    @Column(name = "nombre_vue", nullable = false)
    private Integer nombreVue = 0;
    
    @Column(name = "nombre_favori", nullable = false)
    private Integer nombreFavori = 0;
    
    @Column(name = "nombre_visite", nullable = false)
    private Integer nombreVisite = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_annonceur", referencedColumnName = "id_utilisateur", nullable = false)
    private Annonceur annonceur;
    
    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Photo> photos;
    
    @OneToOne(mappedBy = "annonce", cascade = CascadeType.ALL)
    private Contrat contrat;
    
    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL)
    private List<Visite> visites;
    
    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL)
    private List<Paiement> paiements;
    
    public enum Status {
        DISPONIBLE, RÉSERVÉ, VENDU, REFUSE
    }
    
    // Méthodes utilitaires
    public void incrementerVues() {
        this.nombreVue++;
    }
    
    public void incrementerFavoris() {
        this.nombreFavori++;
    }
    
    public void decrementerFavoris() {
        if (this.nombreFavori > 0) {
            this.nombreFavori--;
        }
    }
    
    public void incrementerVisites() {
        this.nombreVisite++;
    }
    
    public boolean estDisponible() {
        return this.status == Status.DISPONIBLE;
    }
    
    public boolean estReserve() {
        return this.status == Status.RÉSERVÉ;
    }
    
    public boolean estVendu() {
        return this.status == Status.VENDU;
    }
    
    public boolean estRefuser() {
        return this.status == Status.REFUSE;
    }
}