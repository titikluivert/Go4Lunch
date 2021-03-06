package com.ngtiofack.go4lunch.services;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.controler.activities.MainActivity;
import com.ngtiofack.go4lunch.model.RestaurantSelected;
import com.ngtiofack.go4lunch.model.YourLunch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ngtiofack.go4lunch.utils.mainUtils.RESTAURANT_IS_NOT_SELECTED;
import static com.ngtiofack.go4lunch.utils.UserIDUtils.getUserId;
import static com.ngtiofack.go4lunch.utils.RestaurantsUtils.getYourLunch;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static String TAG = "FirebaseMessagingService";
    private List<String> nameOfColleagueForLunch = new ArrayList<>();
    private YourLunch mYourLunch;
    String message;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            message = remoteMessage.getNotification().getBody();
            // 8 - Show notification after received message
            this.firebaseUserAndRestaurant(this);
        }
    }

    private void sendVisualNotification() {

        StringBuilder colleaguesStr = new StringBuilder();
        for (int i = 0; i < nameOfColleagueForLunch.size(); i++) {
            colleaguesStr.append(nameOfColleagueForLunch.get(i)).append(",");
        }

        if (colleaguesStr.length() > 0) {
            colleaguesStr = new StringBuilder(colleaguesStr.substring(0, colleaguesStr.length() - 1));
        } else {
            colleaguesStr = new StringBuilder("no one");
        }
        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Restaurant for Lunch today");
        inboxStyle.setSummaryText("Lunch Reminder");
        inboxStyle.addLine("Restaurant Name : " + mYourLunch.getName());
        inboxStyle.addLine("Address : " + mYourLunch.getVicinity());
        inboxStyle.addLine("Colleagues which come: " + colleaguesStr);

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, TAG)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Info for Lunch")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(TAG, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        int NOTIFICATION_ID = 7;
        String NOTIFICATION_TAG = "FIREBASE_GO4LUNCH";
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

    private void firebaseUserAndRestaurant(final Context context) {
        mYourLunch = getYourLunch(this);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("restaurantSelected");
        //You must remember to remove the listener when you finish using it, also to keep track of changes you can use the ChildChange
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keyALL = dataSnapshot.getChildren();
                if (!mYourLunch.getName().equals(RESTAURANT_IS_NOT_SELECTED)) {
                    for (DataSnapshot keyRestaurantName : keyALL) {
                        Iterable<DataSnapshot> keysFinal = keyRestaurantName.getChildren();
                        if (Objects.equals(keyRestaurantName.getKey(), mYourLunch.getName())) {
                            for (DataSnapshot key0 : keysFinal) {
                                if (key0.getKey() != null) {
                                    if (!key0.getKey().equals(getUserId(context))) {
                                        RestaurantSelected restaurantSelected = key0.getValue(RestaurantSelected.class);
                                        assert restaurantSelected != null;
                                        nameOfColleagueForLunch.add(restaurantSelected.getUserName());
                                    }
                                }
                            }
                            sendVisualNotification();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}






