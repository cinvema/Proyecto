package com.cenfotec.proyecto.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "usuarios")

public class Usuario implements Parcelable{

    @DatabaseField(generatedId = true, columnName = "id_usuario", canBeNull = false)
    public int idUsuario;

    @DatabaseField(columnName = "nombre", canBeNull = false)
    public String nombre;

    @DatabaseField(columnName = "correo", canBeNull = false)
    public String correo;

    @DatabaseField(columnName = "telefono", canBeNull = false)
    public String tel;

    @DatabaseField(columnName = "cedula", canBeNull = false)
    public String cedula;

    @DatabaseField(columnName = "usuario", canBeNull = false)
    public String usuario;

    @DatabaseField(columnName = "contrasena", canBeNull = false)
    public String contrasena;


    public Usuario() {}

    public  Usuario(int idUsuario, String nombre, String correo, String telefono,
                    String cedula, String usuario, String contrasena){

        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.tel = telefono;
        this.cedula = cedula;
        this.usuario = usuario;
        this.contrasena = contrasena;

    }

    protected Usuario(Parcel in) {
        idUsuario = in.readInt();
        nombre = in.readString();
        correo = in.readString();
        tel = in.readString();
        cedula = in.readString();
        usuario = in.readString();
        contrasena = in.readString();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUsuario);
        dest.writeString(nombre);
        dest.writeString(correo);
        dest.writeString(tel);
        dest.writeString(cedula);
        dest.writeString(usuario);
        dest.writeString(contrasena);
    }
}

