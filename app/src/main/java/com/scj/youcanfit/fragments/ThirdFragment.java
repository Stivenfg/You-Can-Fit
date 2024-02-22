package com.scj.youcanfit.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.scj.youcanfit.AuthActivity;
import com.scj.youcanfit.R;

import java.util.HashMap;
import java.util.Map;

public class ThirdFragment extends Fragment {

    Button logOut, changueUser;
    TextView mail,dataSex,edad;
    EditText userName;
    ImageView foto;
    Spinner institut;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth auth;
    FirebaseFirestore db;
    Uri galeriaUri;
    FirebaseUser user;
    String imageName;

    private static final int GALLERY_REQUEST_CODE = 1;


    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false);
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logOut = view.findViewById(R.id.logOut);
        changueUser = view.findViewById(R.id.changueUser);
        userName=view.findViewById(R.id.userName);
        mail=view.findViewById(R.id.mail);
        dataSex=view.findViewById(R.id.dataSex);
        edad=view.findViewById(R.id.edad);
        institut=view.findViewById(R.id.institut);
        foto = view.findViewById(R.id.foto);


        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        imageName = "img-"+user.getDisplayName()+".jpg";

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()){
                                        userName.setText(document.getString("Nom"));
                                    }
                                }
                            }
                        });



        mail.setText(user.getEmail());
        dataSex.setText("Pendiente...");
        edad.setText("Pendiente...");

        db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                Uri fotoUri = Uri.parse(document.getString("Foto"));
                                if (fotoUri != null){
                                    Glide.with(ThirdFragment.this)
                                            .load(fotoUri)
                                            .centerCrop()
                                            .into(foto);
                                }else {
                                    foto.setImageResource(R.drawable.default_user_photo);
                                }
                            }
                        }
                    }
                });



        changueUser.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (!userName.isEnabled()){
                    userName.setEnabled(true);
                    changueUser.setText("Desar");

                }else{
                    userName.setEnabled(false);
                    changueUser.setText("Cambiar nom d'usuari");

                    Map<String,Object> actualitzarNom = new HashMap<>();
                    actualitzarNom.put("Nom",String.valueOf(userName.getText()));

                    db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                            .update(actualitzarNom)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(),"El nom d'usuari s'ha actualitzat correctament",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Hi ha hagut algun error al actualitzar el nom d'usuari",Toast.LENGTH_SHORT).show();

                                }
                            });

                }

            }
        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                googleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),"Sesió tancada",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), AuthActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE && data != null){ //Confirmamos que el usuario ha seleccionado una imagen, en caso contrario nos devuelve a la activity
            galeriaUri =data.getData();
            Glide.with(this)
                    .load(galeriaUri)
                    .centerCrop()
                    .into(foto);

            FirebaseStorage storage = FirebaseStorage.getInstance("gs://you-can-fit-412207.appspot.com");
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("images/" + imageName);
            imageRef.putFile(galeriaUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            guardarUrlEnFirestore(imageUrl);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(getContext(),"Hi ha hagut algun error al pujar la foto",Toast.LENGTH_SHORT).show();
                    });

        }else Toast.makeText(getContext(),"No s'ha seleccionat cap imatge",Toast.LENGTH_SHORT).show();


    }

    private void guardarUrlEnFirestore(String imageUrl) {
        Map<String,Object> actualizarFoto = new HashMap<>();
        actualizarFoto.put("Foto",imageUrl);

        db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                .update(actualizarFoto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),"La foto s'ha actualitzat correctament",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Hi ha hagut algun error al actualitzar la foto",Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
