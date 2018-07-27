package com.cenfotec.proyecto.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.logic.Variables;

public class SplashActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tenemos una plantilla llamada splash.xml donde mostraremos la información que queramos (logotipo, etc.)
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            };
        }, Variables.DURACION_SPLASH);
    }
}