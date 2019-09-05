package com.bit7skes.bake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.bit7skes.bake.models.Cake;
import com.bit7skes.bake.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CakeAdapter.CakeAdapterOnClickHandler {

    private List<Cake> cakeList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CakeAdapter mCakeAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mCakeAdapter = new CakeAdapter(this);
        mRecyclerView.setAdapter(mCakeAdapter);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        new MyAsyncTask().execute();
    }

    @Override
    public void onClick(Cake clickedCake) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("cake", clickedCake);
        intentToStartDetailActivity.putExtra("currentStepNum", 0);
        startActivity(intentToStartDetailActivity);
    }

    public class MyAsyncTask extends AsyncTask<String, Void, List<Cake>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Cake> doInBackground(String... params) {
            URL myURL;
            String result = null;
            try {
                myURL = NetworkUtils.buildUrl();
                result = NetworkUtils.getResponseFromHttpUrl(myURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("json", result);
            final Gson gson = new Gson();
            Type category = new TypeToken<List<Cake>>(){}.getType();
            List<Cake> cakes = gson.fromJson(result, category);
            return cakes;
        }

        @Override
        protected void onPostExecute(List<Cake> cakes) {
            for(Cake cake: cakes) {
                cakeList.add(cake);
            }
            showCakes(cakeList);
        }
    }

    private void showCakes(List<Cake> cakeList) {
        mCakeAdapter.setData(cakeList);
    }
}
