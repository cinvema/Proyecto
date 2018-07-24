package com.cenfotec.proyecto.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.entities.Averia;
import com.cenfotec.proyecto.entities.Ubicacion;
import com.cenfotec.proyecto.entities.Usuario;
import com.cenfotec.proyecto.logic.Adapter;
import com.cenfotec.proyecto.service.GestorServicio;
import com.cenfotec.proyecto.service.ServicioAveria;
import com.cenfotec.proyecto.ui.AddAveriaActivity;
import com.cenfotec.proyecto.ui.DetailsAveriaActivity;
import com.cenfotec.proyecto.ui.MainActivity;
import com.cenfotec.proyecto.ui.UpdateAveriaActivity;
import com.cenfotec.proyecto.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener{

    //Codigo del intent para abrir el formulario
    private final static int PERM_CODE = 1;
    MapView mMapView;
    private GoogleMap googleMap;
    //crear un nuevo marker
    private LatLng mTempPosicion;
    List<Averia> averias;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mapa, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return rootView;
    }



    private void chequearPermiso() {
        //Obtenemos el estado del permiso de ubicacion
        int state = ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        //Si lo tenemos, habilitamos el boton de ubicacion del usuario
        if (state == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            //Si no, pedimos permiso
            preguntarPorPermiso();
        }
    }

    public void preguntarPorPermiso(){
        //Pedimos permiso para el de tipo ACCESS_FINE_LOCATION
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERM_CODE);
    }

    //Esta funcion dicta que ocurre al recibir un evento de presion
    //prolongada sobre una posicion no ocupada del mapa
    @Override
    public void onMapLongClick(LatLng latLng) {
        //Guardamos la ubicacion presionada para recordarla
        mTempPosicion = latLng;

        new AlertDialog.Builder(getContext())
                .setTitle("Información")
                .setMessage("¿Desea crear una averia?")
                .setPositiveButton("Crear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Crear", "Creando...");
                        try {
                           //crear la averia

                            //obtener el id desde el generador de ids
                            Utils utils = new Utils();
                            String id = utils.getRandomString(5);
                            Marker marcador = googleMap.addMarker(new MarkerOptions()
                                    .position(mTempPosicion)
                                    .title("Averia-"+id));

                            Ubicacion ubicacion = new Ubicacion();
                            ubicacion.lat = mTempPosicion.latitude;
                            ubicacion.lon = mTempPosicion.longitude;

                            Intent intent = new Intent(getContext(), AddAveriaActivity.class);
                            //averia agregada desde el mapa
                            intent.putExtra("NuevaAveria", 1);
                            intent.putExtra("Id", id);
                            intent.putExtra("Ubicacion", ubicacion);
                            startActivity(intent);



                        }catch (Exception e){

                            Log.d("Error",e.getMessage().toString());
                        }

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Crear", "Cancelando...");
                    }
                })
                .show();


    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        //Consultar la averia especifica
        String idAveria = marker.getTitle().toString();

        //Se obtiene la referencia singleton desde el gestor.
        ServicioAveria servicio = GestorServicio.obtenerServicio();

        //Se llama al metodo definido en el servicio para obtener los detalles de un post en particular
        servicio.obtenerDetallesDeLaAveria(idAveria).enqueue(new Callback<Averia>() {
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

                Intent intent = new Intent(getContext(), DetailsAveriaActivity.class);
                intent.putExtra("Averia", resultado);
                intent.putExtra( "Usuario", usuario);
                intent.putExtra("Ubicacion", ubicacion);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Averia> call, Throwable t) {
                //Si no, se muestra un error
                Snackbar.make(getView(), "Error al interactuar con el servicio\"", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Si el usuario nos dio permiso, entonces podemos llamar a
        //chequearPermiso de nuevo. Si no, no hacemos nada (y el boton de
        //ubicacion no se va a mostrar)
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            chequearPermiso();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleM) {

        googleMap = googleM;

        //mostrar todas las averias en el mapa
        //hacer un ciclo en el que se obtengan todas las averias y recorrerlas para ir agregando los marcadores

        //Se obtiene la referencia singleton desde el gestor.
        ServicioAveria servicio = GestorServicio.obtenerServicio();

        //Se llama al metodo definido en el servicio para obtener las averias.
        servicio.obtenerTodasLasAverias().enqueue(new Callback<List<Averia>>() {
            @Override
            public void onResponse(Call<List<Averia>> call, Response<List<Averia>> response) {
                //Si es exitosa, recuperamos la lista recibida de response.body()
                averias = response.body();
                for (int i=0; i<averias.size(); i++){

                    //recorrer cada averia y hacer un marcador
                    double latitud , longitud;
                    Ubicacion ubicacion = new Ubicacion();
                    ubicacion = averias.get(i).ubicacion;
                    if(ubicacion != null){
                        if (ubicacion.lat != 0.0 && ubicacion.lon!= 0.0){

                            latitud = (double)ubicacion.lat;
                            longitud = (double)ubicacion.lon;

                            LatLng posicion = new LatLng(latitud, longitud);
                            Marker marcador = googleMap.addMarker(new MarkerOptions()
                                    .position(posicion)
                                    .title(averias.get(i).id.toString())
                                    .snippet(averias.get(i).tipo.toString())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                        }//fin del if

                    }//fin del if externo
                }//fin del for
            }//fin del onResponse

            @Override
            public void onFailure(Call<List<Averia>> call, Throwable t) {
                //Si no, se muestra un error
                Snackbar.make(getView(), "Error al interactuar con el servicio\"", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        googleMap.setOnMapLongClickListener(this);

        // For dropping a marker at a point on the Map
        LatLng marcador = new LatLng(9.9220422, -84.0730673);

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(marcador).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setOnInfoWindowClickListener(this);
        chequearPermiso();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }



}
