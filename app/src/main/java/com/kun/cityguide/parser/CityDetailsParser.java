package com.kun.cityguide.parser;

import com.kun.cityguide.extra.PskHttpRequest;
import com.kun.cityguide.model.CityDetailsList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

public class CityDetailsParser {
    public static CityDetailsList connect(String url) {
        String result = "";
        try {
            result = PskHttpRequest.getText(PskHttpRequest.getInputStreamForGetRequest(url));
            if (result.length() < 1) {
                return null;
            }
            JSONObject detailsObject = new JSONObject(result);
            CityDetailsList detailsData = new CityDetailsList();
            JSONObject resultObject = detailsObject.getJSONObject("result");
            detailsData.setName(resultObject.getString("name"));
            if (resultObject.has("rating"))
                detailsData.setRating(resultObject.getString("rating"));
            detailsData.setIcon(resultObject.getString("icon"));
            detailsData.setFormatted_address(resultObject
                    .getString("formatted_address"));
            detailsData.setFormatted_phone_number(resultObject
                    .getString("formatted_phone_number"));
            if (resultObject.has("website"))
                detailsData.setWebsite(resultObject.getString("website"));
            JSONObject resultGeo = resultObject.getJSONObject("geometry");
            JSONObject location = resultGeo.getJSONObject("location");
            detailsData.setLat(location.getString("lat"));
            detailsData.setLng(location.getString("lng"));
            return detailsData;
        } catch (IOException | JSONException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
