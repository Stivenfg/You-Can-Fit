package com.scj.youcanfit.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.scj.youcanfit.AuthActivity;
import com.scj.youcanfit.R;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirdFragment extends Fragment {

    Button logOut, changueUser;
    TextView mail,dataSex,edat,institut;
    EditText userName;
    ImageView foto;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth auth;
    FirebaseFirestore db;
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
    public void onStart() {
        userName.setEnabled(false);

        super.onStart();
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logOut = view.findViewById(R.id.logOut);
        changueUser = view.findViewById(R.id.changueUser);
        userName=view.findViewById(R.id.userName);
        mail=view.findViewById(R.id.mail);
        dataSex=view.findViewById(R.id.dataSex);
        edat=view.findViewById(R.id.edad);
        foto = view.findViewById(R.id.foto);
        institut=view.findViewById(R.id.institut);

        //Se inicializa la base de datos y el Auth, aparte de que recuperamos el usuario
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        userName.setEnabled(false); //Desactivamos el EditText del nombre

        //Tambien recuperamos los datos del usuario en caso de que haya iniciado sesion con google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        user = auth.getCurrentUser();

        mail.setText(user.getEmail()); //Recuperamos el correo

        db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()){
                                        institut.setText(document.getString("Institut"));
                                        userName.setText(document.getString("Nom"));
                                        dataSex.setText(document.getString("Sexo"));
                                        edat.setText(document.getString("Edat"));
                                    }
                                }
                            }
                        });



        //De igual forma que el nombre de usurio hacemos lo mismo con la foto de perfil
        db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            //Si hay una foto en la base de datos del usuario, guardamos la foto en un Uri y lo ponemos de perfil, si no que ponga la imagen que tenemos por defecto
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


        //Funcion para cambiar el nombre del usuario
        changueUser.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (!userName.isEnabled()){
                    userName.setEnabled(true);
                    changueUser.setText("Desar");

                }else{
                    userName.setEnabled(false);
                    changueUser.setText("Cambiar nom d'usuari");

                    //Hacemos un Map para poder guardar el nombre del usuario e indicar donde lo guardaremos
                    Map<String,Object> actualitzarNom = new HashMap<>();
                    actualitzarNom.put("Nom",String.valueOf(userName.getText()));

                    //Actualizamos la base de datos con el Map creado
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

        //Boton de cerrar sesion
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Instanciamos la base de datos y cerramos la sesion de firebase
                FirebaseAuth.getInstance().signOut();
                //Instanciamos la base de datos y cerramos la sesion de google
                googleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),"Sesió tancada",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), AuthActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }
        });

        //Creamos un nombre para la foto de perfil que suba el usuario
        imageName = "img-"+user.getDisplayName()+".jpg";
        //Al hacer click sobre la foto abrimos la galeria y filtramos para que solo nos muestre las imagenes
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

        Bitmap imgBitmap;

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE && data != null){
            //Si el usuario ha seleccionado una foto la guardamos el Uri en una variable
            Uri galeriaUri =data.getData();

            //Pasamos la imagen recuperada en un bitmap para poderlo subir al FireStorage de forma comprimida
            try {
                imgBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), galeriaUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ByteArrayOutputStream img = new ByteArrayOutputStream(); //Convertimos la imagen en un array ByteArrayOutputStream para poder subirlo
            imgBitmap.compress(Bitmap.CompressFormat.JPEG,50,img); // antes de hacer la conversion comprimimos la imagen para que ocupe la mitad y asi ocupe lo minimo en la base de datos
            byte[] datosImg = img.toByteArray(); //hacemos la conversion


            //Instanciamos FirebaseStorage
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://you-can-fit-412207.appspot.com");
            StorageReference storageRef = storage.getReference();
            //Creamos la referencia de como queremos que se llame la imagen que se subira en FirebaseStorage
            StorageReference imageRef = storageRef.child("images/" + imageName);
            //Subimos la foto en la base de datos
            UploadTask uploadTask = imageRef.putBytes(datosImg);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Una vez se haya subido la foto, la actualizamos en el ImageView
                    Glide.with(getContext())
                            .load(galeriaUri)
                            .centerCrop()
                            .into(foto);

                    Toast.makeText(getContext(),"La foto s'ha actualitzat correctament",Toast.LENGTH_SHORT).show();
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        guardarUrlEnFirestore(imageUrl);
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Hi ha hagut algun error al pujar la foto",Toast.LENGTH_SHORT).show();
                }
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
