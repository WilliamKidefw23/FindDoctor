package com.kid.william.finddoctor.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kid.william.finddoctor.Model.entidad.DoctorInfo;
import com.kid.william.finddoctor.R;
import com.kid.william.finddoctor.utilidades.Common;

public class CallDoctor extends AppCompatActivity {

    private static final String TAG = CallDoctor.class.getName();
    private TextView txtNameDoctor, txtPhoneDoctor;
    private Button btnCalldoctor;
    private String doctorId;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_doctor);

        txtNameDoctor = findViewById(R.id.txtName_doctor);
        txtPhoneDoctor = findViewById(R.id.txtPhone_doctor);
        btnCalldoctor = findViewById(R.id.btnCall_dcctor);

        btnCalldoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + txtPhoneDoctor.getText().toString()));
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });

        if(getIntent() != null){
            doctorId = getIntent().getStringExtra(Common.KEY_DOCTOR);
            double lat = getIntent().getDoubleExtra(Common.LATITUD,-1.0);
            double log = getIntent().getDoubleExtra(Common.LONGITUD,-1.0);
            mLastLocation = new Location("");
            mLastLocation.setLongitude(log);
            mLastLocation.setLatitude(lat);
            loadDoctorInfo(doctorId);

        }
    }

    private void loadDoctorInfo(String doctorId) {
        FirebaseDatabase.getInstance().getReference(Common.TABLE_DOCTOR_INFO)
                .child(doctorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DoctorInfo user = dataSnapshot.getValue(DoctorInfo.class);
                txtNameDoctor.setText(user.getName());
                txtPhoneDoctor.setText(String.valueOf(user.getPhone()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
