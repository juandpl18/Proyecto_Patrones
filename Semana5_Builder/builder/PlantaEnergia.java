package com.smartgrid.smartgrid.builder;

public class PlantaEnergia {
   
    private final String nombre;
    private final String tipoFuente;

   
    private final double capacidadKw;
    private final double voltajeSalida;
    private final boolean tieneBateriaReserva;
    private final String ubicacion;

    private PlantaEnergia(PlantaEnergiaBuilder builder) {
        this.nombre = builder.nombre;
        this.tipoFuente = builder.tipoFuente;
        this.capacidadKw = builder.capacidadKw;
        this.voltajeSalida = builder.voltajeSalida;
        this.tieneBateriaReserva = builder.tieneBateriaReserva;
        this.ubicacion = builder.ubicacion;
    }

 
    public String getNombre() { return nombre; }
    public String getTipoFuente() { return tipoFuente; }
    public double getCapacidadKw() { return capacidadKw; }
    public double getVoltajeSalida() { return voltajeSalida; }
    public boolean isTieneBateriaReserva() { return tieneBateriaReserva; }
    public String getUbicacion() { return ubicacion; }


    public static class PlantaEnergiaBuilder {
        private final String nombre;
        private final String tipoFuente;

        private double capacidadKw = 500.0; 
        private double voltajeSalida = 240.0;
        private boolean tieneBateriaReserva = false;
        private String ubicacion = "No asignada";

        public PlantaEnergiaBuilder(String nombre, String tipoFuente) {
            this.nombre = nombre;
            this.tipoFuente = tipoFuente;
        }

        public PlantaEnergiaBuilder capacidadKw(double capacidadKw) {
            this.capacidadKw = capacidadKw;
            return this;
        }

        public PlantaEnergiaBuilder voltajeSalida(double voltajeSalida) {
            this.voltajeSalida = voltajeSalida;
            return this;
        }

        public PlantaEnergiaBuilder tieneBateriaReserva(boolean tieneBateriaReserva) {
            this.tieneBateriaReserva = tieneBateriaReserva;
            return this;
        }

        public PlantaEnergiaBuilder ubicacion(String ubicacion) {
            this.ubicacion = ubicacion;
            return this;
        }

        public PlantaEnergia build() {
            return new PlantaEnergia(this);
        }
    }
}