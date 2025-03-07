package co.edu.uniandes.dse.parcial1.services;

import co.edu.uniandes.dse.parcial1.entities.EstacionEntity;
import co.edu.uniandes.dse.parcial1.entities.RutaEntity;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@Import(EstacionRutaService.class)
public class EstacionRutaServiceTest {
    @Autowired
    private EstacionRutaService estacionRutaService;

    @Autowired
    private TestEntityManager entityManager;

    private EstacionEntity estacion;
    private RutaEntity ruta;

    @BeforeEach
    void setUp() {
        estacion = new EstacionEntity();
        estacion.setName("Estación Central");
        estacion.setCapacidad(200);
        estacion.setRutas(new ArrayList<>());
        entityManager.persist(estacion);

        ruta = new RutaEntity();
        ruta.setNombre("Ruta Nocturna 1");
        entityManager.persist(ruta);

        estacion.getRutas().add(ruta);
        ruta.getEstaciones().add(estacion);
        entityManager.persist(estacion);
        entityManager.persist(ruta);
    }

    @Test
    void testRemoveEstacionRuta_Correcto() throws EntityNotFoundException, IllegalOperationException {
        estacionRutaService.removeEstacionRuta(estacion.getId(), ruta.getId());
        assertFalse(estacion.getRutas().contains(ruta));
    }

    @Test
    void testRemoveEstacionRuta_EstacionNoExiste() {
        assertThrows(EntityNotFoundException.class, () -> {
            estacionRutaService.removeEstacionRuta(0L, ruta.getId());
        });
    }

    @Test
    void testRemoveEstacionRuta_RutaNoExiste() {
        assertThrows(EntityNotFoundException.class, () -> {
            estacionRutaService.removeEstacionRuta(estacion.getId(), 0L);
        });
    }

    @Test
    void testRemoveEstacionRuta_UnicaRutaNocturna() {
        assertThrows(IllegalOperationException.class, () -> {
            estacionRutaService.removeEstacionRuta(estacion.getId(), ruta.getId());
        });
    }

    //Pruebas para addEstacionRutas

    @Test
    void testAddEstacionRuta_Correcto() throws EntityNotFoundException, IllegalOperationException {
        RutaEntity nuevaRuta = new RutaEntity();
        nuevaRuta.setNombre("Ruta Circular 1");
        entityManager.persist(nuevaRuta);

        estacionRutaService.addEstacionRuta(estacion.getId(), nuevaRuta.getId());
        assertTrue(estacion.getRutas().contains(nuevaRuta));
    }

    @Test
    void testAddEstacionRuta_EstacionNoExiste() {
        assertThrows(EntityNotFoundException.class, () -> {
            estacionRutaService.addEstacionRuta(0L, ruta.getId());
        });
    }

    @Test
    void testAddEstacionRuta_RutaNoExiste() {
        assertThrows(EntityNotFoundException.class, () -> {
            estacionRutaService.addEstacionRuta(estacion.getId(), 0L);
        });
    }

    @Test
    void testAddEstacionRuta_MaximoRutasCirculares() {
        EstacionEntity estacionPequena = new EstacionEntity();
        estacionPequena.setName("Estación Pequeña");
        estacionPequena.setCapacidad(50);
        estacionPequena.setRutas(new ArrayList<>());
        entityManager.persist(estacionPequena);

        RutaEntity rutaCircular1 = new RutaEntity();
        rutaCircular1.setNombre("Circular 1");
        entityManager.persist(rutaCircular1);
        estacionPequena.getRutas().add(rutaCircular1);

        RutaEntity rutaCircular2 = new RutaEntity();
        rutaCircular2.setNombre("Circular 2");
        entityManager.persist(rutaCircular2);
        estacionPequena.getRutas().add(rutaCircular2);

        RutaEntity rutaCircular3 = new RutaEntity();
        rutaCircular3.setNombre("Circular 3");
        entityManager.persist(rutaCircular3);

        assertThrows(IllegalOperationException.class, () -> {
            estacionRutaService.addEstacionRuta(estacionPequena.getId(), rutaCircular3.getId());
        });
    }
}
