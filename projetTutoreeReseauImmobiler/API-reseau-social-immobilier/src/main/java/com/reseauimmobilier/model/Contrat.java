package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contrat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrat")
    private Integer  idContrat;

    @Column(name = "date_signature",nullable = false)
    private LocalDateTime dateSignature = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeContrat type;
    
    @Column(name = "date_debut_contrat")
    private LocalDateTime dateDebutContrat;
    
    @Column(name = "date_fin_contrat")
    private LocalDateTime dateFinContrat;

    @Column(name = "precision_en_surplus", columnDefinition = "TEXT")
    private String precisionEnSurplus;

    @Column(name = "decision_acquereur", nullable = false)
    private Boolean decisionAcquereur = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_annonce", referencedColumnName = "id_annonce", nullable = false)
    private Annonce annonce;

    @ManyToMany
    @JoinTable(
        name = "signataire",
        joinColumns = @JoinColumn(name = "id_contrat"),
        inverseJoinColumns = @JoinColumn(name = "id_utilisateur")
    )
    @Column(name = "signataires")
    private List<Utilisateur> signataires;

    public enum TypeContrat {
        PROMESSE_VENTE,
        VENTE
    }

    // Méthodes métier
    public void ajouterSignataire(Utilisateur utilisateur) {
        if (signataires != null && !signataires.contains(utilisateur)) {
            signataires.add(utilisateur);
        }
    }

    public boolean estValide() {
        return decisionAcquereur != null && decisionAcquereur && 
               signataires != null && !signataires.isEmpty();
    }

    public boolean estSigne() {
        return signataires != null && signataires.size() >= 2; // Au moins acquéreur et annonceur
    }
}