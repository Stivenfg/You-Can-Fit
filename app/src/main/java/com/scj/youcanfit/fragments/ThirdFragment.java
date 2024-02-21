package com.scj.youcanfit.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        FirebaseUser user = auth.getCurrentUser();

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
        Uri fotoUri = user.getPhotoUrl();

        if (fotoUri != null){
            Glide.with(this)
                    .load(fotoUri)
                    .centerInside()
                    .fitCenter()
                    .into(foto);
        }else {
            foto.setImageResource(R.drawable.default_user_photo);
        }

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
    }
}