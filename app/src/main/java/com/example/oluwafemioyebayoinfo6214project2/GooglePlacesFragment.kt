package com.example.oluwafemioyebayoinfo6214project2

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap

class GooglePlacesFragment : Fragment() {

    private lateinit var mGoogleMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ListOfPlaces = listOf<Places>(
            Places("London"),
            Places("America"),
            Places("Chee"),

            )

        val recyclerView = view.findViewById<RecyclerView>(R.id.myRecyclerView)

        recyclerView.setBackgroundColor(Color.GREEN)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MyRecyclerViewAdapter(ListOfPlaces)
    }

    }