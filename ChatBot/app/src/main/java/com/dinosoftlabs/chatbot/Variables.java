package com.dinosoftlabs.chatbot;

import android.content.SharedPreferences;



public class
Variables {
    
    public static String user_name="username";
    public static String user_id="id";
    public static SharedPreferences sharedPreferences;

    public static String pref="pref";
    public static String lat="latitude";
    public static String lng="longitude";
    public static String location="location";
    public static String temperature="temperature";
    public static String weather_icon="weather_icon";
    public static String api_request_time="api_request_time";
    public static String q_and_a="q_and_a";


   // public static String main_domain="http://domain.com";
    //public static String main_domain="http://localhost/public_html/API/index.php?p=";

    public static String main_domain="http://10.0.2.2/public_html/API/index.php?p=";

    public static String bootChat=main_domain+"bootChat";




   // public static String wheather_api_key="wheather_api_key";
    public static String wheather_api_key="b28ea26a38e7688d9341702423a2c2d3";
    public static String weatherapi="https://api.darksky.net/forecast/"+wheather_api_key+"/";



}
