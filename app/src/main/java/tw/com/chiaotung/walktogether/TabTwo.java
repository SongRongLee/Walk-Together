package tw.com.chiaotung.walktogether;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class TabTwo extends Fragment {

    private int [] userImages={R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};
    private String [] userNameList={"Let Us C","c++","JAVA","Jsp","Microsoft .Net","Android","PHP","Jquery","JavaScript"};
    private ListView listView;
    private UserAdapter listAdapter;
    private TextView text;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_tab_two, container, false);

        View TestView = LayoutInflater.from(getActivity()).inflate(R.layout.testing_note, null);
        text = (TextView)TestView.findViewById(R.id.text);
        text.setText("Friends");

        listView = (ListView)rootview.findViewById(R.id.list_view);
        listView.setDivider(null);
        listView.addHeaderView(TestView);
        listAdapter = new UserAdapter(getActivity(),userNameList,userImages);
        listView.setAdapter(listAdapter);


        return rootview;
    }
}