package com.reseauimmobilier.service;

import com.reseauimmobilier.model.*;
import com.reseauimmobilier.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VisiteService {

    @Autowired
    private VisiteRepository visiteRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private AcquereurRepository acquereurRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Visite demanderVisite(Integer annonceId, Integer acquereurId, LocalDate dateVisite) {
        // Vérifier que l'annonce existe
        Optional<Annonce> annonceOpt = annonceRepository.findById(annonceId);
        if (!annonceOpt.isPresent()) {
            throw new RuntimeException("Annonce non trouvée");
        }

        // Vérifier que l'acquéreur existe
        Optional<Acquereur> acquereurOpt = acquereurRepository.findById(acquereurId);
        if (!acquereurOpt.isPresent()) {
            throw new RuntimeException("Acquéreur non trouvé");
        }

        Annonce annonce = annonceOpt.get();
        Acquereur acquereur = acquereurOpt.get();

        // Vérifier que l'annonce est disponible
        if (annonce.getStatus() != Annonce.Status.DISPONIBLE) {
            throw new RuntimeException("Cette annonce n'est plus disponible pour visite");
        }

        // Vérifier qu'il n'y a pas déjà une visite prévue pour cette annonce à cette date par cet acquéreur
        if (visiteRepository.existsByAcquereurIdUtilisateurAndAnnonceIdAnnonceAndDateVisite(
                acquereurId, annonceId, dateVisite)) {
            throw new RuntimeException("Une visite est déjà prévue pour cette annonce à cette date");
        }

        // Créer la visite
        Visite visite = new Visite(dateVisite, annonce, acquereur);
        Visite visiteSauvegardee = visiteRepository.save(visite);

        // Incrémenter le nombre de visites de l'annonce
        annonce.setNombreVisite(annonce.getNombreVisite() + 1);
        annonceRepository.save(annonce);

        // Créer une notification pour l'annonceur et l'admin
        notificationService.notifierNouvelleVisite(acquereur, annonce.getAnnonceur(), annonce.getTitre());

        return visiteSauvegardee;
    }

    @Transactional
    public Visite modifierStatutVisite(Integer visiteId, String nouveauStatut) {
        Optional<Visite> visiteOpt = visiteRepository.findById(visiteId);
        if (!visiteOpt.isPresent()) {
            throw new RuntimeException("Visite non trouvée");
        }

        Visite visite = visiteOpt.get();
        
        try {
            Visite.StatutVisite statut = Visite.StatutVisite.valueOf(nouveauStatut.toUpperCase());
            visite.setStatut(statut);
            
            return visiteRepository.save(visite);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide. Statuts autorisés : DEMANDEE, CONFIRMEE, ANNULEE, EFFECTUEE");
        }
    }

    public List<Visite> obtenirVisitesParAnnonce(Integer annonceId) {
        if (!annonceRepository.existsById(annonceId)) {
            throw new RuntimeException("Annonce non trouvée");
        }
        return visiteRepository.findByAnnonceIdAnnonceOrderByDateVisiteDesc(annonceId);
    }

    public List<Visite> obtenirVisitesParAcquereur(Integer acquereurId) {
        if (!acquereurRepository.existsById(acquereurId)) {
            throw new RuntimeException("Acquéreur non trouvé");
        }
        return visiteRepository.findByAcquereurIdUtilisateurOrderByDateVisiteDesc(acquereurId);
    }

    public List<Visite> obtenirVisitesParAnnonceur(Integer annonceurId) {
        return visiteRepository.findByAnnonceurId(annonceurId);
    }

    public List<Visite> obtenirVisitesParStatut(String statut) {
        try {
            Visite.StatutVisite statutEnum = Visite.StatutVisite.valueOf(statut.toUpperCase());
            return visiteRepository.findByStatutOrderByDateVisiteDesc(statutEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide. Statuts autorisés : DEMANDEE, CONFIRMEE, ANNULEE, EFFECTUEE");
        }
    }

    public List<Visite> obtenirVisitesAVenir() {
        return visiteRepository.findVisitesAVenir(LocalDate.now());
    }

    public List<Visite> obtenirVisitesPassees() {
        return visiteRepository.findVisitesPassees(LocalDate.now());
    }

    public Optional<Visite> obtenirVisiteParId(Integer visiteId) {
        return visiteRepository.findById(visiteId);
    }

    @Transactional
    public void annulerVisite(Integer visiteId) {
        Optional<Visite> visiteOpt = visiteRepository.findById(visiteId);
        if (!visiteOpt.isPresent()) {
            throw new RuntimeException("Visite non trouvée");
        }

        Visite visite = visiteOpt.get();
        visite.setStatut(Visite.StatutVisite.ANNULEE);
        visiteRepository.save(visite);
    }

    @Transactional
    public void supprimerVisite(Integer visiteId) {
        if (!visiteRepository.existsById(visiteId)) {
            throw new RuntimeException("Visite non trouvée");
        }
        visiteRepository.deleteById(visiteId);
    }

    public List<Visite> obtenirVisitesConfirmeesParDate(LocalDate date) {
        return visiteRepository.findByDateVisiteAndStatut(date, Visite.StatutVisite.CONFIRMEE);
    }

    public Long compterVisitesParAnnonce(Integer annonceId) {
        return visiteRepository.countByAnnonceId(annonceId);
    }

    public Long compterVisitesParAcquereurEtStatut(Integer acquereurId, String statut) {
        try {
            Visite.StatutVisite statutEnum = Visite.StatutVisite.valueOf(statut.toUpperCase());
            return visiteRepository.countByAcquereurIdAndStatut(acquereurId, statutEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide");
        }
    }

    // Méthodes utilitaires pour l'admin
    public List<Visite> obtenirToutesLesVisites() {
        return visiteRepository.findAll();
    }

    public List<Visite> obtenirVisitesDemandees() {
        return visiteRepository.findByStatutOrderByDateVisiteDesc(Visite.StatutVisite.DEMANDEE);
    }
}