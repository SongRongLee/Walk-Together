package tw.com.chiaotung.walktogether;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by admin on 2016/3/5.
 */
public class ServerRequest {

    ProgressDialog pdialog;

    public ServerRequest(Context context){
        pdialog = new ProgressDialog(context);
        pdialog.setTitle("Processing...");
        pdialog.setTitle("Please wait...");
    }
    public boolean fetchUserData(User user){
        pdialog.show();
        done();
        return true;
    }
    public void storeUserData(){
        done();
    }
    public void done(){
        pdialog.dismiss();
    }
}
