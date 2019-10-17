package com.example.wyf.classchat.feature.me.weather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.BaseFragment;
import com.example.wyf.classchat.db.City;
import com.example.wyf.classchat.db.City_Table;
import com.example.wyf.classchat.db.County;
import com.example.wyf.classchat.db.County_Table;
import com.example.wyf.classchat.db.Province;
import com.example.wyf.classchat.feature.login.LoginHelper;
import com.example.wyf.classchat.util.ProgressUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author Administrator on 2017/9/21 0021.
 */
public class ChooseAreaFragment extends BaseFragment {

    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.back_button)
    Button backButton;
    @BindView(R.id.list_view)
    ListView listView;

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvonce;
    private City selectedCity;
    private int currentLevel;

    @Override
    public void initData() {
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvonce = provinceList.get(position);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(position);
                queryCounties();
            } else if (currentLevel == LEVEL_COUNTY) {
                String weatherId = countyList.get(position).getWeatherId();
                if (getActivity() instanceof WeatherAreaActivity) {
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                } else if (getActivity() instanceof WeatherActivity) {
                    WeatherActivity activity = (WeatherActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefresh.setRefreshing(true);
                    activity.requestWeather(weatherId);
                }
            }
        });
        backButton.setOnClickListener(v -> {
            if (currentLevel == LEVEL_COUNTY) {
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                queryProvinces();
            }
        });
        queryProvinces();
    }

    @Override
    public int getLayoutId() {
        return R.layout.choose_area;
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    String TAG = "testWeather";

    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = new Select().from(Province.class).queryList();
        Log.e(TAG, "queryProvinces size: " + provinceList.size());
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询全国所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        titleText.setText(selectedProvonce.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList= new Select().from(City.class).where(City_Table.provinceId.eq(selectedProvonce.getId())).queryList();
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvonce.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询全国所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList= new Select().from(County.class).where(County_Table.cityId.eq(selectedCity.getId())).queryList();
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvonce.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"
                    + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address, final String type) {
        ProgressUtils.showProgress(mActivity, "正在加载中……");
        Log.e(TAG, "queryFromServer start: " + address);
        LoginHelper.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: "+Thread.currentThread().getName());
                getActivity().runOnUiThread(() -> {
                    ProgressUtils.dismiss();
                    Toast.makeText(getContext(), "加载失败 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: "+Thread.currentThread().getName());
                String responseText = response.body().string();
                List<String> result = null;
                if ("province".equals(type)) {
                    result = Utility.hanldeProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.hanldeCityResponse(responseText,
                            selectedProvonce.getId());
                } else if ("county".equals(type)) {
                    result = Utility.hanldeCountyResponse(responseText,
                            selectedCity.getId());
                }
                if (result != null && result.size() > 0) {
                    if (type.equals("province")) {
                        provinceList.addAll(new Select().from(Province.class).queryList());
                    } else if (type.equals("city")) {
                        cityList.addAll(new Select().from(City.class).queryList());
                    } else if (type.equals("county")) {
                        countyList.addAll(new Select().from(County.class).queryList());
                    }
                    dataList.clear();
                    dataList.addAll(result);
                    mActivity.runOnUiThread(() -> adapter.notifyDataSetChanged());
                    ProgressUtils.dismiss();
                }
            }
        });
    }
}
