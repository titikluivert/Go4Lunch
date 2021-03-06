package com.ngtiofack.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ngtiofack.go4lunch.model.Go4LunchUsers;
import com.ngtiofack.go4lunch.model.YourLunch;

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

    public static Task<Void> createUser(String uid, String username, String urlPicture, boolean isConnected, YourLunch mYourLunch) {
        Go4LunchUsers userToCreate = new Go4LunchUsers(uid, username, urlPicture, isConnected,mYourLunch);
        return Go4LunchUserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid) {
        return Go4LunchUserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateIsConnected(String uid, boolean isConnected) {
        return Go4LunchUserHelper.getUsersCollection().document(uid).update("isConnected", isConnected);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return Go4LunchUserHelper.getUsersCollection().document(uid).delete();
    }
}
