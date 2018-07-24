package com.cenfotec.proyecto.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.entities.Averia;
import com.cenfotec.proyecto.logic.Adapter;
import com.cenfotec.proyecto.service.GestorServicio;
import com.cenfotec.proyecto.service.ServicioAveria;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AveriasFragment extends Fragment implements  View.OnClickListener {

//
//    @BindView(R.id.btn_agregar_averias)
//    Button botonAgregarAveria;


    @BindView(R.id.fab)
    FloatingActionButton botonAgregarAveria;

   List<Averia> averias;

    private RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;


    //Referencia a la instancia de la interfaz, que es asignada al invocarse
    //este fragment. Es asignada dentro del onAttach y corresponde al activity
    //padre de este fragment.
    GestorBotones gestorBotones;


    //Interfaz que indica los metodos que el activity padre de este fragment
    //debe implementar para poder responder a acciones del usuario (en este caso,
    //tocar alguno de los dos botones).
    public interface GestorBotones{
        void accionBotonAgregar();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inicializacion del view, de manera similar a un activity.
        View view = inflater.inflate(R.layout.fragment_averias,
                container, false);

        //inicializados los botones y lista
        ButterKnife.bind(this, view);

        mRecyclerView = view.findViewById(R.id.lista_averias);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(5);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mLayoutManager = new LinearLayoutManager(getContext()); //crear manager
        mRecyclerView.setLayoutManager(mLayoutManager);

        //pasar la lista de averias llena

        //Se obtiene la referencia singleton desde el gestor.
        ServicioAveria servicio = GestorServicio.obtenerServicio();

        //Se llama al metodo definido en el servicio para obtener las averias.
        servicio.obtenerTodasLasAverias().enqueue(new Callback<List<Averia>>() {
            @Override
            public void onResponse(Call<List<Averia>> call, Response<List<Averia>> response) {
                //Si es exitosa, recuperamos la lista recibida de response.body()
                averias = response.body();
                mAdapter = new Adapter(getContext(), averias);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onFailure(Call<List<Averia>> call, Throwable t) {
                //Si no, se muestra un error
                Snackbar.make(getView(), "Error al interactuar con el servicio\"", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        //Se le asigna esta clase como listener al evento de click del botn de crear averia
        botonAgregarAveria.setOnClickListener(this);
        //listaRecyclerView.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View view) {
        //Al hacerse click en alguno de los dos botones, la accion
        //de respuesta es realizada por la instancia de la interfaz
        //(ver MainActivity.java).
        if(view.equals(botonAgregarAveria)){
            gestorBotones.accionBotonAgregar();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
       // mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Se asigna a la instancia de la interfaz el contexto recibido en este
        //metodo, que corresponde al activity padre de este fragment.
        gestorBotones = (GestorBotones) context;
    }


}
