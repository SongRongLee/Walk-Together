package tw.com.chiaotung.walktogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ScanDevice extends AppCompatActivity {
    public static final String CONNECTION = "Connectioin_status";
    private Button btn_scan;
    private ListView device_list;
    private String[] list = {"Device1","Device2","Device3"};
    private ArrayAdapter<String> listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn_scan = (Button)findViewById(R.id.btn_connect);
        device_list = (ListView)findViewById(R.id.device_list);
        listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        device_list.setAdapter(listAdapter);
        device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent();
                intent.setClass(ScanDevice.this, UserStatus.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(CONNECTION, 1);
                startActivity(intent);
            }
        });
    }

}
