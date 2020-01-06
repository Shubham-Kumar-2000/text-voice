package com.example.speechtotext;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.ui.SpeechProgressView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SpeechProgressView speechProgressView;
    private Button rec;
    private TextView text;
    TextToSpeech spk;
    private String str="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Integer.valueOf(Build.VERSION.SDK_INT)>=23) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        123);
            }
        }
        spk=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    spk.setLanguage(Locale.UK);
                }
            }
        });
        rec=findViewById(R.id.rec);
        text=(TextView)findViewById(R.id.text);
        speechProgressView=findViewById(R.id.progress);
        Speech.init(this, getPackageName());
        int[] heights = {60, 76, 58, 80, 55};
        speechProgressView.setBarMaxHeightsInDp(heights);
        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // you must have android.permission.RECORD_AUDIO granted at this point
                    Speech.getInstance().startListening(speechProgressView,new SpeechDelegate() {
                        @Override
                        public void onStartOfSpeech() {
                            Toast.makeText(getApplicationContext(),"Listening...",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSpeechRmsChanged(float value) {

                        }

                        @Override
                        public void onSpeechPartialResults(List<String> results) {
                            StringBuilder str = new StringBuilder();
                            for (String res : results) {
                                str.append(res).append(" ");
                            }

                        }

                        @Override
                        public void onSpeechResult(String result) {
                            Toast.makeText(getApplicationContext(),"Listened",Toast.LENGTH_SHORT).show();
                            str=result;
                            show();

                        }
                    });
                } catch (SpeechRecognitionNotAvailable exc) {
                    Log.e("speech", "Speech recognition is not available on this device!");

                } catch (GoogleVoiceTypingDisabledException exc) {
                    Log.e("speech", "Google voice typing must be enabled!");
                }

            }
        });
    }
    private void  show(){
        text.setText(str);
        spk.speak(str,TextToSpeech.QUEUE_FLUSH, null);


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 123: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    if(Integer.valueOf(Build.VERSION.SDK_INT)>=23) {
                        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    123);
                        }
                    }
                }
                break;
            }
        }
        return;
    }
}
