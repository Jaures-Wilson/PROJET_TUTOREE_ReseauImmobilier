package com.reseauimmobilier.service;

import com.reseauimmobilier.model.Annonceur;
import com.reseauimmobilier.model.Utilisateur;
import com.reseauimmobilier.repository.AnnonceurRepository;
import com.reseauimmobilier.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AnnonceurService {
    
    @Autowired
    private AnnonceurRepository annonceurRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Transactional
    public Annonceur convertirUtilisateurEnAnnonceur(Integer utilisateurId, 
                                                   Integer numeroMTN_MoMo, 
                                                   Integer numeroOrangeMoney, 
                                                   Integer numeroUBA) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(utilisateurId);
        if (!utilisateurOpt.isPresent()) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        
        Utilisateur utilisateur = utilisateurOpt.get();
        
        // Vérifier si l'utilisateur n'est pas déjà un annonceur
        if (utilisateur.getRole() == Utilisateur.Role.ANNONCEUR) {
            throw new RuntimeException("L'utilisateur est déjà un annonceur");
        }
        
        // Mettre à jour le rôle de l'utilisateur existant
        utilisateur.setRole(Utilisateur.Role.ANNONCEUR);
        utilisateurRepository.save(utilisateur);
        
        // Créer l'entrée Annonceur
        Annonceur annonceur = new Annonceur();
        annonceur.setIdUtilisateur(utilisateur.getIdUtilisateur());
        annonceur.setNom(utilisateur.getNom());
        annonceur.setEmail(utilisateur.getEmail());
        annonceur.setMotDePasse(utilisateur.getMotDePasse());
        annonceur.setRole(Utilisateur.Role.ANNONCEUR);
        annonceur.setNumPhone(utilisateur.getNumPhone());
        annonceur.setFormatNumPays(utilisateur.getFormatNumPays());
        annonceur.setPhotoProfil(utilisateur.getPhotoProfil());
        annonceur.setNumeroMTN_MoMo(numeroMTN_MoMo);
        annonceur.setNumeroOrangeMoney(numeroOrangeMoney);
        annonceur.setNumeroUBA(numeroUBA);
        
        return annonceurRepository.save(annonceur);
    }
    
    public Optional<Annonceur> obtenirAnnonceurParId(Integer id) {
        return annonceurRepository.findById(id);
    }
    
    public Optional<Annonceur> obtenirAnnonceurParUtilisateurId(Integer utilisateurId) {
        return annonceurRepository.findByIdUtilisateur(utilisateurId);
    }
    
    public Optional<Annonceur> obtenirAnnonceurParEmail(String email) {
        return annonceurRepository.findByEmail(email);
    }
    
    public List<Annonceur> obtenirTousLesAnnonceurs() {
        return annonceurRepository.findAll();
    }
    
    public Annonceur modifierAnnonceur(Integer id, Annonceur annonceurModifie) {
        Optional<Annonceur> annonceurExistant = annonceurRepository.findById(id);
        if (annonceurExistant.isPresent()) {
            Annonceur annonceur = annonceurExistant.get();
            
            if (annonceurModifie.getNom() != null) {
                annonceur.setNom(annonceurModifie.getNom());
            }
            if (annonceurModifie.getEmail() != null && !annonceur.getEmail().equals(annonceurModifie.getEmail())) {
                if (annonceurRepository.existsByEmail(annonceurModifie.getEmail())) {
                    throw new RuntimeException("Un annonceur avec cet email existe déjà");
                }
                annonceur.setEmail(annonceurModifie.getEmail());
            }
            if (annonceurModifie.getMotDePasse() != null) {
                annonceur.setMotDePasse(annonceurModifie.getMotDePasse());
            }
            if (annonceurModifie.getNumPhone() != null) {
                annonceur.setNumPhone(annonceurModifie.getNumPhone());
            }
            if (annonceurModifie.getFormatNumPays() != null) {
                annonceur.setFormatNumPays(annonceurModifie.getFormatNumPays());
            }
            if (annonceurModifie.getPhotoProfil() != null) {
                annonceur.setPhotoProfil(annonceurModifie.getPhotoProfil());
            }
            if (annonceurModifie.getNumeroMTN_MoMo() != null) {
                annonceur.setNumeroMTN_MoMo(annonceurModifie.getNumeroMTN_MoMo());
            }
            if (annonceurModifie.getNumeroOrangeMoney() != null) {
                annonceur.setNumeroOrangeMoney(annonceurModifie.getNumeroOrangeMoney());
            }
            if (annonceurModifie.getNumeroUBA() != null) {
                annonceur.setNumeroUBA(annonceurModifie.getNumeroUBA());
            }
            
            return annonceurRepository.save(annonceur);
        }
        throw new RuntimeException("Annonceur non trouvé");
    }
    
    public void supprimerAnnonceur(Integer id) {
        if (!annonceurRepository.existsById(id)) {
            throw new RuntimeException("Annonceur non trouvé");
        }
        annonceurRepository.deleteById(id);
    }
    
    public Long compterAnnoncesParAnnonceur(Integer annonceId) {
        return annonceurRepository.countAnnoncesByAnnonceurId(annonceId);
    }
}