package com.kun.cityguide.parser;

import com.kun.cityguide.extra.PskHttpRequest;
import com.kun.cityguide.holder.BiCyleDetails;
import com.kun.cityguide.model.BicyleTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Power on 8/21/2015.
 */
public class BiCyleDetailsParser {
    public static BicyleTime connect(String url) {
        String result = "";
        try {
            result = PskHttpRequest.getText(PskHttpRequest.getInputStreamForGetRequest(url));
            if (result.length() < 1) {
                return null;
            }
            BiCyleDetails.removeAll();

            JSONObject detailsObject = new JSONObject(result);

            BicyleTime bTime = new BicyleTime();

            JSONArray rowArray = detailsObject.getJSONArray("rows");

            for (int i = 0; i < rowArray.length(); i++) {
                JSONObject eleObject = rowArray.getJSONObject(i);


                JSONArray eleArray = eleObject.getJSONArray("elements");
                for (int j = 0; j < eleArray.length(); j++) {
                    JSONObject eleeeObject = eleArray.getJSONObject(j);
                    JSONObject textD = eleeeObject.getJSONObject("duration");
                    bTime = new BicyleTime();
                    bTime.setTime(textD.getString("text"));
                }
            }
            return bTime;
        } catch (IOException | JSONException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}