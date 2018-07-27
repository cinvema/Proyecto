package com.cenfotec.proyecto.logic;

import java.util.Calendar;

public class Variables {

    // Duración en milisegundos que se mostrará el splash
    public static final int DURACION_SPLASH = 1000; // 3 segundos 3000

    public static boolean usuarioLogueado = false;

    public static String nombre;

    public static String correo;

    public static String tel;

    public static String cedula;

    public static final String CARACTERES_PERMITIDOS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    ////fecha

    public static final String CERO = "0";
    public static final String BARRA = "/";

    //Calendario para obtener fecha & hora
    public static final Calendar C = Calendar.getInstance();

    //Variables para obtener la fecha
    public static final int MES = C.get(Calendar.MONTH);
    public static final int DIA = C.get(Calendar.DAY_OF_MONTH);
    public static final int ANIO = C.get(Calendar.YEAR);

    public static final int PERM_CODE = 1000;
    public static final int REQUEST_TAKE_PHOTO = 101;
}