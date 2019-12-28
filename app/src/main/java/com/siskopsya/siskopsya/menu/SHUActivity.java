package com.siskopsya.siskopsya.menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.siskopsya.siskopsya.MainActivity;
import com.siskopsya.siskopsya.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class SHUActivity extends AppCompatActivity {
    private ArrayList<String> tahunList;
    //Dialog dialog;
    ProgressDialog pDialog, pDialog2;
    //TextView totalD, totalR, tidak;
    String tNoAnggota, tNamaAnggota, tTgLGabung, tTotalSaldo, no_anggota,
            tTahun, tJanuari, tFebruari, tMaret, tApril, tMei, tJuni, tJuli,
            tAgustus, tSeptember, tOktober, tNovember, tDesember;
    TextView noAnggota, namaAnggota, tglGabung, totalSaldo,
            Januari, Februari, Maret, April, Mei, Juni, Juli,
            Agustus, September, Oktober, November, Desember;
    Spinner spnTahun;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shu);
        sharedpreferences = getSharedPreferences("siskopsya", Context.MODE_PRIVATE);
        no_anggota = sharedpreferences.getString("no_anggota", null);
        noAnggota = findViewById(R.id.no_anggota);
        namaAnggota = findViewById(R.id.nama_anggota);
        tglGabung = findViewById(R.id.tgl_gabung);
        totalSaldo = findViewById(R.id.total_saldo);
        Januari = findViewById(R.id.januari);
        Februari= findViewById(R.id.februari);
        Maret= findViewById(R.id.maret);
        April= findViewById(R.id.april);
        Mei = findViewById(R.id.mei);
        Juni= findViewById(R.id.juni);
        Juli= findViewById(R.id.juli);
        Agustus= findViewById(R.id.agustus);
        September= findViewById(R.id.september);
        Oktober= findViewById(R.id.oktober);
        November= findViewById(R.id.november);
        Desember= findViewById(R.id.desember);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("SHU Anggota");
        pDialog = new ProgressDialog(SHUActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memuat data ....");
        pDialog.show();
        getakadList();
        spnTahun = findViewById(R.id.tahun_berjalan);
        spnTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(HutangMurobahahActivity.this, "Silahkan"+position, Toast.LENGTH_LONG).show();
                if(position>0){
                    String tahunKe = spnTahun.getSelectedItem().toString();
                    String fiXtahunKe = tahunKe.replace("/","kk2019");
                    pDialog2 = new ProgressDialog(SHUActivity.this);
                    pDialog2.setCancelable(false);
                    pDialog2.setMessage("Memuat data ....");
                    pDialog2.show();
                    getSaldoList(fiXtahunKe);
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
        final String urll ="https://yayasansehatmadanielarbah.com/api-siskopsya/saldo/shu.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urll + "");
        tahunList= new ArrayList<>();
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
                                SHUActivity.this,R.layout.support_simple_spinner_dropdown_item,tahunList){
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
                Toast.makeText(SHUActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
    private void getSaldoList(String no_kontrak){
        final String urlsaldo ="https://yayasansehatmadanielarbah.com/api-siskopsya/saldo/shuSaldo.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota+"&&no_kontrak="+no_kontrak;
        RequestQueue requestQueue2= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urlsaldo + "");
        StringRequest stringRequest2=new StringRequest(Request.Method.GET,
                urlsaldo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response2) {
                Log.e(HutangMurobahahActivity.class.getSimpleName(), "Auth Response: " +urlsaldo+ response2);
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
                            tJanuari =jsonObject12.getString("januari");
                            tFebruari=jsonObject12.getString("februari");
                            tMaret=jsonObject12.getString("maret");
                            tApril=jsonObject12.getString("april");
                            tMei=jsonObject12.getString("mei");
                            tJuni=jsonObject12.getString("juni");
                            tJuli=jsonObject12.getString("juli");
                            tAgustus=jsonObject12.getString("agustus");
                            tSeptember =jsonObject12.getString("september");
                            tOktober=jsonObject12.getString("oktober");
                            tNovember=jsonObject12.getString("november");
                            tDesember=jsonObject12.getString("desember");
                        }
                        //format harga
                        DecimalFormatSymbols symbolsz = new DecimalFormatSymbols();
                        symbolsz.setGroupingSeparator('.');
                        symbolsz.setDecimalSeparator(',');
                        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###", symbolsz);
                        //inisial format rupiah
                        String rpJanuari = decimalFormat.format(Integer.parseInt(tJanuari));
                        String rpFebruari =decimalFormat.format(Integer.parseInt(tFebruari));
                        String rpMaret = decimalFormat.format(Integer.parseInt(tMaret));
                        String rpApril = decimalFormat.format(Integer.parseInt(tApril));
                        String rpMei = decimalFormat.format(Integer.parseInt(tMei));
                        String rpJuni  = decimalFormat.format(Integer.parseInt(tJuni));
                        String rpJuli = decimalFormat.format(Integer.parseInt(tJuli));
                        String rpAgustus = decimalFormat.format(Integer.parseInt(tAgustus));
                        String rpSeptember  = decimalFormat.format(Integer.parseInt(tSeptember));
                        String rpOktober = decimalFormat.format(Integer.parseInt(tOktober));
                        String rpNovember = decimalFormat.format(Integer.parseInt(tNovember));
                        String rpDesember = decimalFormat.format(Integer.parseInt(tDesember));
                        Januari.setText(rpJanuari);
                        Februari.setText(rpFebruari);
                        Maret.setText(rpMaret);
                        April.setText(rpApril);
                        Mei.setText(rpMei);
                        Juni.setText(rpJuni);
                        Juli.setText(rpJuli);
                        Agustus.setText(rpAgustus);
                        September.setText(rpSeptember);
                        Oktober.setText(rpOktober);
                        November.setText(rpNovember);
                        Desember.setText(rpDesember);
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
                Toast.makeText(SHUActivity.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout2 = 30000;
        RetryPolicy policy2 = new DefaultRetryPolicy(socketTimeout2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest2.setRetryPolicy(policy2);
        requestQueue2.add(stringRequest2);
    }
}
