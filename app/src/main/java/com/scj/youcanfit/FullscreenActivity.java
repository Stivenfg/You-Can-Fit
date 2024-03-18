package com.scj.youcanfit;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 6000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;

    ImageView logoAnimado;
    //Animation animation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        logoAnimado = findViewById(R.id.imageView);
        //animation = AnimationUtils.loadAnimation(logoAnimado.getContext(), R.anim.anima_logo_splash);
        //logoAnimado.startAnimation(animation);



        // Animación de entrada (desde el lado izquierdo)
        ObjectAnimator translationX = ObjectAnimator.ofFloat(logoAnimado, View.TRANSLATION_X, -2 *logoAnimado.getWidth(), 0);
        translationX.setDuration(500);
        translationX.setInterpolator(new AccelerateDecelerateInterpolator());

        // Animación de rotación (360 grados)
        ObjectAnimator rotation = ObjectAnimator.ofFloat(logoAnimado, View.ROTATION, 0f, 360f);
        rotation.setDuration(500);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());

        // Animación de escalado (hasta cubrir la pantalla)
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logoAnimado, View.SCALE_X, 1f, 3f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logoAnimado, View.SCALE_Y, 1f, 3f);
        AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.playTogether(scaleX, scaleY);
        scaleSet.setDuration(2000);
        scaleSet.setInterpolator(new AccelerateDecelerateInterpolator());

        // Combinar todas las animaciones
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(translationX, rotation, scaleSet);

        // Iniciar la animación
        animatorSet.start();


        Thread mythread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    verificarUsuarioLogeado();

                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        mythread.start();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

        }
    };

    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    public void verificarUsuarioLogeado(){

        //Miramos si se puede recuperar un usaurio activo desde la ultima conexion
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(user != null && user.isEmailVerified()){ //si el usuario que se trata de recuperar no es nulo y esta verificado, inicia la sesion y entra en la cuenta de dicho usuario
            db.collection("Usuaris").document(user.getDisplayName()+":"+user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document =task.getResult();
                    if (document.exists()){

                        if (document.getString("Institut").isEmpty() || document.getString("Edat").isEmpty() || document.getString("Sexo").isEmpty()){

                            Intent intent = new Intent(FullscreenActivity.this, FormulariUsuari.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(FullscreenActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            });
        }else{
            Intent intent = new Intent(FullscreenActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }
    }
}