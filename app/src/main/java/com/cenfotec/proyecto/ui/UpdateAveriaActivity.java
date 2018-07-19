package com.cenfotec.proyecto.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.entities.Averia;
import com.cenfotec.proyecto.entities.Ubicacion;
import com.cenfotec.proyecto.entities.Usuario;
import com.cenfotec.proyecto.helpers.PreferencesManager;
import com.cenfotec.proyecto.logic.RespuestaImagen;
import com.cenfotec.proyecto.logic.Variables;
import com.cenfotec.proyecto.service.GestorServicio;
import com.cenfotec.proyecto.service.ServicioAveria;
import com.cenfotec.proyecto.service.ServicioImgur;
import com.cenfotec.proyecto.ui.fragments.AveriasFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpdateAveriaActivity extends AppCompatActivity implements View.OnClickListener{


    @BindView(R.id.id_averia_up)
    EditText id;

    @BindView(R.id.nombre_averia_up)
    EditText nombre;

    @BindView(R.id.tipo_averia_up)
    EditText tipo;

    @BindView(R.id.usuario_averia_up)
    EditText usuario;

    @BindView(R.id.fecha_averia_up)
    EditText fecha;

    @BindView(R.id.descripcion_averia_up)
    EditText descripcion;

    @BindView(R.id.imagen_averia_up)
    ImageView imageView;

    @BindView(R.id.btn_agregar_foto_up)
    Button botonAgregarFoto;

    @BindView(R.id.btn_editar_averia_up)
    Button botonEditar;

    @BindView(R.id.btn_eliminar_averia_up)
    Button botonEliminar;

    @BindView(R.id.ib_obtener_fecha_up)
    ImageButton botonFecha;

    Averia averia;
    Usuario usuarioAveria;
    Ubicacion ubicacion;

    private String mUrlImagen="";
    private Uri mUri;
    private File mFile;
    private static final int PERM_CODE = 1000;
    private static final int REQUEST_TAKE_PHOTO = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_averia);

        ButterKnife.bind(this);

        averia = getIntent().getParcelableExtra("Averia");
        usuarioAveria = getIntent().getParcelableExtra("Usuario");
        ubicacion= getIntent().getParcelableExtra("Ubicacion");

        //set text para todos los campos con validacion de vacios
        if(!averia.id.equals(""))
            id.setText(averia.id.toString());
        if(!averia.nombre.equals(""))
            nombre.setText(averia.nombre.toString());
        if(!averia.tipo.equals(""))
            tipo.setText(averia.tipo.toString());
        if(usuarioAveria.nombre != null) {
            //if(!averia.usuario.nombre.equals(""))
            usuario.setText(usuarioAveria.nombre.toString());
        }
        if(!averia.fecha.equals(""))
            fecha.setText(averia.fecha.toString());
        if(!averia.descripcion.equals(""))
            descripcion.setText(averia.descripcion.toString());
        if(!averia.imagen.equals(""))
            Picasso.get().load(averia.imagen.toString().trim()).into(imageView);

        botonEditar.setOnClickListener(this);
        botonAgregarFoto.setOnClickListener(this);
        botonEliminar.setOnClickListener(this);
        botonFecha.setOnClickListener(this);
    }

    private void actualizarAveria(){
        //la ubicacion y el usuario quedan igual no se editan
        averia.ubicacion = ubicacion;
        averia.usuario = usuarioAveria;

        //los demas datos de la averia tomarlos del layout
        if(mUrlImagen != "")//si la foto se actualizó
            averia.imagen = mUrlImagen;
        averia.descripcion = descripcion.getText().toString();
        averia.fecha = fecha.getText().toString();
        averia.tipo = tipo.getText().toString();
        averia.nombre = nombre.getText().toString();

    }
    @Override
    public void onClick(View view) {

        if(view.equals(botonAgregarFoto)){
            verificarPermisos();
        }
        if(view.equals(botonFecha))
            obtenerFecha();
        if(view.equals(botonEditar)){

            //Se obtiene la referencia singleton desde el gestor.
            ServicioAveria servicio = GestorServicio.obtenerServicio();

            //actualizar los valores de la averia
            actualizarAveria();

            //Se llama al metodo definido en el servicio para editar la averia
            servicio.editarAveria(averia.id, averia).enqueue(new Callback<Averia>() {
                @Override
                public void onResponse(Call<Averia> call, Response<Averia> response) {
                    //Si es exitosa, recuperamos la lista recibida de response.body()
                    Averia resultado = response.body();

                    if(resultado !=null){
                        Toast.makeText(getApplicationContext(),
                                "Exito editando la averia",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UpdateAveriaActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<Averia> call, Throwable t) {

                    Toast.makeText(getApplicationContext(),
                            "Ha ocurrido un error editando la averia",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }//fin del boton editar

        if(view.equals(botonEliminar)){

            new AlertDialog.Builder(UpdateAveriaActivity.this)
                    .setTitle("Precaución")
                    .setMessage("Desea eliminar el registro?.")
                    .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("UpdateAveriaActivity", "Eliminando registro");
                            //obtener el objeto de la bd, eliminarlo
                            try {
                                //Eliminar el registro con el servicio
                                //Se obtiene la referencia singleton desde el gestor.
                                ServicioAveria servicio = GestorServicio.obtenerServicio();

                                //Se llama al metodo definido en el servicio para eliminar la averia
                                servicio.eliminarAveria(averia.id).enqueue(new Callback<Averia>() {
                                    @Override
                                    public void onResponse(Call<Averia> call, Response<Averia> response) {
                                        //Si es exitosa, recuperamos la lista recibida de response.body()
                                        Averia resultado = response.body();

                                        if(resultado !=null){
                                            Toast.makeText(getApplicationContext(),
                                                    "Exito eliminando la averia",
                                                    Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(UpdateAveriaActivity.this, MainActivity.class);
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

//
                            }catch (Exception e){

                                Log.d("Error",e.getMessage().toString());
                            }

                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("DelectContact", "Cancelando...");
                        }
                    })
                    .show();
        }
    }

    private void verificarPermisos() {
        //Obtenemos el estado actual de los permisos
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Si ya tenemos permisos, continuamos tomando la foto
        //Si no, pedimos permiso
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            continuarTomarFoto();
        } else {
            askForPermission();

        }
    }

    public void askForPermission(){
        //Hacemos la solicitud de permiso
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERM_CODE);

        //Obtenemos el estado actual de los permisos
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Si ya tenemos permisos, continuamos tomando la foto
        //Si no, pedimos permiso
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            continuarTomarFoto();
        }
    }

    private void continuarTomarFoto() {
        //Llamamos al metodo crearArchivo para obtener un
        //archivo en el cual guardar la foto
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
            Log.d("Error", e.getMessage());
            return null;
        }
    }

    //Al haber llamado a onStartActivityForResult, indicamos al
    //sistema que llame a este callback una vez la foto haya sido
    //capturada y almacenada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                    Toast.makeText(UpdateAveriaActivity.this, "No se ha podido subir la imagen!", Toast.LENGTH_SHORT)
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
                Toast.makeText(UpdateAveriaActivity.this, "An unknown error has occured.", Toast.LENGTH_SHORT)
                        .show();
               t.printStackTrace();
            }
        });

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


}
