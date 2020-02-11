package com.imam.biancamobile;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.imam.biancamobile.server.AppController;
import com.imam.biancamobile.server.Config_URL;
import com.imam.biancamobile.session.SessionManager;

import com.android.volley.DefaultRetryPolicy;
import android.support.design.widget.Snackbar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {


    private static final String TAG = Login.class.getSimpleName();

    private ProgressDialog pDialog;
    private SessionManager session;
    SharedPreferences prefs;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);


    @BindView(R.id.inputNPM)
    EditText edNPM;

    @BindView(R.id.inputPasswordLogin)
    EditText edPassword;

    @BindView(R.id.ly)
    CoordinatorLayout layout;

    String email;
    String nama;
    String password;
    String npm;
    String prodi;
    String id;
    String Hp;

    private int REQUEST_MICROPHONE = 1 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        ButterKnife.bind(this);

        checkPermission();

        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        id  = prefs.getString("Id","");
        npm  = prefs.getString("username", "");
        nama  = prefs.getString("nmlengkap", "");
        email  = prefs.getString("email","");
        Hp  = prefs.getString("tlp", "");
      //  prodi  = prefs.getString("Prodi", "");

        if(session.isLoggedIn()){
            Intent a = new Intent(getApplicationContext(), Main2Activity.class);
            a.putExtra("id", id);
            a.putExtra("npm", npm);
            a.putExtra("nama", nama);
            a.putExtra("email", email);
            a.putExtra("hp", Hp);
           // a.putExtra("prodi", prodi);

            startActivity(a);
            finish();
        }


    }




    @OnClick(R.id.btnLogin)
    void login(){
        String Npm = edNPM.getText().toString();
        String Pass  = edPassword.getText().toString();

        if (Npm.isEmpty() || Pass.isEmpty()){
            snacBars("Data tidak boleh kosong");
        }else{
            checkLogin(Npm,Pass);
        }

    }


    public void checkLogin(final String NPM, final String PASSWORD){



        //Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Login, Please Wait.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject result = jObj.getJSONObject("results");

                    boolean status = result.getBoolean("status");

                    if(status == true){

                        JSONObject user     = result.getJSONObject("user");
                        id           = user.getString("Id");
                        npm     = user.getString("username");
                        nama    = user.getString("nmlengkap");
                        email        = user.getString("email");
                        Hp     = user.getString("tlp");
                      //  prodi    = user.getString("Prodi");
                        String msg          = result.getString("msg");

                       snacBarsGreen(msg);

                        session.setLogin(true);
                        storeRegIdinSharedPref(getApplicationContext(),id,NPM, nama, email,Hp);
                        Intent a = new Intent(getApplicationContext(), Main2Activity.class);
                        a.putExtra("Id", id);
                        a.putExtra("username", NPM);
                        a.putExtra("nmlengkap", nama);
                        a.putExtra("tlp", Hp);
                        a.putExtra("email", email);
                     //   a.putExtra("Prodi", prodi);
                        startActivity(a);
                        finish();
                        //Create login Session

                    }else {
                        String error_msg = result.getString("msg");
                        snacBars(error_msg);

                    }

                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG, "Login Error : " + error.getMessage());
                error.printStackTrace();
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("NPM", NPM);
                params.put("PASSWORD", PASSWORD);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void storeRegIdinSharedPref(Context context,String iduser,String NPM, String Nama, String EMAIL,String Hp) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Id", iduser);
        editor.putString("username", NPM);
        editor.putString("nmlengkap", Nama);
        editor.putString("email", EMAIL);
        editor.putString("tlp", Hp);
     //   editor.putString("Prodi", prodi);
        editor.commit();
    }


    public void snacBars(String text){
        Snackbar snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setBackgroundColor(layout.getResources().getColor(R.color.colorAccent));
        view.setLayoutParams(params);
        snackbar.show();
    }

    public void snacBarsGreen(String text){
        Snackbar snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setBackgroundColor(layout.getResources().getColor(R.color.green));
        view.setLayoutParams(params);
        snackbar.show();
    }


    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(Login.this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(Login.this, "You have already granted this permission", Toast.LENGTH_SHORT).show();


        }else {
            requestMichrophone();
        }
    }
    public void requestMichrophone(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This Permission is needed because of this and that")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MICROPHONE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MICROPHONE);
        }
    }



    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setMessage("Apakah anda ingin keluar dari App?");
        builder.setCancelable(true);
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //   Appodeal.isLoaded(Appodeal.INTERSTITIAL);
               // startAppAd.onBackPressed();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
