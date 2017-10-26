package com.capternal.news.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capternal.news.R;
import com.capternal.news.model.GalleryImageModel;
import com.capternal.news.model.NewsListModel;
import com.capternal.news.utils.ImageLoader;
import com.capternal.news.view_helper.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsListAdapter extends BaseAdapter {

    private ArrayList<NewsListModel> objects = new ArrayList<NewsListModel>();

    private Context context;
    private LayoutInflater layoutInflater;
    private ArticleViewHolder articleViewHolder = null;
    private OtherViewHolder otherViewHolder = null;
    private GalleryViewHolder galleryViewHolder = null;
    private ArrayList<GalleryImageModel> arrGalleryImageModel = new ArrayList<GalleryImageModel>();

    public NewsListAdapter(Context context, ArrayList<NewsListModel> objects) {
        this.context = context;
        this.objects = objects;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return objects.get(position).getViewType();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public NewsListModel getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        switch (getItemViewType(position)) {
            case 0:
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.article_layout, parent, false);
                    articleViewHolder = new ArticleViewHolder(convertView);
                    convertView.setTag(articleViewHolder);
                } else {
                    articleViewHolder = (ArticleViewHolder) convertView.getTag();
                }
                try {
                    initializeViews((NewsListModel) getItem(position), articleViewHolder, position);
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.other_news_layout, null);
                    otherViewHolder = new OtherViewHolder(convertView);
                    convertView.setTag(otherViewHolder);
                } else {
                    otherViewHolder = (OtherViewHolder) convertView.getTag();
                }
                try {
                    initializeViews((NewsListModel) getItem(position), otherViewHolder, position);
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.gallery_layout, null);
                    galleryViewHolder = new GalleryViewHolder(convertView);
                    convertView.setTag(galleryViewHolder);
                } else {
                    galleryViewHolder = (GalleryViewHolder) convertView.getTag();
                }
                try {
                    initializeViews((NewsListModel) getItem(position), galleryViewHolder, position);
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return convertView;
    }

    private void initializeViews(NewsListModel object, ArticleViewHolder holder, int position) {
        //TODO implement
        try {
            JSONObject objects = new JSONObject(object.getObject());
            ImageLoader.loadWithRectangle(context, objects.getString("img"), holder.imageViewArticalBackground);
            holder.textViewArticleTitle.setText(objects.getString("title"));
            holder.textViewArticleCategory.setText(objects.getString("category"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeViews(NewsListModel object, OtherViewHolder holder, int position) {
        //TODO implement
        try {
            JSONObject objects = new JSONObject(object.getObject());
            ImageLoader.loadWithRectangle(context, objects.getString("img"), holder.imageViewOtherNewsBackground);
            holder.textViewOtherNewsTitle.setText(objects.getString("title"));
            holder.textViewOtherNewsCategory.setText(objects.getString("category"));
            if (object.isShowHeader()) {
                holder.textViewOtherNewsHeader.setVisibility(TextView.VISIBLE);
            } else {
                holder.textViewOtherNewsHeader.setVisibility(TextView.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeViews(NewsListModel object, final GalleryViewHolder holder, int position) {
        //TODO implement
        try {
            JSONObject objects = new JSONObject(object.getObject());
            JSONArray objJsonArrayImages = new JSONArray(objects.getString("images"));
            arrGalleryImageModel.clear();
            for (int index = 0; index < objJsonArrayImages.length(); index++) {
                arrGalleryImageModel.add(new GalleryImageModel(objJsonArrayImages.getJSONObject(index).getString("img"), objJsonArrayImages.getJSONObject(index).getString("desc")));
            }
            SliderAdapter objSliderAdapter = new SliderAdapter(arrGalleryImageModel, context);
            holder.viewPagerGallery.setOffscreenPageLimit(objSliderAdapter.getCount());
            holder.viewPagerGallery.setAdapter(objSliderAdapter);
            holder.textViewGalleryTitle.setText(objects.getString("artist"));
            if (object.isShowHeader()) {
                holder.textViewGalleryHeader.setVisibility(TextView.VISIBLE);
            } else {
                holder.textViewGalleryHeader.setVisibility(TextView.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected class ArticleViewHolder {
        private ImageView imageViewArticalBackground;
        private CustomTextView textViewArticleCategory;
        private LinearLayout linearLayoutTitle;
        private CustomTextView textViewArticleTitle;

        public ArticleViewHolder(View view) {
            imageViewArticalBackground = (ImageView) view.findViewById(R.id.imageView_artical_background);
            textViewArticleCategory = (CustomTextView) view.findViewById(R.id.textView_article_category);
            linearLayoutTitle = (LinearLayout) view.findViewById(R.id.linearLayout_title);
            textViewArticleTitle = (CustomTextView) view.findViewById(R.id.textView_article_title);
        }
    }

    protected class OtherViewHolder {
        private ImageView imageViewOtherNewsBackground;
        private CustomTextView textViewOtherNewsTitle;
        private CustomTextView textViewOtherNewsCategory;
        private CustomTextView textViewOtherNewsHeader;

        public OtherViewHolder(View view) {
            imageViewOtherNewsBackground = (ImageView) view.findViewById(R.id.imageView_other_news_background);
            textViewOtherNewsTitle = (CustomTextView) view.findViewById(R.id.textView_other_news_title);
            textViewOtherNewsCategory = (CustomTextView) view.findViewById(R.id.textView_other_news_category);
            textViewOtherNewsHeader = (CustomTextView) view.findViewById(R.id.textView_other_news_header);
        }
    }

    protected class GalleryViewHolder {
        private ViewPager viewPagerGallery;
        private CustomTextView textViewGalleryTitle;
        private CustomTextView textViewGalleryHeader;

        public GalleryViewHolder(View view) {
            viewPagerGallery = (ViewPager) view.findViewById(R.id.viewPager_gallery);
            textViewGalleryTitle = (CustomTextView) view.findViewById(R.id.textView_gallery_title);
            textViewGalleryHeader = (CustomTextView) view.findViewById(R.id.textView_gallery_header);
        }
    }
}
