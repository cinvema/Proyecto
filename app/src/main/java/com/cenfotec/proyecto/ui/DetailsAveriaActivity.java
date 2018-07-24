package com.cenfotec.proyecto.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.entities.Averia;
import com.cenfotec.proyecto.entities.Ubicacion;
import com.cenfotec.proyecto.entities.Usuario;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsAveriaActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.id_averia_det)
    TextView id;

    @BindView(R.id.nombre_averia_det)
    TextView nombre;

    @BindView(R.id.tipo_averia_det)
    TextView tipo;

    @BindView(R.id.usuario_averia_det)
    TextView usuario;

    @BindView(R.id.fecha_averia_det)
    TextView fecha;

    @BindView(R.id.descripcion_averia_det)
    TextView descripcion;

    @BindView(R.id.ubicacion_averia_det)
    TextView ubicacionD;

    @BindView(R.id.imagen_averia_det)
    ImageView imageView;

    @BindView(R.id.btn_editar_averia_det)
    FloatingActionButton botonEditar;

    Averia averia;
    Usuario usuarioAveria;
    Ubicacion ubicacionAveria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_averia);

        ButterKnife.bind(this);

        averia = getIntent().getParcelableExtra("Averia");
        usuarioAveria = getIntent().getParcelableExtra("Usuario");
        ubicacionAveria = getIntent().getParcelableExtra("Ubicacion");

        //set text para todos los campos con validacion de vacios
        if(!averia.id.equals(""))
            id.setText(averia.id.toString());
        if(!averia.nombre.equals(""))
            nombre.setText(averia.nombre.toString());
        if(!averia.tipo.equals(""))
            tipo.setText(averia.tipo.toString());
        if(usuarioAveria.nombre != null) {
            usuario.setText(usuarioAveria.nombre.toString());
        }
        if(!averia.fecha.equals(""))
            fecha.setText(averia.fecha.toString());
        if(!averia.descripcion.equals(""))
            descripcion.setText(averia.descripcion.toString());
        if(ubicacionAveria!= null){
            ubicacionD.setText("Lat: "+ ubicacionAveria.lat
                    + " / Lon: "+ ubicacionAveria.lon);
        }
        if(!averia.imagen.equals(""))
            Picasso.get().load(averia.imagen.toString().trim()).into(imageView);


        botonEditar.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view.equals(botonEditar)){

            Intent intent = new Intent(this,UpdateAveriaActivity.class);
            intent.putExtra("Averia", averia);
            intent.putExtra("Usuario",usuarioAveria);
            intent.putExtra("Ubicacion", ubicacionAveria);
            startActivity(intent);

        }//fin del boton if
    }//fin del onClick

}
