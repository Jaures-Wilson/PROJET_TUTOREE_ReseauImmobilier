package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer idUtilisateur;

    @Column(name = "nom", nullable = false, length = 40)
    private String nom;

    @Column(name = "email", nullable = false, length = 40, unique = true)
    private String email;

    @Column(name = "mot_de_passe", nullable = false, length = 40)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "num_phone", nullable = false)
    private Integer numPhone;

    @Column(name = "format_num_pays", nullable = false)
    private Integer formatNumPays;

    @Lob
    @Column(name = "photo_profil")
    private byte[] photoProfil;

    public enum Role {
        ADMIN, ANNONCEUR, ACQUEREUR
    }
}
