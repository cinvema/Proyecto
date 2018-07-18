package com.cenfotec.proyecto.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cenfotec.proyecto.entities.Usuario;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private Dao<Usuario, Integer> mUserDao = null;
    private static int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, "ormlite.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Usuario.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Usuario.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //Metodos de ayuda para Usuario.java
    public Dao<Usuario, Integer> getUserDao() throws SQLException {
        if (mUserDao == null) {
            mUserDao = getDao(Usuario.class);
        }
        return mUserDao;
    }
    @Override
    public void close() {
        mUserDao = null;
        super.close();
    }


}
