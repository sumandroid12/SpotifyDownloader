package com.beginner.suman.spotifyinfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by SUMAN on 9/26/2016.
 */
public class SongListViewAdapter extends BaseAdapter {
    private ArrayList<String> titles,sizes;
    private static LayoutInflater inflater = null;
    Context c;

    public SongListViewAdapter(Context context, ArrayList<String> title, ArrayList<String> size) {
        titles = title;
        sizes = size;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        c = context;
    }
    public void updateList(ArrayList<String> uptitles,ArrayList<String> upsizes){
        sizes.clear();
        sizes.addAll(upsizes);
        Log.d("updatelist", titles.toString() + "\n  \n" + sizes.toString() + titles.size() + " " + sizes.size());
        this.notifyDataSetChanged();
    }
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }
    @Override
    public int getCount() {
        if(titles!= null)
            return titles.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder{
        TextView textViewtitle,textViewsize;

    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View mView = view;
        view = null;
        if(view == null){
            viewHolder = new ViewHolder();
            mView = inflater.inflate(R.layout.list_row,null);
            viewHolder.textViewtitle=(TextView) mView.findViewById(R.id.songTitle);
            viewHolder.textViewsize =(TextView) mView.findViewById(R.id.fileSize);
            mView.setTag(viewHolder);
            String size="";
            if(sizes!=null) {
                try{
                if(sizes.size()==titles.size()){
                    size = sizes.get(i);
                }
                }
                catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
            viewHolder.textViewtitle.setText(titles.get(i));
            viewHolder.textViewsize.setText(size);
        }
        return mView;
    }
}
