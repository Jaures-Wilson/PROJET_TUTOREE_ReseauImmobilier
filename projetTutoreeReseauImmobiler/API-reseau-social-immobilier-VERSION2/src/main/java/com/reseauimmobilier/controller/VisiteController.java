package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.Visite;
import com.reseauimmobilier.service.VisiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/visites")
@CrossOrigin(origins = "http://localhost:3000")
public class VisiteController {

    @Autowired
    private VisiteService visiteService;

    // POST /api/visites - Demander une visite
    @PostMapping
    public ResponseEntity<?> demanderVisite(@RequestBody Map<String, Object> request) {
        try {
            Integer annonceId = (Integer) request.get("annonceId");
            Integer acquereurId = (Integer) request.get("acquereurId");
            String dateVisiteStr = (String) request.get("dateVisite");
            
            if (annonceId == null || acquereurId == null || dateVisiteStr == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "annonceId, acquereurId et dateVisite sont requis"));
            }

            LocalDate dateVisite = LocalDate.parse(dateVisiteStr);
            
            // Vérifier que la date n'est pas dans le passé
            if (dateVisite.isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "La date de visite ne peut pas être dans le passé"));
            }

            Visite visite = visiteService.demanderVisite(annonceId, acquereurId, dateVisite);
            return ResponseEntity.status(HttpStatus.CREATED).body(visite);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/visites/annonce/{id} - Voir les visites pour une annonce
    @GetMapping("/annonce/{annonceId}")
    public ResponseEntity<?> obtenirVisitesParAnnonce(@PathVariable Integer annonceId) {
        try {
            List<Visite> visites = visiteService.obtenirVisitesParAnnonce(annonceId);
            return ResponseEntity.ok(visites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/visites/acquereur/{id} - Voir les visites demandées par un acquéreur
    @GetMapping("/acquereur/{acquereurId}")
    public ResponseEntity<?> obtenirVisitesParAcquereur(@PathVariable Integer acquereurId) {
        try {
            List<Visite> visites = visiteService.obtenirVisitesParAcquereur(acquereurId);
            return ResponseEntity.ok(visites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/visites/annonceur/{id} - Voir les visites pour les annonces d'un annonceur
    @GetMapping("/annonceur/{annonceurId}")
    public ResponseEntity<?> obtenirVisitesParAnnonceur(@PathVariable Integer annonceurId) {
        try {
            List<Visite> visites = visiteService.obtenirVisitesParAnnonceur(annonceurId);
            return ResponseEntity.ok(visites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/visites/{id}/statut - Modifier le statut d'une visite
    @PutMapping("/{visiteId}/statut")
    public ResponseEntity<?> modifierStatutVisite(@PathVariable Integer visiteId, 
                                                 @RequestBody Map<String, String> request) {
        try {
            String nouveauStatut = request.get("statut");
            if (nouveauStatut == null || nouveauStatut.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le statut est requis"));
            }

            Visite visite = visiteService.modifierStatutVisite(visiteId, nouveauStatut);
            return ResponseEntity.ok(visite);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/visites/{id} - Obtenir une visite par ID
    @GetMapping("/{visiteId}")
    public ResponseEntity<?> obtenirVisiteParId(@PathVariable Integer visiteId) {
        try {
            Optional<Visite> visite = visiteService.obtenirVisiteParId(visiteId);
            if (visite.isPresent()) {
                return ResponseEntity.ok(visite.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/visites/statut/{statut} - Obtenir les visites par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<?> obtenirVisitesParStatut(@PathVariable String statut) {
        try {
            List<Visite> visites = visiteService.obtenirVisitesParStatut(statut);
            return ResponseEntity.ok(visites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/visites/a-venir - Obtenir les visites à venir
    @GetMapping("/a-venir")
    public ResponseEntity<List<Visite>> obtenirVisitesAVenir() {
        List<Visite> visites = visiteService.obtenirVisitesAVenir();
        return ResponseEntity.ok(visites);
    }

    // GET /api/visites/passees - Obtenir les visites passées
    @GetMapping("/passees")
    public ResponseEntity<List<Visite>> obtenirVisitesPassees() {
        List<Visite> visites = visiteService.obtenirVisitesPassees();
        return ResponseEntity.ok(visites);
    }

    // PUT /api/visites/{id}/annuler - Annuler une visite
    @PutMapping("/{visiteId}/annuler")
    public ResponseEntity<?> annulerVisite(@PathVariable Integer visiteId) {
        try {
            visiteService.annulerVisite(visiteId);
            return ResponseEntity.ok(Map.of("message", "Visite annulée avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/visites/{id} - Supprimer une visite
    @DeleteMapping("/{visiteId}")
    public ResponseEntity<?> supprimerVisite(@PathVariable Integer visiteId) {
        try {
            visiteService.supprimerVisite(visiteId);
            return ResponseEntity.ok(Map.of("message", "Visite supprimée avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/visites/date/{date} - Obtenir les visites confirmées pour une date
    @GetMapping("/date/{date}")
    public ResponseEntity<?> obtenirVisitesParDate(@PathVariable String date) {
        try {
            LocalDate dateVisite = LocalDate.parse(date);
            List<Visite> visites = visiteService.obtenirVisitesConfirmeesParDate(dateVisite);
            return ResponseEntity.ok(visites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/visites/stats/annonce/{annonceId} - Statistiques de visites pour une annonce
    @GetMapping("/stats/annonce/{annonceId}")
    public ResponseEntity<?> obtenirStatsVisitesAnnonce(@PathVariable Integer annonceId) {
        try {
            Long totalVisites = visiteService.compterVisitesParAnnonce(annonceId);
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalVisites", totalVisites);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/visites/stats/acquereur/{acquereurId} - Statistiques de visites pour un acquéreur
    @GetMapping("/stats/acquereur/{acquereurId}")
    public ResponseEntity<?> obtenirStatsVisitesAcquereur(@PathVariable Integer acquereurId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("visitesDemandees", visiteService.compterVisitesParAcquereurEtStatut(acquereurId, "DEMANDEE"));
            stats.put("visitesConfirmees", visiteService.compterVisitesParAcquereurEtStatut(acquereurId, "CONFIRMEE"));
            stats.put("visitesEffectuees", visiteService.compterVisitesParAcquereurEtStatut(acquereurId, "EFFECTUEE"));
            stats.put("visitesAnnulees", visiteService.compterVisitesParAcquereurEtStatut(acquereurId, "ANNULEE"));
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoints pour l'admin
    
    // GET /api/visites/admin/toutes - Toutes les visites (pour admin)
    @GetMapping("/admin/toutes")
    public ResponseEntity<List<Visite>> obtenirToutesLesVisites() {
        List<Visite> visites = visiteService.obtenirToutesLesVisites();
        return ResponseEntity.ok(visites);
    }

    // GET /api/visites/admin/demandees - Visites en attente de confirmation (pour admin)
    @GetMapping("/admin/demandees")
    public ResponseEntity<List<Visite>> obtenirVisitesDemandees() {
        List<Visite> visites = visiteService.obtenirVisitesDemandees();
        return ResponseEntity.ok(visites);
    }
}