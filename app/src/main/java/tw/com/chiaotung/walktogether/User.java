package tw.com.chiaotung.walktogether;

/**
 * Created by admin on 2016/3/5.
 */
public class User {
    int mid;
    String account,name,password;
    String fid_list,fname_list,oid_list,oname_list,step_list;
    public User(){
    }
    public User(String account,String password){
        this.account=account;
        this.password=password;
    }
    public User(String account,String password,int mid){
        this.account=account;
        this.password=password;
        this.mid=mid;
    }
    public User(String account,String password,String name){
        this.account=account;
        this.password=password;
        this.name=name;
    }
}
