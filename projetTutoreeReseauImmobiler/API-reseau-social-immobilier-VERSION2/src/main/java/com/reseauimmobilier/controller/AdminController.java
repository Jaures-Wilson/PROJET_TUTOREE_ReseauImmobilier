package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.*;
import com.reseauimmobilier.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    // Gestion des utilisateurs
    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> obtenirTousLesUtilisateurs() {
        List<Utilisateur> utilisateurs = adminService.obtenirTousLesUtilisateurs();
        return new ResponseEntity<>(utilisateurs, HttpStatus.OK);
    }
    
    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Integer id) {
        try {
            adminService.supprimerUtilisateur(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/utilisateurs/{id}/suspendre")
    public ResponseEntity<Utilisateur> suspendreUtilisateur(@PathVariable Integer id) {
        try {
            Utilisateur utilisateur = adminService.suspendreUtilisateur(id);
            return new ResponseEntity<>(utilisateur, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Gestion des annonces
    @GetMapping("/annonces")
    public ResponseEntity<List<Annonce>> obtenirToutesLesAnnonces() {
        List<Annonce> annonces = adminService.obtenirToutesLesAnnonces();
        return new ResponseEntity<>(annonces, HttpStatus.OK);
    }
    
    @GetMapping("/annonces/validation")
    public ResponseEntity<List<Annonce>> obtenirAnnoncesEnAttenteValidation() {
        List<Annonce> annonces = adminService.obtenirAnnoncesEnAttenteValidation();
        return new ResponseEntity<>(annonces, HttpStatus.OK);
    }
    
    @PutMapping("/annonces/{id}/validation")
    public ResponseEntity<Annonce> validerAnnonce(@PathVariable Integer id, @RequestBody Map<String, Object> decision) {
        try {
            boolean accepter = (Boolean) decision.get("accepter");
            String motif = (String) decision.getOrDefault("motif", "");
            
            Annonce annonce = adminService.validerAnnonce(id, accepter, motif);
            return new ResponseEntity<>(annonce, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/annonces/{id}")
    public ResponseEntity<Void> supprimerAnnonce(@PathVariable Integer id) {
        try {
            adminService.supprimerAnnonce(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Gestion des paiements
    @GetMapping("/paiements")
    public ResponseEntity<List<Paiement>> obtenirTousLesPaiements() {
        List<Paiement> paiements = adminService.obtenirTousLesPaiements();
        return new ResponseEntity<>(paiements, HttpStatus.OK);
    }
    
    @GetMapping("/paiements/attente")
    public ResponseEntity<List<Paiement>> obtenirPaiementsEnAttente() {
        List<Paiement> paiements = adminService.obtenirPaiementsEnAttente();
        return new ResponseEntity<>(paiements, HttpStatus.OK);
    }
    
    @PutMapping("/paiements/{id}/valider")
    public ResponseEntity<Paiement> validerPaiement(@PathVariable Integer id) {
        try {
            Paiement paiement = adminService.validerPaiement(id);
            return new ResponseEntity<>(paiement, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/paiements/{id}/refuser")
    public ResponseEntity<Paiement> refuserPaiement(@PathVariable Integer id, @RequestBody Map<String, String> motifMap) {
        try {
            String motif = motifMap.getOrDefault("motif", "Aucun motif spécifié");
            Paiement paiement = adminService.refuserPaiement(id, motif);
            return new ResponseEntity<>(paiement, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Gestion des plaintes
    @GetMapping("/plaintes")
    public ResponseEntity<List<Plainte>> obtenirToutesLesPlaintes() {
        List<Plainte> plaintes = adminService.obtenirToutesLesPlaintes();
        return new ResponseEntity<>(plaintes, HttpStatus.OK);
    }
    
    @GetMapping("/plaintes/non-lues")
    public ResponseEntity<List<Plainte>> obtenirPlaintesNonLues() {
        List<Plainte> plaintes = adminService.obtenirPlaintesNonLues();
        return new ResponseEntity<>(plaintes, HttpStatus.OK);
    }
    
    @PutMapping("/plaintes/{id}/marquer-lu")
    public ResponseEntity<Plainte> marquerPlainteCommeLue(@PathVariable Integer  id) {
        try {
            Plainte plainte = adminService.marquerPlainteCommeLue(id);
            return new ResponseEntity<>(plainte, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Statistiques
    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesGlobales() {
        Map<String, Object> statistiques = adminService.obtenirStatistiquesGlobales();
        return new ResponseEntity<>(statistiques, HttpStatus.OK);
    }
    
    // Communication
    @PostMapping("/email")
    public ResponseEntity<Map<String, String>> envoyerEmail(@RequestBody Map<String, Object> emailData) {
        try {
        	Integer  utilisateurId = (Integer) emailData.get("utilisateurId");
            String message = (String) emailData.get("message");
            
            adminService.envoyerEmail(utilisateurId, message);
            
            Map<String, String> response = Map.of("status", "Email envoyé avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> response = Map.of("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    // Création d'administrateur
    @PostMapping("/administrateurs")
    public ResponseEntity<Administrateur> creerAdministrateur(@RequestBody Administrateur administrateur) {
        try {
            Administrateur nouvelAdmin = adminService.creerAdministrateur(administrateur);
            return new ResponseEntity<>(nouvelAdmin, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/administrateurs")
    public ResponseEntity<List<Administrateur>> obtenirTousLesAdministrateurs() {
        List<Administrateur> administrateurs = adminService.obtenirTousLesAdministrateurs();
        return new ResponseEntity<>(administrateurs, HttpStatus.OK);
    }
}