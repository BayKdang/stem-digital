package com.stemdigital.inventorytracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material. button.MaterialButton

class InfoFragment : Fragment() {

    private lateinit var btnContactSupport: MaterialButton
    private lateinit var cardPrivacyPolicy: CardView
    private lateinit var cardTermsOfService: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        btnContactSupport = view.findViewById(R.id.btn_contact_support)
        cardPrivacyPolicy = view.findViewById(R.id.card_privacy_policy)
        cardTermsOfService = view.findViewById(R.id.card_terms_of_service)

        // Contact Support button listener
        btnContactSupport.setOnClickListener {
            Toast.makeText(requireContext(), "Opening feedback form...", Toast.LENGTH_SHORT). show()
            // TODO: Implement feedback form or email intent
        }

        // Privacy Policy card listener
        cardPrivacyPolicy.setOnClickListener {
            Toast.makeText(requireContext(), "Opening Privacy Policy...", Toast. LENGTH_SHORT).show()
            // TODO: Open privacy policy (web link or in-app page)
        }

        // Terms of Service card listener
        cardTermsOfService.setOnClickListener {
            Toast.makeText(requireContext(), "Opening Terms of Service...", Toast.LENGTH_SHORT).show()
            // TODO: Open terms of service (web link or in-app page)
        }
    }
}