package com.cenfotec.proyecto.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.cenfotec.proyecto.R;
import com.cenfotec.proyecto.entities.Ubicacion;
import com.cenfotec.proyecto.logic.Variables;
import com.cenfotec.proyecto.ui.fragments.AveriasFragment;
import com.cenfotec.proyecto.ui.fragments.MapaFragment;
import com.cenfotec.proyecto.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements  AveriasFragment.GestorBotones{

   @BindView(R.id.viewpager)
   ViewPager vp;

   PagerAdapter pa;

   @BindView(R.id.strip)
   PagerTabStrip pts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!Variables.usuarioLogueado){
            //verificar variable
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        }

        //inicializar
        ButterKnife.bind(this);
        pa = new Adaptador(getSupportFragmentManager());
        vp.setAdapter(pa);
        vp.setOffscreenPageLimit(2);

    }

    @Override
    public void accionBotonAgregar() {
        Utils utils = new Utils();
        String id = utils.getRandomString(5);
        Ubicacion ubicacion = new Ubicacion();
        //Debe hacer el intent del activity de crear averia
        Intent intent = new Intent(this, AddAveriaActivity.class);
        //pasamos este putExtra para saber si es una averia creada desde la lista
        //indicamos un 0 desde la lista y un 1 desde el mapa
        intent.putExtra("NuevaAveria", 0);
        intent.putExtra("Id", id);
        intent.putExtra("Ubicacion", ubicacion);//se envia la ubicacion en null
        startActivity(intent);
    }

    private class Adaptador extends FragmentPagerAdapter {

        AveriasFragment averiasFragment;
        MapaFragment mapaFragment;

        public Adaptador(FragmentManager fm){
            super(fm);

            averiasFragment = new AveriasFragment();
            mapaFragment = new MapaFragment();

        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return averiasFragment;
            else
                return mapaFragment;

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return "Averias";
            else
                return "Mapa";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(!Variables.usuarioLogueado){
//            //verificar variable
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//
//        }

    }
}
