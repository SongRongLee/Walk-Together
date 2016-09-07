package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    ArrayList<Image_ID> image_id_list;
    int [] imageId;
    private Boolean [] isLiked;
    private Boolean like_clicked;
    private Boolean isFriend;
    private Boolean addFriend_clicked;
    private String Name;
    LocalStoreController localStoreController;
    private static LayoutInflater inflater=null;
    public OthersUserAdapter(Activity a, Message[] messageList, String name, ArrayList<Image_ID> image_list) {
        // TODO Auto-generated constructor stub
        context=a;
        localStoreController = new LocalStoreController(a);
        image_id_list=new ArrayList<>(image_list);
        message = messageList;
        Name = name;
        isLiked=new Boolean[1];
        isLiked[0]=false;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        messageBlocks=new ArrayList<>();
        extractMessageBlock();
        if(messageBlocks.size()==0||messageBlocks.get(0).step!=OthersProfile.step){
            messageBlocks.add(0, new MessageBlock(OthersProfile.step));
        }
        //like_clicked = new Boolean[message.length];
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
        ArrayList<Message> like_list;
        int like_count,step;
        public MessageBlock(){
            like_count=0;
            messages=new ArrayList<>();
            like_list=new ArrayList<>();
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
        LinearLayout like_list;
        ImageButton like;
        TextView like_amount;
        //new UI
        ImageView circle;
        TextView steps,user_name;
        TextView message_count;
        RelativeLayout circle_group;
        LinearLayout note_group;
        TextView comment;
        ImageButton submit;
        ImageView addFriend;
        LinearLayout show_message;
        ImageView link_bar;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.userdata, null);
        //holder.t_message=(TextView) rowView.findViewById(R.id.text_message);
        //holder.t_time=(TextView) rowView.findViewById(R.id.text_time);
        //holder.t_step_status=(TextView) rowView.findViewById(R.id.text_step_status);
        //holder.img=(ImageView) rowView.findViewById(R.id.image_user);
        holder.like_amount=(TextView) rowView.findViewById(R.id.like_count);
        holder.like_amount.setText(Integer.toString(messageBlocks.get(position).like_count));
        //new UI
        holder.link_bar=(ImageView)rowView.findViewById(R.id.image_link);
        if(position==messageBlocks.size()-1){
            holder.link_bar.getLayoutParams().height = (int) (parent.getMeasuredHeight()-convertDpToPixel(170,context));
        }
        holder.steps=(TextView) rowView.findViewById(R.id.steps);
        holder.steps.setText(Integer.toString(messageBlocks.get(position).step));

        holder.show_message=(LinearLayout)rowView.findViewById(R.id.message_count_group);
        holder.show_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Comments")
                        .setAdapter(new MessageAdapter((Activity) context, messageBlocks.get(position).messages, image_id_list), null)
                        .setNegativeButton("Back", null)
                        .show();

            }
        });
        holder.like_list = (LinearLayout) rowView.findViewById(R.id.like_count_group);
        holder.like_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Like List")
                        .setAdapter(new MessageAdapter((Activity) context, messageBlocks.get(position).like_list, image_id_list), null)
                        .setNegativeButton("Back", null)
                        .show();
            }
        });

        holder.message_count=(TextView)rowView.findViewById(R.id.message_count);
        holder.message_count.setText(Integer.toString(messageBlocks.get(position).messages.size()));
        holder.circle=(ImageView) rowView.findViewById(R.id.image_circle);
        holder.circle_group=(RelativeLayout)rowView.findViewById(R.id.circle_group);
        holder.note_group=(LinearLayout)rowView.findViewById(R.id.note_group);
        holder.user_name=(TextView) rowView.findViewById(R.id.UserName);
        holder.user_name.setText(Name);
        //add note
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
                                if(!message_content.trim().isEmpty()) {
                                    int unixTime = (int) (System.currentTimeMillis() / 1000L);
                                    int from = LocalStoreController.userLocalStore.getInt("mid", 1);
                                    Message message = new Message(message_content, unixTime, OthersProfile.step, from, OthersProfile.mid);
                                    ServerRequest request = new ServerRequest(context);
                                    request.upOtherMessage(message, new CallBack() {
                                        @Override
                                        public void done(CallBackContent content) {
                                            if (content != null) {
                                                Log.d("TAG", "UpOtherMessage Success" + "\n");
                                            } else
                                                Log.e("TAG", "UpOtherMessage failed" + "\n");
                                        }
                                    });
                                    request.downImage(localStoreController.getUserID(), new CallBack() {
                                        @Override
                                        public void done(CallBackContent content) {
                                            if (content != null) {
                                                image_id_list.add(new Image_ID(content.usr_image, content.user.mid));
                                            } else {
                                                image_id_list.add(new Image_ID(null, localStoreController.getUserID()));
                                            }
                                        }
                                    });
                                    messageBlocks.get(0).messages.add(0,message);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });
        //add friend
        holder.addFriend=(ImageView)rowView.findViewById(R.id.addFriend);
        String[] fid_list = new String[0];
        if(!LocalStoreController.userLocalStore.getString("fid_list", "").equals("null") && !LocalStoreController.userLocalStore.getString("fid_list", "").equals(""))
            fid_list = LocalStoreController.userLocalStore.getString("fid_list", "").split(",");

        isFriend = false;
        addFriend_clicked = false;
        for (int i = 0; i < fid_list.length; i++) {
            if (OthersProfile.mid == Integer.valueOf(fid_list[i])) {
                isFriend = true;
                break;
            }
        }
        if (isFriend)
            holder.addFriend.setImageResource(R.drawable.friends);
        else {
            holder.addFriend.setImageResource(R.drawable.add_yellow);
            holder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!addFriend_clicked) {
                        holder.addFriend.setImageResource(R.drawable.friends);
                        int from = LocalStoreController.userLocalStore.getInt("mid", 1);
                        ServerRequest request = new ServerRequest(context);
                        request.addFriend(from, OthersProfile.mid);
                        addFriend_clicked = true;
                        request.getMid(new CallBack() {
                            @Override
                            public void done(CallBackContent content) {
                                if (content != null) {
                                    localStoreController.storeAllNameID(content.user);
                                } else
                                    Log.e("TAG", "getMid failed" + "\n");
                            }
                        });
                        Toast.makeText(context, "Add friend successfully !! ", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        //like step
        holder.like=(ImageButton) rowView.findViewById(R.id.like_bt);
        if(!isLiked[0]) {
            //holder.like.setImageResource(R.drawable.ic_thumb_up_before);
            like_clicked = false;
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!like_clicked) {
                        int from = LocalStoreController.userLocalStore.getInt("mid", 1);
                        int unixTime = (int) (System.currentTimeMillis() / 1000L);
                        Message message = new Message();
                        message.message_content="liked step";
                        message.from = from;
                        message.time = unixTime;
                        message.to = OthersProfile.mid;
                        message.step = OthersProfile.step;
                        uploadLikeStep(message);
                        Toast.makeText(context, "You liked the Step !! ", Toast.LENGTH_LONG).show();
                        like_clicked = true;
                        isLiked[0]=true;
                        holder.like.setEnabled(false);
                        holder.like.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        //holder.like.setImageResource(R.drawable.ic_thumb_up_after);
                        String current_likelist = localStoreController.getSelfLikelist();
                        String new_likelist = current_likelist + String.valueOf(OthersProfile.mid) + ",";
                        localStoreController.storeSelfLikelist(new_likelist);
                        ServerRequest request = new ServerRequest(context);
                        request.downImage(localStoreController.getUserID(), new CallBack() {
                            @Override
                            public void done(CallBackContent content) {
                                if (content != null) {
                                    image_id_list.add(new Image_ID(content.usr_image, content.user.mid));
                                } else {
                                    image_id_list.add(new Image_ID(null, localStoreController.getUserID()));
                                }
                            }
                        });
                        messageBlocks.get(0).like_list.add(0, message);
                        messageBlocks.get(0).like_count++;
                        holder.like_amount.setText(Integer.toString(messageBlocks.get(position).like_count));
                    }
                }
            });
        }
        else
        {
            //holder.like.setImageResource(R.drawable.ic_thumb_up_after);
            like_clicked = true;
            holder.like.setEnabled(false);
            holder.like.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }

        //set first block uniqueness
        if(position!=0){
            holder.user_name.setVisibility(TextView.GONE);
            holder.note_group.setVisibility(LinearLayout.GONE);
            holder.addFriend.setVisibility(Button.GONE);
        }

        //set UI size
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
        holder.steps.setTextSize(scale_dp / 4);
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
        if(message!=null){
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
                    messageBlocks.get(messageblock_Itr).like_list.add(message[i]);
                    Log.d("messageBlocks", "like count++");
                } else {
                    messageBlocks.get(messageblock_Itr).messages.add(message[i]);
                    Log.d("messageBlocks", "add message");
                }
            }
        }
    }
    private void uploadLikeStep(Message message) {
        ServerRequest request = new ServerRequest(context);
        request.upLikeStep(message, new CallBack() {
            @Override
            public void done(CallBackContent content) {
                if (content != null) {
                    Log.d("TAG", "UpSelfMessage successful" + "\n");
                } else
                    Log.e("TAG", "UpSelfMessage failed" + "\n");
            }
        });
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