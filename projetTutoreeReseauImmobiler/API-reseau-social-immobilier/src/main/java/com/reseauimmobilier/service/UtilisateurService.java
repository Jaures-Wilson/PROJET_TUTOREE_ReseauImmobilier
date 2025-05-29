package com.reseauimmobilier.service;

import com.reseauimmobilier.model.Utilisateur;
import com.reseauimmobilier.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }
        // Par défaut, un nouvel utilisateur est un acquéreur
        if (utilisateur.getRole() == null) {
            utilisateur.setRole(Utilisateur.Role.ACQUEREUR);
        }
        return utilisateurRepository.save(utilisateur);
    }
    
    public Optional<Utilisateur> obtenirUtilisateurParId(Integer id) {
        return utilisateurRepository.findById(id);
    }
    
    public Optional<Utilisateur> obtenirUtilisateurParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }
    
    public List<Utilisateur> obtenirTousLesUtilisateurs() {
        return utilisateurRepository.findAll();
    }
    
    public Utilisateur modifierUtilisateur(Integer id, Utilisateur utilisateurModifie) {
        Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findById(id);
        if (utilisateurExistant.isPresent()) {
            Utilisateur utilisateur = utilisateurExistant.get();
            
            if (utilisateurModifie.getNom() != null) {
                utilisateur.setNom(utilisateurModifie.getNom());
            }
            if (utilisateurModifie.getEmail() != null && !utilisateur.getEmail().equals(utilisateurModifie.getEmail())) {
                if (utilisateurRepository.existsByEmail(utilisateurModifie.getEmail())) {
                    throw new RuntimeException("Un utilisateur avec cet email existe déjà");
                }
                utilisateur.setEmail(utilisateurModifie.getEmail());
            }
            if (utilisateurModifie.getMotDePasse() != null) {
                utilisateur.setMotDePasse(utilisateurModifie.getMotDePasse());
            }
            if (utilisateurModifie.getNumPhone() != null) {
                utilisateur.setNumPhone(utilisateurModifie.getNumPhone());
            }
            if (utilisateurModifie.getFormatNumPays() != null) {
                utilisateur.setFormatNumPays(utilisateurModifie.getFormatNumPays());
            }
            if (utilisateurModifie.getPhotoProfil() != null) {
                utilisateur.setPhotoProfil(utilisateurModifie.getPhotoProfil());
            }
            
            return utilisateurRepository.save(utilisateur);
        }
        throw new RuntimeException("Utilisateur non trouvé");
    }
    
    public void supprimerUtilisateur(Integer id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        utilisateurRepository.deleteById(id);
    }
    
    public Optional<Utilisateur> connecterUtilisateur(String email, String motDePasse) {
        return utilisateurRepository.findByEmailAndMotDePasse(email, motDePasse);
    }
}