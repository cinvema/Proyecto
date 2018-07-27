package com.cenfotec.proyecto.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.entities.Usuario;
import com.cenfotec.proyecto.helpers.PreferencesManager;
import com.cenfotec.proyecto.logic.GestorUsuarios;
import com.cenfotec.proyecto.logic.Variables;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.user_input)
    EditText usuario;

    @BindView(R.id.password_input)
    EditText password;

    @BindView(R.id.remember)
    CheckBox remember;

    @BindView(R.id.login)
    Button loginButton;

    @BindView(R.id.register)
    Button registerButton;

    GestorUsuarios gestorUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        String usuarioStr = PreferencesManager.getUsernameFromPreferences(this);
        usuario.setText(usuarioStr);

        //recuperar las preferencias del usuario
        if(PreferencesManager.getRememberFromPreferences(this)) {
            remember.setChecked(true);
            String passwordStr = PreferencesManager.getPasswordFromPreferences(this);
            password.setText(passwordStr);
        }

    }

    @OnClick(R.id.login)
    public void loguear(){

        try {

            PreferencesManager.savePreferences(LoginActivity.this,
                    usuario.getText().toString(),
                    password.getText().toString(),
                    remember.isChecked());

            if(gestorUsuarios == null)
                gestorUsuarios = new GestorUsuarios(this);

            //Obtenemos el nombre de usuario ingresado en el campo de texto
            String usuarioIngresado = usuario.getText().toString().trim();

            //lista de usuarios con el mismo nombre de usuario
            List<Usuario> usuarios = gestorUsuarios.getUsuariosFiltro(usuarioIngresado);

            //Si no se encontro ningun usuario, es porque no existe
            if(usuarios.size() == 0){
                Toast.makeText(LoginActivity.this, "El usuario no existe!", Toast.LENGTH_SHORT).show();
                return;

            }else { //si si existe el usuario


                //Obtenemos la referencia al usuario
                Usuario user = usuarios.get(0);
                Variables.nombre = user.nombre.toString();
                Variables.cedula = user.cedula.toString();
                Variables.correo = user.correo.toString();
                Variables.tel = user.tel.toString();

                String passwordIngresado = password.getText().toString();

                //Si los passwords son diferentes, mostramos un error
                if (!user.contrasena.equals(passwordIngresado)) {
                    Toast.makeText(LoginActivity.this, "Password incorrecto!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //ENTRAR A LA CUENTA
                Variables.usuarioLogueado = true;
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        catch(Exception e){
            Log.d("Error", "Error");
        }


    }

    @OnClick(R.id.register)
    public void registrar(){
        Intent intent = new Intent(LoginActivity.this, AddUserActivity.class);
        startActivity(intent);

    }




}
