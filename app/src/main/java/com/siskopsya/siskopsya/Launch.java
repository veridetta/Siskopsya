package com.siskopsya.siskopsya;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.siskopsya.siskopsya.menu.SimpananActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Launch extends AppCompatActivity {
    int login = 0;
    ProgressDialog pDialog;
    final Handler handler = new Handler();
    SharedPreferences sharedpreferences;
    String kode_login, no_anggota, kode_login_update, no_token;
    Boolean session=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        sharedpreferences = getSharedPreferences("siskopsya", Context.MODE_PRIVATE);
        no_anggota = sharedpreferences.getString("no_anggota", "0");
        kode_login = sharedpreferences.getString("kode_login",null);

        session = sharedpreferences.getBoolean("session_status", false);
        pDialog = new ProgressDialog(Launch.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("memvalidasi data, mungkin membutuhkan waktu yang sedikit lebih lama.");
        pDialog.show();

        final ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                no_token = sharedpreferences.getString("token","baru");
                if(no_token.equals("baru")){
                    //Intent belumLogin = new Intent(Launch.this, LoginActivity.class);
                    //belumLogin.putExtra("CEK_LOGIN", "baru");
                    //startActivity(belumLogin);
                }else{
                    scheduleTaskExecutor.shutdown();
                    cekdata();
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 2s = 5000ms
                if(session){

                    Intent sudahLogin = new Intent(Launch.this, MainActivity.class);
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    Toast.makeText(Launch.this, "Selamat Datang Kembali",
                            Toast.LENGTH_LONG).show();
                    startActivity(sudahLogin);
                }else{
                    Intent belumLogin = new Intent(Launch.this, LoginActivity.class);
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    Toast.makeText(Launch.this, "Harap Login Dahulu!",
                            Toast.LENGTH_LONG).show();
                    startActivity(belumLogin);
                }
            }
        }, 3500);*/
    }
    private void cekdata(){
        final String urll ="https://yayasansehatmadanielarbah.com/api-siskopsya/cek_login.php?auth=c2lza29wc3lhOnNpc2tvcHN5YTEyMw==&&no_anggota="+no_anggota+"&&no_token="+no_token;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        Log.wtf("URL Called", urll + "");
        StringRequest stringRequest=new StringRequest(Request.Method.GET,
                urll, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(SimpananActivity.class.getSimpleName(), "Auth Response: " +urll+ response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("data").equals("no data")){
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                        Intent belumLogin = new Intent(Launch.this, LoginActivity.class);
                        //intent.putExtra(EXTRA_MESSAGE, message);
                        belumLogin.putExtra("CEK_LOGIN", "tidak");
                        Toast.makeText(Launch.this, "Harap Login Dahulu!",
                                Toast.LENGTH_LONG).show();
                        startActivity(belumLogin);
                    }else{
                        kode_login_update = jsonObject.getString("kode_login");
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                        if(session){
                            if(kode_login.equals(kode_login_update)){
                                Intent sudahLogin = new Intent(Launch.this, MainActivity.class);
                                //intent.putExtra(EXTRA_MESSAGE, message);
                                Toast.makeText(Launch.this, "Selamat Datang Kembali",
                                        Toast.LENGTH_LONG).show();
                                startActivity(sudahLogin);
                            }else{
                                Intent belumLogin = new Intent(Launch.this, LoginActivity.class);
                                belumLogin.putExtra("CEK_LOGIN", "tidak");
                                Toast.makeText(Launch.this, "Harap Login Dahulu!",
                                        Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putBoolean("session_status", true);
                                editor.putString("no_anggota", "");
                                editor.putString("nama_lengkap", "");
                                editor.putString("kode_login", "");
                                startActivity(belumLogin);
                            }
                        }else{
                            Intent belumLogin = new Intent(Launch.this, LoginActivity.class);
                            //intent.putExtra(EXTRA_MESSAGE, message);
                            belumLogin.putExtra("CEK_LOGIN", "tidak");
                            Toast.makeText(Launch.this, "Harap Login Dahulu!",
                                    Toast.LENGTH_LONG).show();
                            startActivity(belumLogin);
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
                Toast.makeText(Launch.this, "Silahkan coba lagi", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

}
