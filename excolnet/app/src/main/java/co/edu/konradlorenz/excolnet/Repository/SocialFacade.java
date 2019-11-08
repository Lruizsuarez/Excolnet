package co.edu.konradlorenz.excolnet.Repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SocialFacade {

    private DatabaseReference db;

    public SocialFacade(){
        this.db = FirebaseDatabase.getInstance().getReference("BaseDatos");
    }

    public  DatabaseReference getChatById(String id){
        return db.child("Chat").child(id);
    }

    public DatabaseReference getNocturneLifeEvents(){
        return db.child("NocturneLife");
    }

    public  DatabaseReference getPublications(){
        return  db.child("Publicaciones");
    }
}
