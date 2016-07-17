package tw.com.chiaotung.walktogether;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0)return new TabOne();
        else if(position==1)return new TabTwo();
        else
        return new TabOne();
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
