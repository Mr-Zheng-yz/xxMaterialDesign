package com.example.xxmaterialdesign.nestedscroll.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.xxmaterialdesign.R;
import com.example.xxmaterialdesign.databinding.ActivityNestScrollBinding;
import com.example.xxmaterialdesign.databinding.ActivityTabBinding;

public class NestScrollActivity extends AppCompatActivity {

    private ActivityNestScrollBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNestScrollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}