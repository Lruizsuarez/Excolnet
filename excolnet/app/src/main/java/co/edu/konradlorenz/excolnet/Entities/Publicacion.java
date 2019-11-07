package co.edu.konradlorenz.excolnet.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Publicacion implements Serializable {

    private String id;
    private Usuario usuario;
    private String texto;
    private String fechaPublicacion;
    private ArrayList<Comentario> comentarios;
    private List<Usuario> usuariosQueGustan;
    private String imagen;


    private Publicacion(final Builder builder) {
        this.comentarios = new ArrayList<>();
        this.usuariosQueGustan = new ArrayList<>();
        this.id = builder.id;
        this.usuario = builder.usuario;
        this.texto = builder.texto;
        this.fechaPublicacion = builder.fechaPublicacion;
        this.imagen = builder.imagen;
    }


    public String getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getTexto() {
        return texto;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public ArrayList<Comentario> getComentarios() {
        return comentarios;
    }

    public List<Usuario> getUsuariosQueGustan() {
        return usuariosQueGustan;
    }

    public String getImagen() {
        return imagen;
    }

    public static class Builder {
        private String id;
        private Usuario usuario;
        private String texto;
        private String fechaPublicacion;
        private String imagen;


        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder usuario(final Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public Builder texto(final String texto) {
            this.texto = texto;
            return this;
        }

        public Builder fechaPublicacion(final String fechaPublicacion) {
            this.fechaPublicacion = fechaPublicacion;
            return this;
        }


        public Builder imagen(final String imagen) {
            this.imagen = imagen;
            return this;
        }

        public Publicacion create(){
            return new Publicacion(this);
        }
    }
}
