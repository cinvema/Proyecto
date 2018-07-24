package com.cenfotec.proyecto.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.db.DatabaseHelper;
import com.cenfotec.proyecto.entities.Usuario;
import com.cenfotec.proyecto.logic.GestorUsuarios;

import com.j256.ormlite.dao.Dao;

import java.util.List;
import android.support.design.widget.FloatingActionButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddUserActivity extends AppCompatActivity  implements View.OnClickListener {

    @BindView(R.id.name)
    EditText nombreTxt;

    @BindView(R.id.email)
    EditText correoTxt;

    @BindView(R.id.phone)
    EditText telefonoTxt;

    @BindView(R.id.target_id)
    EditText cedulaTxt;

    @BindView(R.id.username)
    EditText nombreUsuarioTxt;

    @BindView(R.id.password)
    EditText contrasenaTxt;

    @BindView(R.id.password_2)
    EditText contrasena2Txt;

    @BindView(R.id.btn_agregar)
    FloatingActionButton botonAgregarUsuario;

    GestorUsuarios gestorUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        ButterKnife.bind(this);

        botonAgregarUsuario.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (v.equals(botonAgregarUsuario)) {
            try {
                String nombre = nombreTxt.getText().toString().trim();
                String correo = correoTxt.getText().toString().trim();
                String telefono = telefonoTxt.getText().toString().trim();
                String cedula = cedulaTxt.getText().toString().trim();
                String usuario = nombreUsuarioTxt.getText().toString().trim();
                String contrasena = contrasenaTxt.getText().toString();
                String contrasena2 = contrasena2Txt.getText().toString();

                if (gestorUsuarios == null)
                    gestorUsuarios = new GestorUsuarios(this);

                if (!contrasena.equals(contrasena2)) {
                    //MOSTRAR ERROR
                    Toast.makeText(AddUserActivity.this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Recuperamos todos los usuarios que tengan ese mismo username
                List<Usuario> usuarios = gestorUsuarios.getUsuarios(usuario);

                if (usuarios.size() > 0) {
                    Toast.makeText(AddUserActivity.this, "El usuario ya existe.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.nombre = nombre;
                nuevoUsuario.correo = correo;
                nuevoUsuario.tel = telefono;
                nuevoUsuario.cedula = cedula;
                nuevoUsuario.usuario = usuario;
                nuevoUsuario.contrasena = contrasena;

                gestorUsuarios.createUsuario(nuevoUsuario);

                finish();
            } catch (Exception e) {
                Toast.makeText(AddUserActivity.this, "Error creando cuenta.", Toast.LENGTH_SHORT).show();
            }

        }
    }

}