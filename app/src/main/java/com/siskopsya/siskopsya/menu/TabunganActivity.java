package com.siskopsya.siskopsya.menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.siskopsya.siskopsya.LoginActivity;
import com.siskopsya.siskopsya.MainActivity;
import com.siskopsya.siskopsya.R;
import com.siskopsya.siskopsya.adapter.MenuAdapter;
import com.siskopsya.siskopsya.adapter.SaldoAdapter;
import com.siskopsya.siskopsya.adapter.TabunganAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class TabunganActivity extends AppCompatActivity {
    private ArrayList<String> jenisList, totalSaldoList,
            debitList, kreditList, saldoList;
    //Dialog dialog;
    ProgressDialog pDialog;
    //TextView totalD, totalR, tidak;
    String txtJenis, txtTotalSaldo, txtDebit, txtKredit, txtSaldo, no_anggota;
    TextView totalSaldo;
    LinearLayout lyNoData;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabungan);
        totalSaldo = findViewById(R.id.total_saldo);
        lyNoData = findViewById(R.id.ly_no_data);
        sharedpreferences = getSharedPreferences("siskopsya", Context.MODE_PRIVATE);
        no_anggota = sharedpreferences.getString("no_anggota", null);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tabungan");
        pDialog = new ProgressDialog(TabunganActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memuat data ....");
        pDialog.show();
        getSaldoList();
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void getSaldoList(){
        String urll ="https://yayasansehatmadanielarbah.com/api-siskopsya/saldo/tabungan.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urll + "");
        jenisList = new ArrayList<>();
        totalSaldoList =  new ArrayList<>();
        debitList = new ArrayList<>();
        kreditList = new ArrayList<>();
        saldoList = new ArrayList<>();


        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                urll, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(MainActivity.class.getSimpleName(), "Auth Response: " + response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("content");
                    JSONArray jTotal = jsonObject.getJSONArray("total");
                    RecyclerView menuView = (RecyclerView) findViewById(R.id.tabungan_list);
                    if(jsonObject.getString("data").equals("no data")){
                        lyNoData.setVisibility(View.VISIBLE);
                        menuView.setVisibility(View.GONE);
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }else{
                        for(int i=0;i<jArray.length();i++){
                            JSONObject jsonObject1=jArray.getJSONObject(i);
                            txtJenis =jsonObject1.getString("jenis");
                            txtTotalSaldo =jsonObject1.getString("totalSaldo");
                            txtDebit= jsonObject1.getString("debit");
                            txtKredit = jsonObject1.getString("kredit");
                            txtSaldo = jsonObject1.getString("saldo");

                            jenisList.add(txtJenis);
                            totalSaldoList.add(txtTotalSaldo);
                            debitList.add(txtDebit);
                            kreditList.add(txtKredit);
                            saldoList.add(txtSaldo);

                        }
                        for(int m=0;m<jTotal.length();m++) {
                            JSONObject getTotal = jTotal.getJSONObject(m);
                            //txtTotalD = getTotal.getString("total_diagnosa");
                        }
                        lyNoData.setVisibility(View.GONE);
                        menuView.setVisibility(View.VISIBLE);
                        //totalD.setText(txtTotalD);
                        txtTotalSaldo = jsonObject.getString("total_saldo");
                        TabunganAdapter menuData = new TabunganAdapter( jenisList, totalSaldoList,
                                debitList, kreditList, saldoList, TabunganActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(TabunganActivity.this,1);
                        menuView.setLayoutManager(mLayoutManager);
                        menuView.setAdapter(menuData);
                        //format harga
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                        symbols.setGroupingSeparator('.');
                        symbols.setDecimalSeparator(',');
                        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbols);
                        //inisial format rupiah
                        String RpTotalSaldo = decimalFormat.format(Integer.parseInt(txtTotalSaldo));
                        totalSaldo.setText(RpTotalSaldo);
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
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
                Toast.makeText(TabunganActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}
