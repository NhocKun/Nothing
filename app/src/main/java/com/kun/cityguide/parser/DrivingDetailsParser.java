package com.kun.cityguide.parser;

import com.kun.cityguide.extra.PskHttpRequest;
import com.kun.cityguide.holder.DrivingDetails;
import com.kun.cityguide.model.DrivingTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Power on 8/19/2015.
 */
public class DrivingDetailsParser {
    public static DrivingTime connect(String url) {
        String result = "";
        try {
            result = PskHttpRequest.getText(PskHttpRequest.getInputStreamForGetRequest(url));
            if (result.length() < 1) {
                return null;
            }
            DrivingDetails.removeAll();
            JSONObject detailsObject = new JSONObject(result);

            DrivingTime dTime = new DrivingTime();

            JSONArray rowArray = detailsObject.getJSONArray("rows");

            for (int i = 0; i < rowArray.length(); i++) {
                final JSONObject eleObject = rowArray.getJSONObject(i);


                final JSONArray eleArray = eleObject.getJSONArray("elements");

                for (int j = 0; j < eleArray.length(); j++) {
                    final JSONObject eleeeObject = eleArray.getJSONObject(j);
                    JSONObject textD = eleeeObject.getJSONObject("duration");
                    dTime = new DrivingTime();
                    dTime.setTime(textD.getString("text"));
                    JSONObject textDD = eleeeObject.getJSONObject("distance");
                    dTime.setDistance(textDD.getString("text"));
                }
            }
            return dTime;
        } catch (IOException | JSONException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
