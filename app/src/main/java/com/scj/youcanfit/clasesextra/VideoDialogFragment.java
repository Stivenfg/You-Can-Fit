package com.scj.youcanfit.clasesextra;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.fragment.app.DialogFragment;

import com.scj.youcanfit.HomeActivity;
import com.scj.youcanfit.R;

public class VideoDialogFragment extends DialogFragment {

    private VideoView videoView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Inicializando VideoView
        View rootView = inflater.inflate(R.layout.fragment_video_dialog,container,false);
        videoView = rootView.findViewById(R.id.videoView);

        // Configurar MediaController para controlar la reproducción del video
        MediaController mediaController = new MediaController(requireContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);


        // Establecer la URL del video a reproducir
        String videoUrl = "https://www.youtube.com/watch?v=Uy2nUNX38xE&ab_channel=Foroatletismo";
        Uri videoUri = Uri.parse(videoUrl);
        videoView.setVideoURI(videoUri);

        // Reproducir el video
        videoView.start();

        return rootView;
    }
}
