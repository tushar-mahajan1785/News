package com.capternal.news.view_helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.capternal.news.R;
import com.capternal.news.utils.Utils;


public class ViewPagerIndicator extends LinearLayout {
    // VIEW PAGER INDICATOR.
    private ImageView[] views;
    private Context context;
    private int noOfDots = 0;

    private String TAG = ViewPagerIndicator.class.getSimpleName();
    private GradientDrawable shape;

    // OVAL SHAPE
    private UnSelectedShape unSelectedShape;
    private SelectedShape selectedShape;
    private int unSelectedColor = 0x7f0e002e;
    private int selectedColor = 0x7f01009b;

    public ViewPagerIndicator(Context context) {
        super(context);
        this.context = context;
        this.setBackgroundColor(context.getResources().getColor(R.color.color_white));
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;


    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;

    }

    public int getUnSelectedColor() {
        return unSelectedColor;
    }

    public void setUnSelectedColor(int unSelectedColor) {
        this.unSelectedColor = unSelectedColor;
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    /**
     * To show the current indicator in pager indicator.
     *
     * @param noOfDots No of indicators to be displayed view pager indicator Layout.
     *                 This method should be called after the initialisation of the layout
     *                 in calling activity.
     */
    public void build(int noOfDots) {
        unSelectedShape = new UnSelectedShape(new OvalShape());
        selectedShape = new SelectedShape(new OvalShape());
        this.removeAllViews();
        setNoOfDots(noOfDots);
        setUpIndicator();
    }

    /**
     * Set the background to the pager indicator view.
     */
    private void setUpIndicator() {
        initIndicator();
        for (View view : views) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(unSelectedShape);
            }

        }
    }

    /**
     * SET UP THE INDICATOR.
     */
    private void initIndicator() {
        try {
            Utils.out("INIT INDICATOR");
            views = new ImageView[noOfDots];

            LayoutParams layoutParams = new LayoutParams(10, 10);
            layoutParams.setMargins(5, 0, 5, 0);

            for (int i = 0; i < views.length; i++) {
                views[i] = new ImageView(context);
                views[i].setLayoutParams(layoutParams);
                this.addView(views[i]);
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To show the current indicator in pager indicator.
     *
     * @param position Position to be highlighted in view pager indicator.
     */
    public void showIndicator(@NonNull int position) {
        Utils.out("SHOW INDICATOR");
        // SET THE UNSELECTED DRAWABLE IN VIEW PAGER INDICATOR.
        for (int i = 0; i < views.length; i++) {
            if (i != position) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    views[i].setBackground(unSelectedShape);
                }
            }
        }
        // SET THE SELECTED DRAWABLE IN VIEW PAGER INDICATOR.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            views[position].setBackground(selectedShape);
        }

    }

    public int getNoOfDots() {
        return noOfDots;
    }

    public void setNoOfDots(@NonNull int noOfDots) {
        Utils.out("SET NO OF DOTS");
        this.noOfDots = noOfDots;
        invalidate();
    }

    class UnSelectedShape extends ShapeDrawable {
        private Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public UnSelectedShape(Shape s) {
            super(s);
            mStrokePaint.setStyle(Paint.Style.FILL);
            Utils.d("DEFAULT_COLOR", "Selected : " + selectedColor + " , FROM METHOD :" + getSelectedColor());
            Utils.d("DEFAULT_COLOR", "UnSelected : " + unSelectedColor + " , FROM METHOD :" + getUnSelectedColor());

            mStrokePaint.setColor(getUnSelectedColor());

        }

        public Paint getStrokePaint() {
            return mStrokePaint;
        }

        @Override
        protected void onDraw(Shape s, Canvas c, Paint p) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                c.drawOval(0, 0, 0, 0, getStrokePaint());
            }
            s.draw(c, p);
            s.draw(c, mStrokePaint);
        }
    }

    class SelectedShape extends ShapeDrawable {
        private Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public SelectedShape(Shape s) {
            super(s);
            mStrokePaint.setStyle(Paint.Style.FILL);
            mStrokePaint.setColor(getSelectedColor());

        }

        public Paint getStrokePaint() {
            return mStrokePaint;
        }

        @Override
        protected void onDraw(Shape s, Canvas c, Paint p) {
            Utils.out("SELECTED CIRCLE HAS BEEN DRAWN.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                c.drawOval(0, 0, 0, 0, getStrokePaint());
            }
            s.draw(c, p);
            s.draw(c, mStrokePaint);
        }
    }
}