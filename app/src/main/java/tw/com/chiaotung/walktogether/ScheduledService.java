package tw.com.chiaotung.walktogether;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by User on 2016/3/30.
 */
public class ScheduledService extends Service
{

    private Timer timer = new Timer();
    private Notification mNotification;
    private NotificationManager mManager;
    private Notification mNotification_friend;
    private NotificationManager mManager_friend;
    private Message[] messageList;
    private String[] newfnameList;
    private String[] newfidList;
    private String newfname;
    private String newfid;
    private String newoname;
    private String newoid;
    private Handler handler = new Handler();
    private int count;
    LocalStoreController storeController;
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        storeController = new LocalStoreController(this);
        count = 0;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                if(count > 1) {
                    handler.post(new Runnable() {
                        public void run() {
                            getMessageInfo();
                            updateEveryoneInfo();
                        }
                    });
                }
            }
        }, 0, 1*15*1000);//15 sec
        initNotifiManager();
        initNotifiManager_friend();
    }


    private void initNotifiManager() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void showNotification(int amount) {
        Intent intent= new Intent();
        intent.setClass(this, UserStatus.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        String m = new String();
        if(amount == 1)
            m = "You have a new message";
        else
            m = "You have some new messages";
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(false);
        builder.setTicker("New Message");
        builder.setContentTitle("New Message");
        builder.setContentText(m);
        builder.setSmallIcon(R.drawable.bamboo);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.build();
        mNotification = builder.getNotification();
        mNotification.defaults=Notification.DEFAULT_ALL;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mManager.notify(0, mNotification);

    }

    private void initNotifiManager_friend() {
        mManager_friend = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void showNotification_friend(String[] newfnamList) {
        Intent intent= new Intent();
        intent.setClass(this, UserStatus.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        String m = new String();
        for(int i=0; i<newfnamList.length; i++)
        {
            if(newfnamList.length>1)
                    m = m + newfnamList[i] + ", ";
            else
                m = newfnamList[i];
        }
        m = m + " add you as new friend";
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(false);
        builder.setTicker("New Message");
        builder.setContentTitle("New Message");
        builder.setContentText(m);
        builder.setSmallIcon(R.drawable.bamboo);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.build();
        mNotification = builder.getNotification();
        mNotification.defaults=Notification.DEFAULT_ALL;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mManager.notify(0, mNotification);

    }

    private void  getMessageInfo() {
        Log.d("TAG", "Test getMessageInfo" + "\n");
        int mid = storeController.userLocalStore.getInt("mid", 1);
        int unixTime = (int) (System.currentTimeMillis() / 1000L);
        Log.d("TAG", "Time " + unixTime + "\n");
        ServerRequest request = new ServerRequest(TabOne.activity);
        request.downMessage(mid, unixTime, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    messageList = new Message[content.message_list.length];
                    messageList = content.message_list;
                    int amount = storeController.getMessageAmount();
                    if (amount < messageList.length) {
                        getMessageFinished();
                        storeController.storeMessageAmount(messageList.length);
                        showNotification(messageList.length - amount);
                    }
                } else
                    Log.e("TAG", "getMessage failed" + "\n");
            }
        });
    }
    private void getMessageFinished() {
        TabOne.listAdapter = new UserAdapter(TabOne.activity, TabOne.userImages, messageList);
        TabOne.listView.setAdapter(TabOne.listAdapter);
        //listAdapter.notifyDataSetChanged();
    }


    private void updateEveryoneInfo() {
        ServerRequest request = new ServerRequest(TabOne.activity);

        request.getMid(new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    getFriendsInfo(content.user);
                } else
                    Log.e("TAG", "getMid failed" + "\n");
            }
        });

    }

    private void getFriendsInfo(User user) {
        Log.d("TAG", "Test getFriendInfo" + "\n");
        String[] current_fid_list,temp_fid_list,current_oid_list,new_oid_list;
        String[] current_fname_list,temp_fname_list,current_oname_list,new_oname,list;
        current_fid_list = LocalStoreController.userLocalStore.getString("fid_list", "").split(",");
        current_fname_list = LocalStoreController.userLocalStore.getString("fname_list", "").split(",");
        temp_fid_list = user.fid_list.split(",");
        temp_fname_list = user.fname_list.split(",");

        if( (LocalStoreController.userLocalStore.getString("fid_list", "").equals("null") || LocalStoreController.userLocalStore.getString("fid_list", "").equals("null"))
                && ( !user.fid_list.equals("null") && !user.fid_list.equals(""))) {
            newfid = new String();
            newfname = new String();
            newoid = new String();
            newoname = new String();
            newfnameList = new String[temp_fid_list.length];
            newfidList = new String[temp_fid_list.length];
            newfnameList = temp_fname_list;
            newfidList =  temp_fid_list;
            /*
            if(!LocalStoreController.userLocalStore.getString("oid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("oid_list", "").equals("")) {
                current_oid_list = LocalStoreController.userLocalStore.getString("oid_list", "").split(",");
                current_oname_list = LocalStoreController.userLocalStore.getString("oname_list", "").split(",");
                for (int i = 0; i < current_oid_list.length; i++) {
                    for (int j = 0; j < newfidList.length; j++) {
                        if (current_oid_list[i] != newfidList[j]) {
                            newoid = newoid + current_oid_list[i] + ",";
                            newoname = newoname + current_oid_list[i] + ",";
                        }
                    }
                }
                for(int i=0; i<newfidList.length;i++)
                {
                    if(i != newfidList.length-1)
                    {
                        newfid = newfid + newfidList[i] + ",";
                        newfname = newfname + newfnameList[i] + ",";
                    }
                    else
                    {
                        newfid = newfid + newfidList[i];
                        newfname = newfname + newfnameList[i];
                    }
                }
                //storeController.updateEveryoneInfo(newfid,newfname,newoid,newoname);
                storeController.storeAllNameID(user);
            }
            else
            {
                for(int i=0; i<newfidList.length;i++)
                {
                    if(i != newfidList.length-1)
                    {
                        newfid = newfid + newfidList[i] + ",";
                        newfname = newfname + newfnameList[i] + ",";
                    }
                    else
                    {
                        newfid = newfid + newfidList[i];
                        newfname = newfname + newfnameList[i];
                    }
                }
                //storeController.updateFriendInfo(newfid, newfname);
                storeController.storeAllNameID(user);
            }*/
            storeController.storeAllNameID(user);
            showNotification_friend(newfnameList);

        }
        else if(current_fid_list.length<temp_fid_list.length)
        {
            newfid = new String();
            newfname = new String();
            newoid = new String();
            newoname = new String();
            int temp_length = temp_fid_list.length;
            int current_length = current_fid_list.length;
            newfnameList = new String[temp_length-current_length];
            newfidList = new String[temp_length-current_length];
            for(int i=current_length; i<temp_length; i++) {
                newfnameList[i - current_length] = temp_fname_list[i];
                newfidList[i - current_length] = temp_fid_list[i];
            }
            /*
            if(!LocalStoreController.userLocalStore.getString("oid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("oid_list", "").equals("")) {
                current_oid_list = LocalStoreController.userLocalStore.getString("oid_list", "").split(",");
                current_oname_list = LocalStoreController.userLocalStore.getString("oname_list", "").split(",");
                for (int i = 0; i < current_oid_list.length; i++) {
                    for (int j = 0; j < newfidList.length; j++) {
                        if (current_oid_list[i] != newfidList[j]) {
                            newoid = newoid + current_oid_list[i] + ",";
                            newoname = newoname + current_oid_list[i] + ",";
                        }
                    }
                }
                for(int i=0; i<newfidList.length;i++)
                {
                    if(i != newfidList.length-1)
                    {
                        newfid = newfid + newfidList[i] + ",";
                        newfname = newfname + newfnameList[i] + ",";
                    }
                    else
                    {
                        newfid = newfid + newfidList[i];
                        newfname = newfname + newfnameList[i];
                    }
                }
                //storeController.updateEveryoneInfo(newfid,newfname,newoid,newoname);
                storeController.storeAllNameID(user);
            }
            else
            {
                for(int i=0; i<newfidList.length;i++)
                {
                    if(i != newfidList.length-1)
                    {
                        newfid = newfid + newfidList[i] + ",";
                        newfname = newfname + newfnameList[i] + ",";
                    }
                    else
                    {
                        newfid = newfid + newfidList[i];
                        newfname = newfname + newfnameList[i];
                    }
                }
                //storeController.updateFriendInfo(newfid, newfname);
                storeController.storeAllNameID(user);
            }*/
            storeController.storeAllNameID(user);
            showNotification_friend(newfnameList);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}
