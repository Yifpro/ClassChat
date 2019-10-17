package com.example.wyf.classchat.feature.me.weather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.BaseFragment;
import com.example.wyf.classchat.bean.gson.Weather;
import com.example.wyf.classchat.feature.login.LoginHelper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by gsh on 2017/9/30.
 */

public class WeatherFragment2 extends BaseFragment {
    public DrawerLayout drawerLayout;
    private Button navButton;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;

    @Override
    public void initData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //尝试从本地缓存读取天气数据
        String weatherString = prefs.getString("weather", null);
        if(weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId=weather.getBasic().getId();
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            mWeatherId=getActivity().getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        String bingPic = prefs.getString("bing_pic", null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void initView() {
        swipeRefresh= (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        weatherLayout= (ScrollView) mView.findViewById(R.id.weather_layout);
        titleCity= (TextView) mView.findViewById(R.id.title_city);
        titleUpdateTime= (TextView) mView.findViewById(R.id.title_update_time);
        degreeText= (TextView) mView.findViewById(R.id.degree_text);
        weatherInfoText= (TextView) mView.findViewById(R.id.weather_info_text);
        forecastLayout= (LinearLayout) mView.findViewById(R.id.forecast_layout);
        aqiText= (TextView) mView.findViewById(R.id.aqi_text);
        pm25Text= (TextView) mView.findViewById(R.id.pm25_text);
        comfortText= (TextView) mView.findViewById(R.id.comfort_text);
        carWashText= (TextView) mView.findViewById(R.id.car_wash_text);
        sportText= (TextView) mView.findViewById(R.id.car_wash_text);

        bingPicImg= (ImageView) mView.findViewById(R.id.bing_pic_img);

        drawerLayout= (DrawerLayout) mView.findViewById(R.id.drawer_layout);
        navButton= (Button) mView.findViewById(R.id.nav_button);
    }

    @Override
    public int getLayoutId() {
        return R.layout.weather_layout2;
    }

    /**
     * 根据天气 id 请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid="+
                weatherId+"&key=3d512123a5964bf4a4a24f5a4a0a12cf";

        LoginHelper.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"获取天气信息失败onFailure",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //System.out.println(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.getStatus())){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).
                                    edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(getContext(),"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }


    /**
     * 处理并展示 Weather 实体类的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.getBasic().getCity();//城市名字
        String updateTime = weather.getBasic().getUpdate().getLoc();//更新时间
        String degree = weather.getNow().getTmp() + "℃";//温度
        String weatherInfo = weather.getNow().getCond().getTxt();//晴

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        for(Weather.DailyForecastBean forecast:weather.getDaily_forecast()){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.
                    forecast_item, forecastLayout, false);
            TextView dateText= (TextView) view.findViewById(R.id.date_text);
            TextView infoText= (TextView) view.findViewById(R.id.info_text);
            TextView maxText= (TextView) view.findViewById(R.id.max_text);
            TextView minText= (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.getDate());
            infoText.setText(forecast.getCond().getTxt_d());
            maxText.setText(forecast.getTmp().getMax());
            minText.setText(forecast.getTmp().getMin());
            forecastLayout.addView(view);
        }


        if(weather.getAqi()!=null){
            aqiText.setText(weather.getAqi().getCity().getAqi());
            pm25Text.setText(weather.getAqi().getCity().getPm25());
        }

        String comfort="舒适度"+weather.getSuggestion().getComf().getTxt();
        String carWash="洗车指数"+weather.getSuggestion().getCw().getTxt();
        String sport="运动建议"+weather.getSuggestion().getSport().getTxt();

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);


    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        LoginHelper.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor= PreferenceManager.
                        getDefaultSharedPreferences(getContext()).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getContext()).
                                load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
}
