package com.kun.cityguide.parser;

import com.kun.cityguide.extra.PskHttpRequest;
import com.kun.cityguide.model.WalkingTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Power on 8/21/2015.
 */
public class WalkingDetailsParser {
    public static WalkingTime connect(String url) {
        String result = "";
        try {
            result = PskHttpRequest.getText(PskHttpRequest.getInputStreamForGetRequest(url));

            if (result.length() < 1) {
                return null;
            }

            JSONObject detailsObject = new JSONObject(result);

            WalkingTime wTime = new WalkingTime();

            JSONArray rowArray = detailsObject.getJSONArray("rows");

            for (int i = 0; i < rowArray.length(); i++) {
                JSONObject eleObject = rowArray.getJSONObject(i);
                JSONArray eleArray = eleObject.getJSONArray("elements");
                for (int j = 0; j < eleArray.length(); j++) {
                    final JSONObject eleeeObject = eleArray.getJSONObject(j);
                    JSONObject textD = eleeeObject.getJSONObject("duration");
                    wTime = new WalkingTime();
                    wTime.setTime(textD.getString("text"));
                }
            }
            return wTime;
        } catch (IOException | JSONException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}