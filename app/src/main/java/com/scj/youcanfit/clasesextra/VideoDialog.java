package com.scj.youcanfit.clasesextra;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.scj.youcanfit.R;


public class VideoDialog extends Dialog {

    private VideoView videoView;

    public VideoDialog(@NonNull Context context) {
        super(context);

        setContentView(R.layout.dialog_video);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Obtener referencia del VideoView
        videoView = findViewById(R.id.videoView);

        // Configura el video a reproducir
        String videoPath = "https://www.pexels.com/es-es/video/un-nino-usando-un-lapiz-escribiendo-en-un-papel-dentro-de-un-aula-3209663/"; // Esto puede ser la ruta local o una URL
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);


        // Inicia la reproducción del video
        videoView.start();
    }
}
