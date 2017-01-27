package saeed.life.NotificationService;

import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    private SharedPreferences isLogged;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        isLogged = getApplicationContext().getSharedPreferences("Check", 0);

        if(remoteMessage.getNotification()!=null &&
                !remoteMessage.getData().get("userId").equals(isLogged.getString("userId", ""))){
            showNotification();
        }
    }

    private void showNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
