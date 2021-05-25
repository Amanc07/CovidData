package com.example.coviddata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {
    private TextView confirmed,confirmed_new,Active,Active_new,Recovered,Recovered_new,Death,Death_new,samples,samples_new;
    private LinearLayout nepal_data,world_data,state_data;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PieChart pieChart;
    private String str_confirmed,str_confirmed_new,str_active,str_active_new,str_recovered,str_recovered_new,str_death,str_death_new,str_samples,str_samples_new;
    private ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        fetchdata();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchdata();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        nepal_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Nepal data clicked", Toast.LENGTH_SHORT).show();
            }
        });
        world_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "world data clicked", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,Worlddata.class);
                startActivity(intent);



            }
        });
        state_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "state data clicked", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void fetchdata() {
        showdialog();
        RequestQueue requestQueue=Volley.newRequestQueue(this);
        String apiurl="https://api.covid19india.org/data.json";
        pieChart.clearChart();
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(
                Request.Method.GET,
                apiurl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray all_state_jsonarray=null;
                        JSONArray testdata_jsonarray=null;
                        try {
                            all_state_jsonarray= response.getJSONArray("statewise");
                            testdata_jsonarray=response.getJSONArray("tested");
                            JSONObject data_india= all_state_jsonarray.getJSONObject(0);
                            JSONObject testdata_india=testdata_jsonarray.getJSONObject(testdata_jsonarray.length()-1);

                            str_confirmed=data_india.getString("confirmed");
                            str_confirmed_new=data_india.getString("deltaconfirmed");

                            str_active=data_india.getString("active");

                            str_recovered=data_india.getString("recovered");
                            str_recovered_new=data_india.getString("deltarecovered");

                            str_death=data_india.getString("deaths");
                            str_death_new=data_india.getString("deltadeaths");

                            str_samples=testdata_india.getString("totalsamplestested");
                            str_samples_new=testdata_india.getString("samplereportedtoday");

                            Handler delayforprogress=new Handler();
                            delayforprogress.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    confirmed.setText(NumberFormat.getInstance().format(Integer.parseInt(str_confirmed)));
                                    confirmed_new.setText("+" + NumberFormat.getInstance().format(Integer.parseInt(str_confirmed_new)));

                                    Active.setText(NumberFormat.getInstance().format(Integer.parseInt(str_active)));

                                    Recovered.setText(NumberFormat.getInstance().format(Integer.parseInt(str_recovered)));
                                    Recovered_new.setText("+" + NumberFormat.getInstance().format(Integer.parseInt(str_recovered_new)));

                                    Death.setText(NumberFormat.getInstance().format(Integer.parseInt(str_death)));
                                    Death_new.setText("+" + NumberFormat.getInstance().format(Integer.parseInt(str_death_new)));

                                    samples.setText(NumberFormat.getInstance().format(Integer.parseInt(str_samples)));
                                    samples_new.setText("+" + NumberFormat.getInstance().format(Integer.parseInt(str_samples_new)));

                                    pieChart.addPieSlice(new PieModel("Active",Integer.parseInt(str_active), Color.parseColor("#007afe")));
                                    pieChart.addPieSlice(new PieModel("Recovered",Integer.parseInt(str_recovered), Color.parseColor("#08a045")));
                                    pieChart.addPieSlice(new PieModel("Death",Integer.parseInt(str_death), Color.parseColor("#F6404F")));


                                    pieChart.startAnimation();
                                    DismissDialog();

                                }
                            },1000);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                }
        );
        requestQueue.add(jsonObjectRequest);


    }
    private void showdialog(){
        progressdialog=new ProgressDialog(this);
        progressdialog.show();
        progressdialog.setContentView(R.layout.progress);
        progressdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
    private void DismissDialog(){
        progressdialog.dismiss();
    }

    private void init() {
        confirmed= findViewById(R.id.confirmed_textview);
        confirmed_new=findViewById(R.id.confirmed_new_textview);
        Active=findViewById(R.id.Active_textview);
        Active_new=findViewById(R.id.Active_new_textview);
        Recovered=findViewById(R.id.Recovered_textview);
        Recovered_new=findViewById(R.id.Recovered_new_textview);
        Death=findViewById(R.id.Death_textview);
        Death_new=findViewById(R.id.Death_new_textview);
        samples=findViewById(R.id.samples_textview);
        samples_new=findViewById(R.id.samples_new_textview);
        swipeRefreshLayout=findViewById(R.id.activity_main_swipe_refresh_layout);
        pieChart=findViewById(R.id.chart);
        nepal_data=findViewById(R.id.nepallin);
        state_data=findViewById(R.id.statewise_lin);
        world_data=findViewById(R.id.worldlin);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu){
            Toast.makeText(MainActivity.this,"menu clicked",Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }
}