package com.example.license.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.license.R
import com.example.license.data.PlantReportViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FirstFragment : Fragment() {

    private lateinit var mPlantReportViewModel: PlantReportViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first, container, false)

        // Recycler View
        val adapter = ListAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview);
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        mPlantReportViewModel = ViewModelProvider(this).get(PlantReportViewModel::class.java)
        mPlantReportViewModel.readAllData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { report -> adapter.setData(report) })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }


}

