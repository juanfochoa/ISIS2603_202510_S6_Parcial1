package co.edu.uniandes.dse.parcial1.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class RutaEntity extends BaseEntity {

    private String nombre;
    private String color;
    private String tipo;

    @PodamExclude
    @OneToMany(fetch= FetchType.LAZY)
    private List<EstacionEntity> estaciones = new ArrayList<>();
}
