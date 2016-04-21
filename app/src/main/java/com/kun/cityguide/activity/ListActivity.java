package com.kun.cityguide.activity;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.kun.cityguide.R;
import com.kun.cityguide.extra.AlertMessage;
import com.kun.cityguide.extra.AllConstants;
import com.kun.cityguide.extra.AllURL;
import com.kun.cityguide.extra.PrintLog;
import com.kun.cityguide.extra.SharedPreferencesHelper;
import com.kun.cityguide.holder.AllCityMenu;
import com.kun.cityguide.model.CityMenuList;
import com.kun.cityguide.parser.CityMenuParser;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ListActivity extends Activity {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    AdView adView;
    AdRequest bannerRequest, fullScreenAdRequest;
    InterstitialAd fullScreenAdd;
    /**
     * Called when the activity is first created.
     */

    private ListView list;
    private Context con;
    private Bitmap defaultBit;
    private RestaurantAdapter adapter;
    private ProgressDialog pDialog;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listlayout);
        setTheme(R.style.HomeTheme);
        aBar();
        con = this;
        initUI();
    }


    private void aBar() {
        // get the action bar
        ActionBar actionBar = getActionBar();
        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("AROUND ME");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.testmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initUI() {
        list = (ListView) findViewById(R.id.menuListView);
        parseQuery();
        PrintLog.myLog("Query in activity : ", AllConstants.query);
    }

    private void parseQuery() {
        if (!SharedPreferencesHelper.isOnline(con)) {
            AlertMessage.showMessage(con, "Error", "No internet connection");
            return;
        }

        pDialog = ProgressDialog.show(this, "", "Loading..", false, false);
        final Thread d = new Thread(new Runnable() {

            public void run() {
                CityMenuParser.connect(AllURL.nearByURL(AllConstants.UPlat, AllConstants.UPlng, AllConstants.query, AllConstants.apiKey));
                runOnUiThread(new Runnable() {

                    public void run() {
                        if (pDialog != null) {
                            pDialog.cancel();
                        }
                        if (AllCityMenu.getAllCityMenu().size() != 0) {
                            adapter = new RestaurantAdapter(con);
                            list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        });
        d.start();
    }

    public void btnHome(View v) {

        Intent next = new Intent(con, MainActivity.class);
        next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(next);

    }

    public void btnBack(View v) {
        finish();

    }

    class RestaurantAdapter extends ArrayAdapter<CityMenuList> {
        private final Context con;

        public RestaurantAdapter(Context context) {
            super(context, R.layout.rowlist, AllCityMenu.getAllCityMenu());
            con = context;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                final LayoutInflater vi = (LayoutInflater) con
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.rowlist, null);
            }

            if (position < AllCityMenu.getAllCityMenu().size()) {
                final CityMenuList CM = AllCityMenu.getCityMenuList(position);
                try {
                    TextView address = (TextView) v.findViewById(R.id.rowAddress);
                    address.setText(CM.getVicinity().trim());
                    AllConstants.photoReferrence = CM.getPhotoReference().trim();
                    PrintLog.myLog("PPRRRef", AllConstants.photoReferrence + "");
                    ImageView icon = (ImageView) v
                            .findViewById(R.id.rowImageView);
                    AllConstants.iconUrl = CM.getIcon().trim();
                    PrintLog.myLog("iconURL:", AllConstants.iconUrl + "");
                    String imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=190&photoreference="
                            + AllConstants.photoReferrence
                            + "&sensor=true&key=" + AllConstants.apiKey;
                    ImageLoader.getInstance().displayImage(imgUrl, icon);
                    // ------Rating ---
                    RatingBar listRatings = (RatingBar) v.findViewById(R.id.ratingBarList);
                    String rating = CM.getRating();
                    Float count = 0f;
                    if (!rating.equals(""))
                        count = Float.parseFloat(rating);
                    listRatings.setRating(count);
                    // ----Name----
                    TextView name = (TextView) v.findViewById(R.id.rowName);
                    name.setText(CM.getName().trim());
                } catch (Exception e) {
                    PrintLog.myLog("Err", e.getMessage());
                }
                v.setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {
                        try {
                            AllConstants.referrence = CM.getReference().trim();
                            AllConstants.photoReferrence = CM.getPhotoReference().trim();
                            AllConstants.Dlat = CM.getdLat().trim();
                            AllConstants.Dlng = CM.getdLan().trim();
                            PrintLog.myLog("DDDLatLng : ", CM.getdLat().trim() + "  " + CM.getdLan().trim());
                            AllConstants.detailsiconUrl = CM.getIcon().trim();
                        } catch (Exception e) {
                            PrintLog.myLog("Err", e.getMessage());
                        }
                        final Intent iii = new Intent(con, ListDetailsActivity.class);
                        iii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(iii);
                    }
                });

            }
            return v;
        }
    }
}
