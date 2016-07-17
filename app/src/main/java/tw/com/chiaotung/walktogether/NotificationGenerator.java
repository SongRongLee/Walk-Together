package tw.com.chiaotung.walktogether;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by User on 2016/3/30.
 */
public class NotificationGenerator
{
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
    public static int messageNum=0;
    public static int messageFriendNum=0;
    public static String messageFriendList="";
    private Context context;
    private Notification mNotification;
    private NotificationManager mManager;
    private Notification mNotification_friend;
    private NotificationManager mManager_friend;
    private ArrayList<Message> messageList=new ArrayList<>();
    private String[] newfnameList;
    private String[] newfidList;
    private String newfname;
    private String newfid;
    private String newoname;
    private String newoid;
    LocalStoreController storeController;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationGenerator.messageNum=0;
            NotificationGenerator.messageFriendNum=0;
            NotificationGenerator.messageFriendList="";
            context.getApplicationContext().unregisterReceiver(this);
        }
    };

    public NotificationGenerator(Context context)
    {
        this.context=context;
        storeController = new LocalStoreController(context);
        initNotifiManager();
        initNotifiManager_friend();
    }

    public void generateNotification(){
        getMessageInfo();
        updateEveryoneInfo();
    }

    private void initNotifiManager() {
        mManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    }

    private void showNotification(int amount) {
        Intent pintent= new Intent();
        pintent.setClass(context, UserStatus.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, pintent, 0);
        Intent dintent = new Intent(NOTIFICATION_DELETED_ACTION);
        PendingIntent dpendintIntent = PendingIntent.getBroadcast(context, 0, dintent, 0);
        context.getApplicationContext().registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));
        String m;
        messageNum+=amount;
        if(messageNum == 1)
            m = "You have a new message";
        else
            m = "You have some new messages";

        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(false);
        builder.setTicker("Walk Together");
        builder.setContentTitle("Walk Together");
        builder.setContentText(m);
        builder.setDeleteIntent(dpendintIntent);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.build();
        mNotification = builder.build();
        mNotification.defaults=Notification.DEFAULT_ALL;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mManager.notify(0, mNotification);

    }

    private void initNotifiManager_friend() {
        mManager_friend = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    }

    private void showNotification_friend(String[] newfnamList) {
        Intent intent= new Intent();
        intent.setClass(context, UserStatus.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 0);
        String m = new String();
        for(int i=0; i<newfnamList.length; i++)
        {
            if(messageFriendNum>0)
                messageFriendList=messageFriendList+", "+newfnamList[i];
            else if(i==0)
                messageFriendList = newfnamList[i];
            else{
                messageFriendList=messageFriendList+", "+newfnamList[i];
            }
        }
        messageFriendNum+=newfnamList.length;
        m = messageFriendList + " add you as new friend";
        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(false);
        builder.setTicker("Walk Together");
        builder.setContentTitle("Walk Together");
        builder.setContentText(m);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.build();
        mNotification = builder.build();
        mNotification.defaults=Notification.DEFAULT_ALL;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mManager.notify(1, mNotification);

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
                    for(int i=0;i<content.message_list.length;i++){
                        messageList.add(content.message_list[i]);
                    }
                    int amount = storeController.getMessageAmount();
                    if (amount < messageList.size()) {
                        getMessageFinished();
                        storeController.storeMessageAmount(messageList.size());
                        showNotification(messageList.size() - amount);
                    }
                } else
                    Log.e("TAG", "getMessage failed" + "\n");
            }
        });
    }
    private void getMessageFinished() {
        TabOne.listAdapter = new UserAdapter(TabOne.activity, messageList);
        TabOne.listView.setAdapter(TabOne.listAdapter);
        //((BaseAdapter)TabOne.listView.getAdapter()).notifyDataSetChanged();
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
        if( (LocalStoreController.userLocalStore.getString("fid_list", "").equals("null") || LocalStoreController.userLocalStore.getString("fname_list", "").equals("null"))
                && ( !user.fid_list.equals("null") && !user.fname_list.equals(""))) {

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
            TabTwo.updateInfo();
            showNotification_friend(newfnameList);
        }
    }
}
