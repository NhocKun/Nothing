package com.kun.cityguide.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.kun.cityguide.R;
import com.kun.cityguide.extra.AlertMessage;
import com.kun.cityguide.extra.AllConstants;
import com.kun.cityguide.extra.AllURL;
import com.kun.cityguide.extra.Common;
import com.kun.cityguide.extra.PrintLog;
import com.kun.cityguide.extra.SharedPreferencesHelper;
import com.kun.cityguide.holder.AllCityReview;
import com.kun.cityguide.model.BicyleTime;
import com.kun.cityguide.model.CityDetailsList;
import com.kun.cityguide.model.DrivingTime;
import com.kun.cityguide.model.ReviewList;
import com.kun.cityguide.model.WalkingTime;
import com.kun.cityguide.parser.BiCyleDetailsParser;
import com.kun.cityguide.parser.CityDetailsParser;
import com.kun.cityguide.parser.CityReviewParser;
import com.kun.cityguide.parser.DrivingDetailsParser;
import com.kun.cityguide.parser.WalkingDetailsParser;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ListDetailsActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    AdView adView;
    AdRequest bannerRequest, fullScreenAdRequest;
    InterstitialAd fullScreenAdd;
    private Context con;
    private String pos = "";
    private TextView cName, cAdd, bDetails, wDetails, dDetails, cPhone, textDis;
    private ProgressDialog pDialog;
    private CityDetailsList CD;
    private DrivingTime DT;
    private BicyleTime BT;
    private WalkingTime WT;
    private RatingBar detailsRat;
    private RestaurantAdapter adapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newdetailslayout);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("DETAILS");
        con = this;
        //  enableAd();
        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu, menu);
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
            case R.id.share_update:
                ShareIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void enableAd() {
        // adding banner add
        adView = (AdView) findViewById(R.id.adView);
        bannerRequest = new AdRequest.Builder().build();
        adView.loadAd(bannerRequest);
        // adding full screen add
        fullScreenAdd = new InterstitialAd(this);
        fullScreenAdd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        fullScreenAdRequest = new AdRequest.Builder().build();
        fullScreenAdd.loadAd(fullScreenAdRequest);
        fullScreenAdd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                Log.i("FullScreenAdd", "Loaded successfully");
                fullScreenAdd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.i("FullScreenAdd", "failed to Load");
            }
        });
    }

    private void initUI() {
        list = (ListView) findViewById(R.id.reviewListView);
        list.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
        cName = (TextView) findViewById(R.id.cName);
        cAdd = (TextView) findViewById(R.id.cAddress);
        cPhone = (TextView) findViewById(R.id.cPhone);
        wDetails = (TextView) findViewById(R.id.walkD);
        bDetails = (TextView) findViewById(R.id.bycleD);
        dDetails = (TextView) findViewById(R.id.driveD);
        textDis = (TextView) findViewById(R.id.textD);
        detailsRat = (RatingBar) findViewById(R.id.detailsRating);
        updateUI();

    }

    /****
     * update wrestler info
     */

    private void updateUI() {
        if (!SharedPreferencesHelper.isOnline(con)) {
            AlertMessage.showMessage(con, "Error", "No internet connection");
            return;
        }

        pDialog = ProgressDialog.show(this, "", "Loading..", false, false);

        final Thread d = new Thread(new Runnable() {
            public void run() {

                CD = CityDetailsParser.connect(AllURL.cityGuideDetailsURL(AllConstants.referrence,
                        AllConstants.apiKey));
                DT = DrivingDetailsParser.connect(AllURL.drivingURL(AllConstants.UPlat, AllConstants.UPlng, AllConstants.Dlat, AllConstants.Dlng,
                        AllConstants.apiKey));
                BT = BiCyleDetailsParser.connect(AllURL.bicycleURL(AllConstants.UPlat, AllConstants.UPlng, AllConstants.Dlat, AllConstants.Dlng,
                        AllConstants.apiKey));
                WT = WalkingDetailsParser.connect(AllURL.walkURL(AllConstants.UPlat, AllConstants.UPlng, AllConstants.Dlat, AllConstants.Dlng,
                        AllConstants.apiKey));
                CityReviewParser.connect(AllURL.cityGuideDetailsURL(AllConstants.referrence, AllConstants.apiKey));

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (pDialog != null) {
                            pDialog.cancel();
                        }
                        try {
                            //Detail
                            if (CD != null) {
                                cName.setText(CD.getName().trim());
                                getActionBar().setTitle(CD.getName().trim());
                                cAdd.setText(CD.getFormatted_address().trim());
                                cPhone.setText(CD.getFormatted_phone_number().trim());
                                AllConstants.lat = CD.getLat().trim();
                                AllConstants.lng = CD.getLng().trim();
                                String rating = CD.getRating();
                                Float count = 0f;
                                if (!rating.equals(""))
                                    count = Float.parseFloat(rating);
                                detailsRat.setRating(count);
                            }
                            //	Distance
                            if (DT != null) {
                                dDetails.setText(DT.getTime().trim());
                                textDis.setText(DT.getDistance().trim());
                            }
                            if (BT != null) {
                                bDetails.setText(BT.getTime().trim());
                            }
                            if (WT != null)
                                wDetails.setText(WT.getTime().trim());

                            String imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                    + AllConstants.photoReferrence + "&sensor=true&key=" + AllConstants.apiKey;
                            ImageView lImage = (ImageView) findViewById(R.id.imageViewL);

                            ImageLoader.getInstance().displayImage(imgUrl, lImage);

                            if (AllCityReview.getAllCityReview().size() != 0) {
                                adapter = new RestaurantAdapter(con);
                                list.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else
                                Common.noItems(ListDetailsActivity.this, list, "No comment!");
                        } catch (Exception e) {
                            PrintLog.myLog("Err", e.getMessage());
                        }
                    }
                }.execute();
            }
        });
        d.start();
    }


    private void call() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + AllConstants.cCell + ""));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            PrintLog.myLog("Err", activityException.getMessage());
        }
    }

    public void cPhone(View v) {
        if (CD.getFormatted_phone_number().length() != 0) {
            try {
                call();
                AllConstants.cCell = CD.getFormatted_phone_number().trim();
                PrintLog.myLog("Tel::", AllConstants.cCell);
            } catch (Exception e) {
                PrintLog.myLog("Err", e.getMessage());
            }
        } else {
            Toast.makeText(ListDetailsActivity.this, "Sorry!No Phone Number Found.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void webView(View v) {
        if (CD.getWebsite().length() != 0) {
            try {
                AllConstants.webUrl = CD.getWebsite().trim();
                Intent next = new Intent(con, DroidWebViewActivity.class);
                next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(next);
                PrintLog.myLog("Website::", AllConstants.cWeb);
            } catch (Exception e) {
                PrintLog.myLog("Err", e.getMessage());
            }
        } else {
            Toast.makeText(ListDetailsActivity.this, "Sorry!No URL Found.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ShareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to Share.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void mapViewBtn(View v) {
        Intent next = new Intent(con, MapViewActivity.class);
        next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(next);
    }

    public void btnHome(View v) {
        Intent next = new Intent(con, MainActivity.class);
        next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(next);
    }

    public void btnBack(View v) {
        finish();
    }

    class RestaurantAdapter extends ArrayAdapter<ReviewList> {
        private final Context con;

        public RestaurantAdapter(Context context) {
            super(context, R.layout.review, AllCityReview.getAllCityReview());
            con = context;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                final LayoutInflater vi = (LayoutInflater) con
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.review, null);
            }
            if (position < AllCityReview.getAllCityReview().size()) {
                try {
                    TextView address = (TextView) v.findViewById(R.id.AuthorName);
                    TextView name = (TextView) v.findViewById(R.id.reView);

                    ReviewList CM = AllCityReview.getReviewList(position);

                    address.setText(CM.getAuthor_name().trim());
                    name.setText(CM.getAuthor_text().trim());
                } catch (Exception e) {
                    PrintLog.myLog("Err", e.getMessage());
                }
            }
            return v;
        }

    }
}
