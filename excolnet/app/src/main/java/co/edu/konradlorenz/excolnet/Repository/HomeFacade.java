package co.edu.konradlorenz.excolnet.Repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFacade {

    private DatabaseReference db;

    public HomeFacade(){
        this.db = FirebaseDatabase.getInstance().getReference("BaseDatos");
    }

    public DatabaseReference getPublicationById(String id){
        return  db.child("Publicaciones")
                .child(id);
    }





}
