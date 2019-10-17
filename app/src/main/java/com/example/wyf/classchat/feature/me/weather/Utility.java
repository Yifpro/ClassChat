package com.example.wyf.classchat.feature.me.weather;

import android.text.TextUtils;
import android.util.Log;

import com.example.wyf.classchat.db.City;
import com.example.wyf.classchat.db.County;
import com.example.wyf.classchat.db.Province;
import com.example.wyf.classchat.bean.gson.Weather;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Administrator on 2017/9/21 0021.
 */
public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static List<String> hanldeProvinceResponse(String response){
        List<String> list = new ArrayList<>();
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvbince = new JSONArray(response);
                for(int i=0;i<allProvbince.length();i++){
                    JSONObject provinceObjrct = allProvbince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceObjrct.getInt("id"));
                    province.setProvinceName(provinceObjrct.getString("name"));
                    Log.e("testWeather", "hanldeProvinceResponse: "+province.save() + ", "+province.getProvinceName());
                    list.add(provinceObjrct.getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("testWeather", "hanldeProvinceResponse: "+new Select().from(Province.class).queryList().size());
        return list;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static List<String> hanldeCityResponse(String response,int provinceId){
        List<String> list = new ArrayList<>();
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity = new JSONArray(response);
                for(int i=0;i<allCity.length();i++){
                    JSONObject cityObjrct = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObjrct.getString("name"));
                    city.setCityCode(cityObjrct.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                    list.add(cityObjrct.getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     * 解析和处理服务器返回的县级数据
     */

    public static List<String> hanldeCountyResponse(String response,int cityId){
        List<String> list = new ArrayList<>();
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounty = new JSONArray(response);
                for(int i=0;i<allCounty.length();i++){
                    JSONObject countyObjrct = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObjrct.getString("name"));
                    county.setWeatherId(countyObjrct.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                    list.add(countyObjrct.getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     * 將返回的 JSON 数据解析成 Weather 实体类
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.get(0).toString();
            return  new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
