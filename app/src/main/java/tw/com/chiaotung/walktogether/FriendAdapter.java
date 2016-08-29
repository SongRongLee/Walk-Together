package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by User on 2016/3/16.
 */
public class FriendAdapter extends BaseAdapter {
    String [] result;
    Context context;
    Bitmap [] usr_images;
    int [] steps;
    //int text_friend_pos;
    //int text_other_pos;
    View rowView;
    private static LayoutInflater inflater=null;
    public FriendAdapter(Activity a, String[] userNameList, Bitmap[] userImages, int[] userStepList) {
        // TODO Auto-generated constructor stub
        result=userNameList;
        context=a;
        usr_images=userImages;
        steps = userStepList;
        //text_friend_pos = text_pos_1;
        //text_other_pos = text_pos_2;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
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
        TextView t_name;
        TextView t_steps;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder=new Holder();

        rowView = inflater.inflate(R.layout.friend_item, null);
        holder.t_name=(TextView) rowView.findViewById(R.id.text_friend_name);
        holder.t_steps=(TextView) rowView.findViewById(R.id.text_friend_steps);
        holder.img=(ImageView) rowView.findViewById(R.id.image_circle);

        holder.t_name.setText(result[position]);
        String S = String.valueOf(steps[position]);
        if((steps[position]/1000)>0){
            holder.t_steps.setTextSize(20);
        }
        holder.t_steps.setText(S);

        if(usr_images[position]!=null){
            holder.img.setImageBitmap(usr_images[position]);
            holder.t_steps.setVisibility(View.GONE);
        }
        return rowView;
    }



}
