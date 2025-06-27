package com.adios.conversee

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.adios.conversee.databinding.FragmentBottomNavigationDrawerBinding

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBottomNavigationDrawerBinding.inflate(inflater, container, false).apply {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                else -> dismiss()
            }
            true
        }
    }.root
}
