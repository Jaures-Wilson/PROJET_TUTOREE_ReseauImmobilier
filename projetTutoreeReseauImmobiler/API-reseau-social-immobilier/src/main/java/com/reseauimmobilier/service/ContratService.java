package com.reseauimmobilier.service;

import com.reseauimmobilier.model.*;
import com.reseauimmobilier.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private AcquereurRepository acquereurRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Contrat creerContrat(Contrat contrat) {
        // Vérifier que l'annonce existe
        if (contrat.getAnnonce() == null || !annonceRepository.existsById(contrat.getAnnonce().getIdAnnonce())) {
            throw new RuntimeException("Annonce non trouvée");
        }

        // Vérifier qu'il n'existe pas déjà un contrat pour cette annonce
        if (contratRepository.existsByAnnonceIdAnnonce(contrat.getAnnonce().getIdAnnonce())) {
            throw new RuntimeException("Un contrat existe déjà pour cette annonce");
        }

        // Définir la date de signature par défaut
        if (contrat.getDateSignature() == null) {
            contrat.setDateSignature(LocalDateTime.now());
        }

        // Sauvegarder le contrat
        Contrat contratSauvegarde = contratRepository.save(contrat);

        // Envoyer une notification à l'annonceur
        if (contrat.getAnnonce().getAnnonceur() != null) {
            notificationService.creerNotification(
            	Notification.Type.CONTRAT,
                "Un nouveau contrat a été généré pour votre annonce: " + contrat.getAnnonce().getTitre(),
                null, // Pas d'expéditeur spécifique pour le système
                contrat.getAnnonce().getAnnonceur()
            );
        }

        return contratSauvegarde;
    }

    public Optional<Contrat> obtenirContratParId(Integer id) {
        return contratRepository.findById(id);
    }

    public List<Contrat> obtenirTousLesContrats() {
        return contratRepository.findAll();
    }

    public List<Contrat> obtenirContratsParAcquereur(Integer acquereurId) {
        if (!acquereurRepository.existsById(acquereurId)) {
            throw new RuntimeException("Acquéreur non trouvé");
        }
        return contratRepository.findByAcquereurId(acquereurId);
    }

    public List<Contrat> obtenirContratsNonSignesParAcquereur(Integer acquereurId) {
        if (!acquereurRepository.existsById(acquereurId)) {
            throw new RuntimeException("Acquéreur non trouvé");
        }
        return contratRepository.findContratNonSignesByAcquereurId(acquereurId);
    }

    public List<Contrat> obtenirContratsSignesParAcquereur(Integer acquereurId) {
        if (!acquereurRepository.existsById(acquereurId)) {
            throw new RuntimeException("Acquéreur non trouvé");
        }
        return contratRepository.findContratSignesByAcquereurId(acquereurId);
    }

    public List<Contrat> obtenirContratsParAnnonceur(Integer annonceurId) {
        return contratRepository.findByAnnonceurId(annonceurId);
    }

    public List<Contrat> obtenirContratsParType(Contrat.TypeContrat type) {
        return contratRepository.findByType(type);
    }

    @Transactional
    public Contrat validerContrat(Integer contratId, Integer acquereurId) {
        Optional<Contrat> contratOpt = contratRepository.findById(contratId);
        if (!contratOpt.isPresent()) {
            throw new RuntimeException("Contrat non trouvé");
        }

        Contrat contrat = contratOpt.get();

        // Vérifier que l'acquéreur fait partie des signataires
        Optional<Utilisateur> acquereur = utilisateurRepository.findById(acquereurId);
        if (!acquereur.isPresent()) {
            throw new RuntimeException("Acquéreur non trouvé");
        }

        if (!contrat.getSignataires().contains(acquereur.get())) {
            throw new RuntimeException("Cet acquéreur n'est pas autorisé à signer ce contrat");
        }

        // Valider le contrat
        contrat.setDecisionAcquereur(true);
        Contrat contratValide = contratRepository.save(contrat);

        // Mettre à jour le statut de l'annonce
        Annonce annonce = contrat.getAnnonce();
        if (contrat.getType() == Contrat.TypeContrat.VENTE) {
            annonce.estVendu();
        } else if (contrat.getType() == Contrat.TypeContrat.PROMESSE_VENTE) {
            annonce.estReserve();
        }
        annonceRepository.save(annonce);

        // Notifier l'annonceur
        notificationService.creerNotification(
        	Notification.Type.CONTRAT,
            "Le contrat pour votre annonce '" + annonce.getTitre() + "' a été validé",
            acquereur.get(),
            annonce.getAnnonceur()
        );

        return contratValide;
    }

    @Transactional
    public Contrat refuserContrat(Integer contratId, Integer acquereurId, String motif) {
        Optional<Contrat> contratOpt = contratRepository.findById(contratId);
        if (!contratOpt.isPresent()) {
            throw new RuntimeException("Contrat non trouvé");
        }

        Contrat contrat = contratOpt.get();

        // Vérifier que l'acquéreur fait partie des signataires
        Optional<Utilisateur> acquereur = utilisateurRepository.findById(acquereurId);
        if (!acquereur.isPresent()) {
            throw new RuntimeException("Acquéreur non trouvé");
        }

        if (!contrat.getSignataires().contains(acquereur.get())) {
            throw new RuntimeException("Cet acquéreur n'est pas autorisé à modifier ce contrat");
        }

        // Refuser le contrat (on peut ajouter un champ motif si nécessaire)
        contrat.setDecisionAcquereur(false);
        if (motif != null && !motif.trim().isEmpty()) {
            contrat.setPrecisionEnSurplus("Motif de refus: " + motif);
        }

        Contrat contratRefuse = contratRepository.save(contrat);

        // Remettre l'annonce en disponible
        Annonce annonce = contrat.getAnnonce();
        annonce.estDisponible();
        annonceRepository.save(annonce);

        // Notifier l'annonceur
        notificationService.creerNotification(
        	Notification.Type.CONTRAT,
            "Le contrat pour votre annonce '" + annonce.getTitre() + "' a été refusé",
            acquereur.get(),
            annonce.getAnnonceur()
        );

        return contratRefuse;
    }

    @Transactional
    public Contrat ajouterSignataire(Integer contratId, Integer utilisateurId) {
        Optional<Contrat> contratOpt = contratRepository.findById(contratId);
        if (!contratOpt.isPresent()) {
            throw new RuntimeException("Contrat non trouvé");
        }

        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        if (!utilisateur.isPresent()) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Contrat contrat = contratOpt.get();
        contrat.ajouterSignataire(utilisateur.get());

        return contratRepository.save(contrat);
    }

    @Transactional
    public Contrat modifierContrat(Integer contratId, Contrat contratModifie) {
        Optional<Contrat> contratOpt = contratRepository.findById(contratId);
        if (!contratOpt.isPresent()) {
            throw new RuntimeException("Contrat non trouvé");
        }

        Contrat contrat = contratOpt.get();

        // Vérifier que le contrat n'est pas encore validé
        if (contrat.getDecisionAcquereur()) {
            throw new RuntimeException("Impossible de modifier un contrat déjà validé");
        }

        // Mettre à jour les champs modifiables
        if (contratModifie.getType() != null) {
            contrat.setType(contratModifie.getType());
        }
        if (contratModifie.getDateDebutContrat() != null) {
            contrat.setDateDebutContrat(contratModifie.getDateDebutContrat());
        }
        if (contratModifie.getDateFinContrat() != null) {
            contrat.setDateFinContrat(contratModifie.getDateFinContrat());
        }
        if (contratModifie.getPrecisionEnSurplus() != null) {
            contrat.setPrecisionEnSurplus(contratModifie.getPrecisionEnSurplus());
        }

        return contratRepository.save(contrat);
    }

    @Transactional
    public void supprimerContrat(Integer contratId) {
        if (!contratRepository.existsById(contratId)) {
            throw new RuntimeException("Contrat non trouvé");
        }

        Optional<Contrat> contratOpt = contratRepository.findById(contratId);
        if (contratOpt.isPresent() && contratOpt.get().getDecisionAcquereur()) {
            throw new RuntimeException("Impossible de supprimer un contrat validé");
        }

        contratRepository.deleteById(contratId);
    }

    // Méthodes utilitaires
    public boolean contratExistePourAnnonce(Integer annonceId) {
        return contratRepository.existsByAnnonceIdAnnonce(annonceId);
    }

    public Long compterContratsParType(Contrat.TypeContrat type) {
        return contratRepository.countByType(type);
    }

    public Long compterContratsValides() {
        return contratRepository.countByDecisionAcquereur(true);
    }

    public List<Contrat> obtenirContratsRecents(int nombreJours) {
        LocalDateTime dateDebut = LocalDateTime.now().minusDays(nombreJours);
        return contratRepository.findRecentContracts(dateDebut);
    }
}