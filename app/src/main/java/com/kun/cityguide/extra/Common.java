package com.kun.cityguide.extra;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kun.cityguide.R;

/**
 * Created by Kun on 21,April,2016
 * Viegrid JSC, Hanoi.
 */
public class Common {

    public static TextView noItems(Context context, ListView lv, String text) {
        TextView emptyView = new TextView(context);
        //Make sure you import android.widget.LinearLayout.LayoutParams;
        emptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        //Instead of passing resource id here I passed resolved color
        //That is, getResources().getColor((R.color.gray_dark))
        emptyView.setTextColor(context.getResources().getColor(R.color.myTextPrimaryColor));
        emptyView.setText(text);
        emptyView.setTextSize(12);
        emptyView.setVisibility(View.GONE);
        emptyView.setGravity(Gravity.CENTER_VERTICAL
                | Gravity.CENTER_HORIZONTAL);

        //Add the view to the list view. This might be what you are missing
        ((ViewGroup) lv.getParent()).addView(emptyView);

        return emptyView;
    }
}
