package com.reseauimmobilier.repository;

import com.reseauimmobilier.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    
    Optional<Utilisateur> findByEmail(String email);
    
    Optional<Utilisateur> findByEmailAndMotDePasse(String email, String motDePasse);
    
    boolean existsByEmail(String email);
    
    // Nouvelle méthode pour trouver les utilisateurs par rôle
    List<Utilisateur> findByRole(Utilisateur.Role role);
}