package com.example.sedaulusal.hiwijob.diagramm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.sedaulusal.hiwijob.diagramm.DynamicFragment;


public class DynamicFragmentAdapter extends FragmentPagerAdapter {
    private int mNumOfTabs;


    DynamicFragmentAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        b.putInt("position", position);
        Fragment frag = DynamicFragment.newInstance();
        frag.setArguments(b);

        return frag;

    }

    @Override
    public int getItemPosition(Object object) {
        DynamicFragment f = (DynamicFragment ) object;
        if (f != null) {
            f.update();
        }
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // get the tags set by FragmentPagerAdapter
        switch (position) {
            case 0:
                String firstTag = createdFragment.getTag();
                break;
            case 1:
                String secondTag = createdFragment.getTag();
                break;
        }

        // ... save the tags somewhere so you can reference them later
        return createdFragment;
    }



    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
