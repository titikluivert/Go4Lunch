package com.ngtiofack.go4lunch.api;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.ngtiofack.go4lunch.utils.mainUtils.RESTAURANTSELECTED;

public class RestaurantHelper {

    public static DatabaseReference getRestaurantCollection() {
        return FirebaseDatabase.getInstance().getReference(RESTAURANTSELECTED);
    }
}
