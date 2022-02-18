package com.dinosoftlabs.chatbot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dinosoftlabs.chatbot.ChatBot_Chat.Chat.Chat_Activity;
import com.dinosoftlabs.chatbot.ChatBot_Chat.Suggestions.Question_Answer_Get_Set;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageButton chat_btn;
    TextView locationtxt,datetxt,temperature_txt;

    ImageView weather_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Variables.sharedPreferences=getSharedPreferences(Variables.pref,MODE_PRIVATE);

        locationtxt=findViewById(R.id.location);
        datetxt=findViewById(R.id.datetxt);
        temperature_txt=findViewById(R.id.temperature_txt);
        weather_icon=findViewById(R.id.weather_icon);

        chat_btn=findViewById(R.id.chat_btn);
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if all the questions is get from the database
                if(Variables.sharedPreferences.getString(Variables.q_and_a,"").equals("")){
                    Toast.makeText(MainActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                }else {
                open_chat();
            }
            }
        });


        Call_Api_to_answers();

        datetxt.setText(Functions.getCurrentdate());


        // if the location and temeperation is already get then we will show to the user immidialty
        if(!Variables.sharedPreferences.getString(Variables.location,"").equals("")){
            locationtxt.setText(Variables.sharedPreferences.getString(Variables.location,""));
            temperature_txt.setText(Variables.sharedPreferences.getString(Variables.temperature,""));

            Set_weather_icon(Variables.sharedPreferences.getString(Variables.weather_icon,""));

        }

        // we get the current time and last api call time if there is a difference of half hour the we
        // update the location and temperature
        long requesttime= Long.parseLong(Variables.sharedPreferences.getString(Variables.api_request_time,"0"));
        long currenttime=System.currentTimeMillis();
        if((currenttime-requesttime)>900000){
            GPSStatus();
        }


    }


    public void open_chat(){
        Intent intent=new Intent(this,Chat_Activity.class);
        startActivity(intent);
    }



    // get the all question and answer from api and save it in location database or shared preference
    private void Call_Api_to_answers() {
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.bootChat, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        save_data(respo);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jsonObjectRequest);
    }

    public void save_data(String responce){
        try {
            JSONObject jsonObject=new JSONObject(responce);
            String code=jsonObject.optString("code");
            if(code.equals("200")){

                JSONArray msg=jsonObject.getJSONArray("msg");
                Map<String,Question_Answer_Get_Set> map=new HashMap();
                for(int i=0; i<msg.length();i++){
                    JSONObject data=msg.optJSONObject(i);
                    Question_Answer_Get_Set item=new Question_Answer_Get_Set();
                    item.id=data.optString("id");
                    item.question=data.optString("question");
                    item.answer=data.optString("answers");
                    item.level=data.optString("level");

                    map.put(data.optString("question"),item);
                }

                Save_Q_A(map);

            }
            else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void Save_Q_A(Map<String,Question_Answer_Get_Set> map) {
        SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(map);
        editor.putString(Variables.q_and_a,json);
        editor.apply();
    }








    // there two method are responsible for get the loaction permisson from user
    private void getLocationPermission() {

        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                123);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case  123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetCurrentlocation();
                } else {
                    Toast.makeText(this, "Please Grant permission", Toast.LENGTH_SHORT).show();
                }
                break;


        }

    }




    // this mehtod will check either user enable its location or not
    // if user has off its location then the app goes the user to setting where he have to anable its location
    public void GPSStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus) {
            Toast.makeText(this, "On Location in High Accuracy", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),2);
        }
        else {
            GetCurrentlocation();
        }
    }




    //when user anable its location in setting and comes back then this call
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            GPSStatus();
        }
    }



    // when all is ok then we will get the location of user
    private FusedLocationProviderClient mFusedLocationClient;
    // main method used for get the location of user
    private void GetCurrentlocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // first check the location permission if not give then we will ask for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        }

        // if the user gives the permission then this method will call and get the current location of user
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.

                        if (location != null) {

                            // save the location inlocal and move to main activity
                            SharedPreferences.Editor editor=Variables.sharedPreferences.edit();
                            editor.putString(Variables.lat,""+location.getLatitude());
                            editor.putString(Variables.lng,""+location.getLongitude());
                            editor.commit();

                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                String city = addresses.get(0).getLocality();
                                String country = addresses.get(0).getCountryName();
                                if(city!=null) {
                                    editor.putString(Variables.location,city+", "+country).commit();
                                }else {
                                    editor.putString(Variables.location,city+", "+country).commit();
                                }

                               locationtxt.setText(Variables.sharedPreferences.getString(Variables.location,""));
                                Call_Api_to_get_weather();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                });
    }




    // Call the api to get temperature of current location
    private void Call_Api_to_get_weather() {
        String url=Variables.weatherapi+Variables.sharedPreferences.getString(Variables.lat,"")+","
                +Variables.sharedPreferences.getString(Variables.lng,"")+"?units=ca";

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {

                // save and show the temperature
                Log.d("resp",response.toString());
                try {

                JSONObject jsonObject=new JSONObject(response);
                JSONObject currently=jsonObject.optJSONObject("currently");
                if(currently!=null && !currently.toString().equals("")) {

                    Variables.sharedPreferences.edit().putString(Variables.temperature, "" + currently.optDouble("temperature") + (char) 0x00B0).commit();
                    Variables.sharedPreferences.edit().putString(Variables.weather_icon, "" + currently.optString("icon")).commit();
                    temperature_txt.setText("" + currently.optDouble("temperature") + (char) 0x00B0);
                    Set_weather_icon(currently.optString("icon"));

                    // save the time when api is call because we will call api every half hour
                    long min = System.currentTimeMillis();
                    Variables.sharedPreferences.edit().putString(Variables.api_request_time, "" + min).commit();
                }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {

                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String,String> map = new HashMap<>(); map.put("Content-Type","multipart/form-data");
                return super.getHeaders();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postRequest);
    }



    // set the weather icon
    public void Set_weather_icon(String icon){
        switch (icon){
            case "clear-day" :
                weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.clear_day));
                break;

            case  "clear-night":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.clear_night));
            break;

            case "rain":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.rain));
            break;

            case "snow":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.snow));
            break;

            case "sleet":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.clear_day));
            break;

            case "wind":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.wind));
            break;

            case "fog":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.fog));
            break;

            case "cloudy":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
            break;

            case "partly-cloudy-day":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.partly_cloudy_day));
            break;

            case "partly-cloudy-night":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.partly_cloudy_night));
            break;

            case "hail":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.hail));
            break;

            case "thunderstorm":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.thunderstorm));
            break;

            case "tornado":
            weather_icon.setImageDrawable(getResources().getDrawable(R.drawable.tornado));
            break;
        }

    }



}
