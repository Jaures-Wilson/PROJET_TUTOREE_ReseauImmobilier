package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "abonnement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Abonnement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abonnement")
    private Integer idAbonnement;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "duree_type", nullable = false)
    private DureeType dureeType;
    
    @Column(name = "montant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;
    
    @Column(name = "date_debut")
    private LocalDate dateDebut;
    
    @Column(name = "date_fin")
    private LocalDate dateFin;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutAbonnement statut = StatutAbonnement.EN_ATTENTE;
    
    @Lob
    @Column(name = "capture_preuve_paiement", nullable = false)
    private byte[] capturePreuvePaiement;
    
    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande = LocalDateTime.now();
    
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
    
    @Column(name = "motif_refus")
    private String motifRefus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_acquereur", nullable = false)
    private Acquereur acquereur;
    
    // Énumérations
    public enum DureeType {
        MENSUEL, ANNUEL
    }
    
    public enum StatutAbonnement {
        EN_ATTENTE, ACTIF, EXPIRE, REFUSE
    }
    
    // Méthodes utilitaires
    public boolean isActif() {
        return statut == StatutAbonnement.ACTIF && 
               dateDebut != null && 
               dateFin != null && 
               LocalDate.now().isBefore(dateFin.plusDays(1));
    }
    
    public boolean isExpire() {
        return statut == StatutAbonnement.ACTIF && 
               dateFin != null && 
               LocalDate.now().isAfter(dateFin);
    }
}