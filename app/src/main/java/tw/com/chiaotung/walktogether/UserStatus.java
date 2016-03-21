package tw.com.chiaotung.walktogether;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import cc.nctu1210.api.koala3x.KoalaDevice;
import cc.nctu1210.api.koala3x.KoalaServiceManager;
import cc.nctu1210.api.koala3x.SensorEvent;
import cc.nctu1210.api.koala3x.SensorEventListener;


public class UserStatus extends AppCompatActivity implements SensorEventListener{
    private final static String TAG = UserStatus.class.getSimpleName();
    public static Toolbar toolbar;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    private TextView steps;
    public static int getStep;
    public static KoalaServiceManager mServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSelectedTab();

        mServiceManager = new KoalaServiceManager(this);
        mServiceManager.registerSensorEventListener(this, SensorEvent.TYPE_PEDOMETER);

/*
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Me"));
        tabLayout.addTab(tabLayout.newTab().setText("Everyone"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        */
    }
    public void setSelectedTab() {

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Me"));
        tabLayout.addTab(tabLayout.newTab().setText("Everyone"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

        @Override
        protected void onResume() {
            super.onResume();

        }

        @Override
        protected void onStop() {
            super.onStop();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            System.exit(0);
        }

    private int findKoalaDevice(String macAddr) {
        if (ScanDevice.mDevices.size() == 0)
            return -1;
        for (int i = 0; i < ScanDevice.mDevices.size(); i++) {
            KoalaDevice tmpDevice = ScanDevice.mDevices.get(i);
            if (macAddr.matches(tmpDevice.getDevice().getAddress()))
                return i;
        }
        return -1;
    }
        @Override
        public void onSensorChange(final SensorEvent e) {

            final int eventType = e.type;
            final double values [] = new double[3];
            final int position2 = findKoalaDevice(e.device.getAddress());
/*
            TextView t = (TextView)findViewById(R.id.test_steps);
            t.setText(String.valueOf(e.values[0]));
*/

            if (position2 != -1) {
                Log.d(TAG, "time=" + System.currentTimeMillis() + "step counts:" + e.values[0] + "\n");
                //displayPDRData(position2, e.values[0]);

                if(TabOne.connection_status != 0) {
                    steps = (TextView)TabOne.ConnectionView.findViewById(R.id.steps);
                    getStep = (int)e.values[0];
                    steps.setText(String.valueOf(getStep));
                }
            }

        }

        @Override
        public void onConnectionStatusChange(boolean status) {

        }

        @Override
        public void onRSSIChange(String addr, float rssi) {

        }

/*
    private BroadcastReceiver mBroadcast = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        TextView t = (TextView)findViewById(R.id.test_steps);
        getStep = intent.getFloatExtra(ScanDevice.STEPS,0);
        t.setText(String.valueOf(getStep));
    }
};
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exit").setMessage("Exit from WalkTogether?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        System.exit(0);
                        //finish();
                        //System.exit(1);
                    }
                }
        );
        dialog.setNegativeButton("No", null);
        dialog.show();
        }

}
