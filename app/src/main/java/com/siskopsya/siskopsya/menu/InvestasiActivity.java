package com.siskopsya.siskopsya.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class InvestasiActivity extends AppCompatActivity {
    private ArrayList<String> tahunList, bulanList;
    //Dialog dialog;
    ProgressDialog pDialog, pDialog2;
    //TextView totalD, totalR, tidak;
    String tKodeProjek, tAkadaAwal, tNilaiProjek, tPorsiModal, no_anggota,
            tTahun, tBulan,
            tOmset, tBiaya, tLaba, tNisbah,
            tOmsetSum, tBiayaSum, tLabaSum, tNisbahSum, db;
    TextView KodeProjek, AkadaAwal, NilaiProjek, PorsiModal,
            Omset, Biaya, Laba, Nisbah,
            OmsetSum, BiayaSum, LabaSum, NisbahSum;
    Spinner spnTahun, spnBulan;
    //LinearLayout lyNoData, lyData;
    SharedPreferences sharedpreferences;
    Integer tahun=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investasi);
        sharedpreferences = getSharedPreferences("siskopsya", Context.MODE_PRIVATE);
        no_anggota = sharedpreferences.getString("no_anggota", null);
        db=sharedpreferences.getString("db", null);
        KodeProjek = findViewById(R.id.code_project);
        AkadaAwal = findViewById(R.id.akad_awal);
        NilaiProjek = findViewById(R.id.nilai_project);
        PorsiModal = findViewById(R.id.porsi_modal);
        Omset = findViewById(R.id.omset);
        Biaya = findViewById(R.id.biaya);
        Laba = findViewById(R.id.laba);
        Nisbah = findViewById(R.id.nisbah_sum);
        OmsetSum = findViewById(R.id.omset_sum);
        BiayaSum = findViewById(R.id.biaya_sum);
        LabaSum = findViewById(R.id.laba_sum);
        NisbahSum = findViewById(R.id.nisbah_sum);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Investasi");
        pDialog = new ProgressDialog(InvestasiActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memuat data ....");
        pDialog.show();
        getTahunList();
        spnTahun = findViewById(R.id.tahun_bagi);
        spnBulan = findViewById(R.id.bulan);
        spnTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(HutangMurobahahActivity.this, "Silahkan"+position, Toast.LENGTH_LONG).show();
                if(position>0){
                    tahun=1;
                    if(tahun>0){
                        bulanList = new ArrayList<>();
                        bulanList.add("Januari");
                        bulanList.add("Februari");
                        bulanList.add("Maret");
                        bulanList.add("April");
                        bulanList.add("Mei");
                        bulanList.add("Juni");
                        bulanList.add("Juli");
                        bulanList.add("Agustus");
                        bulanList.add("September");
                        bulanList.add("Oktober");
                        bulanList.add("November");
                        bulanList.add("Desember");
                        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                InvestasiActivity.this,R.layout.support_simple_spinner_dropdown_item,bulanList){
                        };
                        // attaching data adapter to spinner
                        spnBulan.setAdapter(spinnerArrayAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String strTahun = spnTahun.getSelectedItem().toString();
                    String strBulan = spnBulan.getSelectedItem().toString();
                    pDialog2 = new ProgressDialog(InvestasiActivity.this);
                    pDialog2.setCancelable(false);
                    pDialog2.setMessage("Memuat data ....");
                    pDialog2.show();
                    getSaldoList(strTahun, strBulan, tKodeProjek, no_anggota);
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
    private void getTahunList(){
        final String urll ="https://yayasansehatmadanielarbah.com/api-siskopsya/saldo/investasi.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota+"&&db="+db;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urll + "");
        tahunList = new ArrayList<>();
        tahunList.add("Pilih Tahun");
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                urll, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(MainActivity.class.getSimpleName(), "Auth Response: " +urll+ response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jArray = jsonObject.getJSONArray("content");
                    JSONArray jTotal = jsonObject.getJSONArray("total");
                    JSONArray jNoAkad = jsonObject.getJSONArray("tahun");
                    if(jsonObject.getString("data").equals("no data")){
                        //lyNoData.setVisibility(View.VISIBLE);
                        //lyData.setVisibility(View.GONE);
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }else{
                        for(int a=0;a<jNoAkad.length();a++){
                            JSONObject oNoAkad=jNoAkad.getJSONObject(a);
                            tTahun = oNoAkad.getString("tahun");
                            tahunList.add(tTahun);
                        }
                        for(int i=0;i<jArray.length();i++){
                            JSONObject jsonObject1=jArray.getJSONObject(i);
                            tKodeProjek=jsonObject1.getString("kode_projek");
                            tAkadaAwal =jsonObject1.getString("akad_awal");
                            tNilaiProjek= jsonObject1.getString("nilai_projek");
                            tPorsiModal= jsonObject1.getString("porsi_modal");
                        }
                        //lyNoData.setVisibility(View.GONE);
                        //lyData.setVisibility(View.VISIBLE);
                        //totalD.setText(txtTotalD);
                        //format harga
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                        symbols.setGroupingSeparator('.');
                        symbols.setDecimalSeparator(',');
                        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbols);
                        //inisial format rupiah
                        if(TextUtils.isEmpty(tNilaiProjek)){
                            tNilaiProjek="0";
                        }else{

                        }
                        if(TextUtils.isEmpty(tPorsiModal)){
                            tPorsiModal="0";
                        }else{

                        }
                        String rpNilaiProjek = decimalFormat.format(Integer.parseInt(tNilaiProjek));
                        String rpPorsimodal = decimalFormat.format(Integer.parseInt(tPorsiModal));
                        KodeProjek.setText(tKodeProjek);
                        AkadaAwal.setText(tAkadaAwal);
                        NilaiProjek.setText(rpNilaiProjek);
                        PorsiModal.setText(rpPorsimodal);
                        // Creating adapter for spinner
                        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(HutangMurobahahActivity.this, android.R.layout.simple_spinner_item, noAkadList);
                        // Drop down layout style - list view with radio button
                        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Initializing an ArrayAdapter
                        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                InvestasiActivity.this,R.layout.support_simple_spinner_dropdown_item,tahunList){
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
                        spnTahun.setAdapter(spinnerArrayAdapter);
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
                Toast.makeText(InvestasiActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
    private void getSaldoList(String tahunStr, String bulanStr, String kodeStr, String noAng){
        final String urlsaldo ="https://yayasansehatmadanielarbah.com/api-siskopsya/saldo/investasiSaldo.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota+"&&tahun="+tahunStr+"&&bulan="+bulanStr+"&&kode="+kodeStr+"&&no_anggota="+noAng+"&&db="+db;
        RequestQueue requestQueue2= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urlsaldo + "");
        StringRequest stringRequest2=new StringRequest(Request.Method.GET,
                urlsaldo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response2) {
                Log.e(InvestasiActivity.class.getSimpleName(), "Auth Response: " +urlsaldo+ response2);
                try{
                    JSONObject jsonObject2=new JSONObject(response2);
                    JSONArray jArray2 = jsonObject2.getJSONArray("content");
                    if(jsonObject2.getString("data").equals("no data")){

                        if(pDialog2.isShowing()){
                            pDialog2.dismiss();
                        }
                    }else{
                        for(int ix=0;ix<jArray2.length();ix++){
                            JSONObject jsonObject12=jArray2.getJSONObject(ix);
                            tOmset =jsonObject12.getString("omset");
                            tBiaya=jsonObject12.getString("biaya");
                            tLaba =jsonObject12.getString("laba");
                            tNisbah= jsonObject12.getString("nisbah");
                            tOmsetSum =jsonObject12.getString("omset_sum");
                            tBiayaSum=jsonObject12.getString("biaya_sum");
                            tLabaSum=jsonObject12.getString("laba_sum");
                            tNisbahSum= jsonObject12.getString("nisbah_sum");
                        }
                        //format harga
                        DecimalFormatSymbols symbolsz = new DecimalFormatSymbols();
                        symbolsz.setGroupingSeparator('.');
                        symbolsz.setDecimalSeparator(',');
                        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbolsz);
                        //inisial format rupiah
                        String rpOmset = decimalFormat.format(Integer.parseInt(tOmset));
                        String rpBiaya=decimalFormat.format(Integer.parseInt(tBiaya));
                        String rpLaba = decimalFormat.format(Integer.parseInt(tLaba));
                        String rpNisbah = decimalFormat.format(Integer.parseInt(tNisbah));
                        String rpOmsetSum = decimalFormat.format(Integer.parseInt(tOmsetSum));
                        String rpBiayaSum=decimalFormat.format(Integer.parseInt(tBiayaSum));
                        String rpLabaSum = decimalFormat.format(Integer.parseInt(tLabaSum));
                        String rpNisbahSum = decimalFormat.format(Integer.parseInt(tNisbahSum));
                        Omset.setText(rpOmset);
                        Biaya.setText(rpBiaya);
                        Laba.setText(rpLaba);
                        Nisbah.setText(rpNisbah);
                        OmsetSum.setText(rpOmsetSum);
                        BiayaSum.setText(rpBiayaSum);
                        LabaSum.setText(rpLabaSum);
                        NisbahSum.setText(rpNisbahSum);
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
                Toast.makeText(InvestasiActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout2 = 30000;
        RetryPolicy policy2 = new DefaultRetryPolicy(socketTimeout2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest2.setRetryPolicy(policy2);
        requestQueue2.add(stringRequest2);
    }
}
