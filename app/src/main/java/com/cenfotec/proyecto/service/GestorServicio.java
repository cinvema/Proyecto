package com.cenfotec.proyecto.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GestorServicio {

    //Variable singleton privada, solo accesible por medio del
    //método obtenerServicio()
    private static ServicioAveria singleton;

    //Metodo por medio se accesa la instancia singleton
    public static ServicioAveria obtenerServicio(){

        //Si la instancia nunca ha sido inicializada, se crea:
        //Notese que durante el ciclo de vida de la aplicacion, este
        //bloque dentro del if solo va a ser llamado una vez
        if(singleton == null) {
            //Construcción del servicio usando patron Builder
            Retrofit retrofit = new Retrofit.Builder()
                    //Se especifica el URL base
                    .baseUrl(" https://fn3arhnwsg.execute-api.us-west-2.amazonaws.com/produccion/")
                    //Se especifica el serializados a usarse
                    //(en este caso gson para json)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            //Se crea la instancia de la interfaz ServicioAveria
            singleton = retrofit.create(ServicioAveria.class);
        }

        //Se retorna la instancia
        return singleton;
    }
}
