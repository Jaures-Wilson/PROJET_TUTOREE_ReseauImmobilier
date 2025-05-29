package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Favoris;
import com.reseauimmobilier.model.FavorisId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavorisRepository extends JpaRepository<Favoris, FavorisId> {

    // Trouver tous les favoris d'un acquéreur
    List<Favoris> findByAcquereur_IdUtilisateur(Integer idAcquereur);

    // Trouver tous les favoris d'une annonce (qui a mis cette annonce en favori)
    List<Favoris> findByIdAnnonce(Integer idAnnonce);

    // Vérifier si une annonce est en favori pour un acquéreur
    boolean existsByAcquereur_IdUtilisateurAndIdAnnonce(Integer idAcquereur, Integer idAnnonce);

    // Compter le nombre de favoris d'un acquéreur
    long countByAcquereur_IdUtilisateur(Integer idAcquereur);

    // Compter le nombre de fois qu'une annonce a été mise en favori
    long countByIdAnnonce(Integer idAnnonce);

    // Supprimer un favori spécifique
    void deleteByIdAnnonceAndAcquereur_IdUtilisateur(Integer idAcquereur, Integer idAnnonce);

    // Supprimer tous les favoris d'un acquéreur
    void deleteByAcquereur_IdUtilisateur(Integer idAcquereur);

    // Supprimer tous les favoris d'une annonce
    void deleteByIdAnnonce(Integer idAnnonce);

    // Récupérer les favoris avec les détails des annonces
    @Query("SELECT f FROM Favoris f JOIN FETCH f.annonce WHERE f.idAcquereur = :idAcquereur")
    List<Favoris> findByAcquereur_IdUtilisateurWithAnnonce(@Param("idAcquereur") Integer idAcquereur);

    // Récupérer les favoris récents d'un acquéreur
    @Query("SELECT f FROM Favoris f WHERE f.idAcquereur = :idAcquereur ORDER BY f.dateAjout DESC")
    List<Favoris> findByAcquereur_IdUtilisateurOrderByDateAjoutDesc(@Param("idAcquereur") Integer idAcquereur);

    // Statistiques : annonces les plus mises en favoris
    @Query("SELECT f.idAnnonce, COUNT(f) as nombreFavoris FROM Favoris f GROUP BY f.idAnnonce ORDER BY COUNT(f) DESC")
    List<Object[]> findAnnoncesLesPlusEnFavoris();
}