package tw.com.chiaotung.walktogether;

/**
 * Created by admin on 2016/3/5.
 */
public class User {
    String account,name,password;
    public User(String account,String password){
        this.account=account;
        this.password=password;
    }
    public User(String account,String password,String name){
        this.account=account;
        this.password=password;
        this.name=name;
    }
}
