package com.ngtiofack.go4lunch.services;


import com.ngtiofack.go4lunch.BuildConfig;
import com.ngtiofack.go4lunch.model.RestaurantsModel;
import com.ngtiofack.go4lunch.utils.mainUtils;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.ngtiofack.go4lunch.utils.mainUtils.API_GOOGLE_PATH;

/**
 * Created by NG-TIOFACK on 2/13/2019.
 */
public interface RetrofitMapsServices {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */

     Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(mainUtils.url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();



    @GET(API_GOOGLE_PATH + BuildConfig.ApiKeyGoogleID)
    Observable<RestaurantsModel> getNearbyPlaces(
            @Query("type") String type,
            @Query("location") String location,
            @Query("radius") int radius

    );

}
