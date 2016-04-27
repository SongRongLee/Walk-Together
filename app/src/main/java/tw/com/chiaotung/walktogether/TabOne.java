package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import tw.com.chiaotung.walktogether.view.ModelObject;

public class TabOne extends Fragment {
    public static LocalStoreController storeController;
    public static int [] userImages={R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user};
    public static ListView listView;
    public static UserAdapter listAdapter;
    private User user;
    public static Message [] messageList;
    public static Activity activity;
 //   private ScrollView scrollView;
    public static TextView username;
    public static Button btn_connect;
    public static Button btn_disconnect;
    public static int connection_status;
    public static View ConnectionView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        storeController=new LocalStoreController(activity);
        View rootview =  inflater.inflate(R.layout.fragment_tab_one, container, false);
        //get view block 1
        View UserdataView  = LayoutInflater.from(getActivity()).inflate(R.layout.userdata, null);
        String name = LocalStoreController.userLocalStore.getString("name", "");
        username = (TextView)UserdataView.findViewById(R.id.UserName);
        username.setText(name);
        //get view block 2
        Intent intent_receive = getActivity().getIntent();
        connection_status = intent_receive.getIntExtra(ScanDevice.CONNECTION, 0);

        ConnectionView = LayoutInflater.from(getActivity()).inflate(R.layout.blue_tooth, null);
        btn_connect = (Button) ConnectionView.findViewById(R.id.btn_connect);
        btn_disconnect = (Button) ConnectionView.findViewById(R.id.btn_disconnect);

        if(connection_status == 0) {
            btn_connect.setText("CONNECT");
            btn_connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_scan = new Intent();
                    intent_scan.setClass(getActivity(), ScanDevice.class);
                    startActivity(intent_scan);
                }
            });
        }
        else
        {
            btn_connect.setText("num    steps");
        }

        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             ModelObject returnedUsedDevice = storeController.getUsedDevice();
             if(returnedUsedDevice!=null) {
                 final String used_address = returnedUsedDevice.getAddress();
                 UserStatus.mServiceManager.stopReadingPDRData(used_address);
             }
             UserStatus.mServiceManager.disconnect();
             connection_status = 0;
             btn_connect.setText("CONNECT");
             btn_connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     Intent intent_scan = new Intent();
                     intent_scan.setClass(getActivity(), ScanDevice.class);
                     startActivity(intent_scan);
                 }
            });

            }
        });
        //get view block 3
        View  AddingnoteView = LayoutInflater.from(getActivity()).inflate(R.layout.note, null);



       /*
        scrollView = (ScrollView)rootview.findViewById(R.id.scroll_view);
        scrollView.smoothScrollTo(0, 0);
            */


        //Add a note(block 3)
        LinearLayout Addnote = (LinearLayout)AddingnoteView.findViewById(R.id.Addnote);
        View note = LayoutInflater.from(getActivity()).inflate(R.layout.addnote, null);
        Addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View note = LayoutInflater.from(getActivity()).inflate(R.layout.addnote, null);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Leave Messages")
                        .setView(note)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) note.findViewById(R.id.edittext);
                                String message_content = editText.getText().toString();
                                int unixTime = (int) (System.currentTimeMillis() / 1000L);

                                Message message = new Message(message_content,unixTime,UserStatus.getStep);
                                ServerRequest request = new ServerRequest(getActivity());
                                request.upSelfMessage(message, new CallBack() {
                                    @Override
                                    public void done(CallBackContent content) {
                                        if (content != null) {
                                            Log.d("TAG", "UpSelfMessage successful" + "\n");
                                            int amount = storeController.getMessageAmount();
                                            storeController.storeMessageAmount(amount+1);
                                            getMessageInfo();
                                        }
                                        else
                                            Log.e("TAG", "UpSelfMessage failed" + "\n");
                                    }
                                });

                            }
                        })
                        .show();

            }
        });

        //do getMid
        messageList = new Message[0];
        updateInfo();

        //listitem( block 4)
        listView = (ListView)rootview.findViewById(R.id.list_view);
        listView.setDivider(null);
        listView.addHeaderView(UserdataView);
        listView.addHeaderView(ConnectionView);
        listView.addHeaderView(AddingnoteView);
        listAdapter = new UserAdapter(getActivity(),userImages,messageList);
        listView.setAdapter(listAdapter);

        return rootview;
    }

    public static void updateInfo() {
        ServerRequest request = new ServerRequest(activity);
        //start up notification
        if(LocalStoreController.userLocalStore.getInt("MessageAmount",-1)!=-1){
            NotificationGenerator notificationGenerator=new NotificationGenerator(activity);
            notificationGenerator.generateNotification();
        }
        request.getMid(new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    storeController.storeAllNameID(content.user);
                    username.setText(content.user.name);
                    getMessageInfo();
                } else
                    Log.e("TAG", "getMid failed" + "\n");
            }
        });

    }

    public static void  getMessageInfo() {
        int mid = LocalStoreController.userLocalStore.getInt("mid", 1);
        int unixTime = (int) (System.currentTimeMillis() / 1000L);
        Log.d("TAG", "Time " + unixTime + "\n");
        ServerRequest request = new ServerRequest(activity);
        request.downMessage(mid, unixTime, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    messageList = new Message[content.message_list.length];
                    messageList = content.message_list;
                    storeController.storeMessageAmount(messageList.length);
                    getMessageFinished();
                } else
                    Log.e("TAG", "getMessage failed" + "\n");
            }
        });
    }

    public static void getMessageFinished() {
        listAdapter = new UserAdapter(activity, userImages, messageList);
        listView.setAdapter(listAdapter);
        //listAdapter.notifyDataSetChanged();
    }


}


