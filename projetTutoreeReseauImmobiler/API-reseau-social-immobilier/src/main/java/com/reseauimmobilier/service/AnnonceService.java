package com.reseauimmobilier.service;

import com.reseauimmobilier.model.Annonce;
import com.reseauimmobilier.model.Annonceur;
import com.reseauimmobilier.repository.AnnonceRepository;
import com.reseauimmobilier.repository.AnnonceurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AnnonceService {
    
    @Autowired
    private AnnonceRepository annonceRepository;
    
    @Autowired
    private AnnonceurRepository annonceurRepository;
    
    // Créer une nouvelle annonce
    @Transactional
    public Annonce creerAnnonce(Annonce annonce, Integer idAnnonceur) {
        Optional<Annonceur> annonceurOpt = annonceurRepository.findById(idAnnonceur);
        if (!annonceurOpt.isPresent()) {
            throw new RuntimeException("Annonceur non trouvé");
        }
        
        annonce.setAnnonceur(annonceurOpt.get());
        annonce.setDatePublication(LocalDateTime.now());
        annonce.setStatus(Annonce.Status.DISPONIBLE);
        annonce.setNombreVue(0);
        annonce.setNombreFavori(0);
        annonce.setNombreVisite(0);
        
        return annonceRepository.save(annonce);
    }
    
    // Obtenir toutes les annonces
    public List<Annonce> obtenirToutesLesAnnonces() {
        return annonceRepository.findAll();
    }
    
    // Obtenir une annonce par ID
    public Optional<Annonce> obtenirAnnonceParId(Integer id) {
        return annonceRepository.findById(id);
    }
    
    // Obtenir une annonce par ID avec incrémentation des vues
    @Transactional
    public Optional<Annonce> obtenirAnnonceParIdAvecVue(Integer id) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(id);
        if (annonceOpt.isPresent()) {
            Annonce annonce = annonceOpt.get();
            annonce.incrementerVues();
            annonceRepository.save(annonce);
        }
        return annonceOpt;
    }
    
    // Obtenir les annonces d'un annonceur
    public List<Annonce> obtenirAnnoncesParAnnonceur(Integer idAnnonceur) {
        return annonceRepository.findByAnnonceurIdUtilisateur(idAnnonceur);
    }
    
    // Modifier une annonce
    @Transactional
    public Annonce modifierAnnonce(Integer idAnnonce, Annonce annonceModifiee, Integer idAnnonceur) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(idAnnonce);
        if (!annonceOpt.isPresent()) {
            throw new RuntimeException("Annonce non trouvée");
        }
        
        Annonce annonce = annonceOpt.get();
        
        // Vérifier que l'annonceur est propriétaire de l'annonce
        if (!annonce.getAnnonceur().getIdUtilisateur().equals(idAnnonceur)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cette annonce");
        }
        
        // Mettre à jour les champs modifiables
        if (annonceModifiee.getTitre() != null) {
            annonce.setTitre(annonceModifiee.getTitre());
        }
        if (annonceModifiee.getDescription() != null) {
            annonce.setDescription(annonceModifiee.getDescription());
        }
        if (annonceModifiee.getPrixMensuel() != null) {
            annonce.setPrixMensuel(annonceModifiee.getPrixMensuel());
        }
        if (annonceModifiee.getLocalisation() != null) {
            annonce.setLocalisation(annonceModifiee.getLocalisation());
        }
        if (annonceModifiee.getTarifReservation() != null) {
            annonce.setTarifReservation(annonceModifiee.getTarifReservation());
        }
        
        return annonceRepository.save(annonce);
    }
    
    // Supprimer une annonce
    @Transactional
    public void supprimerAnnonce(Integer idAnnonce, Integer idAnnonceur) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(idAnnonce);
        if (!annonceOpt.isPresent()) {
            throw new RuntimeException("Annonce non trouvée");
        }
        
        Annonce annonce = annonceOpt.get();
        
        // Vérifier que l'annonceur est propriétaire de l'annonce
        if (!annonce.getAnnonceur().getIdUtilisateur().equals(idAnnonceur)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cette annonce");
        }
        
        annonceRepository.deleteById(idAnnonce);
    }
    
    // Changer le statut d'une annonce
    @Transactional
    public Annonce changerStatutAnnonce(Integer idAnnonce, Annonce.Status nouveauStatut, Integer idAnnonceur) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(idAnnonce);
        if (!annonceOpt.isPresent()) {
            throw new RuntimeException("Annonce non trouvée");
        }

        Annonce annonce = annonceOpt.get();

        // Vérifier que l'annonceur est propriétaire de l'annonce
        if (!annonce.getAnnonceur().getIdUtilisateur().equals(idAnnonceur)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cette annonce");
        }

        try {
        	annonce.setStatus(nouveauStatut);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide: " + nouveauStatut);
        }

        return annonceRepository.save(annonce);
    }

    
    // Rechercher des annonces avec filtres
    public List<Annonce> rechercherAnnonces(String localisation, Integer prixMin, Integer prixMax, 
    								Annonce.Status status, String motCle) {
        return annonceRepository.rechercherAvecFiltres(localisation, prixMin, prixMax, status, motCle);
    }
    
    // Obtenir les annonces disponibles
    public List<Annonce> obtenirAnnoncesDisponibles() {
        return annonceRepository.findByStatus("DISPONIBLE");
    }
    
    // Obtenir les annonces par prix croissant
    public List<Annonce> obtenirAnnoncesParPrixCroissant() {
        return annonceRepository.findByStatusOrderByPrixMensuelAsc("DISPONIBLE");
    }
    
    // Obtenir les annonces par prix décroissant
    public List<Annonce> obtenirAnnoncesParPrixDecroissant() {
        return annonceRepository.findByStatusOrderByPrixMensuelDesc("DISPONIBLE");
    }
    
    // Obtenir les annonces les plus populaires
    public List<Annonce> obtenirAnnoncesPopulaires() {
        return annonceRepository.findTop10ByOrderByNombreVueDesc();
    }
    
    // Obtenir les annonces récentes
    public List<Annonce> obtenirAnnoncesRecentes() {
        return annonceRepository.findTop10ByOrderByDatePublicationDesc();
    }
    
    // Rechercher par mot-clé
    public List<Annonce> rechercherParMotCle(String motCle) {
        return annonceRepository.rechercherParMotCle(motCle);
    }
    
    // Obtenir les statistiques d'une annonce
    public Map<String, Object> obtenirStatistiquesAnnonce(Integer idAnnonce, Integer idAnnonceur) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(idAnnonce);
        if (!annonceOpt.isPresent()) {
            throw new RuntimeException("Annonce non trouvée");
        }
        
        Annonce annonce = annonceOpt.get();
        
        // Vérifier que l'annonceur est propriétaire de l'annonce
        if (!annonce.getAnnonceur().getIdUtilisateur().equals(idAnnonceur)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à consulter ces statistiques");
        }
        
        Map<String, Object> statistiques = new HashMap<>();
        statistiques.put("nombreVues", annonce.getNombreVue());
        statistiques.put("nombreFavoris", annonce.getNombreFavori());
        statistiques.put("nombreVisites", annonce.getNombreVisite());
        statistiques.put("datePublication", annonce.getDatePublication());
        statistiques.put("status", annonce.getStatus());
        
        return statistiques;
    }
    
    // Obtenir les statistiques globales d'un annonceur
    public Map<String, Object> obtenirStatistiquesAnnonceur(Integer idAnnonceur) {
        Map<String, Object> statistiques = new HashMap<>();
        
        long nombreAnnonces = annonceRepository.countByAnnonceurIdUtilisateur(idAnnonceur);
        Long totalVues = annonceRepository.sommeVuesParAnnonceur(idAnnonceur);
        Long totalFavoris = annonceRepository.sommeFavorisParAnnonceur(idAnnonceur);
        Long totalVisites = annonceRepository.sommeVisitesParAnnonceur(idAnnonceur);
        
        statistiques.put("nombreAnnonces", nombreAnnonces);
        statistiques.put("totalVues", totalVues != null ? totalVues : 0);
        statistiques.put("totalFavoris", totalFavoris != null ? totalFavoris : 0);
        statistiques.put("totalVisites", totalVisites != null ? totalVisites : 0);
        
        return statistiques;
    }
    
    // Incrémenter le nombre de favoris
    @Transactional
    public void incrementerFavoris(Integer idAnnonce) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(idAnnonce);
        if (annonceOpt.isPresent()) {
            Annonce annonce = annonceOpt.get();
            annonce.incrementerFavoris();
            annonceRepository.save(annonce);
        }
    }
    
    // Décrémenter le nombre de favoris
    @Transactional
    public void decrementerFavoris(Integer idAnnonce) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(idAnnonce);
        if (annonceOpt.isPresent()) {
            Annonce annonce = annonceOpt.get();
            annonce.decrementerFavoris();
            annonceRepository.save(annonce);
        }
    }
    
    // Incrémenter le nombre de visites
    @Transactional
    public void incrementerVisites(Integer idAnnonce) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(idAnnonce);
        if (annonceOpt.isPresent()) {
            Annonce annonce = annonceOpt.get();
            annonce.incrementerVisites();
            annonceRepository.save(annonce);
        }
    }
}