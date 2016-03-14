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

    private int[] userImages = {R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user};
    private String[] userNameList = {"Let Us C", "c++", "JAVA", "Jsp", "Microsoft .Net", "Android", "PHP", "Jquery", "JavaScript"};
    private ListView listView;
    private UserAdapter listAdapter;
    private TextView text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_tab_two, container, false);
        View TestView = LayoutInflater.from(getActivity()).inflate(R.layout.testing_note, null);
        text = (TextView) TestView.findViewById(R.id.text);
        text.setText("Friends");

        listView = (ListView) rootview.findViewById(R.id.list_view);
        listView.setDivider(null);
        listView.addHeaderView(TestView);
        listAdapter = new UserAdapter(getActivity(), userNameList, userImages);
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
}