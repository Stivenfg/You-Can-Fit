package com.scj.youcanfit.clasesextra;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.fragment.app.DialogFragment;

import com.scj.youcanfit.HomeActivity;
import com.scj.youcanfit.R;

public class VideoDialogFragment extends DialogFragment {

    private WebView videoView;
    String [] url;
    String videoID;
    public VideoDialogFragment(String urlVideo) {

        // Seperamos el ID para poder insertarlo en el url del video y darle formato en Embed
        url = urlVideo.split("v=");
        videoID = url[1].toString();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Inicializando VideoView
        View rootView = inflater.inflate(R.layout.fragment_video_dialog,container,false);
        videoView = rootView.findViewById(R.id.videoView);

        // Establecemos la URL del video a reproducir
        String videoUrl = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+videoID+"\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        videoView.loadData(videoUrl,"text/html","utf-8" );
        videoView.getSettings().setJavaScriptEnabled(true);
        videoView.setWebChromeClient(new WebChromeClient());

        return rootView;
    }
}
