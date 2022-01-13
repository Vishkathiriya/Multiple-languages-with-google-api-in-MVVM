package com.iblkotlinmvvm.languageapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.iblkotlinmvvm.languageapi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.mutText.observe(this, Observer {
            if (it != null) {
                binding.textView.text = it
            }
        })
    }
}