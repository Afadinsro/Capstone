package com.adino.capstone.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.adino.capstone.MainActivity;
import com.adino.capstone.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.adino.capstone.util.Constants.NOTIFICATION_ID;
import static com.adino.capstone.util.Constants.REQUEST_PENDING_INTENT;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    public MessagingService() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if incoming message has data payload
        if(remoteMessage.getData().size() > 0){

        }

        if(remoteMessage.getNotification() != null){
            // Default notification title is app name
            String title = getString(R.string.app_name);
            String message = "Message";
            try {
                title = remoteMessage.getNotification().getTitle();
                message = remoteMessage.getNotification().getBody();
            }catch (NullPointerException e){
                Log.d(TAG, "onMessageReceived: ");
            }
            // Send the notification to the user
            sendNotification(title, message);
        }
    }

    @Override
    public void onDeletedMessages() {

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_PENDING_INTENT, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_image_black_24dp)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            try {
                notificationManager.createNotificationChannel(channel);
            }catch (NullPointerException e) {
                Log.d(TAG, "sendNotification: " + e.getMessage());
            }
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

}
