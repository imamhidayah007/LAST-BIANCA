package com.imam.biancamobile;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
//import com.imam.biancamobile.Adapter.AdapterJawaban;
//import com.imam.biancamobile.Data.dataJawaban;
import com.imam.biancamobile.server.AppController;
import com.imam.biancamobile.server.Config_URL;
import com.imam.biancamobile.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity {

    private SessionManager session;
    private ProgressDialog pDialog;
    SharedPreferences prefs;
    private Context context;
    private static final String TAG = Login.class.getSimpleName();
    private int REQUEST_MICROPHONE = 1 ;

//
//    ArrayList<dataJawaban> jawabnya = new ArrayList<dataJawaban>();
//
//    AdapterJawaban adapter;
//    ListView list;
//

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;
    private LinearLayout ly,answer;
  //  String sensorkotor;
    String Pertanyaan;
    @BindView(R.id.jw)
    CoordinatorLayout layout;

    @BindView(R.id.jawaban)
    TextView jawaban;

    @BindView(R.id.tgl)
    TextView tanggal;

    @BindView(R.id.welcome)
    TextView hallo;

    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("   Bianca Mobile");
        getSupportActionBar().setSubtitle("    Online");
        getSupportActionBar().setLogo(R.drawable.mini);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


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
            String   id  = prefs.getString("Id","");
            String   npm  = prefs.getString("username", "");
            String nama  = prefs.getString("nmlengkap", "");
            String email  = prefs.getString("email","");
            String Hp  = prefs.getString("tlp", "");
          //  String prodi  = prefs.getString("Prodi", "");


            hallo.setText("Hi "+nama+" Saya Bianca, Ada Yang Bisa Saya Bantu ?");

        }


        final Calendar c = Calendar.getInstance();
        int year, month, day;
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) +1;
        day = c.get(Calendar.DATE);


        if(month == 1){
            tanggal.setText(day+" Januari");
        }else  if(month == 2){
            tanggal.setText(day+" Februari");
        }else  if(month == 3){
            tanggal.setText(day+" Maret");
        }else  if(month == 4){
            tanggal.setText(day+" April");
        }else  if(month == 5){
            tanggal.setText(day+" Mei");
        }else  if(month == 6){
            tanggal.setText(day+" Juni");
        }else  if(month == 7){
            tanggal.setText(day+" Juli");
        }else  if(month == 8){
            tanggal.setText(day+" Agustus");
        }
        else  if(month == 9){
            tanggal.setText(day+" September");
        }else  if(month == 10){
            tanggal.setText(day+" Oktober");
        }else  if(month == 11){
            tanggal.setText(day+" November");
        }else {
            tanggal.setText(day+" Desember");
        }




        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                //method untuk mendeteksi suara dari text

                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());

                }
            }
        });





        ly = (LinearLayout) findViewById(R.id.qs) ;
        answer = (LinearLayout) findViewById(R.id.answer) ;
        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ly.setVisibility(View.INVISIBLE);
                answer.setVisibility(View.INVISIBLE);
                jawaban.setText("");
            String Stop = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(Stop,TextToSpeech.QUEUE_FLUSH,null,null);



                } else {
                    textToSpeech.speak(Stop, TextToSpeech.QUEUE_FLUSH, null);
                }


                startVoiceInput();

            }
        });


//
//        list = (ListView) findViewById(R.id.array_list);
//        jawabnya.clear();
//
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//
//
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // TODO Auto-generated method stub
//
///**
//
// Intent intent = getIntent();
// //  String IDP = intent.getStringExtra("id_pesan");
// //  Toast.makeText(getApplicationContext(), "Your ID " + idUSER, Toast.LENGTH_SHORT).show();
// intent.putExtra("id_pesan", dataNya.get(position).getIdPesan());
// String IDP = intent.getStringExtra("id_pesan");
// hapusData(IDP);
// **/
//            }
//
//
//
//
//        });
//
//        adapter = new AdapterJawaban(Main2Activity.this, jawabnya);
//        list.setAdapter(adapter);

    }


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, "id-ID");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, Saya Bianca, Ada Yang Bisa Saya Bantu?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    ly.setVisibility(View.VISIBLE);

                    //sensor disini

                        String Teks = result.get(0);
               //     String sensor = result.get(0).replaceAll(katanya() ,"sensorr");
                 //   mVoiceInputTv.setText(sensor);
                  ///  String[] sensorjorok = {"anjing", "babi"};

                    final   String sensor = Teks.replaceAll("anjing","sensorr");
                    mVoiceInputTv.setText(sensor);
                    Pertanyaan = mVoiceInputTv.getText().toString();
                    String   npm  = prefs.getString("username", "");

//                    String SubStr1 = new String("KRS");
//                    String SubStr2 = new String("KHS");


                    String[] Nilai = {"KHS", "khs", "KRS", "khs", "Transkrip", "TRANSKRIP", "transkrip"};
                    for (int i =0; i < Nilai.length; i++) {
                        int AN = Pertanyaan.indexOf(Nilai[i]);
                    }
                    int KHS = Pertanyaan.indexOf(Nilai[0]);
                    int khs = Pertanyaan.indexOf(Nilai[1]);

                    int KRS = Pertanyaan.indexOf(Nilai[2]);
                    int krs = Pertanyaan.indexOf(Nilai[3]);

                    int Transkip = Pertanyaan.indexOf(Nilai[4]);
                    int TRANSKIP = Pertanyaan.indexOf(Nilai[5]);
                    int transkip = Pertanyaan.indexOf(Nilai[6]);


                    String[] Kampus = {"UBL", "ubl", "Universitas Bandar Lampung", "universitas bandar lampung"};
                    int K1 = Pertanyaan.indexOf(Kampus[0]);
                    int K2 = Pertanyaan.indexOf(Kampus[1]);
                    int K3 = Pertanyaan.indexOf(Kampus[2]);
                    int K4 = Pertanyaan.indexOf(Kampus[3]);

                 if(KHS >= 0 || khs >= 0 || KRS >= 0 || krs >= 0 || TRANSKIP >= 0 || Transkip >= 0 || transkip >= 0){
                     String link = "http://ublapps.ubl.ac.id/adminapps/index.php/admin/cetaktranskriplist2/"+npm+"/";
                    String t = "Transkip Mahasiswa";
                     Intent p = new Intent(Main2Activity.this, webview.class);
                     p.putExtra("Title", t);
                     p.putExtra("link", link);
                     startActivity(p);

                 } else{

                     if(K1 >= 0 || K2 >= 0 || K3 >= 0 || K4 >= 0){
                         jawab(Pertanyaan,npm);
                     }else {

                         answer.setVisibility(View.VISIBLE);
                         jawaban.setText(" Mohon Maaf, Silakan Bertanya Mengenai Universitas Bandar Lampung");

                         String suara = jawaban.getText().toString();
                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                             textToSpeech.speak(suara, TextToSpeech.QUEUE_FLUSH, null, null);


                         } else {
                             textToSpeech.speak(suara, TextToSpeech.QUEUE_FLUSH, null);
                         }

                     }

                 }


                }
                break;
            }

        }
    }






//
//
//
//
//
//    public String katanya(){
//        //Tag used to cancel the request
//        String tag_string_req = "req";
//
//        pDialog.setMessage("Please Wait.....");
//        showDialog();
//
//        StringRequest strReq = new StringRequest(Request.Method.GET,
//                Config_URL.sensorkata, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "Login Response: " + response.toString());
//                hideDialog();
//
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    JSONObject result = jObj.getJSONObject("results");
//
//                    boolean status = result.getBoolean("status");
//
//                    if(status == true){
//                        JSONArray data     = result.getJSONArray("kata");
//                        for (int i = 0; i < data.length(); i++) {
//                            JSONObject obj = data.getJSONObject(i);
//                            String sensornya     = obj.getString("kata");
//
//                             String  sensorkotor = sensornya.toLowerCase();
//                            Log.v("kotor", sensorkotor);
//
//
//                        }
//
//
//
//
//                    }else {
//                        String error_msg = result.getString("msg");
//                        Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_SHORT).show();
//
//                    }
//
//                }catch (JSONException e){
//                    //JSON error
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener(){
//
//            @Override
//            public void onErrorResponse(VolleyError error){
//                Log.e(TAG, "Login Error : " + error.getMessage());
//                error.printStackTrace();
//                hideDialog();
//            }
//        }){
//
//            @Override
//            protected Map<String, String> getParams(){
//                Map<String, String> params = new HashMap<String, String>();
//                return params;
//            }
//        };
//
//        //strReq.setRetryPolicy(policy);
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//        return tag_string_req;
//    }
//
//
//







    public void jawab(final String pertanyaan, final String NPM) {




        //Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Please Wait.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.bertanya, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject result = jObj.getJSONObject("results");

                    boolean status = result.getBoolean("status");

                    if(status == true){

                        JSONArray user     = result.getJSONArray("data");
                        for (int i = 0; i < user.length(); i++) {
                            JSONObject obj = user.getJSONObject(i);
                            String kode = obj.getString("kode_info");
                            String Informasi = obj.getString("informasi");
                            String label = obj.getString("label");
                            String keyword = obj.getString("keyword");
                            Log.v("nganu", Informasi);

                            answer.setVisibility(View.VISIBLE);
                            jawaban.append(Html.fromHtml(Informasi + "\n"+"\n"));
                           // Linkify.addLinks(jawaban, Linkify.ALL);


                            String suara = jawaban.getText().toString();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                textToSpeech.speak(suara,TextToSpeech.QUEUE_FLUSH,null,null);



                            } else {
                                textToSpeech.speak(suara, TextToSpeech.QUEUE_FLUSH, null);
                            }




                            String msg = result.getString("msg");
                            snacBarsGreen(msg);

                            //jawabnya.add(new dataJawaban(Informasi));

                        }




                    }else {

                        answer.setVisibility(View.VISIBLE);
                        jawaban.setText("Silahkan Ulangi");

                        String suara = jawaban.getText().toString();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            textToSpeech.speak(suara,TextToSpeech.QUEUE_FLUSH,null,null);



                        } else {
                            textToSpeech.speak(suara, TextToSpeech.QUEUE_FLUSH, null);
                        }

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
                params.put("Question", pertanyaan);
                params.put("NPM", NPM);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.keluar) {

            session.setLogin(false);
            session.setSkip(false);
            session.setSessid(0);

            // Launching the login activity
            Intent intent = new Intent(Main2Activity.this, Login.class);
            startActivity(intent);
            finish();


        }else if(id == R.id.about){
            About.showAbout(Main2Activity.this);
            // finish();
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
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

                jawaban.setText("");
                String Keluar = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(Keluar,TextToSpeech.QUEUE_FLUSH,null,null);



                } else {
                    textToSpeech.speak(Keluar, TextToSpeech.QUEUE_FLUSH, null);
                }


                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
