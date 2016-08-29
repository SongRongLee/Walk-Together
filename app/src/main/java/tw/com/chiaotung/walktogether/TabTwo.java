package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class TabTwo extends Fragment {
    public static LocalStoreController storeController;
    public static String[] fid_list;
    public static String[]  fname_list;
    public static int[] fsteps_list;
    public static Bitmap[] userImages;
    public static Bitmap[] tmp_userImages;
    public static int[] temp_fsteps_list;
    public static int[] fsteps_mid_list;
    public static int[] usr_image_mid_list;
    public static int i;                      //for server request
    public static int j;                      //for fsteps_list
    public static int k;                      //for userImages
    public static ListView listView;
    public static FriendAdapter listAdapter;
    public static Activity activity;
    private TextView text_friend;
    public static TextView username;
    public static TextView userstep;
    public static ImageView image_circle;
    public ImageButton bt_search;
    public static int text_friend_pos;
    public static int text_other_pos;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        storeController=new LocalStoreController(getActivity());

        View rootview = inflater.inflate(R.layout.fragment_tab_two, container, false);
        //String name = LocalStoreController.userLocalStore.getString("name", "");

        //username = (TextView)rootview.findViewById(R.id.text_user_name);
        //username.setText(name);

        userstep = (TextView)rootview.findViewById(R.id.text_user_steps);
        userstep.setText(String.valueOf(UserStatus.getStep));

        image_circle=(ImageView)rootview.findViewById(R.id.image_circle);
        image_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                getActivity().startActivityForResult(pickIntent, UserStatus.REQUESTCODE_PICK);
            }
        });

        bt_search=(ImageButton)rootview.findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(activity, "search clicked!!", Toast.LENGTH_SHORT).show();
                Intent go_search=new Intent(getActivity(),SearchOthers.class);
                startActivity(go_search);
            }
        });
        //do getMid
        fname_list = new String[0];
        fsteps_list = new int[0];
        updateInfo();

        listView = (ListView) rootview.findViewById(R.id.friend_list_view);
        listView.setDivider(null);
        //listView.addHeaderView(TextFriendView);
        listAdapter = new FriendAdapter(getActivity(), fname_list, userImages, fsteps_list);  //for offline
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
                    //username.setText(content.user.name);
                    getEveryoneInfo();
                } else
                    Log.e("TAG", "getMid failed" + "\n");
            }
        });
        request.downImage(storeController.getUserID(), new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if(content!=null){
                    image_circle.setImageBitmap(content.usr_image);
                    userstep.setVisibility(View.GONE);
                }
            }
        });
    }
    public static void getEveryoneInfo() {
        int length=0;
        final String[] temp_fid_list,temp_oid_list;
        String[] temp_fname_list,temp_oname_list;
        if(!LocalStoreController.userLocalStore.getString("fid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("fname_list", "").equals("")) {

            temp_fid_list = LocalStoreController.userLocalStore.getString("fid_list", "").split(",");
            temp_fname_list = LocalStoreController.userLocalStore.getString("fname_list", "").split(",");

            length = temp_fid_list.length;
            fid_list = new String[length];
            fname_list = new String[length];
            for (int i = 0; i < length; i++) {
                fid_list[i] = temp_fid_list[i];
                fname_list[i] = temp_fname_list[i];
            }
        }
        userImages=new Bitmap[length];
        fsteps_list = new int[length];
        fsteps_mid_list = new int[length];
        temp_fsteps_list = new int[length];
        usr_image_mid_list = new int[length];
        tmp_userImages = new Bitmap[length];
        j = 0;
        k=0;
        Log.d("TAG", "length=" + Integer.toString(length));
        for(i=0; i < length;i++) {
            int unixTime = (int) (System.currentTimeMillis() / 1000L);
            final int mid = Integer.valueOf(fid_list[i]);
            ServerRequest request = new ServerRequest(activity);
            request.downStep(mid, unixTime, new CallBack() {
                @Override
                public void done(CallBackContent content) {
                    if (content != null) {
                        if(j>=temp_fsteps_list.length){
                            return;
                        }
                        temp_fsteps_list[j] = content.step;
                        fsteps_mid_list[j] = content.user.mid;

                        if (j == temp_fsteps_list.length-1) {

                            for (int x = 0; x < temp_fsteps_list.length; x++) {
                                for (int y = 0; y < temp_fsteps_list.length; y++) {
                                        if (Integer.valueOf(fid_list[x]) == fsteps_mid_list[y]) {
                                        fsteps_list[x] = temp_fsteps_list[y];
                                        break;
                                    }
                                }
                            }
                            getFriendsFinished();
                        }
                        j++;
                    } else
                        Log.e("TAG", "downStep failed" + "\n");
                }
            });
            request.downImage(mid, new CallBack() {
                @Override
                public void done(CallBackContent content) {
                    if(k>=temp_fsteps_list.length){
                        return;
                    }
                    if (content != null) {
                        tmp_userImages[k] = content.usr_image;
                        usr_image_mid_list[k] = content.user.mid;
                    }
                    else{
                        tmp_userImages[k] = null;
                        usr_image_mid_list[k] = mid;
                    }
                    if (k == temp_fsteps_list.length-1) {

                        for (int x = 0; x < temp_fsteps_list.length; x++) {
                            for (int y = 0; y < temp_fsteps_list.length; y++) {
                                if (Integer.valueOf(fid_list[x]) == usr_image_mid_list[y]) {
                                    userImages[x] = tmp_userImages[y];
                                    break;
                                }
                            }
                        }
                        getFriendsFinished();
                    }
                    k++;
                }
            });
        }
    }
    public static void getFriendsFinished() {
        listAdapter = new FriendAdapter(activity, fname_list, userImages, fsteps_list);
        listView.setAdapter(listAdapter);
        //listAdapter.notifyDataSetChanged();
    }
}