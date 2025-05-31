package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.Annonce;
import com.reseauimmobilier.model.Paiement;
import com.reseauimmobilier.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = "http://localhost:3000")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    // POST /api/acquereur/{id}/paiements → Effectuer un paiement avec capture de preuve
    @PostMapping("/acquereur/{acquereurId}")
    public ResponseEntity<?> effectuerPaiement(
            @PathVariable Integer  acquereurId,
            @RequestParam("montant") Integer montant,
            @RequestParam("moyenPaiement") Paiement.MoyenPaiement moyenPaiement,
            @RequestParam("typeContrat") Paiement.TypeContrat typeContrat,
            @RequestParam("annonceId") Integer  annonceId,
            @RequestParam("annonceurId") Integer  annonceurId,
            @RequestParam("photoPreuve") MultipartFile photoPreuve) {
        
        try {
            // Construire l'objet Paiement
            Paiement paiement = new Paiement();
            paiement.setMontant(montant);
            paiement.setMoyenPaiement(moyenPaiement);
            paiement.setTypeContrat(typeContrat);
            
            // Créer les objets liés (simplifiés)
            com.reseauimmobilier.model.Acquereur acquereur = new com.reseauimmobilier.model.Acquereur();
            acquereur.setIdUtilisateur(acquereurId);
            paiement.setAcquereur(acquereur);
            
            com.reseauimmobilier.model.Annonce annonce = new com.reseauimmobilier.model.Annonce();
            annonce.setIdAnnonce(annonceId);
            paiement.setAnnonce(annonce);
            
            com.reseauimmobilier.model.Annonceur annonceur = new com.reseauimmobilier.model.Annonceur();
            annonceur.setIdUtilisateur(annonceurId);
            paiement.setAnnonceur(annonceur);

            Paiement nouveauPaiement = paiementService.effectuerPaiement(paiement, photoPreuve);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauPaiement);
            
    } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erreur", "Erreur lors du traitement du paiement"));
        }
    }

    // GET /api/paiements/{id} → Récupérer un paiement par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenirPaiement(@PathVariable Integer id) {
        try {
            Optional<Paiement> paiement = paiementService.obtenirPaiementParId(id);
            if (paiement.isPresent()) {
                return ResponseEntity.ok(paiement.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements → Récupérer tous les paiements
    @GetMapping
    public ResponseEntity<?> obtenirTousLesPaiements() {
        try {
            List<Paiement> paiements = paiementService.obtenirTousLesPaiements();
            return ResponseEntity.ok(paiements);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/acquereur/{id} → Paiements effectués par un acquéreur
    @GetMapping("/acquereur/{id}")
    public ResponseEntity<?> obtenirPaiementsAcquereur(@PathVariable Integer id) {
        try {
            List<Paiement> paiements = paiementService.obtenirPaiementsParAcquereur(id);
            return ResponseEntity.ok(paiements);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/annonceur/{id} → Paiements reçus par un annonceur
    @GetMapping("/annonceur/{id}")
    public ResponseEntity<?> obtenirPaiementsAnnonceur(@PathVariable Integer id) {
        try {
            List<Paiement> paiements = paiementService.obtenirPaiementsParAnnonceur(id);
            return ResponseEntity.ok(paiements);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/en-attente → Paiements en attente de validation
    @GetMapping("/en-attente")
    public ResponseEntity<?> obtenirPaiementsEnAttente() {
        try {
            List<Paiement> paiements = paiementService.obtenirPaiementsEnAttente();
            return ResponseEntity.ok(paiements);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/confirmes → Paiements confirmés
    @GetMapping("/confirmes")
    public ResponseEntity<?> obtenirPaiementsConfirmes() {
        try {
            List<Paiement> paiements = paiementService.obtenirPaiementsConfirmes();
            return ResponseEntity.ok(paiements);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/statut/{statut} → Paiements par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<?> obtenirPaiementsParStatut(@PathVariable Paiement.StatutPaiement statut) {
        try {
            List<Paiement> paiements = paiementService.obtenirPaiementsParStatut(statut);
            return ResponseEntity.ok(paiements);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", "Statut de paiement invalide"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // PUT /api/paiements/{id}/valider → Validation manuelle par l'admin
    @PutMapping("/{id}/valider")
    public ResponseEntity<?> validerPaiement(@PathVariable Integer id) {
        try {
            Paiement paiementValide = paiementService.validerPaiement(id);
            return ResponseEntity.ok(paiementValide);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // PUT /api/paiements/{id}/refuser → Refuser un paiement
    @PutMapping("/{id}/refuser")
    public ResponseEntity<?> refuserPaiement(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        try {
            String motif = payload.get("motif");
            Paiement paiementRefuse = paiementService.refuserPaiement(id, motif);
            return ResponseEntity.ok(paiementRefuse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // PUT /api/paiements/{id}/lu → Marquer un paiement comme lu
    @PutMapping("/{id}/lu")
    public ResponseEntity<?> marquerCommeLu(@PathVariable Integer id) {
        try {
            Paiement paiement = paiementService.marquerCommeLu(id);
            return ResponseEntity.ok(paiement);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // POST /api/paiements/{id}/virement → Simuler le virement vers l'annonceur
    @PostMapping("/{id}/virement")
    public ResponseEntity<?> simulerVirement(@PathVariable Integer id) {
        try {
            paiementService.simulerVirementVersAnnonceur(id);
            return ResponseEntity.ok(Map.of("message", "Virement effectué avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // DELETE /api/paiements/{id} → Supprimer un paiement (si non confirmé)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerPaiement(@PathVariable Integer id) {
        try {
            paiementService.supprimerPaiement(id);
            return ResponseEntity.ok(Map.of("message", "Paiement supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/statistiques → Statistiques générales des paiements
    @GetMapping("/statistiques")
    public ResponseEntity<?> obtenirStatistiques() {
        try {
            Map<String, Object> statistiques = paiementService.obtenirStatistiquesPaiements();
            return ResponseEntity.ok(statistiques);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/revenus/annonceur/{id} → Revenus totaux d'un annonceur
    @GetMapping("/revenus/annonceur/{id}")
    public ResponseEntity<?> obtenirRevenusAnnonceur(@PathVariable Integer id) {
        try {
            Long revenus = paiementService.obtenirRevenusTotalAnnonceur(id);
            return ResponseEntity.ok(Map.of("annonceurId", id, "revenus", revenus));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/depenses/acquereur/{id} → Dépenses totales d'un acquéreur
    @GetMapping("/depenses/acquereur/{id}")
    public ResponseEntity<?> obtenirDepensesAcquereur(@PathVariable Integer id) {
        try {
            Long depenses = paiementService.obtenirDepensesTotalAcquereur(id);
            return ResponseEntity.ok(Map.of("acquereurId", id, "depenses", depenses));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/recents → Paiements récents (30 derniers jours par défaut)
    @GetMapping("/recents")
    public ResponseEntity<?> obtenirPaiementsRecents(@RequestParam(defaultValue = "30") int jours) {
        try {
            List<Paiement> paiements = paiementService.obtenirPaiementsRecents(jours);
            return ResponseEntity.ok(paiements);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/periode → Paiements par période
    @GetMapping("/periode")
    public ResponseEntity<?> obtenirPaiementsParPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        try {
            List<Paiement> paiements = paiementService.obtenirPaiementsParPeriode(dateDebut, dateFin);
            return ResponseEntity.ok(paiements);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/paiements/existe/{annonceId}/{acquereurId} → Vérifier si un paiement existe
    @GetMapping("/existe/{annonceId}/{acquereurId}")
    public ResponseEntity<?> verifierExistencePaiement(@PathVariable Integer annonceId, @PathVariable Integer acquereurId) {
        try {
            boolean existe = paiementService.paiementExistePourAnnonce(annonceId, acquereurId);
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // POST /api/paiements/simple → Créer un paiement simple (sans photo, pour les tests)
    @PostMapping("/simple")
    public ResponseEntity<?> creerPaiementSimple(@RequestBody Paiement paiement) {
        try {
            // Pour les tests sans photo obligatoire
            paiement.setCapturePhotoPreuvePaiement(new byte[]{1, 2, 3}); // Photo fictive
            
            Paiement nouveauPaiement = paiementService.effectuerPaiement(paiement, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauPaiement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
}