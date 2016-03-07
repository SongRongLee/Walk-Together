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

public class TabOne extends Fragment {

    private int [] userImages={R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};
    private String [] userNameList={"Let Us C","c++","JAVA","Jsp","Microsoft .Net","Android","PHP","Jquery","JavaScript"};
    private ListView listView;
    private UserAdapter listAdapter;
 //   private ScrollView scrollView;
    private LinearLayout Userdata;
    private LinearLayout Addnote;
    private TextView text;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_tab_one, container, false);

        //get view block 1
        View UserdataView  = LayoutInflater.from(getActivity()).inflate(R.layout.userdata, null);

        //get view block 3
        View  AddingnoteView = LayoutInflater.from(getActivity()).inflate(R.layout.note, null);

        //get testing view  for block 3
        View TestView = LayoutInflater.from(getActivity()).inflate(R.layout.testing_note, null);

       /*
        scrollView = (ScrollView)rootview.findViewById(R.id.scroll_view);
        scrollView.smoothScrollTo(0, 0);
            */


        //Add a note(block 3)
        LinearLayout Addnote = (LinearLayout)AddingnoteView.findViewById(R.id.Addnote);
        View note = LayoutInflater.from(getActivity()).inflate(R.layout.addnote, null);
        text = (TextView)TestView.findViewById(R.id.text);
        Addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View note = LayoutInflater.from(getActivity()).inflate(R.layout.addnote, null);
                new AlertDialog.Builder(getActivity())
                        .setTitle("添加留言")
                        .setView(note)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) note.findViewById(R.id.edittext);
                                text.setText(editText.getText().toString());
                            }
                        })
                        .show();

            }
        });


        //listitem( block 4)
        listView = (ListView)rootview.findViewById(R.id.list_view);
        listView.setDivider(null);
        listView.addHeaderView(UserdataView);
        listView.addHeaderView(AddingnoteView);
        listView.addHeaderView(TestView);
        listAdapter = new UserAdapter(getActivity(),userNameList,userImages);
        listView.setAdapter(listAdapter);


        return rootview;
    }


}


