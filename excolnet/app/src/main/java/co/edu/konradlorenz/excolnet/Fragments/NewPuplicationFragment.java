package co.edu.konradlorenz.excolnet.Fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import co.edu.konradlorenz.excolnet.Entities.Publicacion;
import co.edu.konradlorenz.excolnet.Entities.Usuario;
import co.edu.konradlorenz.excolnet.R;
import co.edu.konradlorenz.excolnet.Utils.FilePaths;
import co.edu.konradlorenz.excolnet.Utils.FileSearch;
import co.edu.konradlorenz.excolnet.Utils.GridImageAdapter;


public class NewPuplicationFragment extends Fragment {

    private static final int NUM_GRID_COLUMNS = 3;
    private static final String TAG = "GalleryFragment";


    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;
    private LinearLayout menuFoto;
    private LinearLayout previsual;
    private ImageView quitarImagenButton;
    private ImageView userImage;
    private ImageView cerrar;
    private Button sendButton;
    private EditText textPublication;
    private DatabaseReference mDatabase;

    private String texto = "";

    private String id = "";
    private String pattern = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private String date = simpleDateFormat.format(new Date());

    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;
    private FirebaseAuth mAuth;
    private TextView userName;
    private Usuario usuario;
    private StorageReference publicationReference;

    private FirebaseStorage storage;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtenerElementosLayout();
        startAnimations();
    }

    private void startAnimations() {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.in_from_bottom);
        anim.reset();
        CardView l = getView().findViewById(R.id.card);
        l.clearAnimation();
        l.startAnimation(anim);
    }

    private void obtenerElementosLayout() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        userImage = Objects.requireNonNull(getView()).findViewById(R.id.user_image);
        assert user != null;
        Glide.with(this).load(user.getPhotoUrl()).into(userImage);
        userName = getView().findViewById(R.id.user_name);
        cerrar = getView().findViewById(R.id.cerrar);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePasswordRecoveryWindow();
            }
        });
        userName.setText(user.getDisplayName());
        ImageView agregarFoto = getView().findViewById(R.id.image_button);
        quitarImagenButton = getView().findViewById(R.id.quitar_imagen);
        agregarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFoto = Objects.requireNonNull(getView()).findViewById(R.id.agregar_imagen);
                menuFoto.setVisibility(View.VISIBLE);
                previsual = getView().findViewById(R.id.previsual);
                previsual.setVisibility(View.VISIBLE);
            }
        });
        quitarImagenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previsual.setVisibility(View.GONE);
                menuFoto.setVisibility(View.GONE);
            }
        });

        textPublication = getView().findViewById(R.id.text_publication);

        sendButton = getView().findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPublicacion();
            }
        });
    }

    private void crearPublicacion() {
        texto = textPublication.getText().toString();

        id = mDatabase.push().getKey();
        pattern = "yyyy-MM-dd";
        simpleDateFormat = new SimpleDateFormat(pattern);
        date = simpleDateFormat.format(new Date());

        if (!TextUtils.isEmpty(texto)) {
            usuario = new Usuario.Builder()
                    .displayName(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName())
                    .email(mAuth.getCurrentUser().getEmail())
                    .photoUrl(Objects.requireNonNull(mAuth.getCurrentUser().getPhotoUrl()).toString())
                    .uid(mAuth.getCurrentUser().getUid())
                    .create();

            Uri file = Uri.fromFile(new File(mSelectedImage));
            publicationReference = storage.getReference("Publication_reference");
            final StorageReference reference = publicationReference.child(file.getLastPathSegment());
            UploadTask uploadTask = reference.putFile(file);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // restartAdapter();
                        Uri downloadUri = task.getResult();

                        mDatabase.child("BaseDatos").child("Publicaciones").child(id).setValue(new Publicacion.Builder()
                                .id(id)
                                .usuario(usuario)
                                .texto(texto)
                                .fechaPublicacion(date)
                                .imagen(downloadUri.toString())
                                .create());
                    }
                }
            });
            closePasswordRecoveryWindow();

        } else {
            Snackbar.make(Objects.requireNonNull(getView()), "Error.", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void closePasswordRecoveryWindow() {
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.out_to_bottom);
        anim.reset();
        CardView l = getView().findViewById(R.id.card);
        l.clearAnimation();
        l.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FragmentManager manager = (NewPuplicationFragment.this).getFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove(NewPuplicationFragment.this);
                trans.commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_puplication, container, false);
        galleryImage = view.findViewById(R.id.galleryImageView);
        gridView = view.findViewById(R.id.gridView);
        directorySpinner = view.findViewById(R.id.spinnerDirectory);
        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();

        ImageView shareClose = view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFoto.setVisibility(View.GONE);
            }
        });

        init();

        return view;
    }

    private void init() {
        FilePaths filePaths = new FilePaths();

        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        directories.add(filePaths.CAMERA);

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupGridView(String selectedDirectory) {
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        try {
            setImage(imgURLs.get(0), galleryImage, mAppend);
            mSelectedImage = imgURLs.get(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " + e.getMessage());
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));

                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);
            }
        });

    }


    private void setImage(String imgURL, ImageView image, String append) {
        Log.d(TAG, "setImage: setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.init(ImageLoaderConfiguration.createDefault(Objects.requireNonNull(getActivity())));

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}