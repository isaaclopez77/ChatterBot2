package com.example.dam.chatterbot;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.dam.chatterbot.java.com.google.code.chatterbotapi.ChatterBot;
import com.example.dam.chatterbot.java.com.google.code.chatterbotapi.ChatterBotFactory;
import com.example.dam.chatterbot.java.com.google.code.chatterbotapi.ChatterBotSession;
import com.example.dam.chatterbot.java.com.google.code.chatterbotapi.ChatterBotType;

import java.util.ArrayList;
import java.util.Locale;

public class ChatterBotApiTest extends Activity implements TextToSpeech.OnInitListener{

    private TextView tvRes;
    private TextToSpeech tts;
    private static final int SST =2;
    private static final int CTE =1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CTE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this,this);

            } else {
                Intent intent = new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }

        if (requestCode == SST) {
            Log.v("ESTADO", "TTS");
            if(data != null) {
                ArrayList<String> textos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (textos.size() > 0) {
                    tvRes.append("Tú> "+textos.get(0) + "\n");
                    Tarea t = new Tarea();
                    String s = textos.get(0);
                    t.execute(s);
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        tvRes= (TextView)findViewById(R.id.tvRespuesta);

        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, CTE);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
            tts.setPitch(1); //tono
            tts.setSpeechRate(1); //velocidad
            Log.v("estado","ha entrado");
        }
    }

    public void hablarle(View v) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"en-US");
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US");
        i.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en-US");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Habla ahora");
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,3000);
        startActivityForResult(i, SST);
    }

    public class Tarea extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                ChatterBotFactory factory = new ChatterBotFactory();
                ChatterBot bot1 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
                ChatterBotSession bot1session = bot1.createSession();

                String s = params[0];

                return bot1session.think(s);
            }catch (Exception e){}
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            tvRes.append("bot> "+s+"\n");
            tts.speak(s,TextToSpeech.QUEUE_FLUSH,null);
        }
    }
}