package com.example.wdy.classifier;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.wdy.predictservice.PersonaePredictService;


public class ProfilePredictActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classifier_profile_predict);
        Button startB = (Button) findViewById(R.id.profile_service_start);
        Button stopB = (Button) findViewById(R.id.profile_service_stop);

        startB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent personaePredict = new Intent(ProfilePredictActivity.this, PersonaePredictService.class);
                startService(personaePredict);
            }
        });
        stopB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent personaePredict = new Intent(ProfilePredictActivity.this, PersonaePredictService.class);
                stopService(personaePredict);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_predict, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
