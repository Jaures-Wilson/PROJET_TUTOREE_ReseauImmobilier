package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.Contrat;
import com.reseauimmobilier.service.ContratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/contrats")
@CrossOrigin(origins = "http://localhost:3000")
public class ContratController {

    @Autowired
    private ContratService contratService;

    // POST /api/contrats → Générer un contrat pour une annonce
    @PostMapping
    public ResponseEntity<?> creerContrat(@RequestBody Contrat contrat) {
        try {
            Contrat nouveauContrat = contratService.creerContrat(contrat);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauContrat);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/contrats/{id} → Récupérer un contrat par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenirContrat(@PathVariable Integer id) {
        try {
            Optional<Contrat> contrat = contratService.obtenirContratParId(id);
            if (contrat.isPresent()) {
                return ResponseEntity.ok(contrat.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/contrats → Récupérer tous les contrats
    @GetMapping
    public ResponseEntity<?> obtenirTousLesContrats() {
        try {
            List<Contrat> contrats = contratService.obtenirTousLesContrats();
            return ResponseEntity.ok(contrats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // PUT /api/contrats/{id}/valider → Valider un contrat
    @PutMapping("/{id}/valider")
    public ResponseEntity<?> validerContrat(@PathVariable Integer id, @RequestBody Map<String, Integer> payload) {
        try {
            Integer acquereurId = payload.get("acquereurId");
            if (acquereurId == null) {
                return ResponseEntity.badRequest().body(Map.of("erreur", "ID de l'acquéreur requis"));
            }
            
            Contrat contratValide = contratService.validerContrat(id, acquereurId);
            return ResponseEntity.ok(contratValide);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // PUT /api/contrats/{id}/refuser → Refuser un contrat
    @PutMapping("/{id}/refuser")
    public ResponseEntity<?> refuserContrat(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        try {
            Integer acquereurId = (Integer) payload.get("acquereurId");
            String motif = (String) payload.get("motif");
            
            if (acquereurId == null) {
                return ResponseEntity.badRequest().body(Map.of("erreur", "ID de l'acquéreur requis"));
            }
            
            Contrat contratRefuse = contratService.refuserContrat(id, acquereurId, motif);
            return ResponseEntity.ok(contratRefuse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/acquereur/{id}/contrats → Récupérer tous les contrats de l'acquéreur
    @GetMapping("/acquereur/{id}")
    public ResponseEntity<?> obtenirContratsAcquereur(@PathVariable Integer id) {
        try {
            List<Contrat> contrats = contratService.obtenirContratsParAcquereur(id);
            return ResponseEntity.ok(contrats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/acquereur/{id}/contrats/non-signes → Récupérer les contrats non signés (en attente)
    @GetMapping("/acquereur/{id}/non-signes")
    public ResponseEntity<?> obtenirContratsNonSignes(@PathVariable Integer id) {
        try {
            List<Contrat> contrats = contratService.obtenirContratsNonSignesParAcquereur(id);
            return ResponseEntity.ok(contrats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/acquereur/{id}/contrats/signes → Récupérer les contrats validés
    @GetMapping("/acquereur/{id}/signes")
    public ResponseEntity<?> obtenirContratsSignes(@PathVariable Integer id) {
        try {
            List<Contrat> contrats = contratService.obtenirContratsSignesParAcquereur(id);
            return ResponseEntity.ok(contrats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/contrats/annonceur/{id} → Récupérer les contrats d'un annonceur
    @GetMapping("/annonceur/{id}")
    public ResponseEntity<?> obtenirContratsAnnonceur(@PathVariable Integer id) {
        try {
            List<Contrat> contrats = contratService.obtenirContratsParAnnonceur(id);
            return ResponseEntity.ok(contrats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/contrats/type/{type} → Récupérer les contrats par type
    @GetMapping("/type/{type}")
    public ResponseEntity<?> obtenirContratsParType(@PathVariable String type) {
        try {
            Contrat.TypeContrat typeContrat = Contrat.TypeContrat.valueOf(type.toUpperCase());
            List<Contrat> contrats = contratService.obtenirContratsParType(typeContrat);
            return ResponseEntity.ok(contrats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", "Type de contrat invalide"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // PUT /api/contrats/{id} → Modifier un contrat
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierContrat(@PathVariable Integer id, @RequestBody Contrat contratModifie) {
        try {
            Contrat contrat = contratService.modifierContrat(id, contratModifie);
            return ResponseEntity.ok(contrat);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // POST /api/contrats/{id}/signataires → Ajouter un signataire à un contrat
    @PostMapping("/{id}/signataires")
    public ResponseEntity<?> ajouterSignataire(@PathVariable Integer id, @RequestBody Map<String, Integer> payload) {
        try {
            Integer utilisateurId = payload.get("utilisateurId");
            if (utilisateurId == null) {
                return ResponseEntity.badRequest().body(Map.of("erreur", "ID de l'utilisateur requis"));
            }
            
            Contrat contrat = contratService.ajouterSignataire(id, utilisateurId);
            return ResponseEntity.ok(contrat);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // DELETE /api/contrats/{id} → Supprimer un contrat
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerContrat(@PathVariable Integer id) {
        try {
            contratService.supprimerContrat(id);
            return ResponseEntity.ok(Map.of("message", "Contrat supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/contrats/statistiques → Statistiques des contrats
    @GetMapping("/statistiques")
    public ResponseEntity<?> obtenirStatistiques() {
        try {
            Map<String, Object> statistiques = Map.of(
                "totalContrats", contratService.obtenirTousLesContrats().size(),
                "contratsVente", contratService.compterContratsParType(Contrat.TypeContrat.VENTE),
                "contratsPromesse", contratService.compterContratsParType(Contrat.TypeContrat.PROMESSE_VENTE),
                "contratsValides", contratService.compterContratsValides(),
                "contratsRecents", contratService.obtenirContratsRecents(30).size()
            );
            
            return ResponseEntity.ok(statistiques);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    // GET /api/contrats/annonce/{annonceId}/existe → Vérifier si un contrat existe pour une annonce
    @GetMapping("/annonce/{annonceId}/existe")
    public ResponseEntity<?> contratExistePourAnnonce(@PathVariable Integer annonceId) {
        try {
            boolean existe = contratService.contratExistePourAnnonce(annonceId);
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
}