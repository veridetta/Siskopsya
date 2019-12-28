package com.siskopsya.siskopsya.menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.dev.materialspinner.MaterialSpinner;
import com.siskopsya.siskopsya.MainActivity;
import com.siskopsya.siskopsya.R;
import com.siskopsya.siskopsya.adapter.SaldoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class HutangMurobahahActivity extends AppCompatActivity {
    private ArrayList<String> noAkadList;
    //Dialog dialog;
    ProgressDialog pDialog;
    //TextView totalD, totalR, tidak;
    String tNoAnggota, tNamaAnggota, tTgLGabung, tTotalSaldo, tNoAkad,
            tTotalAngsuran, tAngsuranDibayar, tSisaAngsuran, no_anggota;
    TextView noAnggota, namaAnggota, tglGabung, totalSaldo,
            totalAngsuran, angsuranDibayar, sisaAngsuran;
    MaterialSpinner spnoAkad;
    LinearLayout lyNoData, lyData;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hutang_murobahah);
        sharedpreferences = getSharedPreferences("siskopsya", Context.MODE_PRIVATE);
        no_anggota = sharedpreferences.getString("no_anggota", null);
        lyData = findViewById(R.id.ly_deskripsi);
        noAnggota = findViewById(R.id.no_anggota);
        namaAnggota = findViewById(R.id.nama_anggota);
        tglGabung = findViewById(R.id.tgl_gabung);
        totalSaldo = findViewById(R.id.total_saldo);
        totalAngsuran = findViewById(R.id.total_angsuran);
        angsuranDibayar = findViewById(R.id.angsuran_dibayar);
        sisaAngsuran = findViewById(R.id.sisa_angsuran);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tagihan Murobahah");
        pDialog = new ProgressDialog(HutangMurobahahActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memuat data ....");
        pDialog.show();
        getSaldoList();
        spnoAkad = findViewById(R.id.no_akad);
        spnoAkad.setError("Please select No Akad");
        spnoAkad.setLabel("Country");
        spnoAkad.getSpinner().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(HutangMurobahahActivity.this, "Silahkan"+position, Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void getSaldoList(){
        String urll ="https://yayasansehatmadanielarbah.com/api-siskopsya/saldo/murobahah.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urll + "");
        noAkadList = new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                urll, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(MainActivity.class.getSimpleName(), "Auth Response: " + response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("content");
                    JSONArray jTotal = jsonObject.getJSONArray("total");
                    JSONArray jNoAkad = jsonObject.getJSONArray("no_akad");
                    if(jsonObject.getString("data").equals("no data")){
                        lyNoData.setVisibility(View.VISIBLE);
                        lyData.setVisibility(View.GONE);
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }else{
                        for(int a=0;a<jNoAkad.length();a++){
                            JSONObject oNoAkad=jNoAkad.getJSONObject(a);
                            tNoAkad = oNoAkad.getString("no_akad");
                            noAkadList.add(tNoAkad);
                        }
                        for(int i=0;i<jArray.length();i++){
                            JSONObject jsonObject1=jArray.getJSONObject(i);
                            tNoAnggota =jsonObject1.getString("no_anggota");
                            tNamaAnggota =jsonObject1.getString("nama_anggota");
                            tTgLGabung= jsonObject1.getString("tgl_gabung");
                            tTotalSaldo = jsonObject1.getString("total_saldo");
                        }
                        for(int m=0;m<jTotal.length();m++) {
                            JSONObject getTotal = jTotal.getJSONObject(m);
                            //txtTotalD = getTotal.getString("total_diagnosa");
                        }
                        lyNoData.setVisibility(View.GONE);
                        lyData.setVisibility(View.VISIBLE);
                        //totalD.setText(txtTotalD);
                        //format harga
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                        symbols.setGroupingSeparator('.');
                        symbols.setDecimalSeparator(',');
                        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbols);
                        //inisial format rupiah
                        String RpTotalSaldo = decimalFormat.format(Integer.parseInt(tTotalSaldo));
                        noAnggota.setText(tNoAnggota);
                        namaAnggota.setText(tNamaAnggota);
                        tglGabung.setText(tTgLGabung);
                        totalSaldo.setText(RpTotalSaldo);
                        // Creating adapter for spinner
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HutangMurobahahActivity.this, android.R.layout.simple_spinner_item, noAkadList);
                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // attaching data adapter to spinner
                        spnoAkad.setAdapter(dataAdapter);
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
                Toast.makeText(HutangMurobahahActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}
