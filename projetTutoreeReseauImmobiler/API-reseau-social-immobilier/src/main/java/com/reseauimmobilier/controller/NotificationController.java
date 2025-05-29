package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.Notification;
import com.reseauimmobilier.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Créer une notification
    @PostMapping
    public ResponseEntity<?> creerNotification(@RequestBody Map<String, Object> notificationData) {
        try {
            String typeStr = (String) notificationData.get("type");
            String contenu = (String) notificationData.get("contenu");
            Integer expediteurId = (Integer) notificationData.get("expediteurId");
            Integer destinataireId = (Integer) notificationData.get("destinataireId");

            // Vérifie si le type est valide avant de le convertir
            if (!isValidType(typeStr)) {
                return ResponseEntity.badRequest().body("Type de notification invalide");
            }

            Notification.Type type = Notification.Type.valueOf(typeStr.toUpperCase());

            Notification notification = notificationService.creerNotification(type, contenu, null, null);
            return ResponseEntity.ok(notification);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Type de notification invalide");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isValidType(String typeStr) {
        try {
            Notification.Type.valueOf(typeStr.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    
    

    // Récupérer toutes les notifications d'un utilisateur
    @GetMapping("/{utilisateurId}")
    public ResponseEntity<?> obtenirNotificationsUtilisateur(@PathVariable Integer utilisateurId) {
        try {
            List<Notification> notifications = notificationService.obtenirNotificationsUtilisateur(utilisateurId);
            
            if (utilisateurId == null || utilisateurId <= 0) {
                return ResponseEntity.badRequest().body("ID utilisateur invalide");
            }
            
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Récupérer les notifications non lues d'un utilisateur
    @GetMapping("/{utilisateurId}/non-lues")
    public ResponseEntity<List<Notification>> obtenirNotificationsNonLues(@PathVariable Integer utilisateurId) {
        try {
            List<Notification> notifications = notificationService.obtenirNotificationsNonLues(utilisateurId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Compter les notifications non lues
    @GetMapping("/{utilisateurId}/count-non-lues")
    public ResponseEntity<Map<String, Long>> compterNotificationsNonLues(@PathVariable Integer utilisateurId) {
        try {
            Long count = notificationService.compterNotificationsNonLues(utilisateurId);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Marquer une notification comme lue
    @PutMapping("/{id}/lu")
    public ResponseEntity<Notification> marquerCommeLue(@PathVariable Integer id) {
        try {
            Notification notification = notificationService.marquerCommeLue(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Marquer toutes les notifications d'un utilisateur comme lues
    @PutMapping("/{utilisateurId}/marquer-toutes-lues")
    public ResponseEntity<Map<String, String>> marquerToutesCommeLues(@PathVariable Integer utilisateurId) {
        try {
            notificationService.marquerToutesCommeLues(utilisateurId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Toutes les notifications ont été marquées comme lues");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Récupérer une notification spécifique
    @GetMapping("/detail/{id}")
    public ResponseEntity<Notification> obtenirNotificationParId(@PathVariable Integer id) {
        try {
            Optional<Notification> notification = notificationService.obtenirNotificationParId(id);
            if (notification.isPresent()) {
                return ResponseEntity.ok(notification.get());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Récupérer les notifications par type
    @GetMapping("/{utilisateurId}/type/{type}")
    public ResponseEntity<?> obtenirNotificationsParType(
            @PathVariable Integer utilisateurId, 
            @PathVariable String type) {
        try {
            if (!isValidType(type)) {
                return ResponseEntity.badRequest().body("Type de notification invalide");
            }

            Notification.Type typeEnum = Notification.Type.valueOf(type.toUpperCase());
            List<Notification> notifications = notificationService.obtenirNotificationsParType(utilisateurId, typeEnum);

            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Type de notification invalide");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // Supprimer une notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> supprimerNotification(@PathVariable Integer id) {
        try {
            notificationService.supprimerNotification(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification supprimée avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoints pour l'administrateur
    @GetMapping("/admin/toutes")
    public ResponseEntity<List<Notification>> obtenirToutesLesNotifications() {
        try {
            List<Notification> notifications = notificationService.obtenirToutesLesNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/admin/count-total-non-lues")
    public ResponseEntity<Map<String, Long>> compterToutesNotificationsNonLues() {
        try {
            Long count = notificationService.compterToutesNotificationsNonLues();
            Map<String, Long> response = new HashMap<>();
            response.put("totalNonLues", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint pour créer des notifications spécialisées (utilitaire pour les tests)
    @PostMapping("/test/paiement")
    public ResponseEntity<Map<String, String>> testerNotificationPaiement(@RequestBody Map<String, Object> data) {
        try {
            // Simulation pour test - normalement ces données viennent d'autres services
            Integer montant = (Integer) data.get("montant");
            String titreAnnonce = (String) data.get("titreAnnonce");
            
            // Pour la démo, on utilise des IDs fictifs
            // notificationService.notifierNouveauPaiement(acquereur, annonceur, montant, titreAnnonce);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notifications de paiement créées");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}