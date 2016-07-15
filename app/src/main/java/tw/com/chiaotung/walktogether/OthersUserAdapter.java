package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
/**
 * Created by User on 2016/3/28.
 */
public class OthersUserAdapter extends BaseAdapter {
    Context context;
    Message[] message;
    ArrayList<MessageBlock> messageBlocks;
    int [] imageId;
    private Boolean isLiked;
    private Boolean[] like_clicked;
    private String Name;
    private static LayoutInflater inflater=null;
    public OthersUserAdapter(Activity a, int[] userImages, Message[] messageList, String name) {
        // TODO Auto-generated constructor stub
        context=a;
        imageId=userImages;
        message = messageList;
        Name = name;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        messageBlocks=new ArrayList<>();
        extractMessageBlock();
        like_clicked = new Boolean[message.length];
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return messageBlocks.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    public class MessageBlock
    {
        ArrayList<Message> messages;
        int like_count,step;
        public MessageBlock(){
            like_count=0;
            messages=new ArrayList<>();
        }
        public MessageBlock(int step){
            this();
            this.step=step;
        }
    }
    public class Holder
    {
        TextView t_message;
        TextView t_time;
        TextView t_step_status;
        ImageView img;
        ImageView like;
        TextView like_amount;
        //new UI
        ImageView circle;
        TextView steps;
        TextView message_count;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.user_item, null);
        //holder.t_message=(TextView) rowView.findViewById(R.id.text_message);
        //holder.t_time=(TextView) rowView.findViewById(R.id.text_time);
        //holder.t_step_status=(TextView) rowView.findViewById(R.id.text_step_status);
        //holder.img=(ImageView) rowView.findViewById(R.id.image_user);
        holder.like=(ImageView) rowView.findViewById(R.id.image_like);
        holder.like_amount=(TextView) rowView.findViewById(R.id.like_count);
        holder.like_amount.setText(Integer.toString(messageBlocks.get(position).like_count));
        //new UI
        holder.circle=(ImageView) rowView.findViewById(R.id.image_circle);

        holder.steps=(TextView) rowView.findViewById(R.id.steps);
        holder.steps.setText(Integer.toString(messageBlocks.get(position).step));

        holder.message_count=(TextView)rowView.findViewById(R.id.message_count);
        holder.message_count.setText(Integer.toString(messageBlocks.get(position).messages.size()));

        //resize circle
        int scale_dp = 80+messageBlocks.get(position).like_count*3;
        if(scale_dp>=170)scale_dp=170;
        int scale_px = (int)convertDpToPixel(scale_dp,context);
        int margin_px = (int)convertDpToPixel(90-scale_dp/2,context);
        RelativeLayout.LayoutParams resizeparameter = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        resizeparameter.setMargins(margin_px, 0, 0, 0);
        resizeparameter.width = scale_px;
        resizeparameter.height = scale_px;
        holder.circle.setLayoutParams(resizeparameter);

        RelativeLayout.LayoutParams relocparameter = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relocparameter.setMargins((int)convertDpToPixel(65,context), (int)convertDpToPixel(scale_dp/3,context), 0, 0);
        holder.steps.setLayoutParams(relocparameter);
        return rowView;
    }
    private int stepExist(int step){
        for(int i=0;i<messageBlocks.size();i++){
            if(step==messageBlocks.get(i).step){
                return i;
            }
        }
        return -1;
    }
    private void extractMessageBlock()
    {
        for(int i=0;i < message.length;i++) {
            String[] temp_step = message[i].message_content.split("@");
            String[] temp_content = message[i].message_content.split(";");
            int step = Integer.parseInt(temp_step[1].substring(2));
            String string_content = temp_content[0];
            int messageblock_Itr = stepExist(step);
            Log.d("messageBlocks", string_content);
            if (messageblock_Itr == -1) {
                messageBlocks.add(new MessageBlock(step));
                Log.d("messageBlocks", "new block");
                messageblock_Itr = messageBlocks.size() - 1;
            }
            if (string_content.equals("liked step")) {
                messageBlocks.get(messageblock_Itr).like_count++;
                Log.d("messageBlocks", "like count++");
            } else {
                messageBlocks.get(messageblock_Itr).messages.add(message[i]);
                Log.d("messageBlocks", "add message");
            }
        }
    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy/MM/dd h:mm a", cal).toString();
        return date;
    }

    private void uploadLike(int msg_id,int from) {
        ServerRequest request = new ServerRequest(context);
        request.upLikeMessage(msg_id, from);
    }
    private float getDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }
    private float convertDpToPixel(float dp, Context context){
        float px = dp * getDensity(context);
        return px;
    }
}