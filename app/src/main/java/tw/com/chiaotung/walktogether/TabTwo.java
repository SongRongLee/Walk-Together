package tw.com.chiaotung.walktogether;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class TabTwo extends Fragment {

    private int [] userImages={R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user};
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