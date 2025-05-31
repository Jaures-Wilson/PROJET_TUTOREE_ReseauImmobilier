package com.reseauimmobilier.controller;

import com.reseauimmobilier.model.Annonceur;
import com.reseauimmobilier.service.AnnonceurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/annonceurs")
@CrossOrigin(origins = "http://localhost:3000")
public class AnnonceurController {
    
    @Autowired
    private AnnonceurService annonceurService;
    
    @PostMapping
    public ResponseEntity<Annonceur> convertirEnAnnonceur(@RequestBody Map<String, Object> demande) {
        try {
            Integer utilisateurId = (Integer) demande.get("utilisateurId");
            Integer numeroMTN_MoMo = (Integer) demande.get("numeroMTN_MoMo");
            Integer numeroOrangeMoney = (Integer) demande.get("numeroOrangeMoney");
            Integer numeroUBA = (Integer) demande.get("numeroUBA");
            
            if (utilisateurId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Annonceur nouvelAnnonceur = annonceurService.convertirUtilisateurEnAnnonceur(
                utilisateurId, numeroMTN_MoMo, numeroOrangeMoney, numeroUBA);
            return new ResponseEntity<>(nouvelAnnonceur, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Annonceur> obtenirAnnonceur(@PathVariable Integer id) {
        Optional<Annonceur> annonceur = annonceurService.obtenirAnnonceurParId(id);
        if (annonceur.isPresent()) {
            return new ResponseEntity<>(annonceur.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<Annonceur> obtenirAnnonceurParUtilisateurId(@PathVariable Integer utilisateurId) {
        Optional<Annonceur> annonceur = annonceurService.obtenirAnnonceurParUtilisateurId(utilisateurId);
        if (annonceur.isPresent()) {
            return new ResponseEntity<>(annonceur.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    
    @GetMapping
    public ResponseEntity<List<Annonceur>> obtenirTousLesAnnonceurs() {
        List<Annonceur> annonceurs = annonceurService.obtenirTousLesAnnonceurs();
        return new ResponseEntity<>(annonceurs, HttpStatus.OK);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Annonceur> modifierAnnonceur(@PathVariable Integer id, @RequestBody Annonceur annonceur) {
        try {
            Annonceur annonceurModifie = annonceurService.modifierAnnonceur(id, annonceur);
            return new ResponseEntity<>(annonceurModifie, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerAnnonceur(@PathVariable Integer id) {
        try {
            annonceurService.supprimerAnnonceur(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/{id}/statistiques")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesAnnonceur(@PathVariable Integer id) {
        try {
            Optional<Annonceur> annonceur = annonceurService.obtenirAnnonceurParId(id);
            if (annonceur.isPresent()) {
                Long nombreAnnonces = annonceurService.compterAnnoncesParAnnonceur(id);
                Map<String, Object> statistiques = Map.of(
                    "annonceur", annonceur.get(),
                    "nombreAnnonces", nombreAnnonces
                );
                return new ResponseEntity<>(statistiques, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}