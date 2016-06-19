package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OthersProfile extends AppCompatActivity {
    public static LocalStoreController storeController;
    public static int [] userImages={R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user};
    public static String name;
    public static int mid;
    public static ListView listView;
    public static OthersUserAdapter listAdapter;
    public static Message [] messageList;
    public static int step;
    //   private ScrollView scrollView;
    private TextView username;
    private Button addFriend;
    public static View ConnectionView;
    public static Toolbar toolbar;
    private Boolean isFriend;
    private Boolean addFriend_clicked;
    private Boolean isLiked;
    private Boolean isDisliked;
    private Boolean like_clicked;
    private Boolean dislike_clicked;
    public static Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        storeController=new LocalStoreController(this);
        showpage();
    }

    public void showpage() {
        Intent intent_receive = this.getIntent();
        name = intent_receive.getStringExtra("uname");
        String temp = intent_receive.getStringExtra("uid");
        mid = Integer.valueOf(temp);

        //get Button addFriend
        addFriend = (Button) findViewById(R.id.addFriend);
        String[] fid_list = new String[0];
        if(!LocalStoreController.userLocalStore.getString("fid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("fid_list", "").equals(""))
            fid_list = LocalStoreController.userLocalStore.getString("fid_list", "").split(",");

        isFriend = false;
        addFriend_clicked = false;
        for (int i = 0; i < fid_list.length; i++) {
            if (mid == Integer.valueOf(fid_list[i])) {
                isFriend = true;
                break;
            }
        }
        if (isFriend)
            addFriend.setText("Friend");
        else {
            addFriend.setText("Add friend");
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!addFriend_clicked) {
                        addFriend.setText("Friend");
                        int from = LocalStoreController.userLocalStore.getInt("mid", 1);
                        ServerRequest request = new ServerRequest(OthersProfile.this);
                        request.addFriend(from, mid);
                        addFriend_clicked = true;
                        updateInfo();
                        Toast.makeText(OthersProfile.this, "Add friend successfully !! ", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        //get view block 1
        View UserdataView = LayoutInflater.from(OthersProfile.this).inflate(R.layout.userdata, null);
        //String useraccount = LocalStoreController.userLocalStore.getString("account", "");
        username = (TextView) UserdataView.findViewById(R.id.UserName);
        username.setText(name);

        //get view block 2
        ConnectionView = LayoutInflater.from(OthersProfile.this).inflate(R.layout.blue_tooth_others, null);
        final ImageView highfive = (ImageView) ConnectionView.findViewById(R.id.stepshighfive);
        final ImageView steps_dislike = (ImageView) ConnectionView.findViewById(R.id.stepsdislike);
        isLiked =false;
        isDisliked=false;
        String selflikelist = storeController.getSelfLikelist();
        String[] selflike_array;
        /*
        if (!selflikelist.equals("") && !selflikelist.equals("null")) {
            selflike_array = selflikelist.split(",");
            for(int i=0; i<selflike_array.length; i++) {
                if(mid == Integer.valueOf(selflike_array[i])) {
                    isLiked = true;
                    break;
                }
            }
        }
        */
        if(!isLiked) {
            highfive.setImageResource(R.drawable.ic_thumb_up_before);
            like_clicked = false;
            highfive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!like_clicked) {
                        int from = LocalStoreController.userLocalStore.getInt("mid", 1);
                        int unixTime = (int) (System.currentTimeMillis() / 1000L);
                        Message message = new Message();
                        message.from = from;
                        message.time = unixTime;
                        message.to = mid;
                        message.step = step;
                        uploadLikeStep(message);
                        Toast.makeText(OthersProfile.this, "You liked the Step !! ", Toast.LENGTH_LONG).show();
                        like_clicked = true;
                        highfive.setImageResource(R.drawable.ic_thumb_up_after);
                        String current_likelist = storeController.getSelfLikelist();
                        String new_likelist = current_likelist + String.valueOf(mid) + ",";
                        storeController.storeSelfLikelist(new_likelist);
                        getMessageInfo();
                    }
                }
            });
        }
        else
        {
            highfive.setImageResource(R.drawable.ic_thumb_up_after);
            like_clicked = true;
        }
        if(!isDisliked) {
            steps_dislike.setImageResource(R.drawable.ic_thumb_down_before);
            dislike_clicked = false;
            steps_dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!dislike_clicked) {
                        int from = LocalStoreController.userLocalStore.getInt("mid", 1);
                        int unixTime = (int) (System.currentTimeMillis() / 1000L);
                        Message message = new Message();
                        message.from = from;
                        message.time = unixTime;
                        message.to = mid;
                        message.step = step;
                        uploadDislikeStep(message);
                        Toast.makeText(OthersProfile.this, "You disliked the Step !! ", Toast.LENGTH_LONG).show();
                        dislike_clicked = true;
                        steps_dislike.setImageResource(R.drawable.ic_thumb_down_after);
                        /*String current_likelist = storeController.getSelfLikelist();
                        String new_likelist = current_likelist + String.valueOf(mid) + ",";
                        storeController.storeSelfLikelist(new_likelist);*/
                        getMessageInfo();
                    }
                }
            });
        }
        else
        {
            steps_dislike.setImageResource(R.drawable.ic_thumb_down_after);
            dislike_clicked = true;
        }
        getStepInfo();
        //get view block 3
        View  AddingnoteView = LayoutInflater.from(OthersProfile.this).inflate(R.layout.note, null);


        //Add a note(block 3)
        LinearLayout Addnote = (LinearLayout)AddingnoteView.findViewById(R.id.Addnote);
        View note = LayoutInflater.from(this).inflate(R.layout.addnote, null);
        Addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View note = LayoutInflater.from(OthersProfile.this).inflate(R.layout.addnote, null);
                new AlertDialog.Builder(OthersProfile.this)
                        .setTitle("Leave Messages")
                        .setView(note)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) note.findViewById(R.id.edittext);
                                String message_content = editText.getText().toString();
                                int unixTime = (int) (System.currentTimeMillis() / 1000L);
                                int from = LocalStoreController.userLocalStore.getInt("mid",1);
                                Message message = new Message(message_content,unixTime,step,from,mid);
                                ServerRequest request = new ServerRequest(OthersProfile.this);
                                request.upOtherMessage(message, new CallBack() {
                                    @Override
                                    public void done(CallBackContent content) {
                                        if (content != null) {
                                            getMessageInfo();
                                        } else
                                            Log.e("TAG", "UpSelfMessage failed" + "\n");
                                    }
                                });
                            }
                        })
                        .show();

            }
        });

        messageList = new Message[0];
        getMessageInfo();
        //listitem( block 4)
        listView = (ListView)findViewById(R.id.list_view_others);
        listView.setDivider(null);
        listView.addHeaderView(UserdataView);
        listView.addHeaderView(ConnectionView);
        listView.addHeaderView(AddingnoteView);
        listAdapter = new OthersUserAdapter(this, userImages, messageList,name);
        listView.setAdapter(listAdapter);


    }
    public static void updateInfo() {
        ServerRequest request = new ServerRequest(activity);

        request.getMid(new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    storeController.storeAllNameID(content.user);
                } else
                    Log.e("TAG", "getMid failed" + "\n");
            }
        });

    }


    private void getStepInfo() {
        ServerRequest request = new ServerRequest(this);
        int unixTime = (int) (System.currentTimeMillis() / 1000L);
        request.downStep(mid, unixTime, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    step = content.step;
                    Button text_step = (Button) ConnectionView.findViewById(R.id.steps);
                    String s = String.valueOf(step) + " steps";
                    text_step.setText(s);
                } else
                    Log.e("TAG", "downStep failed" + "\n");
            }
        });
    }

    public static void  getMessageInfo() {
        int unixTime = (int) (System.currentTimeMillis() / 1000L);
        Log.d("TAG", "Time " + unixTime + "\n");
        ServerRequest request = new ServerRequest(activity);
        request.downMessage(mid, unixTime, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    messageList = new Message[content.message_list.length];
                    messageList = content.message_list;
                    getMessageFinished();
                } else
                    Log.e("TAG", "getMessage failed" + "\n");
            }
        });
    }

    public static void getMessageFinished() {
        listAdapter = new OthersUserAdapter(activity, userImages, messageList,name);
        listView.setAdapter(listAdapter);
        //listAdapter.notifyDataSetChanged();
    }

    private void uploadLikeStep(Message message) {
        ServerRequest request = new ServerRequest(this);
        request.upLikeStep(message, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    Log.d("TAG", "UpSelfMessage successful" + "\n");
                } else
                    Log.e("TAG", "UpSelfMessage failed" + "\n");
            }
        });
    }
    private void uploadDislikeStep(Message message) {
        ServerRequest request = new ServerRequest(this);
        request.upDislikeStep(message, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    Log.d("TAG", "uploadDislikeStep successful" + "\n");
                } else
                    Log.e("TAG", "uploadDislikeStep failed" + "\n");
            }
        });
    }
    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }*/
}
