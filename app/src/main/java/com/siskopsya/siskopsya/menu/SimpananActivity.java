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
import com.siskopsya.siskopsya.adapter.TabunganAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class SimpananActivity extends AppCompatActivity {
    //Dialog dialog;
    ProgressDialog pDialog;
    //TextView totalD, totalR, tidak;
    String tNoAnggota, tNamaAnggota, tTgLGabung, tTotalSaldo,
            tSimpananPokok, tSimpananWajib, tSimpananSukarela, no_anggota;
    TextView noAnggota, namaAnggota, tglGabung, totalSaldo,
            simpananPokok, simpananWajib, simpananSukarela;
    LinearLayout lyNoData, lyData;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simpanan);
        sharedpreferences = getSharedPreferences("siskopsya", Context.MODE_PRIVATE);
        no_anggota = sharedpreferences.getString("no_anggota", null);
        lyNoData= findViewById(R.id.ly_no_data);
        lyData = findViewById(R.id.ly_deskripsi);
        noAnggota = findViewById(R.id.no_anggota);
        namaAnggota = findViewById(R.id.nama_anggota);
        tglGabung = findViewById(R.id.tgl_gabung);
        totalSaldo = findViewById(R.id.total_saldo);
        simpananPokok = findViewById(R.id.simpanan_pokok);
        simpananWajib = findViewById(R.id.simpanan_wajib);
        simpananSukarela = findViewById(R.id.simpanan_sukarela);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Simpanan Anggota");
        pDialog = new ProgressDialog(SimpananActivity.this);
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
        final String urll ="https://yayasansehatmadanielarbah.com/api-siskopsya/saldo/simpanan.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urll + "");
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                urll, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(SimpananActivity.class.getSimpleName(), "Auth Response: " +urll+ response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("content");
                    if(jsonObject.getString("data").equals("no data")){
                        lyNoData.setVisibility(View.VISIBLE);
                        lyData.setVisibility(View.GONE);
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }else{
                        for(int i=0;i<jArray.length();i++){
                            JSONObject jsonObject1=jArray.getJSONObject(i);
                            tNoAnggota =jsonObject1.getString("no_anggota");
                            tNamaAnggota =jsonObject1.getString("nama_anggota");
                            tTgLGabung= jsonObject1.getString("tgl_gabung");
                            tSimpananPokok = jsonObject1.getString("simpanan_pokok");
                            tSimpananWajib= jsonObject1.getString("simpanan_wajib");
                            tSimpananSukarela= jsonObject1.getString("simpanan_sukarela");
                        }
                        tTotalSaldo = jsonObject.getString("total_saldo");
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
                        String rpSimpananPokok =decimalFormat.format(Integer.parseInt(tSimpananPokok));
                        String rpSImpananWajib =decimalFormat.format(Integer.parseInt(tSimpananWajib));
                        String rpSimpananSukarela = decimalFormat.format(Integer.parseInt(tSimpananSukarela));
                        noAnggota.setText(tNoAnggota);
                        namaAnggota.setText(tNamaAnggota);
                        tglGabung.setText(tTgLGabung);
                        totalSaldo.setText(RpTotalSaldo);
                        simpananPokok.setText(rpSimpananPokok);
                        simpananWajib.setText(rpSImpananWajib);
                        simpananSukarela.setText(rpSimpananSukarela);
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
                Toast.makeText(SimpananActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}
