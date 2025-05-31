package com.reseauimmobilier.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavorisId implements Serializable {

    private Integer  idAcquereur;
    private Integer  idAnnonce;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavorisId favorisId = (FavorisId) o;
        return Objects.equals(idAcquereur, favorisId.idAcquereur) && 
               Objects.equals(idAnnonce, favorisId.idAnnonce);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAcquereur, idAnnonce);
    }
}