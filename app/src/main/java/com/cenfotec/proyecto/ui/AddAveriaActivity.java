package com.cenfotec.proyecto.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.entities.Averia;
import com.cenfotec.proyecto.entities.Ubicacion;
import com.cenfotec.proyecto.entities.Upload;
import com.cenfotec.proyecto.entities.Usuario;
import com.cenfotec.proyecto.helpers.PreferencesManager;
import com.cenfotec.proyecto.logic.Variables;
import com.cenfotec.proyecto.service.GestorServicio;
import com.cenfotec.proyecto.service.ServicioAveria;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAveriaActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.id_averia_add)
    EditText id;

    @BindView(R.id.nombre_averia_add)
    EditText nombre;

    @BindView(R.id.tipo_averia_add)
    EditText tipo;

    @BindView(R.id.fecha_averia_add)
    EditText fecha;

    @BindView(R.id.descripcion_averia_add)
    EditText descripcion;

    @BindView(R.id.imagen_averia_add)
    ImageView imageView;

    @BindView(R.id.agregar_nueva_averia_add)
    Button botonAgregar;

    @BindView(R.id.btn_agregar_foto_add)
    Button botonAgregarFoto;

    Averia averia;
    Ubicacion ubicacion;
    Usuario usuarioAveria;

    private Uri mUri;
    // private Upload upload;
    private static final int PERM_CODE = 1000;
    private static final int REQUEST_TAKE_PHOTO = 101;
    LocationManager mLm;
    int mTipoUbicacion = 2;
    private int mValorNuevaAveria = 2 ;
    private String mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_averia);

        ButterKnife.bind(this);
        botonAgregar.setOnClickListener(this);
        botonAgregarFoto.setOnClickListener(this);

        //leer de que tipo es la averia para saber si se carga la ubicacion actual o
        // la ubicacion del mapa en donde indicó el usuario
        mValorNuevaAveria = getIntent().getIntExtra("NuevaAveria",0);
        mId = getIntent().getStringExtra("Id");

        id.setText("Averia-" + mId);

    }

    @Override
    public void onClick(View v) {

        if (v.equals(botonAgregarFoto)) {
            verificarPermisosAlmacenamientoExterno();
        }

        if (v.equals(botonAgregar)) {

            //armar el objeto averia segun lo que indique el usuario
            averia = new Averia();
            ubicacion = new Ubicacion();
            usuarioAveria = new Usuario();

            //obtener el usuario de la base de datos segun el usuario que este logueado
            String usuarioStr = PreferencesManager.getUsernameFromPreferences(this);
            //realizar la busqueda por el usuario logueado en la base de datos

            usuarioAveria.nombre = Variables.nombre;
            usuarioAveria.correo = Variables.correo;
            usuarioAveria.tel = Variables.tel;
            usuarioAveria.cedula = Variables.cedula;

            //obtener la ubicacion actual

            if(mValorNuevaAveria == 0){//si es una averia creada desde la lista

                mLm = (LocationManager) getApplicationContext()
                        .getSystemService(Context.LOCATION_SERVICE);
                //ubicacion = verificarPermisosUbicacion();
                ubicacion =  obtenerLocalizacion();
            }else if(mValorNuevaAveria == 1){ //si es una averia creada desde mapa


            }

            //armar el objeto de la averia
            averia.id = id.getText().toString();
            averia.nombre = nombre.getText().toString();
            averia.tipo = tipo.getText().toString();
            averia.usuario = usuarioAveria;
            averia.fecha = fecha.getText().toString();
            averia.descripcion = descripcion.getText().toString();
            averia.imagen = "url de la imagen";
            averia.ubicacion = ubicacion;

            //Se obtiene la referencia singleton desde el gestor.
            ServicioAveria servicio = GestorServicio.obtenerServicio();

            //Se llama al metodo definido en el servicio para crear la averia

            servicio.crearNuevaAveria(averia).enqueue(new Callback<Averia>() {
                @Override
                public void onResponse(Call<Averia> call, Response<Averia> response) {
                    //Si es exitosa, recuperamos la lista recibida de response.body()
                    Averia resultado = response.body();

                    if (resultado != null) {
                        Toast.makeText(getApplicationContext(),
                                "Exito registrando la averia",
                                Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<Averia> call, Throwable t) {

                    Toast.makeText(getApplicationContext(),
                            "Ha ocurrido un error registrando la averia",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }//fin del boton agregar
    }//fin del onClick

    private void verificarPermisosAlmacenamientoExterno() {
        //Obtenemos el estado actual de los permisos
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Si ya tenemos permisos, continuamos tomando la foto
        //Si no, pedimos permiso
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            continuarTomarFoto();
        } else {
            askForPermissionAccesoExterno();

        }
    }

    public void askForPermissionAccesoExterno() {
        //Hacemos la solicitud de permiso
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERM_CODE);

        //Obtenemos el estado actual de los permisos
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Si ya tenemos permisos, continuamos tomando la foto
        //Si no, pedimos permiso
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            continuarTomarFoto();
        }
    }

    private void continuarTomarFoto() {
        //Llamamos al metodo crearArchivo para obtener un
        //archivo en el cual guardar la foto
       // File archivo = crearArchivo();
        File mFile = crearArchivo();

        //Construimos un intent con una peticion de captura
        //de imagenes
        Intent takePictureIntent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Guardamos en el directorio usando el FileProvider:
        mUri = FileProvider.getUriForFile(this,
                "com.cenfotec.fotos",
                mFile);

        //Especificamos el URI en el que queremos que se guarde
        //la imagen
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        //Ejecutamos el intent, cediendo control a la aplicacion
        //de toma de fotos que el usuario seleccione
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    private File crearArchivo() {
        try {
            //Creamos un nombre unico para el archivo, basado
            //en la fecha y hora actual
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            String imageFileName = "JPEG_" + timeStamp;

            File storageDir =
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            //Creamos el archivo para la imagen...
            File image = File.createTempFile(
                    imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            );
            //...y lo retornamos
            return image;
        }catch(Exception e){
            Log.d("Prueba", e.getMessage());
            return null;
        }
    }

    //Al haber llamado a onStartActivityForResult, indicamos al
    //sistema que llame a este callback una vez la foto haya sido
    //capturada y almacenada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verificamos que el codigo de respuesta sea igual al codigo
        //de peticion que especificamos al ejecutar el intent

        //Tambien verificamos que el codigo resultado sea RESULT_OK,
        //lo cual indica que la foto fue capturada exitosamente.
        if (requestCode == REQUEST_TAKE_PHOTO &&
                resultCode == RESULT_OK) {
            try {
                //Obtenemos el BitMap a partir del URI que habiamos
                //obtenido anteriormente
                Bitmap imageBitmap =
                        MediaStore.Images.Media.
                                getBitmap(getContentResolver(), mUri);

                //Mostramos el bitmap en el ImageView declarado
                //en nuestro layout file
                imageView.setImageBitmap(imageBitmap);

                }catch(Exception e){
                Log.d("Error", e.getMessage());
            }
        }

        ///subida de la imagen

    }

    ///metodos para ubicacion
    private Ubicacion obtenerLocalizacion() {

        Ubicacion ubicacion = new Ubicacion();

        boolean gps_enabled = false;
        boolean network_enabled = false;


        gps_enabled = mLm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = mLm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        //gps = 0
        //network = 1

        if (gps_enabled){
            mTipoUbicacion = 0;
            gps_loc = chequearPermisoUbicacion();
        }
        if (network_enabled){
            mTipoUbicacion = 1;
            net_loc = chequearPermisoUbicacion();
        }

        if (gps_loc != null && net_loc != null) {

            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

            ubicacion = asignarCoordenadas(finalLoc);
        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }

            ubicacion = asignarCoordenadas(finalLoc);
        }
        return ubicacion;

    }

    private Ubicacion asignarCoordenadas (Location location){

        Ubicacion ubicacion = new Ubicacion();
        if(location!= null){
            double lati = location.getLatitude();
            double longit = location.getLongitude();

           ubicacion.lat = lati;
           ubicacion.lon = longit;

        }
        return ubicacion;

    }

    public void permisosUbicacion(){
        //Pedimos permiso para el de tipo ACCESS_FINE_LOCATION
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERM_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Si el usuario nos dio permiso, entonces podemos llamar a
        //chequearPermiso de nuevo. Si no, no hacemos nada (y el boton de
        //ubicacion no se va a mostrar)
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            chequearPermisoUbicacion();
        }
    }
    private Location chequearPermisoUbicacion() {
        Location loc = null;
        //Obtenemos el estado del permiso de ubicacion
        int state = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckAC = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        //Si lo tenemos, habilitamos el boton de ubicacion del usuario
        if (state == PackageManager.PERMISSION_GRANTED && permissionCheckAC == PackageManager.PERMISSION_GRANTED) {
            //hacer lo que se ocupa
            if(mTipoUbicacion == 0)
                loc = mLm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            else if(mTipoUbicacion == 1)
                loc = mLm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } else {
            //Si no, pedimos permiso
            permisosUbicacion();
            int state2 = ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCheckAC2 = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            //Si lo tenemos, habilitamos el boton de ubicacion del usuario
            if (state2 == PackageManager.PERMISSION_GRANTED && permissionCheckAC2 == PackageManager.PERMISSION_GRANTED) {
                //hacer lo que se ocupa
                if(mTipoUbicacion == 0)
                    loc = mLm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                else if(mTipoUbicacion == 1)
                    loc = mLm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            }
        }
        return loc;
    }


}
