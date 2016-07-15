package tw.com.chiaotung.walktogether;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cc.nctu1210.api.koala3x.KoalaDevice;
import cc.nctu1210.api.koala3x.KoalaService;
import cc.nctu1210.api.koala3x.KoalaServiceManager;
import cc.nctu1210.api.koala3x.SensorEvent;
import cc.nctu1210.api.koala3x.SensorEventListener;


public class UserStatus extends AppCompatActivity implements SensorEventListener{
    LocalStoreController storeController;
    private final static String TAG = UserStatus.class.getSimpleName();
    public static Toolbar toolbar;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    static SharedPreferences prefs;
    private TextView steps;
    public static int getStep;
    public static String showStep;
    public static KoalaServiceManager mServiceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storeController=new LocalStoreController(this);
        getStep = storeController.getStep();
        if(getStep==0){
            int mid=storeController.getUserID();
            int unixTime = (int) (System.currentTimeMillis() / 1000L);
            ServerRequest serverRequest=new ServerRequest(this);
            serverRequest.downStep(mid, unixTime, new CallBack() {
                @Override
                public void done(CallBackContent content) {
                    if (content != null) {
                        getStep=content.step;
                    } else
                        Log.e("TAG", "downStep failed" + "\n");
                }
            });
        }

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Me"));
        tabLayout.addTab(tabLayout.newTab().setText("Everyone"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mServiceManager = new KoalaServiceManager(this);
        mServiceManager.registerSensorEventListener(this, SensorEvent.TYPE_ACCELEROMETER);
        mServiceManager.registerSensorEventListener(this, SensorEvent.TYPE_PEDOMETER);
        //Intent intent_service_start= new Intent(UserStatus.this, ScheduledService.class);
        //UserStatus.this.startService(intent_service_start);
        Intent intent_upsStepservice_start= new Intent(UserStatus.this, UpStepService.class);
        UserStatus.this.startService(intent_upsStepservice_start);


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


    public void setTab() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1) {
                    String stepinfo = String.valueOf(UserStatus.getStep) + " steps";
                    TabTwo.userstep.setText(stepinfo);
                }
                /*
                if (tab.getPosition() == 0)
                    TabOne.updateInfo();
                else if (tab.getPosition() == 1) {
                    String stepinfo = String.valueOf(UserStatus.getStep) + " steps";
                    TabTwo.userstep.setText(stepinfo);
                    TabTwo.updateInfo();
                }*/
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
            setTab();
            NotificationGenerator.messageNum=0;
            NotificationGenerator.messageFriendNum=0;
            NotificationGenerator.messageFriendList="";

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
            //PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
            //Intent intent_service_stop = new Intent(UserStatus.this, ScheduledService.class);
            //stopService(intent_service_stop);
            Intent intent_upStepservice_stop = new Intent(UserStatus.this, UpStepService.class);
            stopService(intent_upStepservice_stop);
            Intent intent_koala_stop = new Intent(UserStatus.this, KoalaService.class);
            stopService(intent_koala_stop);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
            notificationManager.cancel(1);
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
            Log.d(TAG, "time=" + System.currentTimeMillis() + "step counts:" + e.values[0] + "\n");

            if(TabOne.connection_status != 0) {
                getStep = (int)e.values[0];
                showStep = String.valueOf(getStep);
                TabOne.btn_connect.setText(showStep + "   steps");
            }

        }

        @Override
        public void onConnectionStatusChange(boolean status) {

            if(status == false)
            {
                TabOne.connection_status = 0;
                storeController.storeStep(getStep);
                Log.d(TAG, "Disconnected from device ." + "\n");
                TabOne.btn_connect.setText("CONNECT");
                TabOne.btn_connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_scan = new Intent();
                        intent_scan.setClass(UserStatus.this, ScanDevice.class);
                        startActivity(intent_scan);
                    }
                });
            }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Exit").setMessage("Really want to Logout?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //PollingUtils.stopPollingService(UserStatus.this, PollingService.class, PollingService.ACTION);
                            //Intent intent_service_stop = new Intent(UserStatus.this, ScheduledService.class);
                            //stopService(intent_service_stop);
                            Intent intent_upStepservice_stop = new Intent(UserStatus.this, UpStepService.class);
                            stopService(intent_upStepservice_stop);
                            Intent intent_koala_stop = new Intent(UserStatus.this, KoalaService.class);
                            stopService(intent_koala_stop);
                            storeController.clearUserData();
                            Intent intent = new Intent();
                            intent.setClass(UserStatus.this, Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(0);
                            notificationManager.cancel(1);
                            finish();
                            startActivity(intent);
                        }
                    }
            );
            dialog.setNegativeButton("No", null);
            dialog.show();
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
                        //PollingUtils.stopPollingService(UserStatus.this, PollingService.class, PollingService.ACTION);
                        //Intent intent_service_stop = new Intent(UserStatus.this, ScheduledService.class);
                        //stopService(intent_service_stop);
                        Intent intent_upStepservice_stop = new Intent(UserStatus.this, UpStepService.class);
                        stopService(intent_upStepservice_stop);
                        Intent intent_koala_stop = new Intent(UserStatus.this, KoalaService.class);
                        stopService(intent_koala_stop);

                        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();

                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
