package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    // Member variables for a user.
    public String name;
    public String screenName;
    public String publicImageUrl;

    // Create a new user from a JSON APIs' response.
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.publicImageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }
}
