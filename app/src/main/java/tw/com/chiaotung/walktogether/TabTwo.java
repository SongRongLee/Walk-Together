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
    LocalStoreController storeController;
    private int[] userImages = {R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user};
    private String[] fid_list;
    private String[]  fname_list;
    private int[] fsteps_list;
    private int i;                      //for server request
    private int j;                      //for fsteps_list
    private ListView listView;
    private FriendAdapter listAdapter;
    private TextView text;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        storeController=new LocalStoreController(getActivity());
        View rootview = inflater.inflate(R.layout.fragment_tab_two, container, false);
        String name = LocalStoreController.userLocalStore.getString("name", "");
        TextView username = (TextView)rootview.findViewById(R.id.text_user_name);
        username.setText(name);

        fid_list = LocalStoreController.userLocalStore.getString("fid_list", "").split(",");
        fname_list = LocalStoreController.userLocalStore.getString("fname_list", "").split(",");
        fsteps_list = new int[fid_list.length];
        updateInfo();
/*
        for(i=0; i < fid_list.length; i++) {
            int unixTime = (int) (System.currentTimeMillis());
            int mid = Integer.valueOf(fid_list[i]);
            ServerRequest request = new ServerRequest(getActivity());
            j=0;
                    request.downStep(mid, unixTime, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                fsteps_list[j] = content.step;
                                j++;
                                if(j == fid_list.length)
                                    getStepsFinished();
                            }
                }
            });
        }
*/
        View TestView = LayoutInflater.from(getActivity()).inflate(R.layout.testing_note, null);
        text = (TextView) TestView.findViewById(R.id.text);
        text.setText("Friends");

        listView = (ListView) rootview.findViewById(R.id.list_view);
        listView.setDivider(null);
        listView.addHeaderView(TestView);
        listAdapter = new FriendAdapter(getActivity(), fname_list, userImages, fsteps_list);  //for offline
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                /*
                UserStatus.tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout);
                UserStatus.viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                UserStatus.viewPager.setCurrentItem(0);
                */
                Intent intent = new Intent();
                intent.putExtra("uid",fname_list[position-1]);
                intent.setClass(getActivity(),OthersProfile.class);
                startActivity(intent);
            }
        });

/*
        MainActivity.viewPager = (ViewPager)getActivity().findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getFragmentManager(), MainActivity.tabLayout.getTabCount());
        MainActivity.viewPager.setAdapter(adapter);
        MainActivity.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(MainActivity.tabLayout));


        MainActivity.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                MainActivity.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        */
        return rootview;
    }

    private void updateInfo() {
        ServerRequest request = new ServerRequest(getActivity());

        request.getMid(new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    storeController.storeAllNameID(content.user);
                    getFriendsInfo();
                }
                else
                    Log.e("TAG", "getMid failed" + "\n");
            }
        });

    }
    private void getFriendsInfo() {
        String[] temp_fid_list;
        String[] temp_fname_list;
        temp_fid_list = LocalStoreController.userLocalStore.getString("fid_list", "").split(",");
        temp_fname_list = LocalStoreController.userLocalStore.getString("fname_list", "").split(",");
        fid_list = new String[temp_fid_list.length];
        fid_list = temp_fid_list;
        fname_list = new String[temp_fname_list.length];
        fname_list = temp_fname_list;
        fsteps_list = new int[fid_list.length];
        j = 0;
        for(i=0; i < fid_list.length; i++) {
            int unixTime = (int) (System.currentTimeMillis());
            int mid = Integer.valueOf(fid_list[i]);
            ServerRequest request = new ServerRequest(getActivity());
            request.downStep(mid, unixTime, new CallBack() {
                @Override
                public void done(CallBackContent content) {
                    if (content != null) {
                        fsteps_list[j] = content.step;
                        j++;
                        if(j == fid_list.length) {
                            getStepsFinished();
                        }
                    }
                    else
                        Log.e("TAG", "downStep failed" + "\n");
                }
            });
        }
    }
    private void getStepsFinished() {
        listAdapter = new FriendAdapter(getActivity(), fname_list, userImages, fsteps_list);
        listView.setAdapter(listAdapter);
        //listAdapter.notifyDataSetChanged();
    }
}