package com.cenfotec.proyecto.utils;

import com.cenfotec.proyecto.logic.Variables;
import java.util.Random;


public class Utils {

    public String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(Variables.CARACTERES_PERMITIDOS.charAt(random.nextInt(Variables.CARACTERES_PERMITIDOS.length())));
        return sb.toString();
    }






}
