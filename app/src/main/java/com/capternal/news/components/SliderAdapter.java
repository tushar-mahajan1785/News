package com.capternal.news.components;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.capternal.news.R;
import com.capternal.news.model.GalleryImageModel;
import com.capternal.news.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by jupitor on 26/10/17.
 */

public class SliderAdapter extends PagerAdapter {
    private ArrayList<GalleryImageModel> arrGalleryImageModel = new ArrayList<GalleryImageModel>();
    private Context activity = null;

    public SliderAdapter(ArrayList<GalleryImageModel> arrGalleryImageModel, Context activity) {
        this.arrGalleryImageModel = arrGalleryImageModel;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return arrGalleryImageModel.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(activity);
        View itemView = mLayoutInflater.inflate(R.layout.gallery_slides, container, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        GalleryImageModel objGalleryImageModel = arrGalleryImageModel.get(position);
        ImageLoader.loadWithRectangle(activity, objGalleryImageModel.getImageUrl(), viewHolder.imageViewGalleryImage);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    protected class ViewHolder {
        private ImageView imageViewGalleryImage;

        public ViewHolder(View view) {
            imageViewGalleryImage = (ImageView) view.findViewById(R.id.imageView_gallery_background);
        }
    }
}
