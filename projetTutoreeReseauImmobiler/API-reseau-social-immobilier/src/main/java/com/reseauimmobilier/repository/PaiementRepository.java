package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Integer> {

    // Recherche par statut
    List<Paiement> findByStatut(String statut);
    
    // Recherche par statut avec enum
    List<Paiement> findByStatut(Paiement.StatutPaiement statut);

    // Recherche par acquéreur
    List<Paiement> findByAcquereurIdUtilisateur(Integer acquereurId);

    // Recherche par annonceur
    List<Paiement> findByAnnonceurIdUtilisateur(Integer annonceurId);

    // Recherche par annonce
    List<Paiement> findByAnnonceIdAnnonce(Integer annonceId);

    // Recherche par type de contrat
    List<Paiement> findByTypeContrat(Paiement.TypeContrat typeContrat);

    // Recherche par moyen de paiement
    List<Paiement> findByMoyenPaiement(Paiement.MoyenPaiement moyenPaiement);

    // Paiements non lus
    List<Paiement> findByLuFalse();

    // Paiements non lus pour un utilisateur spécifique (acquéreur)
    List<Paiement> findByAcquereurIdUtilisateurAndLuFalse(Integer acquereurId);

    // Paiements non lus pour un annonceur
    List<Paiement> findByAnnonceurIdUtilisateurAndLuFalse(Integer annonceurId);

    // Paiements confirmés d'un acquéreur
    List<Paiement> findByAcquereurIdUtilisateurAndStatut(Integer acquereurId, Paiement.StatutPaiement statut);

    // Paiements reçus par un annonceur (confirmés)
    List<Paiement> findByAnnonceurIdUtilisateurAndStatut(Integer annonceurId, Paiement.StatutPaiement statut);

    // Paiements par période
    @Query("SELECT p FROM Paiement p WHERE p.datePaiement BETWEEN :dateDebut AND :dateFin")
    List<Paiement> findByDatePaiementBetween(@Param("dateDebut") LocalDateTime dateDebut, 
                                            @Param("dateFin") LocalDateTime dateFin);

    // Paiements récents
    @Query("SELECT p FROM Paiement p WHERE p.datePaiement >= :dateDebut ORDER BY p.datePaiement DESC")
    List<Paiement> findRecentPaiements(@Param("dateDebut") LocalDateTime dateDebut);

    // Somme des paiements confirmés pour un annonceur
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.annonceur.idUtilisateur = :annonceurId AND p.statut = 'CONFIRMEE'")
    Long sumMontantByAnnonceurAndStatutConfirme(@Param("annonceurId") Integer annonceurId);

    // Somme des paiements effectués par un acquéreur
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.acquereur.idUtilisateur = :acquereurId AND p.statut = 'CONFIRMEE'")
    Long sumMontantByAcquereurAndStatutConfirme(@Param("acquereurId") Integer acquereurId);

    // Compter les paiements par statut
    Long countByStatut(String statut);
    Long countByStatut(Paiement.StatutPaiement statut);

    // Compter les paiements en attente
    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.statut = 'EN_ATTENTE'")
    Long countPaiementsEnAttente();

    // Compter les paiements d'un acquéreur
    Long countByAcquereurIdUtilisateur(Integer acquereurId);

    // Compter les paiements reçus par un annonceur
    Long countByAnnonceurIdUtilisateur(Integer annonceurId);

    // Vérifier l'existence d'un paiement pour une annonce et un acquéreur
    boolean existsByAnnonceIdAnnonceAndAcquereurIdUtilisateur(Integer annonceId, Integer acquereurId);

    // Trouver le dernier paiement d'un acquéreur
    @Query("SELECT p FROM Paiement p WHERE p.acquereur.idUtilisateur = :acquereurId ORDER BY p.datePaiement DESC")
    List<Paiement> findLastPaiementByAcquereur(@Param("acquereurId") Integer acquereurId);

    // Trouver un paiement spécifique pour une annonce et un acquéreur
    Optional<Paiement> findByAnnonceIdAnnonceAndAcquereurIdUtilisateur(Integer annonceId, Integer acquereurId);

    // Statistiques mensuelles
    @Query("SELECT MONTH(p.datePaiement) as mois, COUNT(p) as nombre, SUM(p.montant) as total " +
           "FROM Paiement p WHERE YEAR(p.datePaiement) = :annee AND p.statut = 'CONFIRMEE' " +
           "GROUP BY MONTH(p.datePaiement) ORDER BY MONTH(p.datePaiement)")
    List<Object[]> getStatistiquesMensuelles(@Param("annee") int annee);

    // Top annonceurs par revenus
    @Query("SELECT p.annonceur.idUtilisateur, p.annonceur.nom, SUM(p.montant) as total " +
           "FROM Paiement p WHERE p.statut = 'CONFIRMEE' " +
           "GROUP BY p.annonceur.idUtilisateur, p.annonceur.nom " +
           "ORDER BY total DESC")
    List<Object[]> getTopAnnonceursByRevenus();
}