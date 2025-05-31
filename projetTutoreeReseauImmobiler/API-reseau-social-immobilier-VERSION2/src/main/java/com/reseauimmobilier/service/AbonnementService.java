package com.reseauimmobilier.service;

import com.reseauimmobilier.model.Abonnement;
import com.reseauimmobilier.model.Acquereur;
import com.reseauimmobilier.model.Annonceur;
import com.reseauimmobilier.repository.AbonnementRepository;
import com.reseauimmobilier.repository.AcquereurRepository;
import com.reseauimmobilier.repository.AnnonceurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AbonnementService {
    
    @Autowired
    private AbonnementRepository abonnementRepository;
    
    @Autowired
    private AcquereurRepository acquereurRepository;
    
    @Autowired
    private AnnonceurRepository annonceurRepository;
    
    // Créer une demande d'abonnement
    public Abonnement creerDemandeAbonnement(Integer idAcquereur, 
                                           Abonnement.DureeType dureeType, 
                                           byte[] capturePreuve) {
        Optional<Acquereur> acquereurOpt = acquereurRepository.findById(idAcquereur);
        if (!acquereurOpt.isPresent()) {
            throw new RuntimeException("Acquéreur non trouvé");
        }
        
        // Vérifier qu'il n'a pas déjà un abonnement actif
        if (hasAbonnementActif(idAcquereur)) {
            throw new RuntimeException("Vous avez déjà un abonnement actif");
        }
        
        Abonnement abonnement = new Abonnement();
        abonnement.setAcquereur(acquereurOpt.get());
        abonnement.setDureeType(dureeType);
        abonnement.setMontant(calculerMontant(dureeType));
        abonnement.setCapturePreuvePaiement(capturePreuve);
        abonnement.setStatut(Abonnement.StatutAbonnement.EN_ATTENTE);
        
        return abonnementRepository.save(abonnement);
    }
    
    // Calculer le montant selon la durée
    private BigDecimal calculerMontant(Abonnement.DureeType dureeType) {
        return dureeType == Abonnement.DureeType.MENSUEL ? 
               new BigDecimal("5000.00") : new BigDecimal("50000.00");
    }
    
    // Valider un abonnement
    public Abonnement validerAbonnement(Integer idAbonnement) {
        Optional<Abonnement> abonnementOpt = abonnementRepository.findById(idAbonnement);
        if (!abonnementOpt.isPresent()) {
            throw new RuntimeException("Abonnement non trouvé");
        }
        
        Abonnement abonnement = abonnementOpt.get();
        if (abonnement.getStatut() != Abonnement.StatutAbonnement.EN_ATTENTE) {
            throw new RuntimeException("Cet abonnement ne peut plus être validé");
        }
        
        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = abonnement.getDureeType() == Abonnement.DureeType.MENSUEL ?
                           dateDebut.plusMonths(1) : dateDebut.plusYears(1);
        
        abonnement.setStatut(Abonnement.StatutAbonnement.ACTIF);
        abonnement.setDateDebut(dateDebut);
        abonnement.setDateFin(dateFin);
        abonnement.setDateValidation(LocalDateTime.now());
        
        abonnementRepository.save(abonnement);
        
        // Créer ou mettre à jour le compte annonceur
        creerOuMettreAJourAnnonceur(abonnement);
        
        return abonnement;
    }
    
    // Refuser un abonnement
    public Abonnement refuserAbonnement(Integer idAbonnement, String motifRefus) {
        Optional<Abonnement> abonnementOpt = abonnementRepository.findById(idAbonnement);
        if (!abonnementOpt.isPresent()) {
            throw new RuntimeException("Abonnement non trouvé");
        }
        
        Abonnement abonnement = abonnementOpt.get();
        if (abonnement.getStatut() != Abonnement.StatutAbonnement.EN_ATTENTE) {
            throw new RuntimeException("Cet abonnement ne peut plus être refusé");
        }
        
        abonnement.setStatut(Abonnement.StatutAbonnement.REFUSE);
        abonnement.setMotifRefus(motifRefus);
        abonnement.setDateValidation(LocalDateTime.now());
        
        return abonnementRepository.save(abonnement);
    }
    
    // Créer ou mettre à jour le compte annonceur
    private void creerOuMettreAJourAnnonceur(Abonnement abonnement) {
        Integer idAcquereur = abonnement.getAcquereur().getIdUtilisateur();
        
        Optional<Annonceur> annonceurOpt = annonceurRepository.findById(idAcquereur);
        Annonceur annonceur;
        
        if (annonceurOpt.isPresent()) {
            annonceur = annonceurOpt.get();
        } else {
            annonceur = new Annonceur();
            annonceur.setIdUtilisateur(idAcquereur);
        }
        
        annonceur.setIdAbonnementActif(abonnement.getIdAbonnement());
        annonceurRepository.save(annonceur);
    }
    
    // Vérifier si un acquéreur peut publier (a un abonnement actif)
    public boolean peutPublier(Integer idAcquereur) {
        return hasAbonnementActif(idAcquereur);
    }
    
    // Vérifier si un acquéreur a un abonnement actif
    public boolean hasAbonnementActif(Integer idAcquereur) {
        return abonnementRepository.hasAbonnementActif(idAcquereur, LocalDate.now());
    }
    
    // Récupérer l'abonnement actif d'un acquéreur
    public Optional<Abonnement> getAbonnementActif(Integer idAcquereur) {
        return abonnementRepository.findAbonnementActifByAcquereur(idAcquereur, LocalDate.now());
    }
    
    // Récupérer tous les abonnements d'un acquéreur
    public List<Abonnement> getAbonnementsParAcquereur(Integer idAcquereur) {
        return abonnementRepository.findByAcquereurIdUtilisateurOrderByDateDemandeDesc(idAcquereur);
    }
    
    // Récupérer les abonnements en attente
    public List<Abonnement> getAbonnementsEnAttente() {
        return abonnementRepository.findByStatutOrderByDateDemandeAsc(Abonnement.StatutAbonnement.EN_ATTENTE);
    }
    
    // Marquer les abonnements expirés
    public void marquerAbonnementsExpires() {
        List<Abonnement> expiredAbonnements = abonnementRepository.findAbonnementsExpires(LocalDate.now());
        for (Abonnement abonnement : expiredAbonnements) {
            abonnement.setStatut(Abonnement.StatutAbonnement.EXPIRE);
            abonnementRepository.save(abonnement);
        }
    }
    
    // Récupérer un abonnement par ID
    public Optional<Abonnement> getAbonnementById(Integer idAbonnement) {
        return abonnementRepository.findById(idAbonnement);
    }
}