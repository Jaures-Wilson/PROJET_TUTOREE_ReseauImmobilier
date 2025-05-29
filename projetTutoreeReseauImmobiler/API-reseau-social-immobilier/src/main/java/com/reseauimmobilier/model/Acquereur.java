package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.List;

@Entity
@Table(name = "acquereur")
@PrimaryKeyJoinColumn(name = "id_acquereur", referencedColumnName = "id_utilisateur")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Acquereur extends Utilisateur {

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "historique_recherches",columnDefinition = "json")
    private List<String> historiqueRecherches;

    public Acquereur(String nom, String email, String motDePasse, Integer numPhone,
                     Integer formatNumPays, List<String> historiqueRecherches) {
        super();
        this.setNom(nom);
        this.setEmail(email);
        this.setMotDePasse(motDePasse);
        this.setRole(Role.ACQUEREUR);
        this.setNumPhone(numPhone);
        this.setFormatNumPays(formatNumPays);
        this.historiqueRecherches = historiqueRecherches;
    }
}
