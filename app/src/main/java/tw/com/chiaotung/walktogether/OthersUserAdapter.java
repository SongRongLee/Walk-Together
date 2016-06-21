package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;
/**
 * Created by User on 2016/3/28.
 */
public class OthersUserAdapter extends BaseAdapter {
    Context context;
    Message[] message;
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
        like_clicked = new Boolean[message.length];
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return message.length;
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

    public class Holder
    {
        TextView t_message;
        TextView t_time;
        TextView t_step_status;
        ImageView img;
        ImageView like;
        TextView like_amount;
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
        //holder.like_amount=(TextView) rowView.findViewById(R.id.like_amount);
/*
        if(message.length > 0) {
            int from = message[position].from;
            final int mid = LocalStoreController.userLocalStore.getInt("mid", 1);
            String userName = LocalStoreController.userLocalStore.getString("name", "");
            if (from == mid) {
                String[] content= message[position].message_content.split(";");
                String m = userName + " said '" + content[0] + "'";
                holder.t_message.setText(m);
                String[] step_status = content[1].split("@");
                String s = Name;
                s = s + "@" + step_status[1];
                holder.t_step_status.setText(s);
            }
            else {
                isLiked = false;
               // Log.i("TAG", "Likelist "+ message[position].like_list  + "\n");
                if(!message[position].like_list.equals("null") && !message[position].like_list.equals("")) {
                    String[] like_list = message[position].like_list.split(",");
                    for (int i = 0; i < like_list.length; i++) {
                        if (mid == Integer.valueOf(like_list[i])) {
                            isLiked = true;
                            break;
                        }
                    }
                    String s = String.valueOf(like_list.length);
                    s = s + " likes";
                    holder.like_amount.setText(s);
                }
                if(!isLiked) {
                    like_clicked[position] = false;
                    holder.like.setImageResource(R.drawable.ic_thumb_up_before);
                    holder.like.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!like_clicked[position]) {
                                like_clicked[position] = true;
                                //Log.d("TAG", "From " + mid + "\n");
                                //Log.d("TAG", "Id " + message[position].msg_id + "\n");
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
                                OthersProfile.getMessageInfo();
                                OthersProfile.updateInfo();
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
                    if(LocalStoreController.userLocalStore.getString("oid_list", "") !=  "" && !LocalStoreController.userLocalStore.getString("oid_list", "").equals(""))
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
                else if(LocalStoreController.userLocalStore.getString("oid_list", "") !=  "" && !LocalStoreController.userLocalStore.getString("oid_list", "").equals(""))
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
                String s = Name;
                s = s + "@" + step_status[1];
                holder.t_step_status.setText(s);
            }
        }
        String time = getDate((long)message[position].time*1000L);
        holder.t_time.setText(time);
        holder.img.setImageResource(imageId[0]);*/
/*
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
            }
        });
        */
        return rowView;
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
}



