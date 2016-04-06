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
    private Boolean like_clicked;
    private static LayoutInflater inflater=null;
    public OthersUserAdapter(Activity a, int[] userImages, Message[] messageList) {
        // TODO Auto-generated constructor stub
        context=a;
        imageId=userImages;
        message = messageList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.user_item, null);
        holder.t_message=(TextView) rowView.findViewById(R.id.text_message);
        holder.t_time=(TextView) rowView.findViewById(R.id.text_time);
        holder.t_step_status=(TextView) rowView.findViewById(R.id.text_step_status);
        holder.img=(ImageView) rowView.findViewById(R.id.image_user);
        holder.like=(ImageView) rowView.findViewById(R.id.image_like);

        if(message.length > 0) {
            int from = message[position].from;
            final int mid = LocalStoreController.userLocalStore.getInt("mid", 1);
            String userName = LocalStoreController.userLocalStore.getString("name", "");
            if (from == mid) {
                String[] content= message[position].message_content.split(";");
                String m = userName + " said '" + content[0] + "'";
                holder.t_message.setText(m);
                holder.t_step_status.setText(content[1]);
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
                }
                if(!isLiked) {
                    like_clicked = false;
                    holder.like.setImageResource(R.drawable.ic_thumb_up_before);
                    holder.like.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Log.d("TAG", "From " + mid + "\n");
                            //Log.d("TAG", "Id " + message[position].msg_id + "\n");
                            if (!like_clicked) {
                                like_clicked = true;
                                uploadLike(message[position].msg_id, mid);
                                Toast.makeText(context, "You liked this message !! ", Toast.LENGTH_LONG).show();
                                holder.like.setImageResource(R.drawable.ic_thumb_up_after);
                            }
                        }
                    });
                }
                else {
                    holder.like.setImageResource(R.drawable.ic_thumb_up_after);
                    like_clicked = true;
                }
                String[] fid_list;
                String[] fname_list;
                String[] oid_list;
                String[] oname_list;
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
                else
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
                holder.t_step_status.setText(content[1]);
            }
        }
        String time = getDate((long)message[position].time*1000L);
        holder.t_time.setText(time);
        holder.img.setImageResource(imageId[position]);
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

