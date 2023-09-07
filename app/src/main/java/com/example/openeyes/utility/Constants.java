package com.example.openeyes.utility;

public class Constants {
    // Splash timer
    public static final long SPLASH_DISPLAY_LENGTH = 2000;

    // Permissions
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;


    // Shared preferences
    public static final String PREF_NAME  = "MyAppPreferences";
    public static final String PREF_KEY_USER_EMAIL = "userEmail";
    public static final String PREF_KEY_USER_NICK_NAME = "userNickName";
    public static final String PREF_KEY_USER_FULL_NAME = "userFullName";
    public static final String PREF_KEY_IS_LOGGED_IN = "isLoggedIn";

    // User in DB
    public static final String USER_FULL_NAME = "fullName";
    public static final String USER_NICK_NAME = "nickName";
    public static final String USER_EMAIL = "email";

    // Volley request
    public static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/reverse?";

    // Intent put extra
    public static final String DEFECT_ADDRESS = "address";
    public static final String DEFECT_LATITUDE = "lat";
    public static final String DEFECT_LONGITUDE = "lon";

}
