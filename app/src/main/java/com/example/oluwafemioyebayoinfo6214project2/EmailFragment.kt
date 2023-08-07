package com.example.oluwafemioyebayoinfo6214project2

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EmailFragment : Fragment() {

    private lateinit var emailTextField : EditText
    private lateinit var sf : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailTextField = view.findViewById<EditText>(R.id.editTextTextEmailAddress)

        val latitude = (requireActivity() as MainActivity).latitude
        val longitude = (requireActivity() as MainActivity).longitude
        val address = (requireActivity() as MainActivity).address

        sf = requireActivity().getSharedPreferences("project2_sef", AppCompatActivity.MODE_PRIVATE)

        val btn = view.findViewById<TextView>(R.id.sendEmailButton);

        btn.setOnClickListener {

            val intent = Intent(Intent.ACTION_SENDTO).apply {

                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailTextField.text.toString()));
                putExtra(Intent.EXTRA_SUBJECT, "Your location details");
                putExtra(Intent.EXTRA_TEXT, "This location Latitude: ${latitude}, Location: ${longitude}, Address: ${address}");

                data = Uri.parse("mailto:");
            }

            startActivity(Intent.createChooser(intent, "Choose a Email Client..."));
        }
    }

    override fun onPause() {
        super.onPause()

        val email = emailTextField.text.toString()

        val editor= sf.edit()
        editor.apply {
            putString("sf_email",email)
            commit()
        }
    }

    override fun onResume() {
        super.onResume()

        val email = sf.getString("sf_email",null)

        emailTextField.setText(email)
    }
}