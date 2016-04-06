package tw.com.chiaotung.walktogether;

/**
 * Created by admin on 2016/3/25.
 */
public class Message {
    int msg_id;
    int from;
    int to;
    String message_content;
    int step;
    int time;
    String like_list;
    public Message(){
    }

    public Message(String message_content,int time,int step){
        this.message_content=message_content;
        this.time=time;
        this.step=step;
    }
    public Message(String message_content,int time,int step,int from ){
        this.message_content=message_content;
        this.time=time;
        this.step=step;
        this.from=from;
    }
    public Message(String message_content,int time,int step,int from,int to){
        this.message_content=message_content;
        this.time=time;
        this.step=step;
        this.from=from;
        this.to=to;
    }
}
