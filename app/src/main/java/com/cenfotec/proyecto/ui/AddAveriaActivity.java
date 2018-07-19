package com.cenfotec.proyecto.ui;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.entities.Averia;
import com.cenfotec.proyecto.entities.Ubicacion;
import com.cenfotec.proyecto.entities.Upload;
import com.cenfotec.proyecto.entities.Usuario;
import com.cenfotec.proyecto.helpers.PreferencesManager;
import com.cenfotec.proyecto.logic.RespuestaImagen;
import com.cenfotec.proyecto.logic.Variables;
import com.cenfotec.proyecto.service.GestorServicio;
import com.cenfotec.proyecto.service.ServicioAveria;
import com.cenfotec.proyecto.service.ServicioImgur;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    @BindView(R.id.ib_obtener_fecha)
    ImageButton botonFecha;

    Averia averia;
    Ubicacion mUbicacion;
    Usuario usuarioAveria;

    private Uri mUri;

    LocationManager mLm;
    int mTipoUbicacion = 2;
    private int mValorNuevaAveria = 2 ;
    private String mId;
    private File mFile;
    private String mUrlImagen="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_averia);

        ButterKnife.bind(this);
        botonAgregar.setOnClickListener(this);
        botonAgregarFoto.setOnClickListener(this);
        botonFecha.setOnClickListener(this);

        mUbicacion = new Ubicacion();
        //leer de que tipo es la averia para saber si se carga la ubicacion actual o
        // la ubicacion del mapa en donde indicó el usuario
        mValorNuevaAveria = getIntent().getIntExtra("NuevaAveria",0);
        mId = getIntent().getStringExtra("Id");
        mUbicacion =  getIntent().getParcelableExtra("Ubicacion");
        //asignar el id generado a la caja de texto
        id.setText("1-Averia-" + mId);

    }

    @Override
    public void onClick(View v) {

        if (v.equals(botonAgregarFoto)) {
            verificarPermisosAlmacenamientoExterno();
        }
        if(v.equals(botonFecha))
            obtenerFecha();

        if (v.equals(botonAgregar)) {

            //armar el objeto averia segun lo que indique el usuario
            averia = new Averia();
            Ubicacion ubicacionAveria = new Ubicacion();
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
                ubicacionAveria =  obtenerLocalizacion();
            }else if(mValorNuevaAveria == 1){ //si es una averia creada desde mapa
                ubicacionAveria = mUbicacion;

            }

            //armar el objeto de la averia
            averia.id = id.getText().toString();
            averia.nombre = nombre.getText().toString();
            averia.tipo = tipo.getText().toString();
            averia.usuario = usuarioAveria;
            averia.fecha = fecha.getText().toString();
            averia.descripcion = descripcion.getText().toString();
            averia.imagen = mUrlImagen;
            averia.ubicacion = ubicacionAveria;

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

                        Intent intent = new Intent(AddAveriaActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

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
            preguntarPermisoAccesoExterno();

        }
    }

    public void preguntarPermisoAccesoExterno() {
        //Hacemos la solicitud de permiso
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                Variables.PERM_CODE);

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
        mFile = crearArchivo();

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
        startActivityForResult(takePictureIntent, Variables.REQUEST_TAKE_PHOTO);
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

    private void obtenerFecha(){

        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? Variables.CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? Variables.CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                fecha.setText(diaFormateado + Variables.BARRA + mesFormateado + Variables.BARRA + year);

            }

        },Variables.ANIO, Variables.MES, Variables.DIA);
        //Muestro el widget
        recogerFecha.show();

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
        if (requestCode == Variables.REQUEST_TAKE_PHOTO &&
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
                subirImagen();

                }catch(Exception e){
                Log.d("Error", e.getMessage());
            }
        }

    }

    private void subirImagen(){

        ServicioImgur imgurService = ServicioImgur.retrofit.create(ServicioImgur.class);
        final Call<RespuestaImagen> call =
                imgurService.postImage(
                        "Nombre",
                        "Descripcion", "", "",
                        MultipartBody.Part.createFormData(
                                "image",
                                mFile.getName(),
                                RequestBody.create(MediaType.parse("image/*"), mFile)
                        ));

        call.enqueue(new Callback<RespuestaImagen>() {
            @Override
            public void onResponse(Call<RespuestaImagen> call, Response<RespuestaImagen> response) {
                if (response == null) {
                    Toast.makeText(AddAveriaActivity.this, "No se ha podido subir la imagen!", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (response.isSuccessful()) {

                    Log.d("URL Picture", "http://imgur.com/" + response.body().data.id);
                    mUrlImagen = "http://imgur.com/" + response.body().data.id+".jpg";

                }
            }

            @Override
            public void onFailure(Call<RespuestaImagen> call, Throwable t) {
                Toast.makeText(AddAveriaActivity.this, "An unknown error has occured.", Toast.LENGTH_SHORT)
                        .show();
                t.printStackTrace();
            }
        });

    }
    /*
    *
    * Métodos para actualizar la ubicacion cada vez que se guarda
    *
    *
    * */
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
                Variables.PERM_CODE);
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
