package tw.com.chiaotung.walktogether;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.nctu1210.api.koala3x.KoalaDevice;
import cc.nctu1210.api.koala3x.KoalaService;
import tw.com.chiaotung.walktogether.view.ModelObject;

/**
 * Created by admin on 2016/3/18.
 */
public class LocalStoreController extends Application {
    public static final String SP_NAME = "userLocalStore";

    static SharedPreferences userLocalStore;

    public LocalStoreController(Context context) {
        userLocalStore = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserID(User user) {
        SharedPreferences.Editor userLocalEditor = userLocalStore.edit();
        userLocalEditor.putInt("mid", user.mid);
        userLocalEditor.commit();
    }

    public void storeAllNameID(User user) {
        SharedPreferences.Editor userLocalEditor = userLocalStore.edit();
        userLocalEditor.putString("name", user.name);
        userLocalEditor.putString("fid_list", user.fid_list);
        userLocalEditor.putString("fname_list", user.fname_list);
        userLocalEditor.putString("oid_list", user.oid_list);
        userLocalEditor.putString("oname_list", user.oname_list);

        userLocalEditor.commit();
    }


    public void storeDevice(ModelObject Object) {
        SharedPreferences.Editor userLocalEditor = userLocalStore.edit();
        userLocalEditor.putString("DeviceName", Object.getName());
        userLocalEditor.putString("DeviceAddress", Object.getAddress());
        userLocalEditor.putString("DeviceRssi", Object.getRssi());
/*
        Gson gson = new Gson();
        String json = gson.toJson(Device);
        String json_flag = gson.toJson(Flag);
        userLocalEditor.putString("Koala", json);
        userLocalEditor.putString("Flag", json_flag);
        */
        userLocalEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn,User user) {
        SharedPreferences.Editor userLocalEditor = userLocalStore.edit();
        userLocalEditor.putBoolean("loggedIn", loggedIn);
        userLocalEditor.putString("account", user.account);
        userLocalEditor.putString("passwd", user.password);
        userLocalEditor.putInt("mid", user.mid);
        userLocalEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalEditor = userLocalStore.edit();
        userLocalEditor.clear();
        userLocalEditor.commit();
    }

    public void clearDevice() {
        SharedPreferences.Editor userLocalEditor = userLocalStore.edit();
        userLocalEditor.remove("DeviceName");
        userLocalEditor.remove("DeviceAddress");
        userLocalEditor.remove("DeviceRssi");
        //userLocalEditor.remove("Koala");
        userLocalEditor.apply();
    }

    public User getLogInPreference() {
        String name = userLocalStore.getString("name", "");
        String account = userLocalStore.getString("account", "");
        String password = userLocalStore.getString("passwd", "");
        if(account.equals(""))return null;
        User user = new User(account,password);
        return user;
    }

    public ModelObject getUsedDevice() {
        String DeviceName = userLocalStore.getString("DeviceName", "");
        String DeviceAddress = userLocalStore.getString("DeviceAddress", "");
        String DeviceRssi = userLocalStore.getString("DeviceRssi", "");
        if(DeviceAddress.equals(""))return null;
        ModelObject device = new ModelObject(DeviceName, DeviceAddress, DeviceRssi);
        return device;
    }
/*
    public KoalaDevice getUsedDevice_Koala()
    {
        Gson gson = new Gson();
        String json = userLocalStore.getString("Koala", "");
        KoalaDevice koala = gson.fromJson(json, KoalaDevice.class);
        return koala;
    }

    public AtomicBoolean getUsedFlag()
    {
        Gson gson = new Gson();
        String json = userLocalStore.getString("Flag", "");
        AtomicBoolean flag = gson.fromJson(json, AtomicBoolean.class);
        return flag;
    }
    */
}
