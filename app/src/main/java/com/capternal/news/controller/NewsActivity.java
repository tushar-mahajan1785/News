package com.capternal.news.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capternal.news.R;
import com.capternal.news.components.NewsListAdapter;
import com.capternal.news.model.NewsListModel;
import com.capternal.news.utils.CallWebService;
import com.capternal.news.utils.Constants;
import com.capternal.news.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity implements CallWebService.OnGetUrlResponse, View.OnClickListener {

    private ListView listViewNews = null;
    private NewsActivity activity = null;

    private ArrayList<NewsListModel> arrNewsListModels = new ArrayList<NewsListModel>();
    private NewsListAdapter objAdapter = null;
    private RelativeLayout relativeLayoutRetry;
    private Button buttonRetry;
    private Intent objIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = NewsActivity.this;
        initView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("DailyNews");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        new CallWebService(Constants.API_NEWS, activity, this, true, true, "news_data").execute();

        listViewNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                NewsListModel objNewsListModel = (NewsListModel) arrNewsListModels.get(index);
                switch (objNewsListModel.getViewType()) {
                    case 0:
                        objIntent = new Intent(activity, ArticleDetailActivity.class);
                        objIntent.putExtra("OBJECT", objNewsListModel);
                        Utils.pushToNext(activity, objIntent);
                        break;
                    case 1:
                        /**
                         * Handle item click event for Other News section
                         * */
                        break;
                    case 2:
                        /**
                         * Handle item click event for Gallery section
                         * */
                        break;
                }
            }
        });
    }

    private void initView() {
        listViewNews = (ListView) findViewById(R.id.listView_news);
        relativeLayoutRetry = (RelativeLayout) findViewById(R.id.relativeLayout_retry);
        buttonRetry = (Button) findViewById(R.id.button_retry);
        setListeners();
    }

    private void setListeners() {
        buttonRetry.setOnClickListener(this);
    }

    @Override
    public void onGetUrlResponse(String urlId, String strUrl, String strResult) {
        try {
            relativeLayoutRetry.setVisibility(RelativeLayout.GONE);
            JSONObject objJsonObject = new JSONObject(strResult);
            JSONArray objJsonArrayArticles = new JSONArray(objJsonObject.getString("articles"));
            JSONArray objJsonArrayOtherNews = new JSONArray(objJsonObject.getString("other_news"));
            JSONArray objJsonArraygalleries = new JSONArray(objJsonObject.getString("galleries"));
            for (int index = 0; index < objJsonArrayArticles.length(); index++) {
                arrNewsListModels.add(new NewsListModel(0, String.valueOf(objJsonArrayArticles.getJSONObject(index)), false));
            }
            for (int index = 0; index < objJsonArrayOtherNews.length(); index++) {
                if (index == 0)
                    arrNewsListModels.add(new NewsListModel(1, String.valueOf(objJsonArrayOtherNews.getJSONObject(index)), true));
                else
                    arrNewsListModels.add(new NewsListModel(1, String.valueOf(objJsonArrayOtherNews.getJSONObject(index)), false));
            }
            for (int index = 0; index < objJsonArraygalleries.length(); index++) {
                if (index == 0)
                    arrNewsListModels.add(new NewsListModel(2, String.valueOf(objJsonArraygalleries.getJSONObject(index)), true));
                else
                    arrNewsListModels.add(new NewsListModel(2, String.valueOf(objJsonArraygalleries.getJSONObject(index)), false));
            }
            objAdapter = new NewsListAdapter(activity, arrNewsListModels);
            listViewNews.setAdapter(objAdapter);
        } catch (NullPointerException ne) {
            new AlertDialog.Builder(activity)
                    .setTitle("Error")
                    .setMessage("Server not responding.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            relativeLayoutRetry.setVisibility(RelativeLayout.VISIBLE);
                        }
                    })
                    .setCancelable(true)
                    .show();
            ne.printStackTrace();
        } catch (Exception e) {
            new AlertDialog.Builder(activity)
                    .setTitle("Error")
                    .setMessage("Server not responding.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            relativeLayoutRetry.setVisibility(RelativeLayout.VISIBLE);
                        }
                    })
                    .setCancelable(true)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onGetUrlCancelled(String urlId, String strUrl, String cancelledResult) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_retry:
                new CallWebService(Constants.API_NEWS, activity, this, true, true, "news_data").execute();
                break;
        }
    }
}
