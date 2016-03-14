package tw.com.chiaotung.walktogether;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TabOne extends Fragment {

    private int [] userImages={R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user};
    private String [] userNameList={"Let Us C","c++","JAVA","Jsp","Microsoft .Net","Android","PHP","Jquery","JavaScript"};
    private ListView listView;
    private UserAdapter listAdapter;
 //   private ScrollView scrollView;
    private LinearLayout Userdata;
    private LinearLayout Addnote;
    private TextView text;
    private TextView username;
    private Button btn_connect;
    private int connection_status;
    private View ConnectionView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_tab_one, container, false);

        //get view block 1
        View UserdataView  = LayoutInflater.from(getActivity()).inflate(R.layout.userdata, null);
        String useraccount = UserStatus.prefs.getString("account", "");
        username = (TextView)UserdataView.findViewById(R.id.UserName);
        username.setText(useraccount);

        //get view block 2
        Intent intent_receive = getActivity().getIntent();
        connection_status = intent_receive.getIntExtra(ScanDevice.CONNECTION,0);

        if(connection_status == 0) {
            ConnectionView = LayoutInflater.from(getActivity()).inflate(R.layout.blue_tooth_before, null);
            btn_connect = (Button) ConnectionView.findViewById(R.id.btn_connect);
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
            ConnectionView = LayoutInflater.from(getActivity()).inflate(R.layout.blue_tooth_after, null);
        }

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
                        .setTitle("Leave Messages")
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
        listView.addHeaderView(ConnectionView);
        listView.addHeaderView(AddingnoteView);
        listView.addHeaderView(TestView);
        listAdapter = new UserAdapter(getActivity(),userNameList,userImages);
        listView.setAdapter(listAdapter);


        return rootview;
    }


}


