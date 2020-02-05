package com.siskopsya.siskopsya.setting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.siskopsya.siskopsya.LoginActivity;
import com.siskopsya.siskopsya.MainActivity;
import com.siskopsya.siskopsya.R;
import com.siskopsya.siskopsya.app.AppController;
import com.siskopsya.siskopsya.server.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    EditText pass_lama, pass_baru, konfrim_pass;
    Button btn_konfirm;
    String no_anggota,db;
    ConnectivityManager conMgr;
    SharedPreferences sharedpreferences;
    ProgressDialog pDialog;
    String TAG = LoginActivity.class.getSimpleName();
    int success;
    private String url = Url.URL + "change_pass.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        pass_lama=findViewById(R.id.pass_lama);
        pass_baru=findViewById(R.id.pass_baru);
        konfrim_pass=findViewById(R.id.konfir_baru);
        btn_konfirm=findViewById(R.id.btn_ubah);
        sharedpreferences = getSharedPreferences("siskopsya", Context.MODE_PRIVATE);
        no_anggota = sharedpreferences.getString("no_anggota", null);
        db=sharedpreferences.getString("db", null);
        //title bar
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Pengaturan Akun");
        btn_konfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ----------- CEK YANG MASIH KOSONG ------
                if (pass_lama.getText().toString().trim().length() > 0 && pass_baru.getText().toString().trim().length() > 8 && konfrim_pass.getText().toString().trim().length() > 8) {
                    if(pass_baru.getText().toString().equals(konfrim_pass.getText().toString())){
                        if (conMgr.getActiveNetworkInfo() != null
                                && conMgr.getActiveNetworkInfo().isAvailable()
                                && conMgr.getActiveNetworkInfo().isConnected()) {
                            //checkLogin(edit_email.getText().toString(), edit_password.getText().toString());
                        } else {
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Password Tidak Sama", Toast.LENGTH_LONG).show();
                    }

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Minimal 8 Karakter", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    // ------ FUNCTION CEK LOGIN ---------------
    private void checkLogin(final String email, final String password) {
        pDialog = new ProgressDialog(SettingActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Mengubah Password...");
        pDialog.show();
        Log.e(TAG, "Response: "+url);
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response string: " + response.toString());
                pDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt("success");
                    String no_anggota = jObj.getString("no_anggota");
                    String nama_lengkap = jObj.getString("nama_lengkap");
                    String kode_login =jObj.getString("kode_login");
                    String db =jObj.getString("db");
                    // Check for error node in json
                    if (success == 1) {
                        Log.e("Berhasil Diganti!", jObj.toString());
                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("session_status", false);
                        editor.commit();
                        Toast.makeText(getApplicationContext(),
                                "Berhasil, Silahkan login ulang menggunakan password baru anda. ", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        intent.putExtra("CEK_LOGIN", "baru");
                        finish();
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    Log.wtf(TAG, e.toString());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("no_anggota", no_anggota);
                params.put("pass_lama", pass_lama.getText().toString());
                params.put("pass_baru", pass_baru.getText().toString());
                params.put("db", db);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "json_obj_req");
    }
    /// -----------------
}
