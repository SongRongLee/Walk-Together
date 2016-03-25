package tw.com.chiaotung.walktogether;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class OthersProfile extends AppCompatActivity {

    private int [] userImages={R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user,R.drawable.default_user};
    private String [] userNameList={"Let Us C","c++","JAVA","Jsp","Microsoft .Net","Android","PHP","Jquery","JavaScript"};
    private ListView listView;
    private UserAdapter listAdapter;
    //   private ScrollView scrollView;
    private LinearLayout Userdata;
    private LinearLayout Addnote;
    private TextView text;
    private TextView username;
    public static View ConnectionView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        showpage();
    }

    public void showpage()
    {
        //get view block 1
        View UserdataView  = LayoutInflater.from(OthersProfile.this).inflate(R.layout.userdata, null);
        String useraccount = LocalStoreController.userLocalStore.getString("account", "");
        username = (TextView)UserdataView.findViewById(R.id.UserName);
        username.setText(useraccount);

        //get view block 2
        ConnectionView = LayoutInflater.from(OthersProfile.this).inflate(R.layout.blue_tooth_after, null);


        //get view block 3
        View  AddingnoteView = LayoutInflater.from(OthersProfile.this).inflate(R.layout.note, null);

        //get testing view  for block 3
        View TestView = LayoutInflater.from(OthersProfile.this).inflate(R.layout.testing_note, null);

        //Add a note(block 3)
        LinearLayout Addnote = (LinearLayout)AddingnoteView.findViewById(R.id.Addnote);
        View note = LayoutInflater.from(this).inflate(R.layout.addnote, null);
        text = (TextView)TestView.findViewById(R.id.text);
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
                                text.setText(editText.getText().toString());
                            }
                        })
                        .show();

            }
        });

        //listitem( block 4)
        listView = (ListView)findViewById(R.id.list_view_others);
        listView.setDivider(null);
        listView.addHeaderView(UserdataView);
        listView.addHeaderView(ConnectionView);
        listView.addHeaderView(AddingnoteView);
        listView.addHeaderView(TestView);
        listAdapter = new UserAdapter(this,userNameList,userImages);
        listView.setAdapter(listAdapter);


    }
}
