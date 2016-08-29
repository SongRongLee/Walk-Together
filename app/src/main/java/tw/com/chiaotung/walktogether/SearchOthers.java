package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

public class SearchOthers extends Activity {
    private SearchView mSearchview;
    private ListView mListView;
    private FriendAdapter listAdapter;
    private int [] step_list;
    private int mid;
    private String [] fname_list;
    private String [] fid_list;
    private Bitmap [] userImages;
    private LocalStoreController localStoreController;
    ServerRequest serverRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_others);

        localStoreController = new LocalStoreController(this);
        mid = localStoreController.getUserID();

        serverRequest=new ServerRequest(this);

        mListView=(ListView)findViewById(R.id.other_list_view);
        mListView.setDivider(null);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent();
                intent.setClass(SearchOthers.this, OthersProfile.class);
                intent.putExtra("uid", fid_list[position]);
                intent.putExtra("uname", fname_list[position]);
                // Log.e("TAG", "uid " + fid_list[position] + "\n");
                // Log.e("TAG", "uname " + fname_list[position] + "\n");
                startActivity(intent);
            }
        });

        mSearchview=(SearchView)findViewById(R.id.searchView);
        mSearchview.setIconifiedByDefault(false);
        mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                serverRequest.search(query, new CallBack() {
                    @Override
                    public void done(CallBackContent content) {
                        int length=0;
                        String[] temp_fname_list;
                        String[] temp_fid_list;
                        String[] temp_step_list;

                        if(content!=null) {
                            temp_fname_list = content.user.fname_list.split(",");
                            temp_step_list = content.user.step_list.split(",");
                            temp_fid_list = content.user.fid_list.split(",");
                            length = temp_fname_list.length;
                            for (int i = 0; i < length; i++) {
                                if(Integer.parseInt(temp_fid_list[i])==mid){
                                    length--;
                                    break;
                                }
                            }
                            step_list = new int[length];
                            fname_list = new String[length];
                            fid_list = new String[length];
                            userImages = new Bitmap[length];
                            int j=0;
                            for (int i = 0; i < length; i++) {
                                if(Integer.parseInt(temp_fid_list[i])==mid){
                                    j++;
                                }
                                step_list[i] = Integer.parseInt(temp_step_list[j]);
                                fname_list[i] = temp_fname_list[j];
                                fid_list[i] = temp_fid_list[j];
                                j++;
                            }
                            int id;
                            for(int i=0;i<length;i++){
                                id = Integer.parseInt(fid_list[i]);
                                final int finalLength = length;
                                final int finalI = i;
                                serverRequest.downImage(id, new CallBack() {
                                    @Override
                                    public void done(CallBackContent content) {
                                        if(content!=null){
                                            for(int j=0;j< finalLength;j++){
                                                if(content.user.mid==Integer.parseInt(fid_list[j])){
                                                    userImages[j]=content.usr_image;
                                                    break;
                                                }
                                            }
                                        }
                                        if(finalI ==finalLength-1){
                                            listAdapter=new FriendAdapter(SearchOthers.this,fname_list,userImages,step_list);
                                            mListView.setAdapter(listAdapter);
                                        }
                                    }
                                });
                            }
                        }
                        else{
                            mListView.setAdapter(null);
                        }

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
