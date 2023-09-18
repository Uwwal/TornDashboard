package com.example.torndashboard.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torndashboard.adapter.BooleanArrayAdapter
import com.example.torndashboard.config.AppConfig
import com.example.torndashboard.config.AppConfig.timeFilter
import com.example.torndashboard.config.AppConfig.timeIsZeroTextVisibility
import com.example.torndashboard.databinding.FragmentSettingsBinding
import com.example.torndashboard.utils.FileUtils
import com.example.torndashboard.web.RetrofitClient
import com.google.gson.Gson
import java.io.File
import java.io.IOException

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    private val gson = Gson()

    private fun changeTextViewCheckRemindVisibility(visibility: Int){
        binding.textViewCheckRemind.visibility = visibility
        AppConfig.textViewCheckRemindVisibility = visibility
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = BooleanArrayAdapter(timeFilter, timeIsZeroTextVisibility, requireContext())
        recyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewCheckRemind.visibility = AppConfig.textViewCheckRemindVisibility

        checkConfigFile()
        loadAndFillKey()

        loadAndFillMinAutoSetClock()

        binding.buttonStore.setOnClickListener {
            onButtonStoreClick()
        }

        binding.minAutoSetClockSwitch.setOnClickListener {
            onMinAutoSetClockSwitchClick()
        }

        binding.howToUseTextView.setOnClickListener {
            binding.mainHintTextView.visibility = View.VISIBLE
        }
    }

    private fun loadAndFillMinAutoSetClock() {
        val fileUtils = FileUtils(requireContext())
        val minAutoSetClockStatus = fileUtils.get(fileUtils.minAutoSetClockSwitchStatus)

        if (!minAutoSetClockStatus.isNullOrEmpty()) {
            binding.minAutoSetClockSwitch.isChecked = minAutoSetClockStatus.toBoolean()
        }
    }

    private fun checkConfigFile() {
        val configFile = File(requireContext().filesDir, AppConfig.configFileName)
        if (!configFile.exists()) {
            try {
                configFile.writeText("{}")
                changeTextViewCheckRemindVisibility(View.VISIBLE)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun loadAndFillKey() {
        val fileUtils = FileUtils(requireContext())
        val key = fileUtils.get(fileUtils.key)

        if (key.isNullOrEmpty()) {
                changeTextViewCheckRemindVisibility(View.VISIBLE)
            } else {
                binding.editTextKey.setText(key)
                RetrofitClient.setApiKey(key)
            }
    }

    private fun onButtonStoreClick() {
        val file = FileUtils(requireContext())
        val configMap = file.loadConfigMapFromFile()

        val keyText = binding.editTextKey.text.toString().trim()
        if (keyText.isNotEmpty()) {
            configMap[file.key] = keyText
            try {
                file.write(gson.toJson(configMap))
                changeTextViewCheckRemindVisibility(View.GONE)

                RetrofitClient.setApiKey(keyText)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            changeTextViewCheckRemindVisibility(View.VISIBLE)
        }
    }

    private fun onMinAutoSetClockSwitchClick() {
        val file = FileUtils(requireContext())
        val configMap = file.loadConfigMapFromFile()

        val isChecked = binding.minAutoSetClockSwitch.isChecked

        configMap[file.minAutoSetClockSwitchStatus] = "$isChecked"

        file.write(gson.toJson(configMap))
    }

}
