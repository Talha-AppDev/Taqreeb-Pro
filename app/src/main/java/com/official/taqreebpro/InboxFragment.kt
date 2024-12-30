package com.official.taqreebpro

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.official.taqreebpro.databinding.FragmentInboxBinding

class InboxFragment : Fragment() {
    private lateinit var binding: FragmentInboxBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInboxBinding.inflate(inflater, container, false)

        binding.ivMenu.isClickable = true
        binding.ivMenu.isFocusable = true
        binding.ivMenu.isFocusableInTouchMode = true


        binding.ivMenu.setOnClickListener {
            Log.d("inboxFragment", "Menu button clicked")
            Toast.makeText(requireContext(), "clicked", Toast.LENGTH_SHORT).show()
            showPopupMenu(it)
        }

        binding.ivSend.setOnClickListener {
            Toast.makeText(requireContext(), "clicked", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.chat_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_mute -> {
                    Toast.makeText(requireContext(), "Mute Notification", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_block -> {
                    Toast.makeText(requireContext(), "Block", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_report -> {
                    Toast.makeText(requireContext(), "Report", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}
