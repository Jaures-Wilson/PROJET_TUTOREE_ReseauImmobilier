package com.reseauimmobilier.service;

import com.reseauimmobilier.model.Message;
import com.reseauimmobilier.model.Notification;
import com.reseauimmobilier.model.Utilisateur;
import com.reseauimmobilier.repository.MessageRepository;
import com.reseauimmobilier.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Message envoyerMessage(Integer expediteurId, Integer destinataireId, String contenu) {
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new RuntimeException("Le contenu du message ne peut pas être vide");
        }

        Optional<Utilisateur> expediteurOpt = utilisateurRepository.findById(expediteurId);
        Optional<Utilisateur> destinataireOpt = utilisateurRepository.findById(destinataireId);

        if (!expediteurOpt.isPresent()) {
            throw new RuntimeException("Expéditeur non trouvé");
        }
        if (!destinataireOpt.isPresent()) {
            throw new RuntimeException("Destinataire non trouvé");
        }

        Utilisateur expediteur = expediteurOpt.get();
        Utilisateur destinataire = destinataireOpt.get();

        Message message = new Message(contenu.trim(), expediteur, destinataire);
        Message messageSauvegarde = messageRepository.save(message);

        // Créer une notification pour le destinataire
        try {
            notificationService.creerNotification(
            	Notification.Type.MESSAGE, 
                "Nouveau message de " + expediteur.getNom(),
                expediteur,
                destinataire
            );
        } catch (Exception e) {
            // Log l'erreur mais ne fait pas échouer l'envoi du message
            System.err.println("Erreur lors de la création de la notification: " + e.getMessage());
        }

        return messageSauvegarde;
    }

    public List<Message> obtenirMessagesRecus(Integer utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        return messageRepository.findByDestinataire_IdUtilisateurOrderByDateEnvoiDesc(utilisateurId);
    }

    public List<Message> obtenirMessagesEnvoyes(Integer utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        return messageRepository.findByExpediteur_IdUtilisateurOrderByDateEnvoiDesc(utilisateurId);
    }

    public List<Message> obtenirTousLesMessages(Integer utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        return messageRepository.findAllMessagesForUser(utilisateurId);
    }

    public List<Message> obtenirConversation(Integer utilisateur1Id, Integer utilisateur2Id) {
        if (!utilisateurRepository.existsById(utilisateur1Id)) {
            throw new RuntimeException("Premier utilisateur non trouvé");
        }
        if (!utilisateurRepository.existsById(utilisateur2Id)) {
            throw new RuntimeException("Deuxième utilisateur non trouvé");
        }
        return messageRepository.findConversation(utilisateur1Id, utilisateur2Id);
    }

    public List<Message> obtenirMessagesNonLus(Integer utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        return messageRepository.findByDestinataire_IdUtilisateurAndLuFalseOrderByDateEnvoiDesc(utilisateurId);
    }

    public long compterMessagesNonLus(Integer utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        return messageRepository.countByDestinataire_IdUtilisateurAndLuFalse(utilisateurId);
    }

    @Transactional
    public Message marquerCommeLu(Integer messageId, Integer utilisateurId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            throw new RuntimeException("Message non trouvé");
        }

        Message message = messageOpt.get();
        
        // Vérifier que l'utilisateur est bien le destinataire
        if (!message.getDestinataire().getIdUtilisateur().equals(utilisateurId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à marquer ce message comme lu");
        }

        message.setLu(true);
        return messageRepository.save(message);
    }

    @Transactional
    public void marquerConversationCommeLue(Integer utilisateur1Id, Integer utilisateur2Id, Integer destinataireId) {
        List<Message> conversation = messageRepository.findConversation(utilisateur1Id, utilisateur2Id);
        
        for (Message message : conversation) {
            if (message.getDestinataire().getIdUtilisateur().equals(destinataireId) && !message.getLu()) {
                message.setLu(true);
                messageRepository.save(message);
            }
        }
    }

    @Transactional
    public void supprimerMessage(Integer messageId, Integer utilisateurId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            throw new RuntimeException("Message non trouvé");
        }

        Message message = messageOpt.get();
        
        // Vérifier que l'utilisateur est soit l'expéditeur soit le destinataire
        if (!message.getExpediteur().getIdUtilisateur().equals(utilisateurId) && 
            !message.getDestinataire().getIdUtilisateur().equals(utilisateurId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce message");
        }

        messageRepository.delete(message);
    }

    public List<Utilisateur> obtenirPartenairesConversation(Integer utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        return messageRepository.findConversationPartners(utilisateurId);
    }

    public List<Message> obtenirMessagesRecents(int nombreHeures) {
        LocalDateTime dateDebut = LocalDateTime.now().minusHours(nombreHeures);
        return messageRepository.findMessagesRecents(dateDebut);
    }

    public Map<String, Object> obtenirStatistiquesMessages(Integer utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Map<String, Object> statistiques = new HashMap<>();
        statistiques.put("messagesRecus", messageRepository.findByDestinataire_IdUtilisateurOrderByDateEnvoiDesc(utilisateurId).size());
        statistiques.put("messagesEnvoyes", messageRepository.findByExpediteur_IdUtilisateurOrderByDateEnvoiDesc(utilisateurId).size());
        statistiques.put("messagesNonLus", messageRepository.countByDestinataire_IdUtilisateurAndLuFalse(utilisateurId));
        statistiques.put("partenairesConversation", messageRepository.findConversationPartners(utilisateurId).size());

        return statistiques;
    }
}