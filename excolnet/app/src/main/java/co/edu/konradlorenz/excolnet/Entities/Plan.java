package co.edu.konradlorenz.excolnet.Entities;

public class Plan {
    private String agencia;
    private String nombrePlan;
    private String photoPlan;
    private String description;
    private String precio;


    private Plan(final Builder builder) {
        this.agencia = builder.agencia;
        this.nombrePlan = builder.nombrePlan;
        this.photoPlan = builder.photoPlan;
        this.description = builder.description;
        this.precio = builder.precio;
    }

    static class Builder {
        private String agencia;
        private String nombrePlan;
        private String photoPlan;
        private String description;
        private String precio;

        public Builder setAgencia(final String agencia) {
            this.agencia = agencia;
            return this;
        }

        public Builder setNombrePlan(final String nombrePlan) {
            this.nombrePlan = nombrePlan;
            return this;
        }

        public Builder setPhotoPlan(final String photoPlan) {
            this.photoPlan = photoPlan;
            return this;
        }

        public Builder setDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder setPrecio(final String precio) {
            this.precio = precio;
            return this;
        }

        public Plan create() {
            return new Plan(this);
        }
    }

}


