package com.kun.cityguide.parser;

import com.kun.cityguide.extra.PskHttpRequest;
import com.kun.cityguide.holder.AllCityMenu;
import com.kun.cityguide.model.CityMenuList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

public class CityMenuParser {
    public static boolean connect(String url) {

        String result = "";
        try {
            result = PskHttpRequest.getText(PskHttpRequest
                    .getInputStreamForGetRequest(url));
        } catch (final URISyntaxException | IOException e1) {
            e1.printStackTrace();
        }

        if (result.length() < 1) {
            return false;
        }

        AllCityMenu.removeAll();

        final JSONObject catObject;
        try {
            catObject = new JSONObject(result);
            CityMenuList menuData;
            JSONArray resultArray = catObject.getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++) {
                final JSONObject resultObject = resultArray.getJSONObject(i);
                menuData = new CityMenuList();

                JSONObject disObject = resultObject.getJSONObject("geometry");
                JSONObject dObject = disObject.getJSONObject("location");

                try {

                    menuData.setdLat(dObject.getString("lat"));

                    menuData.setdLan(dObject.getString("lng"));
                } catch (Exception e) {
                }

                try {
                    JSONArray photoArray = resultObject.getJSONArray("photos");
                    for (int j = 0; j < photoArray.length(); j++) {

                        final JSONObject photoObject = photoArray.getJSONObject(j);

                        try {
                            menuData.setPhotoReference(photoObject.getString("photo_reference"));
                        } catch (Exception e) {
                        }

                    }
                } catch (Exception e) {
                }

                try {
                    menuData.setIcon(resultObject.getString("icon"));
                } catch (Exception e) {
                }

                try {
                    menuData.setReference(resultObject.getString("reference"));
                } catch (Exception e) {
                }

                try {
                    menuData.setName(resultObject.getString("name"));
                } catch (Exception e) {
                }
                try {
                    menuData.setRating(resultObject.getString("rating"));
                } catch (Exception e) {
                }
                try {
                    menuData.setVicinity(resultObject
                            .getString("vicinity"));
                } catch (Exception e) {
                }

                AllCityMenu.setCityMenuList(menuData);
                menuData = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

}
