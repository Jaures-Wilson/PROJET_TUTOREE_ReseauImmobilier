package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Plainte;
import com.reseauimmobilier.model.Acquereur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlainteRepository extends JpaRepository<Plainte, Integer> {

    // Trouver les plaintes par statut
    List<Plainte> findByStatut(String statut);

    // Compter les plaintes par statut
    long countByStatut(String statut);

    // Trouver les plaintes d'un acquéreur spécifique
    List<Plainte> findByAcquereur(Acquereur acquereur);

    // Trouver les plaintes d'un acquéreur par son ID
    List<Plainte> findByAcquereur_IdUtilisateur(Integer idAcquereur);

    // Trouver les plaintes par statut et acquéreur
    List<Plainte> findByStatutAndAcquereur(String statut, Acquereur acquereur);

    // Trouver les plaintes dans une période donnée
    List<Plainte> findByDatePlainteBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    // Trouver les plaintes récentes (par exemple, les 30 derniers jours)
    @Query("SELECT p FROM Plainte p WHERE p.datePlainte >= :dateDebut ORDER BY p.datePlainte DESC")
    List<Plainte> findPlaintesRecentes(@Param("dateDebut") LocalDateTime dateDebut);

    // Trouver les plaintes par statut triées par date (plus récentes en premier)
    List<Plainte> findByStatutOrderByDatePlainteDesc(String statut);

    // Compter le nombre total de plaintes
    @Query("SELECT COUNT(p) FROM Plainte p")
    long countTotalPlaintes();

    // Compter les plaintes d'un acquéreur
    long countByAcquereur(Acquereur acquereur);

    // Trouver les plaintes contenant un mot-clé dans le contenu
    @Query("SELECT p FROM Plainte p WHERE LOWER(p.contenu) LIKE LOWER(CONCAT('%', :motCle, '%'))")
    List<Plainte> findByContenuContainingIgnoreCase(@Param("motCle") String motCle);
}