package com.capternal.news.controller;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.ImageView;

import com.capternal.news.R;
import com.capternal.news.model.NewsListModel;
import com.capternal.news.utils.ImageLoader;
import com.capternal.news.utils.Utils;
import com.capternal.news.view_helper.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ArticleDetailActivity extends AppCompatActivity {

    private ArticleDetailActivity activity = null;
    private CustomTextView textViewArticleDetailViewCount;
    private CustomTextView textViewArticleDetailLikeCount;
    private CustomTextView textViewArticleDetailDislikeCount;
    private CustomTextView textViewArtileDetailTitle;
    private CustomTextView textViewArticleDetailAuthor;
    private ImageView imageViewArticleDetailBackground;
    private CustomTextView textViewArticleDetailBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        activity = ArticleDetailActivity.this;
        initView();

        if (getIntent().getSerializableExtra("OBJECT") != null) {
            NewsListModel objNewsListModel = (NewsListModel) getIntent().getSerializableExtra("OBJECT");
            try {
                JSONObject objJsonObject = new JSONObject(objNewsListModel.getObject());
                textViewArtileDetailTitle.setText(objJsonObject.getString("title"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textViewArticleDetailBody.setText(Html.fromHtml(objJsonObject.getString("body"), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    textViewArticleDetailBody.setText(Html.fromHtml(objJsonObject.getString("body")));
                }
                SpannableString styledString = new SpannableString("Author By " + objJsonObject.getString("author"));
                /**
                 * Make Author name bold
                 * */
                styledString.setSpan(new StyleSpan(Typeface.BOLD), 10, styledString.length(), 0);
                textViewArticleDetailAuthor.setText(styledString);

                textViewArticleDetailViewCount.setText(objJsonObject.getString("views"));
                textViewArticleDetailLikeCount.setText(objJsonObject.getString("likes"));
                textViewArticleDetailDislikeCount.setText(objJsonObject.getString("dislikes"));
                ImageLoader.loadWithRectangle(this, objJsonObject.getString("img"), imageViewArticleDetailBackground);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        textViewArticleDetailViewCount = (CustomTextView) findViewById(R.id.textView_article_detail_view_count);
        textViewArticleDetailLikeCount = (CustomTextView) findViewById(R.id.textView_article_detail_like_count);
        textViewArticleDetailDislikeCount = (CustomTextView) findViewById(R.id.textView_article_detail_dislike_count);
        textViewArtileDetailTitle = (CustomTextView) findViewById(R.id.textView_artile_detail_title);
        textViewArticleDetailAuthor = (CustomTextView) findViewById(R.id.textView_article_detail_author);
        imageViewArticleDetailBackground = (ImageView) findViewById(R.id.imageView_article_detail_background);
        textViewArticleDetailBody = (CustomTextView) findViewById(R.id.textView_article_detail_body);
    }

    @Override
    public void onBackPressed() {
        Utils.pushToBack(activity);
    }
}
