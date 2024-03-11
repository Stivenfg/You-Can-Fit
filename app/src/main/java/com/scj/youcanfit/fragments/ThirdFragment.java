package com.scj.youcanfit.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class ThirdFragment extends Fragment {

    Button logOut;
    TextView mail,dataSex,edat,institut;
    EditText userName;
    ImageView foto,changueUser;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;
    String imageName;
    private static final int GALLERY_REQUEST_CODE = 1;

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

        db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                //Insersion de foto
                                Uri fotoUri = Uri.parse(document.getString("Foto"));
                                //Si hay una foto en la base de datos del usuario, guardamos la foto en un Uri y lo ponemos de perfil, si no que ponga la imagen que tenemos por defecto
                                if (fotoUri != null){
                                    Glide.with(ThirdFragment.this)
                                            .load(fotoUri)
                                            .centerCrop()
                                            .into(foto);
                                }else {
                                    foto.setImageResource(R.drawable.default_user_photo);
                                }

                                institut.setText(document.getString("Institut"));
                                userName.setText(document.getString("Nom"));
                                dataSex.setText(document.getString("Sexo"));
                                edat.setText(document.getString("Edat"));

                                String dataNaixement = document.getString("Data naixement");
                                String [] fechaNacimiento= dataNaixement.split("/");
                                int day = Integer.parseInt(fechaNacimiento[0]);
                                int moth = Integer.parseInt(fechaNacimiento[1]);
                                int year = Integer.parseInt(fechaNacimiento[2]);


                                LocalDate fechaActual = LocalDate.now();
                                LocalDate fechaUsuario = LocalDate.of(year,moth+1,day);

                                Period period = Period.between(fechaUsuario,fechaActual);
                                String edat = String.valueOf(period.getYears());
                                String edatDB = document.getString("Edat");

                                if (!edat.equals(edatDB)){
                                    HashMap<String,Object> actualizarEdat = new HashMap<>();
                                    actualizarEdat.put("Edat",edat);
                                    db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid()).update(actualizarEdat);
                                }
                            }

                        }
                    }
                });

        mail.setText(user.getEmail()); //Recuperamos el correo


        //Funcion para cambiar el nombre del usuario
        changueUser.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (!userName.isEnabled()){
                    userName.setEnabled(true);
                    userName.setTextColor(Color.parseColor("#808080"));

                }else{
                    userName.setEnabled(false);
                    userName.setTextColor(Color.parseColor("#4B4A4A"));
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
            //Guardamos el URI de la imagen seleccionada
            Uri galeriaUri =data.getData();

            try {
                //Pasamos la imagen URI en un Bitmap
                imgBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), galeriaUri);
                //Recogemos los metadatos de la imagen para saber la orientacion de la imagen y evitar que se guarde erroneamente en Firestore
                InputStream inputStream = getContext().getContentResolver().openInputStream(galeriaUri);
                ExifInterface exif = new ExifInterface(inputStream);
                //Guardamos la orientacion correcta de la imagen en un integer y posteriormente en la matrix
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
                Matrix matrix = new Matrix();

                switch (orientation){
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                    default:
                        break;
                }
                //Editamos la imagen con la orientacion correcta
                imgBitmap = Bitmap.createBitmap(imgBitmap,0,0,imgBitmap.getWidth(),imgBitmap.getHeight(),matrix,true);
                ByteArrayOutputStream img = new ByteArrayOutputStream(); //Convertimos la imagen en un array ByteArrayOutputStream para poder subirlo
                imgBitmap.compress(Bitmap.CompressFormat.JPEG,50,img); // antes de hacer la conversion comprimimos la imagen para que ocupe el 50%
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


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