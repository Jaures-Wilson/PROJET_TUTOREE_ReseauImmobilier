package com.reseauimmobilier.service;

import com.reseauimmobilier.model.*;
import com.reseauimmobilier.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private AcquereurRepository acquereurRepository;

    @Autowired
    private AnnonceurRepository annonceurRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ContratService contratService;

    @Transactional
    public Paiement effectuerPaiement(Paiement paiement, MultipartFile photoPreuve) throws IOException {
        // Vérifications de base
        if (paiement.getAcquereur() == null || !acquereurRepository.existsById(paiement.getAcquereur().getIdUtilisateur())) {
            throw new RuntimeException("Acquéreur non trouvé");
        }

        if (paiement.getAnnonce() == null || !annonceRepository.existsById(paiement.getAnnonce().getIdAnnonce())) {
            throw new RuntimeException("Annonce non trouvée");
        }

        // Récupérer l'annonce complète avec l'annonceur
        Annonce annonce = annonceRepository.findById(paiement.getAnnonce().getIdAnnonce())
            .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        if (annonce.getAnnonceur() == null) {
            throw new RuntimeException("Annonceur de l'annonce non trouvé");
        }

        // Vérifier que l'annonce est disponible
        if (annonce.getStatus() != Annonce.Status.DISPONIBLE) {
            throw new RuntimeException("Cette annonce n'est plus disponible");
        }

        // Vérifier qu'un paiement n'existe pas déjà pour cette annonce et cet acquéreur
        if (paiementRepository.existsByAnnonceIdAnnonceAndAcquereurIdUtilisateur(
                annonce.getIdAnnonce(), paiement.getAcquereur().getIdUtilisateur())) {
            throw new RuntimeException("Un paiement existe déjà pour cette annonce");
        }

        // Traiter la photo de preuve
        if (photoPreuve == null || photoPreuve.isEmpty()) {
            throw new RuntimeException("La preuve de paiement (photo) est obligatoire");
        }

        try {
            paiement.setCapturePhotoPreuvePaiement(photoPreuve.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du traitement de la photo de preuve");
        }

        // Définir les valeurs par défaut
        paiement.setAnnonce(annonce);
        paiement.setAnnonceur(annonce.getAnnonceur());
        paiement.setStatut(Paiement.StatutPaiement.EN_ATTENTE);
        paiement.setLu(false);
        
        if (paiement.getDatePaiement() == null) {
            paiement.setDatePaiement(LocalDateTime.now());
        }

        // Sauvegarder le paiement
        Paiement paiementSauvegarde = paiementRepository.save(paiement);

        // Créer une notification pour l'administrateur
        notificationService.creerNotification(
        		Notification.Type.PAIEMENT,
            "Nouveau paiement en attente de validation - Montant: " + paiement.getMontant() + " FCFA",
            paiement.getAcquereur(),
            null // Pour l'admin, on peut passer null
        );

        // Créer une notification pour l'annonceur
        notificationService.creerNotification(
        		Notification.Type.PAIEMENT,
            "Un acquéreur a effectué un paiement pour votre annonce: " + annonce.getTitre(),
            paiement.getAcquereur(),
            annonce.getAnnonceur()
        );

        return paiementSauvegarde;
    }

    public Optional<Paiement> obtenirPaiementParId(Integer id) {
        return paiementRepository.findById(id);
    }

    public List<Paiement> obtenirTousLesPaiements() {
        return paiementRepository.findAll();
    }

    public List<Paiement> obtenirPaiementsParAcquereur(Integer acquereurId) {
        if (!acquereurRepository.existsById(acquereurId)) {
            throw new RuntimeException("Acquéreur non trouvé");
        }
        return paiementRepository.findByAcquereurIdUtilisateur(acquereurId);
    }

    public List<Paiement> obtenirPaiementsParAnnonceur(Integer annonceurId) {
        if (!annonceurRepository.existsById(annonceurId)) {
            throw new RuntimeException("Annonceur non trouvé");
        }
        return paiementRepository.findByAnnonceurIdUtilisateur(annonceurId);
    }

    public List<Paiement> obtenirPaiementsEnAttente() {
        return paiementRepository.findByStatut(Paiement.StatutPaiement.EN_ATTENTE);
    }

    public List<Paiement> obtenirPaiementsConfirmes() {
        return paiementRepository.findByStatut(Paiement.StatutPaiement.CONFIRMEE);
    }

    public List<Paiement> obtenirPaiementsParStatut(Paiement.StatutPaiement statut) {
        return paiementRepository.findByStatut(statut);
    }

    @Transactional
    public Paiement validerPaiement(Integer paiementId) {
        Optional<Paiement> paiementOpt = paiementRepository.findById(paiementId);
        if (!paiementOpt.isPresent()) {
            throw new RuntimeException("Paiement non trouvé");
        }

        Paiement paiement = paiementOpt.get();

        if (paiement.estConfirme()) {
            throw new RuntimeException("Ce paiement est déjà confirmé");
        }

        // Confirmer le paiement
        paiement.confirmerPaiement();
        Paiement paiementValide = paiementRepository.save(paiement);

        // Mettre à jour le statut de l'annonce selon le type de contrat
        Annonce annonce = paiement.getAnnonce();
        if (paiement.getTypeContrat() == Paiement.TypeContrat.VENTE) {
            annonce.estVendu();
        } else if (paiement.getTypeContrat() == Paiement.TypeContrat.PROMESSE_VENTE) {
            annonce.estReserve();
        }
        annonceRepository.save(annonce);

        // Créer ou mettre à jour le contrat associé
        try {
            if (!contratService.contratExistePourAnnonce(annonce.getIdAnnonce())) {
                Contrat contrat = new Contrat();
                contrat.setAnnonce(annonce);
                contrat.setType(paiement.getTypeContrat() == Paiement.TypeContrat.VENTE ? 
                    Contrat.TypeContrat.VENTE : Contrat.TypeContrat.PROMESSE_VENTE);
                contrat.setDateDebutContrat(LocalDateTime.now());
                contrat.setDateFinContrat(LocalDateTime.now().plusMonths(6)); // 6 mois par défaut
                contratService.creerContrat(contrat);
            }
        } catch (Exception e) {
            // Log de l'erreur mais ne pas faire échouer la validation du paiement
            System.err.println("Erreur lors de la création du contrat: " + e.getMessage());
        }

        // Notifier l'acquéreur
        notificationService.creerNotification(
        		Notification.Type.PAIEMENT,
            "Votre paiement a été confirmé - " + annonce.getTitre(),
            null,
            paiement.getAcquereur()
        );

        // Notifier l'annonceur
        notificationService.creerNotification(
        		Notification.Type.PAIEMENT,
            "Le paiement pour votre annonce '" + annonce.getTitre() + "' a été confirmé",
            null,
            paiement.getAnnonceur()
        );

        return paiementValide;
    }

    @Transactional
    public Paiement refuserPaiement(Integer paiementId, String motif) {
        Optional<Paiement> paiementOpt = paiementRepository.findById(paiementId);
        if (!paiementOpt.isPresent()) {
            throw new RuntimeException("Paiement non trouvé");
        }

        Paiement paiement = paiementOpt.get();

        if (paiement.estConfirme()) {
            throw new RuntimeException("Impossible de refuser un paiement déjà confirmé");
        }

        // Refuser le paiement
        paiement.refuserPaiement();
        Paiement paiementRefuse = paiementRepository.save(paiement);

        // Notifier l'acquéreur
        String message = "Votre paiement a été refusé";
        if (motif != null && !motif.trim().isEmpty()) {
            message += " - Motif: " + motif;
        }

        notificationService.creerNotification(
        		Notification.Type.PAIEMENT,
            message,
            null,
            paiement.getAcquereur()
        );

        return paiementRefuse;
    }

    @Transactional
    public Paiement marquerCommeLu(Integer paiementId) {
        Optional<Paiement> paiementOpt = paiementRepository.findById(paiementId);
        if (!paiementOpt.isPresent()) {
            throw new RuntimeException("Paiement non trouvé");
        }

        Paiement paiement = paiementOpt.get();
        paiement.marquerCommeLu();

        return paiementRepository.save(paiement);
    }

    // Simulation du virement vers l'annonceur (dans un vrai système, intégration avec API bancaire)
    @Transactional 
    public void simulerVirementVersAnnonceur(Integer paiementId) {
        Optional<Paiement> paiementOpt = paiementRepository.findById(paiementId);
        if (!paiementOpt.isPresent()) {
            throw new RuntimeException("Paiement non trouvé");
        }

        Paiement paiement = paiementOpt.get();

        if (!paiement.estConfirme()) {
            throw new RuntimeException("Le paiement doit être confirmé avant le virement");
        }

        // Simulation du virement
        System.out.println("Virement simulé de " + paiement.getMontant() + " FCFA vers " + 
                          paiement.getAnnonceur().getNom());

        // Notifier l'annonceur
        notificationService.creerNotification(
        		Notification.Type.PAIEMENT,
            "Virement reçu: " + paiement.getMontant() + " FCFA pour l'annonce: " + 
            paiement.getAnnonce().getTitre(),
            null,
            paiement.getAnnonceur()
        );
    }

    // Méthodes de statistiques
    public Map<String, Object> obtenirStatistiquesPaiements() {
        Map<String, Object> statistiques = new HashMap<>();
        
        statistiques.put("totalPaiements", paiementRepository.count());
        statistiques.put("paiementsEnAttente", paiementRepository.countByStatut(Paiement.StatutPaiement.EN_ATTENTE));
        statistiques.put("paiementsConfirmes", paiementRepository.countByStatut(Paiement.StatutPaiement.CONFIRMEE));
        statistiques.put("paiementsRefuses", paiementRepository.countByStatut(Paiement.StatutPaiement.REFUSE));
        
        return statistiques;
    }

    public Long obtenirRevenusTotalAnnonceur(Integer annonceurId) {
        if (!annonceurRepository.existsById(annonceurId)) {
            throw new RuntimeException("Annonceur non trouvé");
        }
        return paiementRepository.sumMontantByAnnonceurAndStatutConfirme(annonceurId);
    }

    public Long obtenirDepensesTotalAcquereur(Integer acquereurId) {
        if (!acquereurRepository.existsById(acquereurId)) {
            throw new RuntimeException("Acquéreur non trouvé");
        }
        return paiementRepository.sumMontantByAcquereurAndStatutConfirme(acquereurId);
    }

    public List<Paiement> obtenirPaiementsRecents(int nombreJours) {
        LocalDateTime dateDebut = LocalDateTime.now().minusDays(nombreJours);
        return paiementRepository.findRecentPaiements(dateDebut);
    }

    public List<Paiement> obtenirPaiementsParPeriode(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return paiementRepository.findByDatePaiementBetween(dateDebut, dateFin);
    }

    public boolean paiementExistePourAnnonce(Integer annonceId, Integer acquereurId) {
        return paiementRepository.existsByAnnonceIdAnnonceAndAcquereurIdUtilisateur(annonceId, acquereurId);
    }

    @Transactional
    public void supprimerPaiement(Integer paiementId) {
        Optional<Paiement> paiementOpt = paiementRepository.findById(paiementId);
        if (!paiementOpt.isPresent()) {
            throw new RuntimeException("Paiement non trouvé");
        }

        Paiement paiement = paiementOpt.get();
        if (paiement.estConfirme()) {
            throw new RuntimeException("Impossible de supprimer un paiement confirmé");
        }

        paiementRepository.deleteById(paiementId);
    }
}