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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2016/3/5.
 */
public class ServerRequest {
    static String serverURL="http://nol.cs.nctu.edu.tw/~backzero20/";
    LocalStoreController storeController;
    RequestQueue requestQueue;
    ProgressDialog pdialog;

    public ServerRequest(Context context){
        requestQueue= Volley.newRequestQueue(context);
        storeController = new LocalStoreController(context);
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
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"login.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.d("TAG", response.toString());
                            JSONArray data = response.getJSONArray("data");
                            User returnedUser=new User(user.account,user.password,Integer.parseInt(((JSONObject)data.get(0)).getString("mid")));
                            callBack.loginDone(returnedUser);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                        callBack.loginDone(null);
                    }
                });
        requestQueue.add(jsonObjectRequest);
        pdialog.dismiss();
    }
    public void signUp(final User user, final CallBack callBack)  {
        pdialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("name", user.name);
            param.put("account", user.account);
            param.put("passwd", user.password);
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"sign_up.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            Log.d("TAG", response.toString());
                            callBack.signUpDone(user);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                        //
                        //callBack.signUpDone(null);
                        //
                        callBack.signUpDone(user);
                    }
                });
        requestQueue.add(jsonObjectRequest);
        pdialog.dismiss();
    }
}