package com.cenfotec.proyecto.logic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.entities.Averia;
import com.cenfotec.proyecto.entities.Ubicacion;
import com.cenfotec.proyecto.entities.Usuario;
import com.cenfotec.proyecto.service.GestorServicio;
import com.cenfotec.proyecto.service.ServicioAveria;
import com.cenfotec.proyecto.ui.DetailsAveriaActivity;
import com.cenfotec.proyecto.utils.Utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>  {

    private List<Averia> averias;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    public Adapter(Context context, List<Averia> data) {
        this.mInflater = LayoutInflater.from(context);
        Collections.sort(data, new ComparadorAverias());
        this.averias = data;
        this.context=context;
    }

    class ComparadorAverias implements Comparator<Averia> {
        public int compare(Averia a, Averia b) {
            return a.id.compareTo(b.id);
        }
    }
    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.averia_item,parent,false);
        ViewHolder vh = new ViewHolder(view, this.context);
        return vh;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            holder.tvId.setText(averias.get(position).id);
            holder.tvNombre.setText(averias.get(position).nombre);
            holder.tvTipo.setText(averias.get(position).tipo);
            holder.tvDescripcion.setText(averias.get(position).descripcion);

            holder.id = averias.get(position).id;

        }catch (Exception e ){
            Log.d("mensaje", e.getMessage().toString());
        }

    }//fin del onBindViewHolder

    // total number of rows
    @Override
    public int getItemCount() {
        return averias.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        Context contexto;

        @BindView(R.id.id_averia_it)
        TextView tvId;

        @BindView(R.id.nombre_averia_it)
        TextView tvNombre;

        @BindView(R.id.tipo_averia_it)
        TextView tvTipo;

        @BindView(R.id.descripcion_averia_it)
        TextView tvDescripcion;

        public String  id = "";

        ViewHolder(View itemView, final Context context) {
            super(itemView);
            this.contexto=context;
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            //Se obtiene la referencia singleton desde el gestor.
            ServicioAveria servicio = GestorServicio.obtenerServicio();

            //Se llama al metodo definido en el servicio para obtener los detalles de un post en particular
            servicio.obtenerDetallesDeLaAveria(averias.get(getAdapterPosition()).id+"").enqueue(new Callback<Averia>() {
                @Override
                public void onResponse(Call<Averia> call, Response<Averia> response) {
                    //Si es exitosa, recuperamos la lista recibida de response.body()
                    Averia resultado = response.body();
                    Usuario usuario = new Usuario();
                    Ubicacion ubicacion = new Ubicacion();

                    usuario.nombre = resultado.usuario.nombre.toString();
                    usuario.correo = resultado.usuario.correo.toString();
                    usuario.tel = resultado.usuario.tel.toString();
                    usuario.cedula = resultado.usuario.cedula.toString();

                    ubicacion.lat = resultado.ubicacion.lat;
                    ubicacion.lon = resultado.ubicacion.lon;

                    Intent intent = new Intent(contexto, DetailsAveriaActivity.class);
                    intent.putExtra("Averia", resultado);
                    intent.putExtra( "Usuario", usuario);
                    intent.putExtra("Ubicacion", ubicacion);
                    contexto.startActivity(intent);
                }

                @Override
                public void onFailure(Call<Averia> call, Throwable t) {
                    //Si no, se muestra un error
                    Toast.makeText(contexto,
                            "Error al interactuar con el servicio",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
