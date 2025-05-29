package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.Favoris;
import com.reseauimmobilier.service.FavorisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoris")
@CrossOrigin(origins = "*")
public class FavorisController {

    @Autowired
    private FavorisService favorisService;

    // POST /api/favoris - Ajouter une annonce aux favoris
    @PostMapping
    public ResponseEntity<?> ajouterAuxFavoris(@RequestBody Map<String, Integer> request) {
        try {
            Integer idAcquereur = request.get("idAcquereur");
            Integer idAnnonce = request.get("idAnnonce");

            if (idAcquereur == null || idAnnonce == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("erreur", "Les paramètres idAcquereur et idAnnonce sont requis"));
            }

            Favoris favori = favorisService.ajouterAuxFavoris(idAcquereur, idAnnonce);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Annonce ajoutée aux favoris avec succès");
            response.put("favori", favori);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erreur", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne du serveur"));
        }
    }

    // GET /api/favoris/{acquereurId} - Voir ses annonces en favoris
    @GetMapping("/{acquereurId}")
    public ResponseEntity<?> obtenirFavorisAcquereur(@PathVariable Integer acquereurId) {
        try {
            List<Favoris> favoris = favorisService.obtenirFavorisAcquereur(acquereurId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("favoris", favoris);
            response.put("total", favoris.size());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erreur", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne du serveur"));
        }
    }

    // GET /api/favoris/{acquereurId}/recents - Voir ses favoris récents
    @GetMapping("/{acquereurId}/recents")
    public ResponseEntity<?> obtenirFavorisRecents(@PathVariable Integer acquereurId) {
        try {
            List<Favoris> favoris = favorisService.obtenirFavorisRecentsAcquereur(acquereurId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("favoris", favoris);
            response.put("total", favoris.size());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erreur", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne du serveur"));
        }
    }

    // DELETE /api/favoris/{idAcquereur}/{idAnnonce} - Supprimer des favoris
    @DeleteMapping("/{idAcquereur}/{idAnnonce}")
    public ResponseEntity<?> supprimerDesFavoris(
            @PathVariable Integer idAcquereur, 
            @PathVariable Integer idAnnonce) {
        try {
            favorisService.supprimerDesFavoris(idAcquereur, idAnnonce);
            
            return ResponseEntity.ok(Map.of("message", "Annonce supprimée des favoris avec succès"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erreur", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne du serveur"));
        }
    }

    // GET /api/favoris/verifier/{idAcquereur}/{idAnnonce} - Vérifier si en favoris
    @GetMapping("/verifier/{idAcquereur}/{idAnnonce}")
    public ResponseEntity<?> verifierSiEnFavoris(
            @PathVariable Integer idAcquereur, 
            @PathVariable Integer idAnnonce) {
        try {
            boolean estEnFavoris = favorisService.estEnFavoris(idAcquereur, idAnnonce);
            
            Map<String, Object> response = new HashMap<>();
            response.put("estEnFavoris", estEnFavoris);
            response.put("idAcquereur", idAcquereur);
            response.put("idAnnonce", idAnnonce);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne du serveur"));
        }
    }

    // POST /api/favoris/basculer - Basculer le statut favori (ajouter/supprimer)
    @PostMapping("/basculer")
    public ResponseEntity<?> basculerFavori(@RequestBody Map<String, Integer> request) {
        try {
            Integer idAcquereur = request.get("idAcquereur");
            Integer idAnnonce = request.get("idAnnonce");

            if (idAcquereur == null || idAnnonce == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("erreur", "Les paramètres idAcquereur et idAnnonce sont requis"));
            }

            boolean ajoute = favorisService.basculerFavori(idAcquereur, idAnnonce);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", ajoute ? "Ajouté aux favoris" : "Supprimé des favoris");
            response.put("estEnFavoris", ajoute);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erreur", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne du serveur"));
        }
    }

    // GET /api/favoris/compter/{acquereurId} - Compter les favoris d'un acquéreur
    @GetMapping("/compter/{acquereurId}")
    public ResponseEntity<?> compterFavorisAcquereur(@PathVariable Integer acquereurId) {
        try {
            long nombreFavoris = favorisService.compterFavorisAcquereur(acquereurId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("nombreFavoris", nombreFavoris);
            response.put("idAcquereur", acquereurId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne du serveur"));
        }
    }

    // GET /api/favoris/annonce/{annonceId} - Voir qui a mis cette annonce en favoris
    @GetMapping("/annonce/{annonceId}")
    public ResponseEntity<?> obtenirFavorisAnnonce(@PathVariable Integer annonceId) {
        try {
            List<Favoris> favoris = favorisService.obtenirFavorisAnnonce(annonceId);
            long nombreFavoris = favorisService.compterFavorisAnnonce(annonceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("favoris", favoris);
            response.put("nombreFavoris", nombreFavoris);
            response.put("idAnnonce", annonceId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne du serveur"));
        }
    }

    // DELETE /api/favoris/acquereur/{acquereurId} - Supprimer tous les favoris d'un acquéreur
    @DeleteMapping("/acquereur/{acquereurId}")
    public ResponseEntity<?> supprimerTousFavorisAcquereur(@PathVariable Integer acquereurId) {
        try {
            favorisService.supprimerTousFavorisAcquereur(acquereurId);
            
            return ResponseEntity.ok(Map.of("message", "Tous les favoris ont été supprimés avec succès"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("erreur", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne du serveur"));
        }
    }

    // GET /api/favoris/populaires - Obtenir les annonces les plus populaires
    @GetMapping("/populaires")
    public ResponseEntity<?> obtenirAnnoncesPopulaires() {
        try {
            List<Object[]> statistiques = favorisService.obtenirAnnoncesLesPlusPopulaires();
            
            Map<String, Object> response = new HashMap<>();
            response.put("statistiques", statistiques);
            response.put("total", statistiques.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erreur", "Erreur interne du serveur"));
        }
    }
}