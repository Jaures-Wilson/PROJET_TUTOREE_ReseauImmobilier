package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Integer> {
    
    // Trouver l'abonnement actif d'un acquéreur
    @Query("SELECT a FROM Abonnement a WHERE a.acquereur.idUtilisateur = :idAcquereur " +
           "AND a.statut = 'ACTIF' AND a.dateFin >= :dateActuelle ORDER BY a.dateFin DESC")
    Optional<Abonnement> findAbonnementActifByAcquereur(@Param("idAcquereur") Integer idAcquereur, 
                                                        @Param("dateActuelle") LocalDate dateActuelle);
    
    // Trouver tous les abonnements d'un acquéreur
    List<Abonnement> findByAcquereurIdUtilisateurOrderByDateDemandeDesc(Integer idAcquereur);
    
    // Trouver les abonnements en attente de validation
    List<Abonnement> findByStatutOrderByDateDemandeAsc(Abonnement.StatutAbonnement statut);
    
    // Trouver les abonnements expirés
    @Query("SELECT a FROM Abonnement a WHERE a.statut = 'ACTIF' AND a.dateFin < :dateActuelle")
    List<Abonnement> findAbonnementsExpires(@Param("dateActuelle") LocalDate dateActuelle);
    
    // Vérifier si un acquéreur a un abonnement actif
    @Query("SELECT COUNT(a) > 0 FROM Abonnement a WHERE a.acquereur.idUtilisateur = :idAcquereur " +
           "AND a.statut = 'ACTIF' AND a.dateFin >= :dateActuelle")
    boolean hasAbonnementActif(@Param("idAcquereur") Integer idAcquereur, 
                              @Param("dateActuelle") LocalDate dateActuelle);
}