package com.imam.biancamobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class profile extends AppCompatActivity {

    private SessionManager session;
    private ProgressDialog pDialog;
    SharedPreferences prefs;
    private Context context;

    private static final String TAG = Login.class.getSimpleName();

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    @BindView(R.id.NPMProfile)
    EditText NPMProfile;

    @BindView(R.id.EmailProfile)
    EditText EmailProfile;

    @BindView(R.id.PassProfile)
    EditText PassProfile;

    @BindView(R.id.HPProfile)
    EditText HPProfile;

    @BindView(R.id.NamaProfile)
    EditText NamaProfile;

    @BindView(R.id.PassProfile2)
    EditText PassProfile2;

    @BindView(R.id.ProdiProfile)
    EditText ProdiProfile;


    @BindView(R.id.txtpassbaru)
    TextView txtpassbaru;

    @BindView(R.id.txtpassbaru2)
    TextView txtpassbaru2;

    @BindView(R.id.showpassbaru)
    TextInputLayout showpassbaru;

    @BindView(R.id.showpassbaru2)
    TextInputLayout showpassbaru2;

    @BindView(R.id.simpanProfile)
    Button simpanProfile;

    @BindView(R.id.editProfile)
    Button EDITbtn;


    @BindView(R.id.layout)
    CoordinatorLayout layout;

    String id;
//
//    @BindView(R.id.keluar)
//    TextView keluar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("   Update Profile");
        ButterKnife.bind(this);




        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        //Session Login
        if(session.isLoggedIn()) {
            prefs = getSharedPreferences("UserDetails",
                    Context.MODE_PRIVATE);
         String   id  = prefs.getString("id","");
            String   npm  = prefs.getString("NPM", "");
            String nama  = prefs.getString("Nama", "");
            String email  = prefs.getString("Email","");
            String Hp  = prefs.getString("NoHp", "");
            String prodi  = prefs.getString("Prodi", "");

            NPMProfile.setText(npm);
            NamaProfile.setText(nama);
            EmailProfile.setText(email);
            HPProfile.setText(Hp);
            ProdiProfile.setText(prodi);


        }



    }

    @OnClick(R.id.editProfile)
    void editbtn() {

        EmailProfile.setEnabled(true);
        HPProfile.setEnabled(true);
        PassProfile.setVisibility(View.VISIBLE);
        PassProfile2.setVisibility(View.VISIBLE);
        simpanProfile.setVisibility(View.VISIBLE);
        txtpassbaru.setVisibility(View.VISIBLE);
        txtpassbaru2.setVisibility(View.VISIBLE);
        showpassbaru.setVisibility(View.VISIBLE);
        showpassbaru2.setVisibility(View.VISIBLE);
        EDITbtn.setVisibility(View.INVISIBLE);

    }

    @OnClick(R.id.simpanProfile)
    void simpan() {

        String Email = EmailProfile.getText().toString();
        String NPM = NPMProfile.getText().toString();
        String Password = PassProfile.getText().toString();
        String Telepon  = HPProfile.getText().toString();

        String Password2 = PassProfile2.getText().toString();
        String prodi = ProdiProfile.getText().toString();
        String nama = NamaProfile.getText().toString();


        if (Email.isEmpty() || NPM.isEmpty() || Password.isEmpty() || Telepon.isEmpty() || Password2.isEmpty()
                || prodi.isEmpty() || nama.isEmpty()){
            snacBars("Data Harus Lengkap");
        }else {

            if (!Password.equals(Password2)) {
                snacBars("Password Tidak Sama");
            } else {
                simpanPerubahan(NPM, nama, Password2, Email, Telepon, prodi);
            }
        }

    }

    public void simpanPerubahan(final String npm, final String nama, final String password2, final String email
            , final String telepon,  final String prodi) {


        //Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Register, Please Wait.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.updateUser, new Response.Listener<String>() {
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
                        storeRegIdinSharedPref(getApplicationContext(),id,npm,nama,email,telepon,prodi);


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
                params.put("NPM", npm);
                params.put("NAMA", nama);
                params.put("PASSWORD", password2);
                params.put("TELEPON", telepon);
                params.put("EMAIL", email);
                params.put("PRODI", prodi);
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


    private void storeRegIdinSharedPref(Context context,String iduser ,String npm,String nama,   String email,String telp, String prodi) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", iduser);
        editor.putString("NPM", npm);
        editor.putString("Nama", nama);
        editor.putString("Email", email);
        editor.putString("NoHp", telp);
        editor.putString("Prodi", prodi);
        editor.commit();
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode ==REQUEST_MICROPHONE){
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
//                initializeTexToSpeech();
//                initializeSpeechRecognizer();
//            }else {
//                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}


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
