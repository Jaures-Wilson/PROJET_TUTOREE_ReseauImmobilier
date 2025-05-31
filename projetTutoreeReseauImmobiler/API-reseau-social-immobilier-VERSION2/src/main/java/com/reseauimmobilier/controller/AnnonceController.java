package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.Annonce;
import com.reseauimmobilier.service.AnnonceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/annonces")
@CrossOrigin(origins = "http://localhost:3000")
public class AnnonceController {
    
    @Autowired
    private AnnonceService annonceService;
    
    // Créer une nouvelle annonce
    @PostMapping
    public ResponseEntity<?> creerAnnonce(@RequestBody Annonce annonce, 
                                        @RequestParam Integer idAnnonceur) {
        try {
            Annonce nouvelleAnnonce = annonceService.creerAnnonce(annonce, idAnnonceur);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleAnnonce);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    // Obtenir toutes les annonces
    @GetMapping
    public ResponseEntity<List<Annonce>> obtenirToutesLesAnnonces() {
        List<Annonce> annonces = annonceService.obtenirToutesLesAnnonces();
        return ResponseEntity.ok(annonces);
    }
    
    // Obtenir une annonce par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenirAnnonceParId(@PathVariable Integer id,
                                               @RequestParam(defaultValue = "false") boolean incrementerVue) {
        try {
            Optional<Annonce> annonce;
            if (incrementerVue) {
                annonce = annonceService.obtenirAnnonceParIdAvecVue(id);
            } else {
                annonce = annonceService.obtenirAnnonceParId(id);
            }
            
            if (annonce.isPresent()) {
                return ResponseEntity.ok(annonce.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    // Modifier une annonce
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierAnnonce(@PathVariable Integer id,
                                           @RequestBody Annonce annonceModifiee,
                                           @RequestParam Integer idAnnonceur) {
        try {
            Annonce annonce = annonceService.modifierAnnonce(id, annonceModifiee, idAnnonceur);
            return ResponseEntity.ok(annonce);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    // Supprimer une annonce
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerAnnonce(@PathVariable Integer id,
                                            @RequestParam Integer idAnnonceur) {
        try {
            annonceService.supprimerAnnonce(id, idAnnonceur);
            return ResponseEntity.ok().body("Annonce supprimée avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    // Changer le statut d'une annonce
    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatutAnnonce(@PathVariable Integer id,
    												@RequestParam(required = false) Annonce.Status status,
                                                @RequestParam Integer idAnnonceur) {
        try {
            Annonce annonce = annonceService.changerStatutAnnonce(id, status, idAnnonceur);
            return ResponseEntity.ok(annonce);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    // Rechercher des annonces avec filtres
    @GetMapping("/recherche")
    public ResponseEntity<List<Annonce>> rechercherAnnonces(
            @RequestParam(required = false) String localisation,
            @RequestParam(required = false) Integer prixMin,
            @RequestParam(required = false) Integer prixMax,
            @RequestParam(required = false) Annonce.Status status,
            @RequestParam(required = false) String motCle) {
        
        List<Annonce> annonces = annonceService.rechercherAnnonces(localisation, prixMin, prixMax, status, motCle);
        return ResponseEntity.ok(annonces);
    }
    
    // Obtenir les annonces disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<Annonce>> obtenirAnnoncesDisponibles() {
        List<Annonce> annonces = annonceService.obtenirAnnoncesDisponibles();
        return ResponseEntity.ok(annonces);
    }
    
    // Obtenir les annonces par prix croissant
    @GetMapping("/prix-croissant")
    public ResponseEntity<List<Annonce>> obtenirAnnoncesParPrixCroissant() {
        List<Annonce> annonces = annonceService.obtenirAnnoncesParPrixCroissant();
        return ResponseEntity.ok(annonces);
    }
    
    // Obtenir les annonces par prix décroissant
    @GetMapping("/prix-decroissant")
    public ResponseEntity<List<Annonce>> obtenirAnnoncesParPrixDecroissant() {
        List<Annonce> annonces = annonceService.obtenirAnnoncesParPrixDecroissant();
        return ResponseEntity.ok(annonces);
    }
    
    // Obtenir les annonces populaires
    @GetMapping("/populaires")
    public ResponseEntity<List<Annonce>> obtenirAnnoncesPopulaires() {
        List<Annonce> annonces = annonceService.obtenirAnnoncesPopulaires();
        return ResponseEntity.ok(annonces);
    }
    
    // Obtenir les annonces récentes
    @GetMapping("/recentes")
    public ResponseEntity<List<Annonce>> obtenirAnnoncesRecentes() {
        List<Annonce> annonces = annonceService.obtenirAnnoncesRecentes();
        return ResponseEntity.ok(annonces);
    }
    
    // Rechercher par mot-clé
    @GetMapping("/mot-cle")
    public ResponseEntity<List<Annonce>> rechercherParMotCle(@RequestParam String motCle) {
        List<Annonce> annonces = annonceService.rechercherParMotCle(motCle);
        return ResponseEntity.ok(annonces);
    }
    
    // Obtenir les statistiques d'une annonce
    @GetMapping("/{id}/statistiques")
    public ResponseEntity<?> obtenirStatistiquesAnnonce(@PathVariable Integer id,
                                                       @RequestParam Integer idAnnonceur) {
        try {
            Map<String, Object> statistiques = annonceService.obtenirStatistiquesAnnonce(id, idAnnonceur);
            return ResponseEntity.ok(statistiques);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    // Incrémenter les favoris
    @PutMapping("/{id}/favoris/incrementer")
    public ResponseEntity<?> incrementerFavoris(@PathVariable Integer id) {
        try {
            annonceService.incrementerFavoris(id);
            return ResponseEntity.ok().body("Favori ajouté");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    // Décrémenter les favoris
    @PutMapping("/{id}/favoris/decrementer")
    public ResponseEntity<?> decrementerFavoris(@PathVariable Integer id) {
        try {
            annonceService.decrementerFavoris(id);
            return ResponseEntity.ok().body("Favori retiré");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
    
    // Incrémenter les visites
    @PutMapping("/{id}/visites/incrementer")
    public ResponseEntity<?> incrementerVisites(@PathVariable Integer id) {
        try {
            annonceService.incrementerVisites(id);
            return ResponseEntity.ok().body("Visite ajoutée");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}