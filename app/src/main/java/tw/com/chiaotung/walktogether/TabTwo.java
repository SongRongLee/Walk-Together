package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;


public class TabTwo extends Fragment {
    public static LocalStoreController storeController;
    public static int[] userImages = {R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user};
    public static String[] fid_list;
    public static String[]  fname_list;
    public static int[] fsteps_list;
    public static int[] temp_fsteps_list;
    public static int[] fsteps_mid_list;
    public static int i;                      //for server request
    public static int j;                      //for fsteps_list
    public static ListView listView;
    public static FriendAdapter listAdapter;
    public static Activity activity;
    private TextView text_friend;
    public static TextView username;
    public static TextView userstep;
    public static int text_friend_pos;
    public static int text_other_pos;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        storeController=new LocalStoreController(getActivity());
        View rootview = inflater.inflate(R.layout.fragment_tab_two, container, false);
        String name = LocalStoreController.userLocalStore.getString("name", "");
        username = (TextView)rootview.findViewById(R.id.text_user_name);
        username.setText(name);

        userstep = (TextView)rootview.findViewById(R.id.text_user_steps);
        String stepinfo = String.valueOf(UserStatus.getStep) + " steps";
        userstep.setText(stepinfo);

/*
        String[] temp_fid_list,temp_oid_list;
        String[] temp_fname_list,temp_oname_list;
        temp_fid_list = LocalStoreController.userLocalStore.getString("fid_list", "").split(",");
        temp_fname_list = LocalStoreController.userLocalStore.getString("fname_list", "").split(",");
        if(LocalStoreController.userLocalStore.getString("fid_list", "") ==  "") {
            temp_oid_list = LocalStoreController.userLocalStore.getString("oid_list", "").split(",");
            temp_oname_list = LocalStoreController.userLocalStore.getString("oname_list", "").split(",");
            fid_list = new String[temp_fid_list.length+temp_oid_list.length];
            fname_list = new String[temp_fname_list.length+temp_fname_list.length];
            for(int i=0; i<temp_fid_list.length+temp_oid_list.length; i++)
            {
                if(i<temp_fid_list.length) {
                    fid_list[i] = temp_fid_list[i];
                    fname_list[i] = temp_fname_list[i];
                }
                else
                {
                    fid_list[i] = temp_oid_list[i-temp_fid_list.length];
                    fname_list[i] = temp_oname_list[i-temp_fname_list.length];
                }
                Log.e("TAG", "Test " +fid_list[i]  + "\n");
            }
        }
        else
        {
            fid_list = temp_fid_list;
            fname_list = temp_fname_list;
        }
        fsteps_list = new int[fid_list.length];
        fsteps_mid_list = new int[fid_list.length];
        temp_fsteps_list = new int[fid_list.length];*/
        //do getMid
        fname_list = new String[0];
        fsteps_list = new int[0];
        updateInfo();
/*
        View TextFriendView = LayoutInflater.from(getActivity()).inflate(R.layout.testing_note, null);
        text_friend = (TextView) TextFriendView.findViewById(R.id.text);
        text_friend.setText(" Others");
*/
        listView = (ListView) rootview.findViewById(R.id.friend_list_view);
        listView.setDivider(null);
        //listView.addHeaderView(TextFriendView);
        listAdapter = new FriendAdapter(getActivity(), fname_list, userImages, fsteps_list,text_friend_pos,text_other_pos);  //for offline
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                position -= listView.getHeaderViewsCount();
                Intent intent = new Intent();
                intent.setClass(getActivity(), OthersProfile.class);
                intent.putExtra("uid", fid_list[position]);
                intent.putExtra("uname", fname_list[position]);
               // Log.e("TAG", "uid " + fid_list[position] + "\n");
               // Log.e("TAG", "uname " + fname_list[position] + "\n");
                startActivity(intent);
            }
        });

        return rootview;
    }

    public static void updateInfo() {
        ServerRequest request = new ServerRequest(activity);

        request.getMid(new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    storeController.storeAllNameID(content.user);
                    username.setText(content.user.name);
                    getEveryoneInfo();
                }
                else
                    Log.e("TAG", "getMid failed" + "\n");
            }
        });

    }
    public static void getEveryoneInfo() {
        String[] temp_fid_list,temp_oid_list;
        String[] temp_fname_list,temp_oname_list;

        if(!LocalStoreController.userLocalStore.getString("fid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("fid_list", "").equals("")) {
            temp_fid_list = LocalStoreController.userLocalStore.getString("fid_list", "").split(",");
            temp_fname_list = LocalStoreController.userLocalStore.getString("fname_list", "").split(",");
            if(!LocalStoreController.userLocalStore.getString("oid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("oid_list", "").equals("")) {
                temp_oid_list = LocalStoreController.userLocalStore.getString("oid_list", "").split(",");
                temp_oname_list = LocalStoreController.userLocalStore.getString("oname_list", "").split(",");
                int length = temp_fid_list.length+temp_oid_list.length;
                fid_list = new String[length+2];
                fname_list = new String[length+2];
                text_friend_pos = 0;
                for(int i=1,j=0; i<length+2;i++)
                {
                    if(i<=temp_fid_list.length) {
                        fid_list[i] = temp_fid_list[j];
                        fname_list[i] = temp_fname_list[j];
                        j++;
                    }
                    else if(i == temp_fid_list.length+1)
                        text_other_pos = i;
                    else
                    {
                        fid_list[i] = temp_oid_list[j-temp_fid_list.length];
                        fname_list[i] = temp_oname_list[j-temp_fname_list.length];
                        j++;
                    }
                }
            }
            else {
                int length = temp_fid_list.length;
                fid_list = new String[length+1];
                fname_list = new String[length + 1];
                text_friend_pos = 0;
                text_other_pos = -1;
                for (int i = 0; i < length; i++) {
                    fid_list[i + 1] = temp_fid_list[i];
                    fname_list[i + 1] = temp_fname_list[i];
                }
            }
        }
        else if(!LocalStoreController.userLocalStore.getString("oid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("oid_list", "").equals(""))
        {
            text_friend_pos = -1;
            text_other_pos = 0;
            temp_oid_list = LocalStoreController.userLocalStore.getString("oid_list", "").split(",");
            temp_oname_list = LocalStoreController.userLocalStore.getString("oname_list", "").split(",");
            int length = temp_oid_list.length;
            fid_list = new String[length+1];
            fname_list = new String[length + 1];
            for (int i = 0; i < length; i++) {
                fid_list[i + 1] = temp_oid_list[i];
                fname_list[i + 1] = temp_oname_list[i];
            }
        }
        final int count;
        if(text_friend_pos != -1 && text_other_pos != -1)
            count = 2;
        else
            count = 1;
        final int length = fid_list.length;
        fsteps_list = new int[length];
        fsteps_mid_list = new int[length-count];
        temp_fsteps_list = new int[length-count];
        j = 0;
        for(i=0; i < length;i++) {
            if (i != text_friend_pos && i != text_other_pos) {
                int unixTime = (int) (System.currentTimeMillis() / 1000L);
                int mid = Integer.valueOf(fid_list[i]);
                ServerRequest request = new ServerRequest(activity);
                request.downStep(mid, unixTime, new CallBack() {
                    @Override
                    public void done(CallBackContent content) {
                        if (content != null) {
                            temp_fsteps_list[j] = content.step;
                            fsteps_mid_list[j] = content.user.mid;
                            j++;
                            if (j == length - count -1) {

                                for (int x = 0; x < length; x++) {
                                    for (int y = 0; y < length - count; y++) {

                                        if(x == text_friend_pos) {
                                            fsteps_list[x] = 0;
                                            break;
                                        }
                                        else if(x == text_other_pos) {
                                            fsteps_list[x] = 0;
                                            break;
                                        }
                                        else
                                        if (Integer.valueOf(fid_list[x]) == fsteps_mid_list[y]) {
                                            fsteps_list[x] = temp_fsteps_list[y];
                                            break;
                                        }
                                    }
                                }
                                getFriendsFinished();
                            }

                        } else
                            Log.e("TAG", "downStep failed" + "\n");
                    }
                });
            }
        }
    }
    public static void getFriendsFinished() {
        listAdapter = new FriendAdapter(activity, fname_list, userImages, fsteps_list,text_friend_pos,text_other_pos);
        listView.setAdapter(listAdapter);
        //listAdapter.notifyDataSetChanged();
    }
}