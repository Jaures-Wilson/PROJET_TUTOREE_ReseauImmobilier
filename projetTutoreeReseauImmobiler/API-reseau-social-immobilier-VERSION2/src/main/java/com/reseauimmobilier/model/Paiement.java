package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "paiement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement")
    private Integer  idPaiement;

    @Column(name = "montant", nullable = false)
    private Integer montant;

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "moyen_paiement", nullable = false)
    private MoyenPaiement moyenPaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_contrat", nullable = false)
    private TypeContrat typeContrat;

    @Lob
    @Column(name = "capture_photo_preuve_paiement", nullable = false)
    private byte[] capturePhotoPreuvePaiement;

    @Column(name = "lu", nullable = false)
    private Boolean lu = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_annonce",referencedColumnName = "id_annonce", nullable = false)
    private Annonce annonce;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_acquereur",referencedColumnName = "id_acquereur", nullable = false)
    private Acquereur acquereur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_annonceur",referencedColumnName = "id_annonceur", nullable = false)
    private Annonceur annonceur;

    public enum MoyenPaiement {
        CARTE_UBA,
        OM,
        MoMo
    }

    public enum StatutPaiement {
        CONFIRMEE,
        EN_ATTENTE,
        REFUSE
    }

    public enum TypeContrat {
        PROMESSE_VENTE,
        VENTE
    }

    // Méthodes métier
    public boolean estConfirme() {
        return StatutPaiement.CONFIRMEE.equals(this.statut);
    }

    public boolean estEnAttente() {
        return StatutPaiement.EN_ATTENTE.equals(this.statut);
    }

    public boolean estRefuse() {
        return StatutPaiement.REFUSE.equals(this.statut);
    }

    public void marquerCommeLu() {
        this.lu = true;
    }

    public void confirmerPaiement() {
        this.statut = StatutPaiement.CONFIRMEE;
        this.lu = true;
    }

    public void refuserPaiement() {
        this.statut = StatutPaiement.REFUSE;
        this.lu = true;
    }

    public String genererReferencePaiement() {
        return "PAY-" + this.idPaiement + "-" + this.datePaiement.toLocalDate().toString().replace("-", "");
    }
}