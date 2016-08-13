package tw.com.chiaotung.walktogether;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by User on 2016/3/31.
 */
public class UpStepService extends Service
{
    static boolean service_state=false;
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private int count;
    private ServerRequest request;
    LocalStoreController storeController;
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        storeController = new LocalStoreController(this);
        count = 0;
        service_state=true;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                if(count > 1) {
                    upStep();
                    /*handler.post(new Runnable() {
                        public void run() {
                            //upStep();
                        }
                    });*/
                }
            }
        }, 0, 1*15*1000);//15 sec
    }
    private void upStep()
    {
        int unixTime = (int) (System.currentTimeMillis() / 1000L);
        request=new ServerRequest(TabOne.activity);
        int step = UserStatus.getStep;
        request.upStep(step,unixTime);
        Log.d("TAG", "Service UpStep : "+ step + "\n");
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}
