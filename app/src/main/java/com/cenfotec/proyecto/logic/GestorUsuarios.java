package com.cenfotec.proyecto.logic;

import android.content.Context;

import com.cenfotec.proyecto.db.DatabaseHelper;
import com.cenfotec.proyecto.entities.Usuario;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

public class GestorUsuarios {

    private Dao<Usuario, Integer> mUsuarioDao = null;
    DatabaseHelper bdHelper;

    public GestorUsuarios(Context context){
        try {
            //Inicializamos el DBHelper
            if(bdHelper == null) {
                bdHelper = new DatabaseHelper(context);
            }
            mUsuarioDao = bdHelper.getUserDao();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Usuario> getUsuarios(String usuario){
        List<Usuario> usuarios = null;

        try {
            usuarios = mUsuarioDao.queryBuilder().
                    where().eq("usuario", usuario.trim()).query();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;

    }

    public void createUsuario(Usuario usuario){

        try {
            mUsuarioDao.create(usuario);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Usuario> getUsuariosFiltro(String usuario){
        List<Usuario> usuarios = null;

        try {
            //Generamos un filtro y obtenemos la lista resultado
            Where filtro = mUsuarioDao.queryBuilder()
                    .where()
                    .eq("usuario", usuario);

             usuarios = filtro.query();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;

    }



}
