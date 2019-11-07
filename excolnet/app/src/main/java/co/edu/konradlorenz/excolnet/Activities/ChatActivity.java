package co.edu.konradlorenz.excolnet.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import co.edu.konradlorenz.excolnet.Adapters.ChatAdapter;
import co.edu.konradlorenz.excolnet.Entities.Mensaje;
import co.edu.konradlorenz.excolnet.Entities.Usuario;
import co.edu.konradlorenz.excolnet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int OK_PHOTO = 200;
    private CircleImageView userChatImage;
    private TextView userChatName;
    private RecyclerView messageList;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton galleryButton;
    private Usuario chatUser;
    private FirebaseUser currentUser;
    private DatabaseReference myConversation;
    private DatabaseReference externalConversation;
    private DatabaseReference chatReference;
    private ArrayList<Mensaje> messages;
    private ValueEventListener valueEventListener;
    private RecyclerView.LayoutManager layoutManager;
    private ChatAdapter adapter;
    private final String message_Type_message = "1";
    private final String message_Type_image = "2";

    private FirebaseStorage storage;
    private StorageReference chatImgReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getLayoutComponents();
        getIntentUser();
        getFirebaseComponents();
        initializeRecycleView();
        addListeners();


        this.messages = new ArrayList<>();


    }


    @Override
    protected void onStart() {
        super.onStart();
        changeChatLayoutValues();

    }

    public void getLayoutComponents() {
        this.userChatImage = findViewById(R.id.circleAvatarImage);
        this.userChatName = findViewById(R.id.UserChatName);
        this.messageList = findViewById(R.id.MessageList);
        this.messageInput = findViewById(R.id.message_input);
        this.sendButton = findViewById(R.id.sendMessage_button);
        this.galleryButton = findViewById(R.id.galleryChatImage);

    }

    public void getIntentUser() {

        Usuario usuario = (Usuario) getIntent().getSerializableExtra("UserChat");

        if (usuario != null) {
            this.chatUser = usuario;
        }


    }


    public void changeChatLayoutValues() {
        if (chatUser != null) {
            Glide.with(getApplicationContext()).load(chatUser.getPhotoUrl()).placeholder(R.drawable.ic_profile).error(R.drawable.com_facebook_profile_picture_blank_square).into(userChatImage);
            userChatName.setText(chatUser.getDisplayName());
        }
    }

    public void getFirebaseComponents() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        chatReference = FirebaseDatabase.getInstance().getReference("BaseDatos").child("Chat");
        myConversation = chatReference.child(currentUser.getUid());
        externalConversation = chatReference.child(chatUser.getUid());
        storage = FirebaseStorage.getInstance();


    }

    public void initializeRecycleView() {
        this.layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new ChatAdapter(getApplicationContext(), currentUser.getUid());

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                ScrollBar();
            }
        });

        messageList.setHasFixedSize(true);
        messageList.setLayoutManager(layoutManager);
        messageList.setAdapter(adapter);
    }

    public void ScrollBar() {
        messageList.scrollToPosition(adapter.getItemCount() - 1);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getChatData();
    }


    public void getChatData() {
        this.adapter.getMessages().clear();
        this.valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //restartAdapter();


                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    Mensaje msg = dataSnap.getValue(Mensaje.class);

                    if (msg.getSenderUID().equals(currentUser.getUid())) {
                        if (msg.getDestinyUUID().equals(chatUser.getUid())) {
                            adapter.addMessage(msg);
                        }
                    } else if (msg.getSenderUID().equals(chatUser.getUid())) {
                        if (msg.getDestinyUUID().equals(currentUser.getUid())) {
                            adapter.addMessage(msg);
                        }
                    }
                }
                messageList.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity error: ", databaseError.getMessage() + " : " + databaseError.getDetails());
            }
        };

        myConversation.addValueEventListener(valueEventListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        myConversation.removeEventListener(valueEventListener);
    }

    public void addListeners() {
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Send a photo"), OK_PHOTO);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString();
                String time = getMessageTime();

                if (!message.isEmpty()) {
                    Mensaje mensaje = new Mensaje.Builder()
                            .senderUID(currentUser.getUid())
                            .destinyUUID(chatUser.getUid())
                            .senderDisplayName(chatUser.getDisplayName())
                            .photoUrl(Objects.requireNonNull(currentUser.getPhotoUrl()).toString())
                            .senderTime(time)
                            .message(message)
                            .message_type(message_Type_message)
                            .create();

                    myConversation.push().setValue(mensaje);
                    externalConversation.push().setValue(mensaje);
                    messageInput.setText("");
                }
            }
        });

    }


    public String getMessageTime() {
        String returnString = "";
        String amPm = "";
        Calendar time = new GregorianCalendar();
        int hour = time.get(Calendar.HOUR);
        int minute = time.get(Calendar.MINUTE);
        int amOrPm = time.get(Calendar.AM_PM);

        if (amOrPm == 0) {
            amPm = "a.m.";
        } else {
            amPm = "p.m.";
        }

        returnString = hour + ":" + minute + " " + amPm;

        return returnString;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == OK_PHOTO && resultCode == RESULT_OK) {
            Uri photo = data.getData();
            chatImgReference = storage.getReference("Chat_reference");
            final StorageReference referencePhoto = chatImgReference.child(photo.getLastPathSegment());


            UploadTask uploadTask = referencePhoto.putFile(photo);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return referencePhoto.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String time = getMessageTime();
                        Mensaje mensaje = new Mensaje.Builder()
                                .senderUID(currentUser.getUid())
                                .destinyUUID(chatUser.getUid())
                                .senderDisplayName(chatUser.getDisplayName())
                                .photoUrl(Objects.requireNonNull(currentUser.getPhotoUrl()).toString())
                                .senderTime(time)
                                .message(" ha enviado una foto.")
                                .message_type(message_Type_image)
                                .create();

                        myConversation.push().setValue(mensaje);
                        externalConversation.push().setValue(mensaje);
                    }
                }
            });

        }
    }
}
