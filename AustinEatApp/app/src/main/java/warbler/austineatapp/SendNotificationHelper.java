package warbler.austineatapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AhPan on 12/1/17.
 */

public class SendNotificationHelper {
    //private final static String FIREBASE_URL = "https://austineat-186301.firebaseio.com/";

    public void sendNotificationToUser(String user, final String message) {
        //Firebase rootRef = new Firebase("FIREBASE_URL");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference notifications = ref.child("notificationRequests");

        Map notification = new HashMap<>();
        notification.put("username", user);
        notification.put("message", message);

        notifications.push().setValue(notification);
    }
}
