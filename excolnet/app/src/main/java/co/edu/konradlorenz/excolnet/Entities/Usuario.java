package co.edu.konradlorenz.excolnet.Entities;


import java.io.Serializable;


/*
Entity for manage Users
Author: Leonardo Ruiz
 */
public class Usuario implements Serializable {

    private String displayName;
    private String email;
    private String photoUrl;
    private String uid;

    private Usuario(final Builder builder) {
        this.displayName = builder.displayName;
        this.email = builder.email;
        this.photoUrl = builder.photoUrl;
        this.uid = builder.uid;
    }


    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public static class Builder {
        private String displayName;
        private String email;
        private String photoUrl;
        private String uid;

        public Builder displayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public Builder photoUrl(final String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public Builder uid(final String uid) {
            this.uid = uid;
            return this;
        }

        public Usuario create() {
            return new Usuario(this);
        }
    }
}
