package com.imam.biancamobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.imam.biancamobile.server.AppController;
import com.imam.biancamobile.server.Config_URL;
import com.imam.biancamobile.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Registrasi extends AppCompatActivity {


    private static final String TAG = Login.class.getSimpleName();

    private ProgressDialog pDialog;
    private SessionManager session;
    SharedPreferences prefs;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);



    @BindView(R.id.inputNPMregister)
    EditText inputNPM;

    @BindView(R.id.inputEmail)
    EditText inputEmail;

    @BindView(R.id.inputPasswordRegister)
    EditText inputPasswordRegister;

    @BindView(R.id.inputHP)
    EditText inputHP;

    @BindView(R.id.inputNama)
    EditText inputNama;

    @BindView(R.id.inputPasswordRegister2)
    EditText inputPasswordRegister2;

    @BindView(R.id.inputProdi)
    EditText inputProdi;

//    @BindView(R.id.btnRegister)
//    Button btnRegister;

    String id;

    @BindView(R.id.daftar)
    CoordinatorLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);
        ButterKnife.bind(this);




        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());


    }


    @OnClick(R.id.btnRegister)
    void registrasi(){

        String email = inputEmail.getText().toString();
        String NPM = inputNPM.getText().toString();
        String Password = inputPasswordRegister.getText().toString();
        String Telepon  = inputHP.getText().toString();

        String Password2 = inputPasswordRegister2.getText().toString();
        String prodi = inputProdi.getText().toString();
        String nama = inputNama.getText().toString();


        if (email.isEmpty() || NPM.isEmpty() || Password.isEmpty() || Telepon.isEmpty() || Password2.isEmpty()
                || prodi.isEmpty() || nama.isEmpty()){
          snacBars("Data Harus Lengkap");
        }else {

            if (!Password.equals(Password2)) {
                snacBars("Password Tidak Sama");
            } else {
                daftar(NPM, nama, Password2, email,Telepon, prodi);
            }
        }
    }



    public void daftar(final String NPM, final String  NAMA, final String PASS, final String EMAIL, final String TELP,final String PRODI ){

        //Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Register, Please Wait.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.registerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject result = jObj.getJSONObject("results");

                    boolean status = result.getBoolean("status");

                    if(status == true){

                        String msg          = result.getString("msg");
                       snacBarsGreen(msg);
                        session.setLogin(true);
                        storeRegIdinSharedPref(getApplicationContext(),id,NPM,NAMA,EMAIL,TELP,PRODI);
                        Intent a = new Intent(getApplicationContext(), Main2Activity.class);
                        a.putExtra("id", id);
                        a.putExtra("NPM", NPM);
                        a.putExtra("Nama", NAMA);
                        a.putExtra("Email", EMAIL);
                        a.putExtra("NoHP", TELP);
                        a.putExtra("Prodi", PRODI);
                        startActivity(a);
                        finish();

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
                params.put("NAMA", NAMA);
                params.put("PASSWORD", PASS);
                params.put("TELEPON", TELP);
                params.put("EMAIL", EMAIL);
                params.put("PRODI", PRODI);
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

    private void storeRegIdinSharedPref(Context context,String iduser,String NPM, String Nama, String EMAIL,String Hp,String prodi) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", iduser);
        editor.putString("NPM", NPM);
        editor.putString("Nama", Nama);
        editor.putString("Email", EMAIL);
        editor.putString("NoHp", Hp);
        editor.putString("Prodi", prodi);
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


}
