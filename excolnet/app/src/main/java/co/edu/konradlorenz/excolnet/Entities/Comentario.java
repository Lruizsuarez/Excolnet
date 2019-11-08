package co.edu.konradlorenz.excolnet.Entities;

/*
Entity for manage Comments
Author: Leonardo Ruiz
Modificado por Enrique Suarez:6/03/2019
 */
public class Comentario {

    private Usuario usuario;
    private String texto;
    private String fechaComentario;

    public Comentario() {
    }

    public Comentario(final Builder builder) {
        this.usuario = builder.usuario;
        this.texto = builder.texto;
        this.fechaComentario = builder.fechaComentario;

    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getTexto() {
        return texto;
    }

    public String getFechaComentario() {
        return fechaComentario;
    }

    public static class Builder {
        private Usuario usuario;
        private String texto;
        private String fechaComentario;

        public Builder usuario(final Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public Builder texto(final String texto) {
            this.texto = texto;
            return this;
        }

        public Builder fechaComentario(final String fechaComentario) {
            this.fechaComentario = fechaComentario;
            return this;
        }

        public Comentario create() {
            return new Comentario(this);
        }
    }
}
