package com.muzakki.ahmad.lib.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.muzakki.ahmad.lib.R;

public abstract class NotificationReceiver extends BroadcastReceiver {
    private static int mNotificationId = 0;



    private boolean hasForegroundNotification = true;
    public static final int ACCEPTED_FOREGROUND = 200;

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendOrderedBroadcast(new Intent(intent.getAction()+".FOREGROUND"),null,new FinalReceiver(),null,
                0,null,intent.getExtras());
    }

    private class FinalReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationReceiver.this.onReceive(context,getResultExtras(true),
                    getResultCode()==ACCEPTED_FOREGROUND);
        }
    }

    protected void onReceive(Context context,Bundle data,boolean hasForeground){
        if(hasForeground && hasForegroundNotification){
            createNotification(context,data);
        }else{
            createNotification(context,data);
        }
    }

    private void createNotification(Context context,Bundle data) {
        NotificationCompat.Builder mBuilder = getNotificationBuilder(context,data);

        mNotificationId++;
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    protected NotificationCompat.Builder getNotificationBuilder(Context context,Bundle data) {
        Bundle msg_data = data.getBundle("data");
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.jingle)
                .setContentTitle(getTitle(msg_data))
                .setContentText(getSubTitle(msg_data))
                .setAutoCancel(true);

        String tag = data.getString("tag");
        Intent resultIntent = new Intent(tag);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        return mBuilder;
    }

    protected abstract String getTitle(Bundle b);

    protected abstract String getSubTitle(Bundle b);

    public void setHasForegroundNotification(boolean hasForegroundNotification) {
        this.hasForegroundNotification = hasForegroundNotification;
    }
}
