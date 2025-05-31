package com.reseauimmobilier.service;

import com.reseauimmobilier.model.*;
import com.reseauimmobilier.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FavorisService {

    @Autowired
    private FavorisRepository favorisRepository;

    @Autowired
    private AcquereurRepository acquereurRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    // Ajouter une annonce aux favoris
    @Transactional
    public Favoris ajouterAuxFavoris(Integer idAcquereur, Integer idAnnonce) {
        // Vérifier que l'acquéreur existe
        if (!acquereurRepository.existsById(idAcquereur)) {
            throw new RuntimeException("Acquéreur non trouvé avec l'ID: " + idAcquereur);
        }

        // Vérifier que l'annonce existe
        if (!annonceRepository.existsById(idAnnonce)) {
            throw new RuntimeException("Annonce non trouvée avec l'ID: " + idAnnonce);
        }

        // Vérifier si l'annonce n'est pas déjà en favoris
        if (favorisRepository.existsByAcquereur_IdUtilisateurAndIdAnnonce(idAcquereur, idAnnonce)) {
            throw new RuntimeException("Cette annonce est déjà dans vos favoris");
        }

        // Créer et sauvegarder le favori
        Favoris favori = new Favoris(idAcquereur, idAnnonce);
        Favoris favorisauve = favorisRepository.save(favori);

        // Mettre à jour le compteur de favoris de l'annonce
        mettreAJourCompteurFavorisAnnonce(idAnnonce);

        return favorisauve;
    }

    // Supprimer une annonce des favoris
    @Transactional
    public void supprimerDesFavoris(Integer idAcquereur, Integer idAnnonce) {
        // Vérifier que le favori existe
        if (!favorisRepository.existsByAcquereur_IdUtilisateurAndIdAnnonce(idAcquereur, idAnnonce)) {
            throw new RuntimeException("Cette annonce n'est pas dans vos favoris");
        }

        favorisRepository.deleteByIdAnnonceAndAcquereur_IdUtilisateur(idAcquereur, idAnnonce);

        // Mettre à jour le compteur de favoris de l'annonce
        mettreAJourCompteurFavorisAnnonce(idAnnonce);
    }

    // Obtenir tous les favoris d'un acquéreur
    public List<Favoris> obtenirFavorisAcquereur(Integer idAcquereur) {
        if (!acquereurRepository.existsById(idAcquereur)) {
            throw new RuntimeException("Acquéreur non trouvé avec l'ID: " + idAcquereur);
        }

        return favorisRepository.findByAcquereur_IdUtilisateurWithAnnonce(idAcquereur);
    }

    // Obtenir les favoris récents d'un acquéreur
    public List<Favoris> obtenirFavorisRecentsAcquereur(Integer idAcquereur) {
        if (!acquereurRepository.existsById(idAcquereur)) {
            throw new RuntimeException("Acquéreur non trouvé avec l'ID: " + idAcquereur);
        }

        return favorisRepository.findByAcquereur_IdUtilisateurOrderByDateAjoutDesc(idAcquereur);
    }

    // Vérifier si une annonce est en favoris pour un acquéreur
    public boolean estEnFavoris(Integer idAcquereur, Integer idAnnonce) {
        return favorisRepository.existsByAcquereur_IdUtilisateurAndIdAnnonce(idAcquereur, idAnnonce);
    }

    // Compter le nombre de favoris d'un acquéreur
    public long compterFavorisAcquereur(Integer idAcquereur) {
        return favorisRepository.countByAcquereur_IdUtilisateur(idAcquereur);
    }

    // Compter le nombre de fois qu'une annonce a été mise en favoris
    public long compterFavorisAnnonce(Integer idAnnonce) {
        return favorisRepository.countByIdAnnonce(idAnnonce);
    }

    // Obtenir toutes les annonces mises en favoris (pour statistiques)
    public List<Favoris> obtenirFavorisAnnonce(Integer idAnnonce) {
        return favorisRepository.findByIdAnnonce(idAnnonce);
    }

    // Supprimer tous les favoris d'un acquéreur
    @Transactional
    public void supprimerTousFavorisAcquereur(Integer idAcquereur) {
        if (!acquereurRepository.existsById(idAcquereur)) {
            throw new RuntimeException("Acquéreur non trouvé avec l'ID: " + idAcquereur);
        }

        // Récupérer toutes les annonces en favoris pour mettre à jour leurs compteurs
        List<Favoris> favoris = favorisRepository.findByAcquereur_IdUtilisateur(idAcquereur);
        
        favorisRepository.deleteByAcquereur_IdUtilisateur(idAcquereur);

        // Mettre à jour les compteurs des annonces concernées
        for (Favoris favori : favoris) {
            mettreAJourCompteurFavorisAnnonce(favori.getIdAnnonce());
        }
    }

    // Obtenir les annonces les plus populaires (les plus mises en favoris)
    public List<Object[]> obtenirAnnoncesLesPlusPopulaires() {
        return favorisRepository.findAnnoncesLesPlusEnFavoris();
    }

    // Méthode privée pour mettre à jour le compteur de favoris d'une annonce
    private void mettreAJourCompteurFavorisAnnonce(Integer idAnnonce) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(idAnnonce);
        if (annonceOpt.isPresent()) {
            Annonce annonce = annonceOpt.get();
            long nombreFavoris = favorisRepository.countByIdAnnonce(idAnnonce);
            annonce.setNombreFavori((int) nombreFavoris);
            annonceRepository.save(annonce);
        }
    }

    // Basculer le statut favori (ajouter si pas en favoris, supprimer si en favoris)
    @Transactional
    public boolean basculerFavori(Integer idAcquereur, Integer idAnnonce) {
        if (favorisRepository.existsByAcquereur_IdUtilisateurAndIdAnnonce(idAcquereur, idAnnonce)) {
            supprimerDesFavoris(idAcquereur, idAnnonce);
            return false; // Supprimé des favoris
        } else {
            ajouterAuxFavoris(idAcquereur, idAnnonce);
            return true; // Ajouté aux favoris
        }
    }
}