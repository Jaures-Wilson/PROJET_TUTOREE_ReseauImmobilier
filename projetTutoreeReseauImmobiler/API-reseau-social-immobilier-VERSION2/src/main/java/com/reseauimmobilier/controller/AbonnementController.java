package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.Abonnement;
import com.reseauimmobilier.service.AbonnementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/abonnements")
@CrossOrigin(origins = "*")
public class AbonnementController {
    
    @Autowired
    private AbonnementService abonnementService;
    
    // Créer une demande d'abonnement
    @PostMapping
    public ResponseEntity<Map<String, Object>> creerDemandeAbonnement(
            @RequestParam Integer idAcquereur,
            @RequestParam String dureeType,
            @RequestParam MultipartFile capturePreuve) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (capturePreuve.isEmpty()) {
                response.put("success", false);
                response.put("message", "La capture de preuve de paiement est obligatoire");
                return ResponseEntity.badRequest().body(response);
            }
            
            Abonnement.DureeType duree = Abonnement.DureeType.valueOf(dureeType.toUpperCase());
            byte[] captureBytes = capturePreuve.getBytes();
            
            Abonnement abonnement = abonnementService.creerDemandeAbonnement(idAcquereur, duree, captureBytes);
            
            response.put("success", true);
            response.put("message", "Demande d'abonnement créée avec succès");
            response.put("abonnement", mapAbonnementToResponse(abonnement));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Vérifier si un acquéreur peut publier
    @GetMapping("/peut-publier/{idAcquereur}")
    public ResponseEntity<Map<String, Object>> peutPublier(@PathVariable Integer idAcquereur) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean peutPublier = abonnementService.peutPublier(idAcquereur);
            
            response.put("peutPublier", peutPublier);
            response.put("message", peutPublier ? "L'utilisateur peut publier" : "Abonnement requis pour publier");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("peutPublier", false);
            response.put("message", "Erreur lors de la vérification");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Récupérer l'abonnement actif d'un acquéreur
    @GetMapping("/actif/{idAcquereur}")
    public ResponseEntity<Map<String, Object>> getAbonnementActif(@PathVariable Integer idAcquereur) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Abonnement> abonnementOpt = abonnementService.getAbonnementActif(idAcquereur);
            
            if (abonnementOpt.isPresent()) {
                response.put("success", true);
                response.put("abonnement", mapAbonnementToResponse(abonnementOpt.get()));
            } else {
                response.put("success", false);
                response.put("message", "Aucun abonnement actif trouvé");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Récupérer tous les abonnements d'un acquéreur
    @GetMapping("/acquereur/{idAcquereur}")
    public ResponseEntity<Map<String, Object>> getAbonnementsParAcquereur(@PathVariable Integer idAcquereur) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Abonnement> abonnements = abonnementService.getAbonnementsParAcquereur(idAcquereur);
            
            response.put("success", true);
            response.put("abonnements", abonnements.stream()
                    .map(this::mapAbonnementToResponse)
                    .toArray());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Récupérer les abonnements en attente (pour l'admin)
    @GetMapping("/en-attente")
    public ResponseEntity<Map<String, Object>> getAbonnementsEnAttente() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Abonnement> abonnements = abonnementService.getAbonnementsEnAttente();
            
            response.put("success", true);
            response.put("abonnements", abonnements.stream()
                    .map(this::mapAbonnementToResponseAvecAcquereur)
                    .toArray());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Valider un abonnement (admin)
    @PutMapping("/{idAbonnement}/valider")
    public ResponseEntity<Map<String, Object>> validerAbonnement(@PathVariable Integer idAbonnement) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Abonnement abonnement = abonnementService.validerAbonnement(idAbonnement);
            
            response.put("success", true);
            response.put("message", "Abonnement validé avec succès");
            response.put("abonnement", mapAbonnementToResponse(abonnement));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Refuser un abonnement (admin)
    @PutMapping("/{idAbonnement}/refuser")
    public ResponseEntity<Map<String, Object>> refuserAbonnement(
            @PathVariable Integer idAbonnement,
            @RequestBody Map<String, String> requestBody) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String motifRefus = requestBody.get("motifRefus");
            if (motifRefus == null || motifRefus.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Le motif de refus est obligatoire");
                return ResponseEntity.badRequest().body(response);
            }
            
            Abonnement abonnement = abonnementService.refuserAbonnement(idAbonnement, motifRefus);
            
            response.put("success", true);
            response.put("message", "Abonnement refusé");
            response.put("abonnement", mapAbonnementToResponse(abonnement));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Récupérer un abonnement par ID
    @GetMapping("/{idAbonnement}")
    public ResponseEntity<Map<String, Object>> getAbonnementById(@PathVariable Integer idAbonnement) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Abonnement> abonnementOpt = abonnementService.getAbonnementById(idAbonnement);
            
            if (abonnementOpt.isPresent()) {
                response.put("success", true);
                response.put("abonnement", mapAbonnementToResponseAvecAcquereur(abonnementOpt.get()));
            } else {
                response.put("success", false);
                response.put("message", "Abonnement non trouvé");
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Mapper un abonnement vers une réponse simple
    private Map<String, Object> mapAbonnementToResponse(Abonnement abonnement) {
        Map<String, Object> map = new HashMap<>();
        map.put("idAbonnement", abonnement.getIdAbonnement());
        map.put("dureeType", abonnement.getDureeType().toString());
        map.put("montant", abonnement.getMontant());
        map.put("dateDebut", abonnement.getDateDebut());
        map.put("dateFin", abonnement.getDateFin());
        map.put("statut", abonnement.getStatut().toString());
        map.put("dateDemande", abonnement.getDateDemande());
        map.put("dateValidation", abonnement.getDateValidation());
        map.put("motifRefus", abonnement.getMotifRefus());
        map.put("isActif", abonnement.isActif());
        map.put("isExpire", abonnement.isExpire());
        return map;
    }
    
    // Mapper un abonnement avec les infos de l'acquéreur
    private Map<String, Object> mapAbonnementToResponseAvecAcquereur(Abonnement abonnement) {
        Map<String, Object> map = mapAbonnementToResponse(abonnement);
        
        Map<String, Object> acquereurInfo = new HashMap<>();
        acquereurInfo.put("idAcquereur", abonnement.getAcquereur().getIdUtilisateur());
        acquereurInfo.put("nom", abonnement.getAcquereur().getNom());
        acquereurInfo.put("email", abonnement.getAcquereur().getEmail());
        
        map.put("acquereur", acquereurInfo);
        return map;
    }
}