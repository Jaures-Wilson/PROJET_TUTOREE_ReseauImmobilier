package com.reseauimmobilier.service;

import com.reseauimmobilier.model.*;
import com.reseauimmobilier.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdministrateurRepository administrateurRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private AnnonceRepository annonceRepository;
    
    @Autowired
    private PaiementRepository paiementRepository;
    
    @Autowired
    private PlainteRepository plainteRepository;
    
    @Autowired
    private AnnonceurRepository annonceurRepository;
    
    public Administrateur creerAdministrateur(Administrateur administrateur) {
        if (administrateurRepository.existsByEmail(administrateur.getEmail())) {
            throw new RuntimeException("Un administrateur avec cet email existe déjà");
        }
        administrateur.setRole(Utilisateur.Role.ADMIN);
        return administrateurRepository.save(administrateur);
    }
    
    public Optional<Administrateur> obtenirAdministrateurParId(Integer id) {
        return administrateurRepository.findById(id);
    }
    
    public List<Administrateur> obtenirTousLesAdministrateurs() {
        return administrateurRepository.findAll();
    }
    
    // Gestion des utilisateurs
    public List<Utilisateur> obtenirTousLesUtilisateurs() {
        return utilisateurRepository.findAll();
    }
    
    @Transactional
    public void supprimerUtilisateur(Integer utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        utilisateurRepository.deleteById(utilisateurId);
    }
    
    @Transactional
    public Utilisateur suspendreUtilisateur(Integer utilisateurId) {
        // Simulation de suspension - dans un vrai système, on ajouterait un champ "statut"
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        if (utilisateur.isPresent()) {
            // Pour la simulation, on peut modifier un autre champ
            return utilisateur.get();
        }
        throw new RuntimeException("Utilisateur non trouvé");
    }
    
    // Gestion des annonces
    public List<Annonce> obtenirToutesLesAnnonces() {
        return annonceRepository.findAll();
    }
    
    public List<Annonce> obtenirAnnoncesEnAttenteValidation() {
        // Simulation - dans un vrai système, on aurait un statut "EN_ATTENTE"
        return annonceRepository.findByStatus("DISPONIBLE");
    }
    
    @Transactional
    public Annonce validerAnnonce(Integer annonceId, boolean accepter, String motif) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(annonceId);
        if (!annonceOpt.isPresent()) {
            throw new RuntimeException("Annonce non trouvée");
        }
        
        Annonce annonce = annonceOpt.get();
        if (accepter) {
            annonce.estDisponible();
        } else {
            annonce.estRefuser();
            // Dans un vrai système, on sauvegarderait le motif
        }
        
        return annonceRepository.save(annonce);
    }
    
    @Transactional
    public void supprimerAnnonce(Integer annonceId) {
        if (!annonceRepository.existsById(annonceId)) {
            throw new RuntimeException("Annonce non trouvée");
        }
        annonceRepository.deleteById(annonceId);
    }
    
    // Gestion des paiements
    public List<Paiement> obtenirTousLesPaiements() {
        return paiementRepository.findAll();
    }
    
    public List<Paiement> obtenirPaiementsEnAttente() {
        return paiementRepository.findByStatut("EN_ATTENTE");
    }
    
    @Transactional
    public Paiement validerPaiement(Integer paiementId) {
        Optional<Paiement> paiementOpt = paiementRepository.findById(paiementId);
        if (!paiementOpt.isPresent()) {
            throw new RuntimeException("Paiement non trouvé");
        }
        
        Paiement paiement = paiementOpt.get();
        paiement.estConfirme();
        paiement.setLu(true);
        
        // Simulation du virement vers l'annonceur
        // Dans un vrai système, on intégrerait une API de paiement
        
        return paiementRepository.save(paiement);
    }
    
    @Transactional
    public Paiement refuserPaiement(Integer paiementId, String motif) {
        Optional<Paiement> paiementOpt = paiementRepository.findById(paiementId);
        if (!paiementOpt.isPresent()) {
            throw new RuntimeException("Paiement non trouvé");
        }
        
        Paiement paiement = paiementOpt.get();
        paiement.estRefuse();
        paiement.setLu(true);
        
        return paiementRepository.save(paiement);
    }
    
    // Gestion des plaintes
    public List<Plainte> obtenirToutesLesPlaintes() {
        return plainteRepository.findAll();
    }
    
    public List<Plainte> obtenirPlaintesNonLues() {
        return plainteRepository.findByStatut("NOUVELLE");
    }
    
    @Transactional
    public Plainte marquerPlainteCommeLue(Integer plainteId) {
        Optional<Plainte> plainteOpt = plainteRepository.findById(plainteId);
        if (!plainteOpt.isPresent()) {
            throw new RuntimeException("Plainte non trouvée");
        }
        
        Plainte plainte = plainteOpt.get();
        plainte.setStatut("LU");
        
        return plainteRepository.save(plainte);
    }
    
    // Statistiques globales
    public Map<String, Object> obtenirStatistiquesGlobales() {
        Map<String, Object> statistiques = new HashMap<>();
        
        statistiques.put("totalUtilisateurs", administrateurRepository.countTotalUtilisateurs());
        statistiques.put("totalAnnonces", administrateurRepository.countTotalAnnonces());
        statistiques.put("totalPaiements", administrateurRepository.countTotalPaiements());
        statistiques.put("totalPlaintes", administrateurRepository.countTotalPlaintes());
        statistiques.put("paiementsEnAttente", paiementRepository.countByStatut("EN_ATTENTE"));
        statistiques.put("plaintesNonLues", plainteRepository.countByStatut("NOUVELLE"));
        
        return statistiques;
    }
    
    // Simulation d'envoi d'email
    public void envoyerEmail(Integer utilisateurId, String message) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        if (utilisateur.isPresent()) {
            // Simulation - dans un vrai système, on utiliserait un service d'email
            System.out.println("Email envoyé à " + utilisateur.get().getEmail() + ": " + message);
        } else {
            throw new RuntimeException("Utilisateur non trouvé");
        }
    }
}