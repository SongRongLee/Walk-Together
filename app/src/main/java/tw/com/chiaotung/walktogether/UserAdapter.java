package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by User on 2016/3/6.
 */
public class UserAdapter extends BaseAdapter {
    Context context;
    ArrayList<Message> message;
    ArrayList<MessageBlock> messageBlocks;
    LocalStoreController localStoreController;
    //int [] imageId;
    private Boolean[] isLiked;
    private Boolean[] like_clicked;
    private static LayoutInflater inflater=null;
    public UserAdapter(Activity a, ArrayList<Message> messageList) {
        // TODO Auto-generated constructor stub
        context=a;
        localStoreController=new LocalStoreController(context);
        //imageId=userImages;
        message = messageList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        messageBlocks=new ArrayList<>();
        extractMessageBlock();
        Log.d("messageBlocksNUM", Integer.toString(messageBlocks.size()));
        if(messageBlocks.size()==0||messageBlocks.get(0).step!=localStoreController.getStep()){
            messageBlocks.add(0, new MessageBlock(localStoreController.getStep()));
        }
        isLiked = new Boolean[message.size()];
        like_clicked = new Boolean[message.size()];

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(messageBlocks.get(0).step!=localStoreController.getStep()){
            if(messageBlocks.get(0).like_count!=0&&messageBlocks.get(0).messages.size()!=0){
                return messageBlocks.size()+1;
            }
        }
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
        TextView steps,user_name;
        TextView message_count;
        RelativeLayout circle_group;
        LinearLayout note_group;
        TextView comment;
        ImageButton submit;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.user_item, null);
        if(position==0&&messageBlocks.get(0).step!=localStoreController.getStep()){
            if(messageBlocks.get(0).like_count!=0||messageBlocks.get(0).messages.size()!=0){
                messageBlocks.add(0, new MessageBlock(localStoreController.getStep()));
            }
            else{
                messageBlocks.get(0).step=localStoreController.getStep();
            }
        }
        //holder.t_message=(TextView) rowView.findViewById(R.id.text_message);
        //holder.t_time=(TextView) rowView.findViewById(R.id.text_time);
        //holder.t_step_status=(TextView) rowView.findViewById(R.id.text_step_status);
        //holder.img=(ImageView) rowView.findViewById(R.id.image_user);
        holder.like=(ImageView) rowView.findViewById(R.id.image_like);
        holder.like_amount=(TextView) rowView.findViewById(R.id.like_count);
        holder.like_amount.setText(Integer.toString(messageBlocks.get(position).like_count));
        //new UI
        holder.circle=(ImageView) rowView.findViewById(R.id.image_circle);
        holder.circle_group=(RelativeLayout)rowView.findViewById(R.id.circle_group);
        holder.note_group=(LinearLayout)rowView.findViewById(R.id.note_group);
        holder.user_name=(TextView) rowView.findViewById(R.id.UserName);
        holder.user_name.setText(LocalStoreController.userLocalStore.getString("name", ""));
        holder.submit=(ImageButton)rowView.findViewById(R.id.submit);
        holder.submit.setEnabled(false);
        holder.comment=(TextView)rowView.findViewById(R.id.comment);
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    final View note = LayoutInflater.from(context).inflate(R.layout.addnote, null);
                    new AlertDialog.Builder(context)
                            .setTitle("Leave Messages")
                            .setView(note)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = (EditText) note.findViewById(R.id.edittext);
                                    String message_content = editText.getText().toString();
                                    if(!message_content.trim().isEmpty()){
                                        int unixTime = (int) (System.currentTimeMillis() / 1000L);

                                        Message message = new Message(message_content, unixTime, localStoreController.getStep());
                                        ServerRequest request = new ServerRequest(context);
                                        request.upSelfMessage(message, new CallBack() {
                                            @Override
                                            public void done(CallBackContent content) {
                                                if (content != null) {
                                                    Log.d("TAG", "UpSelfMessage successful" + "\n");
                                                    int amount = localStoreController.getMessageAmount();
                                                    localStoreController.storeMessageAmount(amount + 1);
                                                } else
                                                    Log.e("TAG", "UpSelfMessage failed" + "\n");
                                            }
                                        });
                                        messageBlocks.get(0).messages.add(message);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();

                }
        });
        if(position!=0){
            holder.user_name.setVisibility(TextView.GONE);
            holder.note_group.setVisibility(LinearLayout.GONE);
        }
        holder.steps=(TextView) rowView.findViewById(R.id.steps);
        holder.steps.setText(Integer.toString(messageBlocks.get(position).step));

        holder.message_count=(TextView)rowView.findViewById(R.id.message_count);
        holder.message_count.setText(Integer.toString(messageBlocks.get(position).messages.size()));

        //resize circle
        int scale_dp = 80+messageBlocks.get(position).like_count*5;
        if(scale_dp>=170)scale_dp=170;
        if(position==0)scale_dp=170;
        int scale_px = (int)convertDpToPixel(scale_dp,context);
        int margin_px = (int)convertDpToPixel(90-scale_dp/2,context);
        RelativeLayout.LayoutParams resizeparameter = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //resizeparameter.setMargins(margin_px, 0, 0, 0);
        resizeparameter.width = scale_px;
        resizeparameter.height = scale_px;
        holder.circle.setLayoutParams(resizeparameter);
        resizeparameter = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        resizeparameter.setMargins(margin_px, 0, 0, 0);
        holder.circle_group.setLayoutParams(resizeparameter);

        //RelativeLayout.LayoutParams relocparameter = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //relocparameter.setMargins((int)convertDpToPixel(65,context), (int)convertDpToPixel(scale_dp/3,context), 0, 0);
        //holder.steps.setLayoutParams(relocparameter);
        holder.steps.setTextSize(scale_dp/4);

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
        for(int i=0;i < message.size();i++)
        {
            String [] temp_step=message.get(i).message_content.split("@");
            String [] temp_content=message.get(i).message_content.split(";");
            int step=Integer.parseInt(temp_step[1].substring(2));
            String string_content=temp_content[0];
            int messageblock_Itr=stepExist(step);
            Log.d("messageBlocks", string_content);
            if (messageblock_Itr==-1)
            {
                messageBlocks.add(new MessageBlock(step));
                Log.d("messageBlocks", "new block");
                messageblock_Itr=messageBlocks.size()-1;
            }
            if(string_content.equals("liked step")){
                messageBlocks.get(messageblock_Itr).like_count++;
                Log.d("messageBlocks", "like count++");
            }
            else{
                messageBlocks.get(messageblock_Itr).messages.add(message.get(i));
                Log.d("messageBlocks", "add message");
            }
        }
        /*
        if(message.length > 0) {
            int from = message[position].from;
            final int mid = LocalStoreController.userLocalStore.getInt("mid", 1);
            if (from == mid) {
                String name = LocalStoreController.userLocalStore.getString("name", "");
                String[] content = message[position].message_content.split(";");
                String m = name + " said '" + content[0] + "'";
                holder.t_message.setText(m);
                String[] step_status = content[1].split("@");
                String s = name;
                s = s + "@" + step_status[1];
                holder.t_step_status.setText(s);
            } else {
                isLiked[position] = false;
                if(!message[position].like_list.equals("null") && !message[position].like_list.equals("")) {
                    String[] like_list = message[position].like_list.split(",");
                    for (int i = 0; i < like_list.length; i++) {
                        if (mid == Integer.valueOf(like_list[i])) {
                            isLiked[position] = true;
                            break;
                        }
                    }
                    String s = String.valueOf(like_list.length);
                    s = s + " likes";
                    holder.like_amount.setText(s);
                }
                if(!isLiked[position]) {
                    like_clicked[position] = false;
                    holder.like.setImageResource(R.drawable.ic_thumb_up_before);
                    holder.like.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(!like_clicked[position]) {
                                    like_clicked[position] = true;
                                    uploadLike(message[position].msg_id, mid);
                                    Toast.makeText(context, "You liked this message !! ", Toast.LENGTH_LONG).show();
                                    holder.like.setImageResource(R.drawable.ic_thumb_up_after);
                                    if (!message[position].like_list.equals("null") && !message[position].like_list.equals("")) {
                                        String[] like_list = message[position].like_list.split(",");
                                        int amount = 1 + like_list.length;
                                        String s = String.valueOf(amount);
                                        s = s + " likes";
                                        holder.like_amount.setText(s);
                                    } else {
                                        String s = String.valueOf(1);
                                        s = s + " likes";
                                        holder.like_amount.setText(s);
                                    }
                                    TabOne.updateInfo();
                                }
                        }
                    });
                }
                else {
                    holder.like.setImageResource(R.drawable.ic_thumb_up_after);
                    like_clicked[position] = true;
                }
                String[] fid_list = new String[0];
                String[] fname_list = new String[0];
                String[] oid_list = new String[0];
                String[] oname_list = new String[0];
                if(!LocalStoreController.userLocalStore.getString("fid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("fid_list", "").equals(""))
                {
                    if(!LocalStoreController.userLocalStore.getString("oid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("oid_list", "").equals(""))
                    {
                        fid_list = LocalStoreController.userLocalStore.getString("fid_list", "").split(",");
                        fname_list = LocalStoreController.userLocalStore.getString("fname_list", "").split(",");
                        oid_list = LocalStoreController.userLocalStore.getString("oid_list", "").split(",");
                        oname_list = LocalStoreController.userLocalStore.getString("oname_list", "").split(",");
                    }
                    else
                    {
                        fid_list = LocalStoreController.userLocalStore.getString("fid_list", "").split(",");
                        fname_list = LocalStoreController.userLocalStore.getString("fname_list", "").split(",");
                        oid_list = new String[0];
                        oname_list = new String[0];
                    }
                }
                else if(!LocalStoreController.userLocalStore.getString("oid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("oid_list", "").equals(""))
                {
                    fid_list = new String[0];
                    fname_list =  new String[0];
                    oid_list = LocalStoreController.userLocalStore.getString("oid_list", "").split(",");
                    oname_list = LocalStoreController.userLocalStore.getString("oname_list", "").split(",");
                }
                int pos = 0;
                int check=-1;
                String name = new String();
                for (int i = 0; i < fid_list.length; i++) {
                    if (from == Integer.valueOf(fid_list[i])) {
                        pos = i;
                        check = 0;
                    }
                }
                if(check == -1)
                {
                    int iter= 0;
                    for (int i = 0; i < oid_list.length; i++) {
                        if (from == Integer.valueOf(oid_list[i]))
                            iter = i;
                    }
                    name = oname_list[iter];
                }
                else
                    name = fname_list[pos];
                String []content = message[position].message_content.split(";");
                String m = name + " said '" + content[0] + "'";
                holder.t_message.setText(m);
                String[] step_status = content[1].split("@");
                String s = LocalStoreController.userLocalStore.getString("name", "");
                s = s + "@" + step_status[1];
                holder.t_step_status.setText(s);
            }
        }
        String time = getDate((long)message[position].time*1000L);
        holder.t_time.setText(time);
        holder.img.setImageResource(imageId[0]);

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
            }
        });
        */
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


    //convert dp to px
    private float convertDpToPixel(float dp, Context context){
        float px = dp * getDensity(context);
        return px;
    }

    private float convertPixelToDp(float px, Context context){
        float dp = px / getDensity(context);
        return dp;
    }

    private float getDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }
}