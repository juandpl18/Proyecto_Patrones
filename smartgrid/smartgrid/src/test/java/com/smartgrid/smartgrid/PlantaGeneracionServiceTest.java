package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import com.smartgrid.smartgrid.model.EstadoPlanta;
import com.smartgrid.smartgrid.model.PlantaGeneracion;
import com.smartgrid.smartgrid.model.TipoFuente;
import com.smartgrid.smartgrid.repository.PlantaGeneracionRepository;
import com.smartgrid.smartgrid.service.PlantaGeneracionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantaGeneracionServiceTest {

    @Mock
    private PlantaGeneracionRepository repository;

    @InjectMocks
    private PlantaGeneracionService servicio;

    @Test
    void registraPlantaActivaConSuFuente() {
        when(repository.existsByNombreIgnoreCase("Solar Norte"))
                .thenReturn(false);
        when(repository.save(any(PlantaGeneracion.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PlantaEnergia planta = new PlantaEnergia.PlantaEnergiaBuilder(
                "Solar Norte", "solar").capacidadKw(800).build();

        PlantaGeneracion guardada = servicio.registrar(planta, "admin");

        assertEquals(TipoFuente.SOLAR, guardada.getTipoFuente());
        assertEquals(EstadoPlanta.ACTIVA, guardada.getEstado());
        assertEquals("admin", guardada.getCreadoPor());
    }

    @Test
    void rechazaNombreDuplicado() {
        when(repository.existsByNombreIgnoreCase("Solar Norte"))
                .thenReturn(true);

        PlantaEnergia planta = new PlantaEnergia.PlantaEnergiaBuilder(
                "Solar Norte", "SOLAR").build();

        assertThrows(ResponseStatusException.class,
                () -> servicio.registrar(planta, "admin"));
        verify(repository, never()).save(any());
    }
}