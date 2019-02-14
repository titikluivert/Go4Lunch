package com.ngtiofack.go4lunch.utils;


import com.ngtiofack.go4lunch.model.RestaurantsModel;
import com.ngtiofack.go4lunch.utils.services.RetrofitMapsServices;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by NG-TIOFACK on 10/31/2018.
 */
public class RestaurantsServiceStreams {

    public static Observable<RestaurantsModel> streamFetchRestaurantsItems(String type, String location, int radius) {
        RetrofitMapsServices mapsServices = RetrofitMapsServices.retrofit.create(RetrofitMapsServices.class);
        return mapsServices.getNearbyPlaces(type, location, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
