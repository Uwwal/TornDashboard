package com.example.torndashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.torndashboard.databinding.ActivityMainBinding
import com.example.torndashboard.fragment.LogFragment
import com.example.torndashboard.fragment.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "日志"

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