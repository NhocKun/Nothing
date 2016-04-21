package com.kun.cityguide.parser;

import com.kun.cityguide.extra.PskHttpRequest;
import com.kun.cityguide.holder.AllCityReview;
import com.kun.cityguide.model.ReviewList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

public class CityReviewParser {
    public static boolean connect(String url) {
        String result = "";
        try {
            result = PskHttpRequest.getText(PskHttpRequest.getInputStreamForGetRequest(url));

            if (result.length() < 1) {
                return false;
            }
            AllCityReview.removeAll();

            final JSONObject detailsObject = new JSONObject(result);

            ReviewList reviewList;

            JSONObject resultObject = detailsObject.getJSONObject("result");

            JSONArray reviewsArray = resultObject.getJSONArray("reviews");

            for (int i = 0; i < reviewsArray.length(); i++) {
                final JSONObject reviewsObject = reviewsArray.getJSONObject(i);
                reviewList = new ReviewList();
                reviewList.setAuthor_name(reviewsObject.getString("author_name"));
                reviewList.setAuthor_text(reviewsObject.getString("text"));
                AllCityReview.setReviewList(reviewList);
            }
            return true;
        } catch (IOException | URISyntaxException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
