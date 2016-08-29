package tw.com.chiaotung.walktogether;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cc.nctu1210.api.koala3x.KoalaDevice;
import cc.nctu1210.api.koala3x.KoalaServiceManager;
import cc.nctu1210.api.koala3x.SensorEvent;
import cc.nctu1210.api.koala3x.SensorEventListener;


public class UserStatus extends AppCompatActivity implements SensorEventListener{
    LocalStoreController storeController;
    public final static int REQUESTCODE_PICK=0;
    public final static int REQUESTCODE_CUTTING=1;
    public static String urlpath;
    public String imgUrl;
    private ProgressDialog pd;
    private final static String TAG = UserStatus.class.getSimpleName();
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int pushed;
    public static boolean koalaserviceup=false;
    static SharedPreferences prefs;
    private TextView steps;
    public static int getStep;
    public static String showStep;
    public static KoalaServiceManager mServiceManager;
    public ServerRequest serverRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);
        serverRequest=new ServerRequest(this);
        storeController=new LocalStoreController(this);
        imgUrl="http://140.113.169.174/walk_together/up_image.php";
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
                        storeController.storeStep(getStep);
                    } else
                        Log.e("TAG", "downStep failed" + "\n");
                }
            });
        }
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.friends));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.more));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        if(koalaserviceup==false){
            mServiceManager = new KoalaServiceManager(this);
            koalaserviceup=true;
        }
        mServiceManager.registerSensorEventListener(this, SensorEvent.TYPE_ACCELEROMETER);
        mServiceManager.registerSensorEventListener(this, SensorEvent.TYPE_PEDOMETER);
        //Intent intent_service_start= new Intent(UserStatus.this, ScheduledService.class);
        //UserStatus.this.startService(intent_service_start);
        if(UpStepService.service_state==false){
            Intent intent_upsStepservice_start= new Intent(UserStatus.this, UpStepService.class);
            UserStatus.this.startService(intent_upsStepservice_start);
        }

        setTab();
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
                Log.d("TAB",Integer.toString(tab.getPosition()));
                if(tab.getPosition() == 2){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(UserStatus.this);
                    dialog.setTitle("Exit").setMessage("Really want to Logout?");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //update step
                                    int unixTime = (int) (System.currentTimeMillis() / 1000L);
                                    int step = UserStatus.getStep;
                                    serverRequest.upStep(step, unixTime);

                                    Intent intent_upStepservice_stop = new Intent(UserStatus.this, UpStepService.class);
                                    stopService(intent_upStepservice_stop);
                                    /*Intent intent_koala_stop = new Intent(UserStatus.this, KoalaService.class);
                                    stopService(intent_koala_stop);*/

                                    FileUtil.deleteFile(storeController.getUserID()+".jpg");
                                    storeController.clearUserData();
                                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.cancelAll();

                                    Intent intent = new Intent();
                                    intent.setClass(UserStatus.this, Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }
                    );
                    dialog.setNegativeButton("No", null);
                    dialog.show();
                }
                else{
                    pushed = tab.getPosition();
                    if(pushed==0){
                        setTab();
                    }
                    viewPager.setCurrentItem(pushed);
                    //if (tab.getPosition() == 1) {
                    //    String stepinfo = String.valueOf(UserStatus.getStep) + " steps";
                        //TabTwo.userstep.setText(stepinfo);
                    //}
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
                    onTabSelected(tab);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        setTab();
        viewPager.setCurrentItem(pushed);
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
        Log.d(TAG, "onActivityResult"+requestCode);
        switch (requestCode) {
            case REQUESTCODE_PICK:
                Log.d(TAG, "REQUESTCODE_PICK");
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case REQUESTCODE_CUTTING:
                Log.d(TAG, "REQUESTCODE_CUTTING");
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            urlpath = FileUtil.saveFile(this, storeController.getUserID() + ".jpg", photo);

            // 新线程后台上传服务端
            pd = ProgressDialog.show(this, null, "uploading image...please wait...");
            new Thread(uploadImageRunnable).start();
        }
    }
    Runnable uploadImageRunnable = new Runnable() {
        @Override
        public void run() {

            Map<String, String> textParams = new HashMap<String, String>();
            Map<String, File> fileparams = new HashMap<String, File>();

            try {
                // 创建一个URL对象
                URL url = new URL(imgUrl);
                textParams = new HashMap<String, String>();
                fileparams = new HashMap<String, File>();
                // 要上传的图片文件
                File file = new File(urlpath);
                fileparams.put("image", file);
                // 利用HttpURLConnection对象从网络中获取网页数据
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
                conn.setConnectTimeout(5000);
                // 设置允许输出（发送POST请求必须设置允许输出）
                conn.setDoOutput(true);
                // 设置使用POST的方式发送
                conn.setRequestMethod("POST");
                // 设置不使用缓存（容易出现问题）
                conn.setUseCaches(false);
                conn.setRequestProperty("Charset", "UTF-8");//设置编码
                // 在开始用HttpURLConnection对象的setRequestProperty()设置,就是生成HTML文件头
                conn.setRequestProperty("ser-Agent", "Fiddler");
                // 设置contentType
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
                OutputStream os = conn.getOutputStream();
                DataOutputStream ds = new DataOutputStream(os);
                NetUtil.writeStringParams(textParams, ds);
                NetUtil.writeFileParams(fileparams, ds);
                NetUtil.paramsEnd(ds);
                // 对文件流操作完,要记得及时关闭
                os.close();
                // 服务器返回的响应吗
                int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
                // 对响应码进行判断
                if (code == 200) {// 返回的响应码200,是成功
                    // 得到网络返回的输入流
                    InputStream is = conn.getInputStream();
                    Log.d("TAG",NetUtil.readString(is));
                } else {
                    Toast.makeText(UserStatus.this, "upload image fail!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
        }
    };
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    pd.dismiss();
                    ServerRequest request=new ServerRequest(UserStatus.this);
                    request.downImage(storeController.getUserID(), new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                TabTwo.image_circle.setImageBitmap(content.usr_image);
                                TabTwo.userstep.setVisibility(View.GONE);
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
        //Intent intent_service_stop = new Intent(UserStatus.this, ScheduledService.class);
        //stopService(intent_service_stop);
        pushed=0;
        Intent intent_upStepservice_stop = new Intent(UserStatus.this, UpStepService.class);
        stopService(intent_upStepservice_stop);
        /*Intent intent_koala_stop = new Intent(UserStatus.this, KoalaService.class);
        stopService(intent_koala_stop);*/
        koalaserviceup=false;
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
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
            if(storeController.getStep()==0){
                int unixTime = (int) (System.currentTimeMillis() / 1000L);
                serverRequest.upStep(getStep,unixTime);
            }
            storeController.storeStep(getStep);
            Log.d(TAG, "NOTI LIST" + Integer.toString(getStep));
            TabOne.listAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onConnectionStatusChange(boolean status) {

        if(status == false)
        {
            TabOne.connection_status = 0;
            storeController.storeStep(getStep);
            Log.d(TAG, "Disconnected from device ." + "\n");
            TabOne.btn_connect.setImageResource(R.drawable.connect);
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
    /*@Override
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

                            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancelAll();

                            Intent intent = new Intent();
                            intent.setClass(UserStatus.this, Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
            );
            dialog.setNegativeButton("No", null);
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }*/

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
                        //update step
                        int unixTime = (int) (System.currentTimeMillis() / 1000L);
                        int step = UserStatus.getStep;
                        serverRequest.upStep(step,unixTime);

                        Intent intent_upStepservice_stop = new Intent(UserStatus.this, UpStepService.class);
                        stopService(intent_upStepservice_stop);
                        /*Intent intent_koala_stop = new Intent(UserStatus.this, KoalaService.class);
                        stopService(intent_koala_stop);*/

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
