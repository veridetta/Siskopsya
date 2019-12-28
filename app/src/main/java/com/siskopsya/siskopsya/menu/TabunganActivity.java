package com.siskopsya.siskopsya.menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.tabs.TabLayout;
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
    private ArrayList<String> jenisList, kodeList;
    //Dialog dialog;
    ProgressDialog pDialog, pDialog2;
    //TextView totalD, totalR, tidak;
    String tNoAnggota, tNamaAnggota, tTgLGabung, tTotalSaldo,no_anggota,
            tJenis, tDebit, tKredit, tSaldo, tKode;
    TextView noAnggota, namaAnggota, tglGabung, totalSaldo,
            debit, kredit, saldo;
    Spinner spnJenis;
    LinearLayout lyNoData, lyData;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabungan);
        sharedpreferences = getSharedPreferences("siskopsya", Context.MODE_PRIVATE);
        no_anggota = sharedpreferences.getString("no_anggota", null);
        lyNoData = findViewById(R.id.ly_no_data);
        lyData = findViewById(R.id.ly_deskripsi);
        noAnggota = findViewById(R.id.no_anggota);
        namaAnggota = findViewById(R.id.nama_anggota);
        tglGabung = findViewById(R.id.tgl_gabung);
        totalSaldo = findViewById(R.id.total_saldo);
        debit = findViewById(R.id.debit);
        kredit= findViewById(R.id.kredit);
        saldo= findViewById(R.id.saldo);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tabungan");
        pDialog = new ProgressDialog(TabunganActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memuat data ....");
        pDialog.show();
        getakadList();
        spnJenis = findViewById(R.id.jenis_tabungan);
        spnJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(HutangMurobahahActivity.this, "Silahkan"+position, Toast.LENGTH_LONG).show();
                if(position>0){
                    String jenis = kodeList.get(position).toString();
                    String fixJenis = jenis.replace("/","kk2019");
                    pDialog2 = new ProgressDialog(TabunganActivity.this);
                    pDialog2.setCancelable(false);
                    pDialog2.setMessage("Memuat data ....");
                    pDialog2.show();
                    getSaldoList(fixJenis);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void getakadList(){
        final String urll ="https://yayasansehatmadanielarbah.com/api-siskopsya/saldo/tabungan.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urll + "");
        jenisList = new ArrayList<>();
        kodeList = new ArrayList<>();
        jenisList.add("Pilih Jenis Tabungan");
        kodeList.add("Pilh Kode Tabungan");
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                urll, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TabunganActivity.class.getSimpleName(), "Auth Response: " +urll+ response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("content");
                    JSONArray jTotal = jsonObject.getJSONArray("total");
                    JSONArray jNoAkad = jsonObject.getJSONArray("jenis_tabungan");
                    if(jsonObject.getString("data").equals("no data")){
                        //lyNoData.setVisibility(View.VISIBLE);
                        //lyData.setVisibility(View.GONE);
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }else{
                        for(int a=0;a<jNoAkad.length();a++){
                            JSONObject oNoAkad=jNoAkad.getJSONObject(a);
                            tJenis = oNoAkad.getString("jenis_tabungan");
                            tKode = oNoAkad.getString("kode_simpanan");
                            jenisList.add(tJenis);
                            kodeList.add(tKode);
                        }
                        for(int i=0;i<jArray.length();i++){
                            JSONObject jsonObject1=jArray.getJSONObject(i);
                            tNoAnggota =jsonObject1.getString("no_anggota");
                            tNamaAnggota =jsonObject1.getString("nama_anggota");
                            tTgLGabung= jsonObject1.getString("tgl_gabung");
                        }
                        tTotalSaldo = jsonObject.getString("total_saldo");
                        //lyNoData.setVisibility(View.GONE);
                        //lyData.setVisibility(View.VISIBLE);
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
                        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HutangMurobahahActivity.this, android.R.layout.simple_spinner_item, noAkadList);
                        // Drop down layout style - list view with radio button
                        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Initializing an ArrayAdapter
                        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                TabunganActivity.this,R.layout.support_simple_spinner_dropdown_item,jenisList){
                            @Override
                            public boolean isEnabled(int position){
                                if(position == 0)
                                {
                                    // Disable the second item from Spinner
                                    return false;
                                }
                                else
                                {
                                    return true;
                                }
                            }

                            @Override
                            public View getDropDownView(int position, View convertView,
                                                        ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if(position==1) {
                                    // Set the disable item text color
                                    tv.setTextColor(Color.GRAY);
                                }
                                else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return view;
                            }
                        };
                        // attaching data adapter to spinner
                        spnJenis.setAdapter(spinnerArrayAdapter);
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
    private void getSaldoList(String no_kontrak){
        final String urlsaldo ="https://yayasansehatmadanielarbah.com/api-siskopsya/saldo/tabunganSaldo.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota+"&&no_kontrak="+no_kontrak;
        RequestQueue requestQueue2= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urlsaldo + "");
        StringRequest stringRequest2=new StringRequest(Request.Method.GET,
                urlsaldo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response2) {
                Log.e(TabunganActivity.class.getSimpleName(), "Auth Response: " +urlsaldo+ response2);
                try{
                    JSONObject jsonObject2=new JSONObject(response2);
                    JSONArray jArray2 = jsonObject2.getJSONArray("content");
                    if(jsonObject2.getString("data").equals("no data")){
                        lyNoData.setVisibility(View.VISIBLE);
                        lyData.setVisibility(View.GONE);
                        if(pDialog2.isShowing()){
                            pDialog2.dismiss();
                        }
                    }else{
                        for(int ix=0;ix<jArray2.length();ix++){
                            JSONObject jsonObject12=jArray2.getJSONObject(ix);
                            tDebit=jsonObject12.getString("debit");
                            tKredit=jsonObject12.getString("kredit");
                            tSaldo= jsonObject12.getString("saldo");
                        }
                        lyNoData.setVisibility(View.GONE);
                        lyData.setVisibility(View.VISIBLE);
                        //format harga
                        DecimalFormatSymbols symbolsz = new DecimalFormatSymbols();
                        symbolsz.setGroupingSeparator('.');
                        symbolsz.setDecimalSeparator(',');
                        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbolsz);
                        //inisial format rupiah
                        String rpDebit = decimalFormat.format(Integer.parseInt(tDebit));
                        String rpKredit=decimalFormat.format(Integer.parseInt(tKredit));
                        String rpSaldo= decimalFormat.format(Integer.parseInt(tSaldo));
                        debit.setText(rpDebit);
                        kredit.setText(rpKredit);
                        saldo.setText(rpSaldo);
                        if(pDialog2.isShowing()){
                            pDialog2.dismiss();
                        }
                    }

                }catch (JSONException e){e.printStackTrace(); }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if(pDialog2.isShowing()){
                    pDialog2.dismiss();
                }
                Toast.makeText(TabunganActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout2 = 30000;
        RetryPolicy policy2 = new DefaultRetryPolicy(socketTimeout2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest2.setRetryPolicy(policy2);
        requestQueue2.add(stringRequest2);
    }
}
