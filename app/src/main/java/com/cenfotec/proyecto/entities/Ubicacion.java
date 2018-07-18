package com.cenfotec.proyecto.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Ubicacion implements Parcelable{

    public double lat;
    public double lon;

    public Ubicacion(double lat, double lon){
        this.lat = lat;
        this.lon = lon;

    }
    public Ubicacion(){}

    protected Ubicacion(Parcel in) {
        lat = in.readDouble();
        lon = in.readDouble();
    }

    public static final Creator<Ubicacion> CREATOR = new Creator<Ubicacion>() {
        @Override
        public Ubicacion createFromParcel(Parcel in) {
            return new Ubicacion(in);
        }

        @Override
        public Ubicacion[] newArray(int size) {
            return new Ubicacion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }
}
