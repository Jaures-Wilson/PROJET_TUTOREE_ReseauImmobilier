package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Integer> {
    
    Optional<Administrateur> findByEmail(String email);
    
    @Query("SELECT a FROM Administrateur a WHERE a.idUtilisateur = :id")
    Optional<Administrateur> findByIdUtilisateur(@Param("id") Integer id);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM Utilisateur u")
    Long countTotalUtilisateurs();
    
    @Query("SELECT COUNT(a) FROM Annonce a")
    Long countTotalAnnonces();
    
    @Query("SELECT COUNT(p) FROM Paiement p")
    Long countTotalPaiements();
    
    @Query("SELECT COUNT(pl) FROM Plainte pl")
    Long countTotalPlaintes();
}