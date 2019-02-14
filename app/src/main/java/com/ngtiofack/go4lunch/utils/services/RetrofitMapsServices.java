package com.ngtiofack.go4lunch.utils.services;

import com.ngtiofack.go4lunch.model.RestaurantsModel;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by NG-TIOFACK on 2/13/2019.
 */
public interface RetrofitMapsServices {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyCXBKZ5tT07uT8XdXsUuAMsVkV-Uxs70E8")
    Call<RestaurantsModel> getNearbyPlaces(
            @Query("type") String type,
            @Query("location") String location,
            @Query("radius") int radius);

}
