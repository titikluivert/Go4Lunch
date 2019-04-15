package com.ngtiofack.go4lunch.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.evernote.android.job.Job;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.model.RestaurantSelected;
import com.ngtiofack.go4lunch.model.YourLunch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ngtiofack.go4lunch.utils.Utils.RESTAURANTISNOTSELECTED;
import static com.ngtiofack.go4lunch.utils.Utils.getUserId;
import static com.ngtiofack.go4lunch.utils.Utils.getYourLunch;


/**
 * Created by NG-TIOFACK on 11/18/2018.
 */
public class SyncJob extends Job {

    public static final String TAG = "notificationTag";
    private List<String> nameOfColleagueForLunch = new ArrayList<>();
    private YourLunch mYourLunch;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    @NonNull
    protected Result onRunJob(@NonNull Params params) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(TAG, "Job Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Job notification");
            Objects.requireNonNull(getContext().getSystemService(NotificationManager.class)).createNotificationChannel(channel);
        }

        /**
         * Il rappellera à l'utilisateur le nom du restaurant qu'il a choisi, l'adresse, ainsi que la liste des collègues qui viendront avec lui.
         */
        mYourLunch = getYourLunch(getContext());
        firebaseUserAndRestaurant();

        Notification notification = new NotificationCompat.Builder(getContext(), TAG)
                .setContentTitle("New notifications")
                .setContentText("Tester un peu")
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("Name : " + mYourLunch.getName())
                        .addLine("Address : " + mYourLunch.getVicinity())
                        .addLine(nameOfColleagueForLunch.get(0))
                        .addLine("This is line 4")
                        .addLine("This is line 5")
                        .addLine("This is line 6")
                        .addLine("This is line 7")
                        .setBigContentTitle("Restaurant for Lunch today")
                        .setSummaryText("Info for Lunch"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setColor(getContext().getColor(R.color.colorGoogle))
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(getContext()).notify(1, notification);
        return Result.SUCCESS;
    }
    private void firebaseUserAndRestaurant() {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("restaurantSelected");
        //You must remember to remove the listener when you finish using it, also to keep track of changes you can use the ChildChange
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keyALL = dataSnapshot.getChildren();
                if (!mYourLunch.getName().equals(RESTAURANTISNOTSELECTED)) {
                    for (DataSnapshot keyRestaurantName : keyALL) {
                        Iterable<DataSnapshot> keysFinal = keyRestaurantName.getChildren();
                        if (Objects.equals(keyRestaurantName.getKey(), mYourLunch.getName())) {
                            for (DataSnapshot key0 : keysFinal) {
                                if (!key0.getKey().equals(getUserId(getContext()))) {
                                    RestaurantSelected restaurantSelected = key0.getValue(RestaurantSelected.class);
                                    assert restaurantSelected != null;
                                    nameOfColleagueForLunch.add(restaurantSelected.getUserName());
                                }
                            }
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
