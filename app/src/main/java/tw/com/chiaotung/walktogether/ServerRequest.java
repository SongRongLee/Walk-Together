package tw.com.chiaotung.walktogether;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
    static String serverURL="http://140.113.169.174/walk_together/";
    LocalStoreController storeController;
    static RequestQueue requestQueue;
    //ProgressDialog pdialog;
    Context context;

    public ServerRequest(Context context){
        this.context=context;
        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(context);
        }
        storeController = new LocalStoreController(context);
        /*pdialog = new ProgressDialog(context);
        pdialog.setTitle("Processing...");
        pdialog.setMessage("Please wait...");*/
    }
    public void logInCheck(final User user, final CallBack callBack)  {
        //pdialog.show();
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
                            CallBackContent content=new CallBackContent();
                            content.user=returnedUser;
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

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
        //pdialog.dismiss();
    }
    public void signUp(final User user, final CallBack callBack)  {
        //pdialog.show();
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
                        CallBackContent content=new CallBackContent();
                        content.user=user;
                        callBack.done(content);
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
        //pdialog.dismiss();
    }
    public void upRegID(final String regID)  {
        //pdialog.show();
        int mid = LocalStoreController.userLocalStore.getInt("mid", 1);
        JSONObject param = new JSONObject();
        try {
            param.put("mid", Integer.toString(mid));
            param.put("regID",regID);
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"up_regID.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                        Toast.makeText(context,"Internet connection unstable.", Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
        //pdialog.dismiss();
    }
    public void upStep(final int step, final int time)  {
        //pdialog.show();
        int mid = LocalStoreController.userLocalStore.getInt("mid", 1);
        JSONObject param = new JSONObject();
        try {
            param.put("mid", Integer.toString(mid));
            param.put("step",Integer.toString(step));
            param.put("time",Integer.toString(time));
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"up_step.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                        Toast.makeText(context,"Internet connection unstable.", Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
        //pdialog.dismiss();
    }
    public void downStep(int id,int time ,final CallBack callBack)  {
        //pdialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("mid", Integer.toString(id));
            param.put("time",Integer.toString(time));
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"down_step.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.d("TAG", response.toString());
                            JSONArray data = response.getJSONArray("data");
                            CallBackContent content=new CallBackContent();
                            content.user = new User();
                            content.user.mid = Integer.parseInt(((JSONObject)data.get(0)).getString("mid"));
                            content.step=Integer.parseInt(((JSONObject)data.get(0)).getString("step"));
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
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
        //pdialog.dismiss();
    }

    //input message must includes message_content,time,step.
    public void upSelfMessage(Message message,final CallBack callBack)  {
        //pdialog.show();
        int mid = LocalStoreController.userLocalStore.getInt("mid", 1);
        JSONObject param = new JSONObject();
        try {
            param.put("mid", Integer.toString(mid));
            param.put("message",message.message_content);
            param.put("time",Integer.toString(message.time));
            param.put("step",Integer.toString(message.step));
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"up_self_message.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.d("TAG", response.toString());
                            JSONArray data = response.getJSONArray("data");
                            CallBackContent content=new CallBackContent();
                            content.msg_id=Integer.parseInt(((JSONObject)data.get(0)).getString("msg_id"));
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
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
        //pdialog.dismiss();
    }

    //input message must includes message_content,time,step,from,to.
    public void upOtherMessage(Message message,final CallBack callBack)  {
        //pdialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("from", Integer.toString(message.from));
            param.put("to",Integer.toString(message.to));
            param.put("message",message.message_content);
            param.put("time",Integer.toString(message.time));
            param.put("step",Integer.toString(message.step));
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"up_other_message.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.d("TAG", response.toString());
                            JSONArray data = response.getJSONArray("data");
                            CallBackContent content=new CallBackContent();
                            content.msg_id=Integer.parseInt(((JSONObject)data.get(0)).getString("msg_id"));
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
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
        //pdialog.dismiss();
    }
    public void downMessage(int ID,int time ,final CallBack callBack)  {
        //pdialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("mid", Integer.toString(ID));
            param.put("time",Integer.toString(time));
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, serverURL + "down_message.php", param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", response.toString());
                CallBackContent content=new CallBackContent();
                try{
                    JSONArray data = response.getJSONArray("data");
                    content.message_list=new Message[data.length()];
                    for(int i=0;i<data.length();i++){
                        content.message_list[i]=new Message();
                        content.message_list[i].msg_id=Integer.parseInt(((JSONObject)data.get(i)).getString("msg_id"));
                        content.message_list[i].from=Integer.parseInt(((JSONObject) data.get(i)).getString("from"));
                        content.message_list[i].message_content=((JSONObject)data.get(i)).getString("message");
                        content.message_list[i].time=((JSONObject) data.get(i)).getInt("time");
                        content.message_list[i].like_list=((JSONObject)data.get(i)).getString("like_list");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                callBack.done(content);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                callBack.done(null);
            }
        });
        requestQueue.add(jsonObjectRequest);
        //pdialog.dismiss();
    }


    //input message must includes blank_message_content,time,step,from,to.
    public void upLikeStep(Message message,final CallBack callBack)  {
        //pdialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("from", Integer.toString(message.from));
            param.put("to",Integer.toString(message.to));
            param.put("time",Integer.toString(message.time));
            param.put("step",Integer.toString(message.step));
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"up_like_step.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.d("TAG", response.toString());
                            JSONArray data = response.getJSONArray("data");
                            CallBackContent content=new CallBackContent();
                            content.msg_id=Integer.parseInt(((JSONObject)data.get(0)).getString("msg_id"));
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
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
        //pdialog.dismiss();
    }
    //input message must includes blank_message_content,time,step,from,to.
    public void upDislikeStep(Message message,final CallBack callBack)  {
        //pdialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("from", Integer.toString(message.from));
            param.put("to",Integer.toString(message.to));
            param.put("time",Integer.toString(message.time));
            param.put("step",Integer.toString(message.step));
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"up_dislike_step.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.d("TAG", response.toString());
                            JSONArray data = response.getJSONArray("data");
                            CallBackContent content=new CallBackContent();
                            content.msg_id=Integer.parseInt(((JSONObject)data.get(0)).getString("msg_id"));
                            callBack.done(content);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
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
        //pdialog.dismiss();
    }
    public void upLikeMessage(int msg_id,int from)  {
        //pdialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("msg_id", Integer.toString(msg_id));
            param.put("from", Integer.toString(from));
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"up_like_message.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse (JSONObject response) {
                        Log.d("TAG", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });
        requestQueue.add(jsonObjectRequest);
        //pdialog.dismiss();
    }

    public void addFriend(int from,int to)  {
        //pdialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("from", Integer.toString(from));
            param.put("to",Integer.toString(to));
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"add_friend.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });
        requestQueue.add(jsonObjectRequest);
        //pdialog.dismiss();
    }
    public void getMid(final CallBack callBack)  {
        //pdialog.show();
        int mid = LocalStoreController.userLocalStore.getInt("mid", 1);
        JSONObject param = new JSONObject();
        try {
            param.put("mid", Integer.toString(mid));
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"get_mid.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CallBackContent content=new CallBackContent();
                        User returnedUser=new User();
                        try{
                            Log.d("TAG", response.toString());
                            JSONArray data = response.getJSONArray("data");
                            returnedUser.name=((JSONObject)data.get(0)).getString("mname");
                            returnedUser.fid_list=((JSONObject)data.get(0)).getString("fid_list");
                            returnedUser.fname_list=((JSONObject)data.get(0)).getString("fname_list");
                            returnedUser.oid_list=((JSONObject)data.get(0)).getString("oid_list");
                            returnedUser.oname_list=((JSONObject)data.get(0)).getString("oname_list");
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        content.user=returnedUser;
                        callBack.done(content);
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
        //pdialog.dismiss();
    }
    public void search(String key_word, final CallBack callBack)  {
        //pdialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("keyword", key_word);
        }catch (JSONException e){}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,serverURL+"search.php",param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CallBackContent content=new CallBackContent();
                        User returnedUser=new User();
                        try{
                            Log.d("TAG", response.toString());
                            JSONArray data = response.getJSONArray("data");
                            returnedUser.fid_list=((JSONObject)data.get(0)).getString("fid_list");
                            returnedUser.fname_list=((JSONObject)data.get(0)).getString("fname_list");
                            returnedUser.step_list=((JSONObject)data.get(0)).getString("step_list");
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        content.user=returnedUser;
                        callBack.done(content);
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
        //pdialog.dismiss();
    }
}