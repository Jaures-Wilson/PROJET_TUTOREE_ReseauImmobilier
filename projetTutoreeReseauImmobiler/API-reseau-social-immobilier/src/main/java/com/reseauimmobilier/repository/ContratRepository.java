package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Integer> {

    // Trouver les contrats par annonce
    List<Contrat> findByAnnonceIdAnnonce(Integer annonceId);

    // Trouver les contrats par type
    List<Contrat> findByType(Contrat.TypeContrat type);

    // Trouver les contrats signés ou non signés
    List<Contrat> findByDecisionAcquereur(Boolean decision);

    // Trouver les contrats d'un acquéreur spécifique
    @Query("SELECT c FROM Contrat c JOIN c.signataires s WHERE s.idUtilisateur = :acquereurId AND s.role = 'ACQUEREUR'")
    List<Contrat> findByAcquereurId(@Param("acquereurId") Integer acquereurId);

    // Trouver les contrats non signés d'un acquéreur
    @Query("SELECT c FROM Contrat c JOIN c.signataires s WHERE s.idUtilisateur = :acquereurId AND s.role = 'ACQUEREUR' AND c.decisionAcquereur = false")
    List<Contrat> findContratNonSignesByAcquereurId(@Param("acquereurId") Integer acquereurId);

    // Trouver les contrats signés d'un acquéreur
    @Query("SELECT c FROM Contrat c JOIN c.signataires s WHERE s.idUtilisateur = :acquereurId AND s.role = 'ACQUEREUR' AND c.decisionAcquereur = true")
    List<Contrat> findContratSignesByAcquereurId(@Param("acquereurId") Integer acquereurId);

    // Trouver les contrats d'un annonceur
    @Query("SELECT c FROM Contrat c WHERE c.annonce.annonceur.idUtilisateur = :annonceurId")
    List<Contrat> findByAnnonceurId(@Param("annonceurId") Integer annonceurId);

    // Trouver un contrat par annonce et acquéreur
    @Query("SELECT c FROM Contrat c JOIN c.signataires s WHERE c.annonce.idAnnonce = :annonceId AND s.idUtilisateur = :acquereurId AND s.role = 'ACQUEREUR'")
    Optional<Contrat> findByAnnonceIdAndAcquereurId(@Param("annonceId") Integer annonceId, @Param("acquereurId") Integer acquereurId);

    // Compter les contrats par type
    Long countByType(Contrat.TypeContrat type);

    // Compter les contrats validés
    Long countByDecisionAcquereur(Boolean decision);

    // Vérifier l'existence d'un contrat pour une annonce
    boolean existsByAnnonceIdAnnonce(Integer annonceId);

    // Trouver les contrats récents (pour statistiques)
    @Query("SELECT c FROM Contrat c WHERE c.dateSignature >= :dateDebut ORDER BY c.dateSignature DESC")
    List<Contrat> findRecentContracts(@Param("dateDebut") java.time.LocalDateTime dateDebut);
}