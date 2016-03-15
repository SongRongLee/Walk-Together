package tw.com.chiaotung.walktogether;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2016/3/5.
 */
public class ServerRequest {
    static String serverURL="http://nol.cs.nctu.edu.tw/~backzero20/";
    RequestQueue requestQueue;
    ProgressDialog pdialog;

    public ServerRequest(Context context){
        requestQueue= Volley.newRequestQueue(context);

        pdialog = new ProgressDialog(context);
        pdialog.setTitle("Processing...");
        pdialog.setTitle("Please wait...");
    }
    public void logInCheck(final User user, final CallBack callBack)  {
        pdialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("account", user.account);
            param.put("passwd", user.password);
        }catch (JSONException je){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"login.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        callBack.done(user);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                        callBack.done(null);
                    }
                });
        requestQueue.add(jsonObjectRequest);
        pdialog.dismiss();
    }
    public void storeUserData(){
    }
}