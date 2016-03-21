package tw.com.chiaotung.walktogether;

/**
 * Created by admin on 2016/3/12.
 */
public interface CallBack {
    public abstract void loginDone(User returnedUser);
    public abstract void signUpDone(User returnedUser);
}
