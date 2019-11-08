package co.edu.konradlorenz.excolnet.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import co.edu.konradlorenz.excolnet.Adapters.ChatAdapter;
import co.edu.konradlorenz.excolnet.Entities.Mensaje;
import co.edu.konradlorenz.excolnet.Entities.Usuario;
import co.edu.konradlorenz.excolnet.Factory.AdapterEnum;
import co.edu.konradlorenz.excolnet.Factory.AdapterFactory;
import co.edu.konradlorenz.excolnet.R;
import co.edu.konradlorenz.excolnet.Repository.AuthFacade;
import co.edu.konradlorenz.excolnet.Repository.SocialFacade;
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
    private ValueEventListener valueEventListener;
    private RecyclerView.LayoutManager layoutManager;
    private ChatAdapter adapter;
    private final String message_Type_message = "1";
    private final String message_Type_image = "2";
    private AuthFacade authFacade;
    private SocialFacade socialFacade;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getLayoutComponents();
        getIntentUser();
        getFirebaseComponents();
        initializeRecycleView();
        addListeners();
    }


    @Override
    protected void onStart() {
        super.onStart();
        this.authFacade = new AuthFacade();
        this.socialFacade = new SocialFacade();
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
        socialFacade.getChatById(authFacade.getUser().getUid());
        storage = FirebaseStorage.getInstance();
    }

    public void initializeRecycleView() {
        this.layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = (ChatAdapter) AdapterFactory.getAdapter(AdapterEnum.CHAT);
        adapter.setCurrentUserUID(authFacade.getUser().getUid());
        adapter.setMyContext(getApplicationContext());

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
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    Mensaje msg = dataSnap.getValue(Mensaje.class);

                    if (Objects.requireNonNull(msg).getSenderUID().equals(authFacade.getUser().getUid())) {
                        if (msg.getDestinyUUID().equals(chatUser.getUid()))
                            adapter.addMessage(msg);

                    } else if (msg.getSenderUID().equals(chatUser.getUid())) {
                        if (msg.getDestinyUUID().equals(authFacade.getUser().getUid()))
                            adapter.addMessage(msg);
                    }
                    messageList.setAdapter(adapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity error: ", databaseError.getMessage() + " : " + databaseError.getDetails());
            }
        };

        socialFacade.getChatById(authFacade.getUser().getUid()).addValueEventListener(valueEventListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        socialFacade.getChatById(authFacade.getUser().getUid()).removeEventListener(valueEventListener);
    }

    public void addListeners() {
        galleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Send a photo"), OK_PHOTO);
        });

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString();
            String time = getMessageTime();

            if (!message.isEmpty()) {
                Mensaje mensaje = new Mensaje.Builder()
                        .senderUID(authFacade.getUser().getUid())
                        .destinyUUID(chatUser.getUid())
                        .senderDisplayName(chatUser.getDisplayName())
                        .photoUrl(Objects.requireNonNull(authFacade.getUser().getPhotoUrl()).toString())
                        .senderTime(time)
                        .message(message)
                        .message_type(message_Type_message)
                        .create();

                socialFacade.getChatById(authFacade.getUser().getUid()).push().setValue(mensaje);
                socialFacade.getChatById(chatUser.getUid()).push().setValue(mensaje);
                messageInput.setText("");
            }
        });

    }


    public String getMessageTime() {
        String returnString;
        String amPm;
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
            Uri photo = Objects.requireNonNull(data).getData();
            StorageReference chatImgReference = storage.getReference("Chat_reference");
            final StorageReference referencePhoto = chatImgReference.child(photo.getLastPathSegment());

            UploadTask uploadTask = referencePhoto.putFile(photo);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                return referencePhoto.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String time = getMessageTime();
                    Mensaje mensaje = new Mensaje.Builder()
                            .senderUID(authFacade.getUser().getUid())
                            .destinyUUID(chatUser.getUid())
                            .senderDisplayName(chatUser.getDisplayName())
                            .photoUrl(Objects.requireNonNull(authFacade.getUser().getPhotoUrl()).toString())
                            .senderTime(time)
                            .message(" ha enviado una foto.")
                            .message_type(message_Type_image)
                            .create();

                    socialFacade.getChatById(authFacade.getUser().getUid()).push().setValue(mensaje);
                    socialFacade.getChatById(chatUser.getUid()).push().setValue(mensaje);
                }
            });
        }
    }
}
