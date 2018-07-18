package com.cenfotec.proyecto.service;
import com.cenfotec.proyecto.entities.Averia;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServicioAveria {

    String header = "x-api-key: rabArf10E86thWRQ5u4MH3pFXVpiQiXv8jg1c4hO";

    @Headers({header})
    @GET("averias")
    Call<List<Averia>> obtenerTodasLasAverias();

    @Headers({header})
    @GET("averias/{id}")
    Call<Averia> obtenerDetallesDeLaAveria(@Path("id") String id);

    @Headers({header})
    @POST("averias")
    Call<Averia> crearNuevaAveria(@Body Averia nueva);

    @Headers({header})
    @POST("averias/{id}")
    Call<Averia> editarAveria(@Path("id") String id, @Body Averia nueva);

    @Headers({header})
    @DELETE("averias/{id}")
    Call<Averia> eliminarAveria(@Path("id") String id);

}
