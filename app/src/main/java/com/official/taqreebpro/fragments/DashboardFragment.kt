package com.official.taqreebpro.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.official.taqreebpro.MainActivity
import com.official.taqreebpro.SessionManager
import com.official.taqreebpro.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        arguments?.let {
            param1 = it.getString(com.official.taqreebpro.ARG_PARAM1)
            param2 = it.getString(com.official.taqreebpro.ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)



        sessionManager = SessionManager(requireContext())
        // Set up the logout button click listener
        binding.tvCompanyName.text = sessionManager.getCompanyName()
        binding.btnLogout.setOnClickListener {
            // Perform logout operation
            auth.signOut()

            // Show a toast message confirming sign-out
            Toast.makeText(requireContext(), "You have been logged out", Toast.LENGTH_SHORT).show()

            // Redirect to the login activity or fragment after signing out
            sessionManager.setLoginState(false)
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Finish the current activity to prevent going back
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(com.official.taqreebpro.ARG_PARAM1, param1)
                    putString(com.official.taqreebpro.ARG_PARAM2, param2)
                }
            }
    }
}
