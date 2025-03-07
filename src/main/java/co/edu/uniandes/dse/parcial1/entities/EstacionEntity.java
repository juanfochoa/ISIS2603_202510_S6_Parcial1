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
public class EstacionEntity extends BaseEntity {
    private String name;
    private String direccion;
    private Integer capacidad;

    @PodamExclude
    @OneToMany(fetch= FetchType.LAZY)
    private List<RutaEntity> rutas = new ArrayList<>();
}
