package com.cenfotec.proyecto.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Averia  implements Parcelable {

    public String id;
    public String nombre ;
    public String tipo ;
    public Usuario usuario;
    public String fecha;
    public String descripcion;
    public String imagen;
    public Ubicacion ubicacion;


    public Averia( String id, String nombre, String tipo, Usuario usuario, String fecha,
                   String descripcion, String imagen, Ubicacion ubicacion){

        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.usuario = usuario;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.ubicacion = ubicacion;

    }

    public Averia(){}
    protected Averia(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        tipo = in.readString();
        fecha = in.readString();
        descripcion = in.readString();
        imagen = in.readString();
    }

    public static final Creator<Averia> CREATOR = new Creator<Averia>() {
        @Override
        public Averia createFromParcel(Parcel in) {
            return new Averia(in);
        }

        @Override
        public Averia[] newArray(int size) {
            return new Averia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(tipo);
        dest.writeString(fecha);
        dest.writeString(descripcion);
        dest.writeString(imagen);
    }
}
