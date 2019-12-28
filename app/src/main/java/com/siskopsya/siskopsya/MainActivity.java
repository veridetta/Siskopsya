package com.siskopsya.siskopsya;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.siskopsya.siskopsya.adapter.MenuAdapter;
import com.siskopsya.siskopsya.menu.TabunganActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> judulList, saldoList, gambarList;
    //Dialog dialog;
    ProgressDialog pDialog;
    //TextView totalD, totalR, tidak;
    String txtJudul, txtSaldo, txtGambar, no_anggota;
    boolean doubleBackToExitPressedOnce = false;
    TextView txt_logout;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences("siskopsya", Context.MODE_PRIVATE);
        no_anggota = sharedpreferences.getString("no_anggota", null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memuat data ....");
        pDialog.show();
        getMenuList(no_anggota);
        txt_logout=findViewById(R.id.txt_logout);

        txt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("session_status", false);
                editor.commit();
                Toast.makeText(getApplicationContext(),
                        "Berhasil Logout ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
    private void getMenuList(String no_anggotae){
        String url_provinsi="https://yayasansehatmadanielarbah.com/api-siskopsya/menulist.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggotae;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        Log.wtf("URL Called", url_provinsi + "");
        judulList = new ArrayList<>();
        saldoList =  new ArrayList<>();
        gambarList = new ArrayList<>();

        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                url_provinsi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(MainActivity.class.getSimpleName(), "Register Response: " + response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("content");
                    JSONArray jTotal = jsonObject.getJSONArray("total");
                    for(int i=0;i<jArray.length();i++){
                        JSONObject jsonObject1=jArray.getJSONObject(i);
                        txtJudul =jsonObject1.getString("judul");
                        txtSaldo =jsonObject1.getString("saldo");
                        txtGambar = jsonObject1.getString("gambar");
                        judulList.add(txtJudul);
                        saldoList.add(txtSaldo);
                        gambarList.add(txtGambar);
                    }
                    for(int m=0;m<jTotal.length();m++) {
                        JSONObject getTotal = jTotal.getJSONObject(m);
                        //txtTotalD = getTotal.getString("total_diagnosa");
                    }
                    //totalD.setText(txtTotalD);
                    RecyclerView menuView = (RecyclerView) findViewById(R.id.rc_menu);
                    MenuAdapter menuData = new MenuAdapter( judulList, saldoList,
                            gambarList, MainActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this,1);
                    menuView.setLayoutManager(mLayoutManager);
                    menuView.setAdapter(menuData);
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                }catch (JSONException e){e.printStackTrace(); }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }
                Toast.makeText(MainActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
