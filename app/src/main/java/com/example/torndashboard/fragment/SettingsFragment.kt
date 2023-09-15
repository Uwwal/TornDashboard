package com.example.torndashboard.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.torndashboard.config.AppConfig
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewCheckRemind.visibility = AppConfig.textViewCheckRemindVisibility

        checkConfigFile()
        loadAndFillKey()

        binding.buttonStore.setOnClickListener {
            onButtonStoreClick()
        }
    }

    private fun checkConfigFile() {
        val configFile = File(requireContext().filesDir, AppConfig.configFileName)
        if (!configFile.exists()) {
            try {
                configFile.writeText("{}") // Create an empty JSON object
                changeTextViewCheckRemindVisibility(View.VISIBLE)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun loadAndFillKey() {
        val fileUtils = FileUtils(requireContext())
        val key = fileUtils.getKey()

        if (key.isNullOrEmpty()) {
                changeTextViewCheckRemindVisibility(View.VISIBLE)
            } else {
                binding.editTextKey.setText(key)
                RetrofitClient.setApiKey(key)
            }
    }

    private fun onButtonStoreClick() {
        val configFile = File(requireContext().filesDir, AppConfig.configFileName)
        val configMap = mutableMapOf<String, String>()

        val keyText = binding.editTextKey.text.toString().trim()
        if (keyText.isNotEmpty()) {
            configMap["KEY"] = keyText
            try {
                configFile.writeText(gson.toJson(configMap))
                changeTextViewCheckRemindVisibility(View.GONE)

                RetrofitClient.setApiKey(keyText)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            changeTextViewCheckRemindVisibility(View.VISIBLE)
        }
    }
}
