package com.example.torndashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.torndashboard.config.AppConfig
import com.example.torndashboard.databinding.ActivityMainBinding
import com.example.torndashboard.fragment.LogFragment
import com.example.torndashboard.fragment.SettingsFragment
import com.example.torndashboard.utils.FileUtils
import com.example.torndashboard.web.RetrofitClient
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private fun initApiKey(){
        val configFile = File(filesDir, AppConfig.configFileName)
        if (configFile.exists()) {
            val fileUtils = FileUtils(this)
            val key = fileUtils.getKey()

            if (!key.isNullOrEmpty()) {
                RetrofitClient.setApiKey(key)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppConfig.initialize(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "日志"

        initApiKey()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_logs -> {
                    supportActionBar?.title = "日志"
                    showFragment(LogFragment())
                    true
                }
                R.id.action_settings -> {
                    supportActionBar?.title = "设置"
                    showFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
        showFragment(LogFragment())
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}