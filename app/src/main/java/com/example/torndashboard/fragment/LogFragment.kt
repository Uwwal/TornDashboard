package com.example.torndashboard.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torndashboard.adapter.TextAdapter
import com.example.torndashboard.utils.itemsList
import com.example.torndashboard.databinding.FragmentLogBinding

class LogFragment : Fragment() {
    private lateinit var binding: FragmentLogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLogBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = TextAdapter(itemsList) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.torn.com/events.php"))
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        return binding.root
    }
}
