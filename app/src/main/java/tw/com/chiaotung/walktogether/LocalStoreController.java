package tw.com.chiaotung.walktogether;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 2016/3/18.
 */
public class LocalStoreController {
    public static final String SP_NAME = "userLocalStore";

    static SharedPreferences userLocalStore;

    public LocalStoreController(Context context) {
        userLocalStore = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserName(User user) {
        SharedPreferences.Editor userLocalEditor = userLocalStore.edit();
        userLocalEditor.putString("name", user.name);
        userLocalEditor.commit();
    }

    public void storeUserID(int userID) {
        SharedPreferences.Editor userLocalEditor = userLocalStore.edit();
        userLocalEditor.putInt("mid", userID);
        userLocalEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn,User user) {
        SharedPreferences.Editor userLocalEditor = userLocalStore.edit();
        userLocalEditor.putBoolean("loggedIn", loggedIn);
        userLocalEditor.putString("account", user.account);
        userLocalEditor.putString("passwd", user.password);
        userLocalEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalEditor = userLocalStore.edit();
        userLocalEditor.clear();
        userLocalEditor.commit();
    }

    public User getLogInPreference() {
        String name = userLocalStore.getString("name", "");
        String account = userLocalStore.getString("account", "");
        String password = userLocalStore.getString("passwd", "");
        if(account.equals(""))return null;
        User user = new User(account,password);
        return user;
    }
}
