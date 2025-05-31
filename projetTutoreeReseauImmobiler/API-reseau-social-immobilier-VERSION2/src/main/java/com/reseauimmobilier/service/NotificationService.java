package com.reseauimmobilier.service;

import com.reseauimmobilier.model.Notification;
import com.reseauimmobilier.model.Utilisateur;
import com.reseauimmobilier.repository.NotificationRepository;
import com.reseauimmobilier.repository.UtilisateurRepository;
import com.reseauimmobilier.repository.AdministrateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Transactional
    public Notification creerNotification(Notification.Type type, String contenu, Utilisateur expediteur, Utilisateur destinataire) {
        // Si destinataire est null, on envoie à l'admin (premier admin trouvé)
        if (destinataire == null) {
            List<Utilisateur> admins = utilisateurRepository.findByRole(Utilisateur.Role.ADMIN);
            if (!admins.isEmpty()) {
                destinataire = admins.get(0);
            } else {
                throw new RuntimeException("Aucun administrateur trouvé pour recevoir la notification");
            }
        }

        Notification notification = new Notification();
        notification.setType(type);
        notification.setContenu(contenu);
        notification.setExpediteur(expediteur);
        notification.setDestinataire(destinataire);
        notification.setDateCreation(LocalDateTime.now());
        notification.setLu(false);

        return notificationRepository.save(notification);
    }

    public List<Notification> obtenirNotificationsUtilisateur(Integer utilisateurId) {
        return notificationRepository.findByDestinataireIdUtilisateurOrderByDateCreationDesc(utilisateurId);
    }

    public List<Notification> obtenirNotificationsNonLues(Integer utilisateurId) {
        return notificationRepository.findByDestinataireIdUtilisateurAndLuOrderByDateCreationDesc(utilisateurId, false);
    }

    public Long compterNotificationsNonLues(Integer utilisateurId) {
        return notificationRepository.countNotificationsNonLues(utilisateurId);
    }

    @Transactional
    public Notification marquerCommeLue(Integer notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (!notificationOpt.isPresent()) {
            throw new RuntimeException("Notification non trouvée");
        }

        Notification notification = notificationOpt.get();
        notification.setLu(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void marquerToutesCommeLues(Integer utilisateurId) {
        List<Notification> notificationsNonLues = obtenirNotificationsNonLues(utilisateurId);
        for (Notification notification : notificationsNonLues) {
            notification.setLu(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void supprimerNotification(Integer notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new RuntimeException("Notification non trouvée");
        }
        notificationRepository.deleteById(notificationId);
    }

    public Optional<Notification> obtenirNotificationParId(Integer notificationId) {
        return notificationRepository.findById(notificationId);
    }

    public List<Notification> obtenirNotificationsParType(Integer utilisateurId, Notification.Type type) {
        return notificationRepository.findByDestinataireAndType(utilisateurId, type);
    }

    // Méthodes pour l'administrateur
    public List<Notification> obtenirToutesLesNotifications() {
        return notificationRepository.findAllOrderByDateCreationDesc();
    }

    public Long compterToutesNotificationsNonLues() {
        return notificationRepository.countTotalNotificationsNonLues();
    }

    // Méthodes utilitaires pour créer des notifications spécifiques
    @Transactional
    public void notifierNouveauPaiement(Utilisateur acquereur, Utilisateur annonceur, Integer montant, String titreAnnonce) {
        // Notification pour l'admin
        creerNotification(
        	Notification.Type.PAIEMENT,
            "Nouveau paiement en attente de validation - Montant: " + montant + " FCFA",
            acquereur,
            null
        );

        // Notification pour l'annonceur
        creerNotification(
        	Notification.Type.PAIEMENT,
            "Un acquéreur a effectué un paiement pour votre annonce: " + titreAnnonce,
            acquereur,
            annonceur
        );
    }

    @Transactional
    public void notifierNouvelleVisite(Utilisateur acquereur, Utilisateur annonceur, String titreAnnonce) {
        // Notification pour l'annonceur
        creerNotification(
        		Notification.Type.VISITE,
            "Nouvelle demande de visite pour votre annonce: " + titreAnnonce,
            acquereur,
            annonceur
        );

        // Notification pour l'admin
        creerNotification(
        		Notification.Type.VISITE,
            "Nouvelle demande de visite à organiser pour l'annonce: " + titreAnnonce,
            acquereur,
            null
        );
    }

    @Transactional
    public void notifierNouveauContrat(Utilisateur acquereur, Utilisateur annonceur, String titreAnnonce, String typeContrat) {
        // Notification pour l'annonceur
        creerNotification(
        		Notification.Type.CONTRAT,
            "Nouveau contrat (" + typeContrat + ") généré pour votre annonce: " + titreAnnonce,
            acquereur,
            annonceur
        );

        // Notification pour l'admin
        creerNotification(
        		Notification.Type.CONTRAT,
            "Nouveau contrat (" + typeContrat + ") en attente de validation pour l'annonce: " + titreAnnonce,
            acquereur,
            null
        );
    }

    @Transactional
    public void notifierConvoitiseAnnonce(Utilisateur acquereur, Utilisateur annonceur, String titreAnnonce) {
        // Notification pour l'annonceur
        creerNotification(
        		Notification.Type.CONVOITISE,
            "Un acquéreur s'intéresse à votre annonce: " + titreAnnonce,
            acquereur,
            annonceur
        );
    }
}