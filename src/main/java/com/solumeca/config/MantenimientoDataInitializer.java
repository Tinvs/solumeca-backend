package com.solumeca.config;

import com.solumeca.model.Mantenimiento;
import com.solumeca.model.Maquinaria;
import com.solumeca.repository.MantenimientoRepository;
import com.solumeca.repository.MaquinariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class MantenimientoDataInitializer implements ApplicationRunner {

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private MaquinariaRepository maquinariaRepository;

    @Override
    public void run(ApplicationArguments args) {
        try {
            long count = mantenimientoRepository.count();
            if (count < 4) {
                List<Maquinaria> maquinas = maquinariaRepository.findAll();
                Long idMaq1 = !maquinas.isEmpty() ? maquinas.get(0).getId() : 1L;
                Long idMaq2 = maquinas.size() > 1 ? maquinas.get(1).getId() : idMaq1;
                Long idMaq3 = maquinas.size() > 2 ? maquinas.get(2).getId() : idMaq1;
                Long idMaq4 = maquinas.size() > 3 ? maquinas.get(3).getId() : idMaq1;

                // 1. Presolicitud
                Mantenimiento m1 = new Mantenimiento();
                m1.setMaquinariaId(idMaq1);
                m1.setSolicitante("carlos.mora");
                m1.setTipo("Correctivo");
                m1.setDescripcion("Falla en el sistema hidráulico y baja presión");
                m1.setEstado("Presolicitud");
                m1.setFecha(LocalDate.now().minusDays(1));
                mantenimientoRepository.save(m1);

                // 2. Diagnosticado
                Mantenimiento m2 = new Mantenimiento();
                m2.setMaquinariaId(idMaq2);
                m2.setSolicitante("constructora.bogota");
                m2.setTipo("Preventivo");
                m2.setDescripcion("Revisión de tracción y filtros de combustible");
                m2.setEstado("Diagnosticado");
                m2.setAnalisis("Fisura en manguera de retorno y sellos gastados.");
                m2.setSolucion("Sustitución de manguera 3/4 R2 y calibración hidráulica.");
                m2.setTecnicoAsignado("tecnico");
                m2.setDiasEstimados(2);
                m2.setFecha(LocalDate.now().minusDays(2));
                mantenimientoRepository.save(m2);

                // 3. Cotizada
                Mantenimiento m3 = new Mantenimiento();
                m3.setMaquinariaId(idMaq3);
                m3.setSolicitante("inversiones.norte");
                m3.setTipo("Correctivo");
                m3.setDescripcion("Desgaste en rodillos de oruga y pernos flojos");
                m3.setEstado("Cotizada");
                m3.setAnalisis("Rodillos inferiores con desgaste excesivo.");
                m3.setSolucion("Cambio de 4 rodillos inferiores y torque según norma.");
                m3.setCostoEstimado(1450000.0);
                m3.setValorTotal(1450000.0);
                m3.setNumeroOrden("ORD-2026-004");
                m3.setTecnicoAsignado("tecnico");
                m3.setDiasEstimados(3);
                m3.setFecha(LocalDate.now().minusDays(3));
                mantenimientoRepository.save(m3);

                // 4. Orden de trabajo
                Mantenimiento m4 = new Mantenimiento();
                m4.setMaquinariaId(idMaq4);
                m4.setSolicitante("transporte.andino");
                m4.setTipo("Preventivo");
                m4.setDescripcion("Mantenimiento programado de 500 horas de motor");
                m4.setEstado("Orden de trabajo");
                m4.setAnalisis("Servicio preventivo regular según horómetro oficial.");
                m4.setSolucion("Cambio de aceites 15W40, filtros y refrigerante.");
                m4.setCostoEstimado(850000.0);
                m4.setValorTotal(850000.0);
                m4.setNumeroOrden("ORD-2026-005");
                m4.setFechaAprobacion(LocalDate.now().minusDays(1));
                m4.setTecnicoAsignado("tecnico");
                m4.setDiasEstimados(1);
                m4.setFecha(LocalDate.now().minusDays(4));
                mantenimientoRepository.save(m4);
            }
        } catch (Exception ex) {
            System.err.println("Aviso MantenimientoDataInitializer: " + ex.getMessage());
        }
    }
}
