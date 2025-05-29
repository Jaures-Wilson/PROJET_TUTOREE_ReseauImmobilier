package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.Message;
import com.reseauimmobilier.model.Utilisateur;
import com.reseauimmobilier.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Envoyer un message
    @PostMapping
    public ResponseEntity<?> envoyerMessage(@RequestBody Map<String, Object> messageData) {
        try {
            Integer expediteurId = (Integer) messageData.get("expediteurId");
            Integer destinataireId = (Integer) messageData.get("destinataireId");
            String contenu = (String) messageData.get("contenu");

            if (expediteurId == null || destinataireId == null || contenu == null || contenu.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Les champs expediteurId, destinataireId et contenu sont obligatoires"));
            }

            Message message = messageService.envoyerMessage(expediteurId, destinataireId, contenu);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Récupérer une conversation entre deux utilisateurs
    @GetMapping("/conversation/{id1}/{id2}")
    public ResponseEntity<?> obtenirConversation(@PathVariable Integer id1, @PathVariable Integer id2) {
        try {
            List<Message> conversation = messageService.obtenirConversation(id1, id2);
            return ResponseEntity.ok(conversation);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Récupérer tous les messages d'un utilisateur
    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<?> obtenirTousLesMessages(@PathVariable Integer id) {
        try {
            List<Message> messages = messageService.obtenirTousLesMessages(id);
            return ResponseEntity.ok(messages);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Récupérer les messages reçus par un utilisateur
    @GetMapping("/{destinataireId}")
    public ResponseEntity<?> obtenirMessagesRecus(@PathVariable Integer destinataireId) {
        try {
            List<Message> messages = messageService.obtenirMessagesRecus(destinataireId);
            return ResponseEntity.ok(messages);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Récupérer les messages envoyés par un utilisateur
    @GetMapping("/envoyes/{expediteurId}")
    public ResponseEntity<?> obtenirMessagesEnvoyes(@PathVariable Integer expediteurId) {
        try {
            List<Message> messages = messageService.obtenirMessagesEnvoyes(expediteurId);
            return ResponseEntity.ok(messages);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Récupérer les messages non lus d'un utilisateur
    @GetMapping("/non-lus/{utilisateurId}")
    public ResponseEntity<?> obtenirMessagesNonLus(@PathVariable Integer utilisateurId) {
        try {
            List<Message> messages = messageService.obtenirMessagesNonLus(utilisateurId);
            return ResponseEntity.ok(messages);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Compter les messages non lus
    @GetMapping("/non-lus/count/{utilisateurId}")
    public ResponseEntity<?> compterMessagesNonLus(@PathVariable Integer utilisateurId) {
        try {
            long count = messageService.compterMessagesNonLus(utilisateurId);
            return ResponseEntity.ok(Map.of("count", count));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Marquer un message comme lu
    @PutMapping("/{messageId}/lu")
    public ResponseEntity<?> marquerCommeLu(@PathVariable Integer messageId, 
                                          @RequestBody Map<String, Integer> userData) {
        try {
            Integer utilisateurId = userData.get("utilisateurId");
            if (utilisateurId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "L'ID de l'utilisateur est obligatoire"));
            }

            Message message = messageService.marquerCommeLu(messageId, utilisateurId);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Marquer toute une conversation comme lue
    @PutMapping("/conversation/{id1}/{id2}/lu")
    public ResponseEntity<?> marquerConversationCommeLue(@PathVariable Integer id1, 
                                                        @PathVariable Integer id2,
                                                        @RequestBody Map<String, Integer> userData) {
        try {
            Integer destinataireId = userData.get("destinataireId");
            if (destinataireId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "L'ID du destinataire est obligatoire"));
            }

            messageService.marquerConversationCommeLue(id1, id2, destinataireId);
            return ResponseEntity.ok(Map.of("message", "Conversation marquée comme lue"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Supprimer un message
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> supprimerMessage(@PathVariable Integer messageId,
                                            @RequestParam Integer utilisateurId) {
        try {
            messageService.supprimerMessage(messageId, utilisateurId);
            return ResponseEntity.ok(Map.of("message", "Message supprimé avec succès"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir les partenaires de conversation d'un utilisateur
    @GetMapping("/partenaires/{utilisateurId}")
    public ResponseEntity<?> obtenirPartenairesConversation(@PathVariable Integer utilisateurId) {
        try {
            List<Utilisateur> partenaires = messageService.obtenirPartenairesConversation(utilisateurId);
            return ResponseEntity.ok(partenaires);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir les messages récents (dernières 24h par défaut)
    @GetMapping("/recents")
    public ResponseEntity<?> obtenirMessagesRecents(@RequestParam(defaultValue = "24") int heures) {
        try {
            List<Message> messages = messageService.obtenirMessagesRecents(heures);
            return ResponseEntity.ok(messages);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtenir les statistiques de messages d'un utilisateur
    @GetMapping("/statistiques/{utilisateurId}")
    public ResponseEntity<?> obtenirStatistiquesMessages(@PathVariable Integer utilisateurId) {
        try {
            Map<String, Object> statistiques = messageService.obtenirStatistiquesMessages(utilisateurId);
            return ResponseEntity.ok(statistiques);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}