package com.example.location.feature.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.location.R;
import com.example.location.databinding.ActivityRegisterBinding;
import com.example.location.feature.location.FusedLocationLiveData;
import com.example.location.feature.otp.OTPActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();

        binding.done.setOnClickListener(view -> {
            if (validate()) {
                String countryCode= binding.countryCode.getSelectedCountryCode();
                String phone= binding.phoneBox.getText().toString();
                Intent intent=new Intent(RegisterActivity.this, OTPActivity.class);
                Log.d("OTP","+"+countryCode+" "+phone);
                intent.putExtra("phoneNumber","+"+countryCode+" "+phone);
                startActivity(intent);
            }

        });

    }

    private boolean validate() {
        if (binding.phoneBox.getText().toString().isEmpty()) {
            binding.phoneBox.setError("Please add phone number");
            return false;
        }
        return true;
    }
}