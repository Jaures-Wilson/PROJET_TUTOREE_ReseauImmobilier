package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Visite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisiteRepository extends JpaRepository<Visite, Integer> {

    // Trouver toutes les visites pour une annonce spécifique
    List<Visite> findByAnnonceIdAnnonceOrderByDateVisiteDesc(Integer annonceId);

    // Trouver toutes les visites d'un acquéreur
    List<Visite> findByAcquereurIdUtilisateurOrderByDateVisiteDesc(Integer acquereurId);

    // Trouver les visites par statut
    List<Visite> findByStatutOrderByDateVisiteDesc(Visite.StatutVisite statut);

    // Trouver les visites d'un acquéreur avec un statut spécifique
    List<Visite> findByAcquereurIdUtilisateurAndStatutOrderByDateVisiteDesc(
            Integer acquereurId, Visite.StatutVisite statut);

    // Trouver les visites d'une annonce avec un statut spécifique
    List<Visite> findByAnnonceIdAnnonceAndStatutOrderByDateVisiteDesc(
            Integer annonceId, Visite.StatutVisite statut);

    // Vérifier si une visite existe déjà pour un acquéreur et une annonce à une date donnée
    boolean existsByAcquereurIdUtilisateurAndAnnonceIdAnnonceAndDateVisite(
            Integer acquereurId, Integer annonceId, LocalDate dateVisite);

    // Compter le nombre de visites pour une annonce
    @Query("SELECT COUNT(v) FROM Visite v WHERE v.annonce.idAnnonce = :annonceId")
    Long countByAnnonceId(@Param("annonceId") Integer annonceId);

    // Compter le nombre de visites par statut pour un acquéreur
    @Query("SELECT COUNT(v) FROM Visite v WHERE v.acquereur.idUtilisateur = :acquereurId AND v.statut = :statut")
    Long countByAcquereurIdAndStatut(@Param("acquereurId") Integer acquereurId, 
                                     @Param("statut") Visite.StatutVisite statut);

    // Trouver les visites à venir (date >= aujourd'hui)
    @Query("SELECT v FROM Visite v WHERE v.dateVisite >= :today ORDER BY v.dateVisite ASC")
    List<Visite> findVisitesAVenir(@Param("today") LocalDate today);

    // Trouver les visites passées (date < aujourd'hui)
    @Query("SELECT v FROM Visite v WHERE v.dateVisite < :today ORDER BY v.dateVisite DESC")
    List<Visite> findVisitesPassees(@Param("today") LocalDate today);

    // Trouver les visites d'un annonceur (via ses annonces)
    @Query("SELECT v FROM Visite v WHERE v.annonce.annonceur.idUtilisateur = :annonceurId ORDER BY v.dateVisite DESC")
    List<Visite> findByAnnonceurId(@Param("annonceurId") Integer annonceurId);

    // Trouver les visites confirmées pour une date donnée
    List<Visite> findByDateVisiteAndStatut(LocalDate dateVisite, Visite.StatutVisite statut);
}