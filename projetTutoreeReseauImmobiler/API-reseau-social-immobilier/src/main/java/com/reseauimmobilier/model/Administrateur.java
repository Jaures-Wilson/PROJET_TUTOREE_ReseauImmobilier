package com.reseauimmobilier.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "administrateur")
@PrimaryKeyJoinColumn(name = "id_admin", referencedColumnName = "id_utilisateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Administrateur extends Utilisateur {

    @Column(name = "numero_mtn_momo")
    private Integer numeroMTN_MoMo;

    @Column(name = "numero_orange_money")
    private Integer numeroOrangeMoney;

    @Column(name = "numero_uba")
    private Integer numeroUBA;

    public Administrateur(String nom, String email, String motDePasse, Integer numPhone,
                         Integer formatNumPays, Integer numeroMTN_MoMo,
                         Integer numeroOrangeMoney, Integer numeroUBA) {
        super();
        this.setNom(nom);
        this.setEmail(email);
        this.setMotDePasse(motDePasse);
        this.setRole(Role.ADMIN);
        this.setNumPhone(numPhone);
        this.setFormatNumPays(formatNumPays);
        this.numeroMTN_MoMo = numeroMTN_MoMo;
        this.numeroOrangeMoney = numeroOrangeMoney;
        this.numeroUBA = numeroUBA;
    }
}