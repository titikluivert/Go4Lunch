package com.ngtiofack.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ngtiofack.go4lunch.model.Go4LunchUsers;

/**
 * Created by NG-TIOFACK on 2/16/2019.
*/

public class Go4LunchUserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, boolean isConnected, String restaurantSel) {
        Go4LunchUsers userToCreate = new Go4LunchUsers(uid, username, urlPicture, isConnected, restaurantSel);
        return Go4LunchUserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid) {
        return Go4LunchUserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return Go4LunchUserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateUrlPicture(String urlPicture, String uid) {
        return Go4LunchUserHelper.getUsersCollection().document(uid).update("urlPicture", urlPicture);
    }

    public static Task<Void> updateIsConnected(String uid, boolean isConnected) {
        return Go4LunchUserHelper.getUsersCollection().document(uid).update("isConnected", isConnected);
    }

    public static Task<Void> updateRestaurantSelected(String uid, String restaurantSelected) {
        return Go4LunchUserHelper.getUsersCollection().document(uid).update("restaurantSelected", restaurantSelected);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return Go4LunchUserHelper.getUsersCollection().document(uid).delete();
    }
}
