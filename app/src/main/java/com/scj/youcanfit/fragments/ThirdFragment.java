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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.scj.youcanfit.AuthActivity;
import com.scj.youcanfit.R;
import com.scj.youcanfit.clasesextra.Alumne;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    Alumne alumne = new Alumne();
    private static final int GALLERY_REQUEST_CODE = 1;


    public ThirdFragment() {

    }
    public void setAlumne(Alumne alumne) {
        this.alumne = alumne;
        // Actualizar las vistas con los datos del alumno
        ActualizarDatos();
    }
    public ThirdFragment(Alumne alumne) {
        this.alumne = alumne;
        System.out.println("PUNTOS ALUMNES PERFIL: "+alumne);

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
        String userDB = user.getDisplayName()+":"+user.getUid();
        ActualizarDatos();


        //CAMBIAR EL NOMBRE DEL USUARIO
        changueUser.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (!userName.isEnabled()){
                    userName.setEnabled(true);
                    userName.setTextColor(Color.parseColor("#808080"));

                }else{
                    userName.setEnabled(false);
                    userName.setTextColor(Color.parseColor("#4B4A4A"));

                    alumne.setNom(String.valueOf(userName.getText()));
                    //ACTUALIZAMOS EL NOMBRE DEL ALUMNO EN LA BASE DATOS, TANTO EN LA COLECCIÓN DE "USUARIS" COMO "PUNTUAJE USUARIOS"
                    Map<String,Object> actualitzarNom = new HashMap<>();
                    actualitzarNom.put("Nom",String.valueOf(userName.getText()));

                    db.collection("Usuaris").document(userDB).update(actualitzarNom)
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
                    db.collection("Puntuaje Usuarios").document(user.getDisplayName()+":"+user.getUid()).update(actualitzarNom);
                }
            }
        });

        //BOTON PARA CERRAR SESION
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cerramos la sesion de firebase
                FirebaseAuth.getInstance().signOut();
                //y cerramos la sesion de google
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


        //SELECCIONAR Y SUBIR FOTO AL FIREBASE FIRESTORE

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

                //En caso de que la imagen se invierta, lo revertimos
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
                //Convertimos la imagen en un array ByteArrayOutputStream para poder subirlo
                ByteArrayOutputStream img = new ByteArrayOutputStream();
                // antes de hacer la conversion comprimimos la imagen para que ocupe el 50%
                imgBitmap.compress(Bitmap.CompressFormat.JPEG,50,img);
                //hacemos la conversion
                byte[] datosImg = img.toByteArray();


                //Instanciamos FirebaseStorage facilitando la ubicacion de la carpeta de almacenaje
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
        alumne.setFoto(imageUrl);

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

    public void ActualizarDatos(){
        institut.setText(alumne.getInstitut());
        userName.setText(alumne.getNom());
        dataSex.setText(alumne.getSexo());
        edat.setText(alumne.getEdat());
        mail.setText(alumne.getEmail());

        if (alumne.getFoto() != null){
            Uri fotoUri = Uri.parse(alumne.getFoto());
            if (fotoUri != null) {
                Glide.with(ThirdFragment.this)
                        .load(fotoUri)
                        .centerCrop()
                        .into(foto);
            }
        }else {
            foto.setImageResource(R.drawable.default_user_photo);
        }

    }
}