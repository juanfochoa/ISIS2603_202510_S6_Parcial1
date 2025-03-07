package co.edu.uniandes.dse.parcial1.services;

import co.edu.uniandes.dse.parcial1.entities.EstacionEntity;
import co.edu.uniandes.dse.parcial1.entities.RutaEntity;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.parcial1.repositories.EstacionRepository;
import co.edu.uniandes.dse.parcial1.repositories.RutaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EstacionRutaService {
    @Autowired
    private EstacionRepository estacionRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Transactional
    public void removeEstacionRuta(Long idEstacion, Long idRuta) throws EntityNotFoundException, IllegalOperationException {
        log.info("Iniciando la eliminación de la estación {} de la ruta {}", idEstacion, idRuta);

        Optional<EstacionEntity> estacionOpt = estacionRepository.findById(idEstacion);
        if (estacionOpt.isEmpty()) {
            throw new EntityNotFoundException("La estación especificada no existe.");
        }

        Optional<RutaEntity> rutaOpt = rutaRepository.findById(idRuta);
        if (rutaOpt.isEmpty()) {
            throw new EntityNotFoundException("La ruta especificada no existe.");
        }
        EstacionEntity estacion = estacionOpt.get();
        RutaEntity ruta = rutaOpt.get();

        if (!estacion.getRutas().contains(ruta)) {
            throw new IllegalOperationException("La estación no está asociada a la ruta.");
        }

        boolean esNocturna = ruta.getNombre().toLowerCase().contains("nocturna");

        int rutasNocturnas = 0;
        for (RutaEntity r : estacion.getRutas()) {
            if (r.getNombre().toLowerCase().contains("nocturna")) {
                rutasNocturnas++;
            }
        }
        if (esNocturna && rutasNocturnas == 1) {
            throw new IllegalOperationException("No se puede eliminar la única ruta nocturna de la estación.");
        }

        estacion.getRutas().remove(ruta);
        ruta.getEstaciones().remove(estacion);

        estacionRepository.save(estacion);
        rutaRepository.save(ruta);

        log.info("Se eliminó la estación {} de la ruta {}", idEstacion, idRuta);


    }

    @Transactional
    public void addEstacionRuta(Long idEstacion, Long idRuta) throws EntityNotFoundException, IllegalOperationException {
        log.info("Iniciando la adición de la estación {} a la ruta {}", idEstacion, idRuta);
        Optional<EstacionEntity> estacionOpt = estacionRepository.findById(idEstacion);
        if (estacionOpt.isEmpty()) {
            throw new EntityNotFoundException("La estación especificada no existe.");
        }

        Optional<RutaEntity> rutaOpt = rutaRepository.findById(idRuta);
        if (rutaOpt.isEmpty()) {
            throw new EntityNotFoundException("La ruta especificada no existe.");
        }

        EstacionEntity estacion = estacionOpt.get();
        RutaEntity ruta = rutaOpt.get();

        boolean esCircular = ruta.getNombre().toLowerCase().contains("circular");
        int rutasCirculares = 0;
        for (RutaEntity r : estacion.getRutas()) {
            if (r.getNombre().toLowerCase().contains("circular")) {
                rutasCirculares++;
            }
        }
        if (estacion.getCapacidad() < 100 && esCircular && rutasCirculares >= 2) {
            throw new IllegalOperationException("La estación no puede tener más de 2 rutas circulares si su capacidad es menor a 100.");
        }

        estacion.getRutas().add(ruta);
        ruta.getEstaciones().add(estacion);

        estacionRepository.save(estacion);
        rutaRepository.save(ruta);

        log.info("Se agregó la estación {} a la ruta {}", idEstacion, idRuta);

    }
}
