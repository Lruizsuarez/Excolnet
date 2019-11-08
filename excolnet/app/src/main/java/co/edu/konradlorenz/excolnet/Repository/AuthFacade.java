package co.edu.konradlorenz.excolnet.Repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.edu.konradlorenz.excolnet.Entities.Usuario;

public class AuthFacade {

    private DatabaseReference db;
    private FirebaseAuth auth;

    public AuthFacade() {
        db = FirebaseDatabase.getInstance().getReference("BaseDatos");
        auth = FirebaseAuth.getInstance();
    }

    public void saveUser(Usuario usuario) {
        db.child("Users")
                .child(usuario.getUid())
                .setValue(usuario);
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }

    public DatabaseReference getUsers(){
        return this.db.child("Users");
    }
}
