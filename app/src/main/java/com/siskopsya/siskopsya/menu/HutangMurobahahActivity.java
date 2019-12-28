package com.siskopsya.siskopsya.menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
    private ArrayList<String> kodeSaldoList, saldoList, tglAkadList, tenorList,
            pinjamanList, bayarList, sisaList;
    //Dialog dialog;
    ProgressDialog pDialog;
    //TextView totalD, totalR, tidak;
    String txtTotalSaldo, txtKodeSaldo, txtSaldo, txtTglAkad, txtTenor,
            txtPinjaman, txtBayar, txtSisa;
    TextView totalSaldo;
    LinearLayout lyNoData;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hutang_murobahah);
        lyNoData = findViewById(R.id.ly_no_data);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tabungan");
        pDialog = new ProgressDialog(HutangMurobahahActivity.this);
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
    private void getSaldoList() {
        String url_provinsi = "https://yayasansehatmadanielarbah.com/api-siskopsya/saldo.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota=001&&tipe=tabungan";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.wtf("URL Called", url_provinsi + "");
        kodeSaldoList = new ArrayList<>();
        saldoList = new ArrayList<>();
        tglAkadList = new ArrayList<>();
        tenorList = new ArrayList<>();
        pinjamanList = new ArrayList<>();
        bayarList = new ArrayList<>();
        sisaList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url_provinsi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(MainActivity.class.getSimpleName(), "Auth Response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("content");
                    JSONArray jTotal = jsonObject.getJSONArray("total");
                    //totalD.setText(txtTotalD);
                    txtTotalSaldo = jsonObject.getString("total_saldo");
                    RecyclerView menuView = (RecyclerView) findViewById(R.id.sado_list);
                    if (txtTotalSaldo.equals("no data")) {
                        lyNoData.setVisibility(View.VISIBLE);
                        menuView.setVisibility(View.GONE);
                    } else {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonObject1 = jArray.getJSONObject(i);
                            txtKodeSaldo = jsonObject1.getString("kodeSaldo");
                            txtSaldo = jsonObject1.getString("saldo");
                            txtTglAkad = jsonObject1.getString("tglAkad");
                            txtTenor = jsonObject1.getString("tenor");
                            txtPinjaman = jsonObject1.getString("pinjaman");
                            txtBayar = jsonObject1.getString("bayar");
                            txtSisa = jsonObject1.getString("sisa");
                            kodeSaldoList.add(txtKodeSaldo);
                            saldoList.add(txtSaldo);
                            tglAkadList.add(txtTglAkad);
                            tenorList.add(txtTenor);
                            pinjamanList.add(txtPinjaman);
                            bayarList.add(txtBayar);
                            sisaList.add(txtSisa);
                        }
                        for (int m = 0; m < jTotal.length(); m++) {
                            JSONObject getTotal = jTotal.getJSONObject(m);
                            //txtTotalD = getTotal.getString("total_diagnosa");
                        }
                        lyNoData.setVisibility(View.GONE);
                        SaldoAdapter menuData = new SaldoAdapter(kodeSaldoList, saldoList,
                                tglAkadList, tenorList, pinjamanList, bayarList, sisaList, HutangMurobahahActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(HutangMurobahahActivity.this, 1);
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
                    }
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (pDialog.isShowing()) {
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
