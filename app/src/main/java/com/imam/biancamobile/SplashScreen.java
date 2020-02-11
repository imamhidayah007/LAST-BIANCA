package com.imam.biancamobile;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (!isNetworkAvailable()){
            //Create an alertDialog
            AlertDialog.Builder checkBuilder = new AlertDialog.Builder(SplashScreen.this);
            checkBuilder.setIcon(R.drawable.ic_action_error);
            checkBuilder.setTitle("Peringatan!");
            checkBuilder.setMessage("Pastikan Anda Terhubung Dengan Jaringan Internet!");

            //Builder retry button
            checkBuilder.setPositiveButton("Muat Ulang", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Restart the activity
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
            checkBuilder.setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            AlertDialog alert = checkBuilder.create();
            alert.show();
        }
        else {
            if (isNetworkAvailable()){
                Thread thread = new Thread(){
                    public void run(){
                        try {
                            sleep(3000);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        finally {
                            startActivity(new Intent(SplashScreen.this, Login.class));
                            finish();
                        }
                    }
                };
                thread.start();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo           = connectivityManager.getActiveNetworkInfo();
        return  activeNetworkInfo != null;
    }
}