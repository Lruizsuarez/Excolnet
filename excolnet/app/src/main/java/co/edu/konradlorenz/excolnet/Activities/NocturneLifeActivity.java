package co.edu.konradlorenz.excolnet.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.konradlorenz.excolnet.Adapters.NocturneLifeAdapter;
import co.edu.konradlorenz.excolnet.Entities.Interes;
import co.edu.konradlorenz.excolnet.Factory.AdapterEnum;
import co.edu.konradlorenz.excolnet.Factory.AdapterFactory;
import co.edu.konradlorenz.excolnet.R;
import co.edu.konradlorenz.excolnet.Repository.SocialFacade;

public class NocturneLifeActivity extends AppCompatActivity {

    private RecyclerView nocturneList;
    private Toolbar toolbar;
    private ArrayList<Interes> intereses;
    private NocturneLifeAdapter adapt;
    private SocialFacade socialFacade;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nocturne_life);
        intereses = new ArrayList<>();
        getViewElements();
        initializeRecyclerView();
    }

    public void initializeRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        this.nocturneList.setHasFixedSize(true);
        this.nocturneList.setLayoutManager(manager);
    }

    public void getViewElements() {
        this.nocturneList = findViewById(R.id.nocturneList);
        this.toolbar = findViewById(R.id.toolbarNocturneLife);
        this.toolbar.setTitle("Nightlife");

    }


    @Override
    protected void onStart() {
        super.onStart();
        this.socialFacade = new SocialFacade();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {


        socialFacade.getNocturneLifeEvents().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("NocLife", "onDataChange bruh: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String tempTopicType = data.getKey();

                    data.getChildren().forEach(x -> {
                        Interes interes = x.getValue(Interes.class);
                        interes.setTopicName(x.getKey());
                        interes.setTopicType(tempTopicType);
                        intereses.add(interes);
                    });
                }
                Collections.shuffle(intereses);
                adapt = (NocturneLifeAdapter) AdapterFactory.getAdapter(AdapterEnum.NOCTURNE);
                adapt.setMyContext(getApplicationContext());
                adapt.setCurrentInterests(intereses);
                nocturneList.setAdapter(adapt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
