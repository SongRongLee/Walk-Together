package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 2016/3/16.
 */
public class MessageAdapter extends BaseAdapter {
    private int [] gotStep;
    private int [] image_displayed;
    Context context;
    ArrayList<Image_ID> usr_images;
    ArrayList<Message> messages;
    LocalStoreController storeController;
    ServerRequest request;
    //int text_friend_pos;
    //int text_other_pos;
    View rowView;
    private static LayoutInflater inflater=null;
    public MessageAdapter(Activity a, ArrayList<Message> messages, ArrayList<Image_ID> image_id) {
        // TODO Auto-generated constructor stub
        context=a;
        storeController = new LocalStoreController(a);
        request = new ServerRequest(a);
        usr_images=image_id;
        this.messages = messages;
        gotStep=new int[messages.size()];
        image_displayed=new int[messages.size()];
        for(int i=0;i<gotStep.length;i++){
            gotStep[i]=-1;
            image_displayed[i]=-1;
        }
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return messages.size();
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

    static public class Holder
    {
        TextView t_name;
        TextView t_steps;
        TextView message_content;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final Holder holder=new Holder();

        rowView = inflater.inflate(R.layout.message_item, null);
        holder.t_name=(TextView) rowView.findViewById(R.id.text_friend_name);
        holder.t_steps=(TextView) rowView.findViewById(R.id.text_friend_steps);
        holder.img=(ImageView) rowView.findViewById(R.id.image_circle);
        holder.message_content=(TextView)rowView.findViewById(R.id.message_content);

        //USER NAME
        holder.t_name.setText(storeController.getUserName(messages.get(position).from));

        //USER STEP
        if(gotStep[position]==-1){
            int unixTime = (int) (System.currentTimeMillis() / 1000L);
            request.downStep(messages.get(position).from, unixTime, new CallBack() {
                @Override
                public void done(CallBackContent content) {
                    if (content != null) {
                        if ((content.step / 1000) > 0) {
                            holder.t_steps.setTextSize(20);
                        }
                        holder.t_steps.setText(Integer.toString(content.step));
                        gotStep[position]=content.step;
                    }
                }
            });
        }
        else{
            if ((gotStep[position] / 1000) > 0) {
                holder.t_steps.setTextSize(20);
            }
            holder.t_steps.setText(Integer.toString(gotStep[position]));
        }

        //USER PHOTO
        if(image_displayed[position]==-1){
            for(int i=0;i<usr_images.size();i++){
                if(usr_images.get(i).id==messages.get(position).from){
                    if(usr_images.get(i).image!=null)
                    {
                        holder.img.setImageBitmap(usr_images.get(i).image);
                        holder.t_steps.setVisibility(View.GONE);
                    }
                    image_displayed[position]=i;
                    break;
                }
            }
        }
        else{
            if(usr_images.get(image_displayed[position]).image!=null){
                holder.img.setImageBitmap(usr_images.get(image_displayed[position]).image);
                holder.t_steps.setVisibility(View.GONE);
            }
        }
        //MESSAGE CONTENT
        String [] mes=messages.get(position).message_content.split(";");
        holder.message_content.setText(mes[0]);
        return rowView;
    }



}
