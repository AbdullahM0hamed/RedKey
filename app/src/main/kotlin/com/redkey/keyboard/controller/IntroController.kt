package com.redkey.keyboard.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bluelinelabs.conductor.Controller
import com.google.android.material.tabs.TabLayoutMediator
import com.redkey.keyboard.adapter.IntroAdapter
import com.redkey.keyboard.databinding.IntroBinding

class IntroController(val ctx: Context?, bundle: Bundle?) : Controller(bundle) {
    private var pager: ViewPager2? = null

    constructor(bundle: Bundle?) : this(null, bundle)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val binding = IntroBinding.inflate(inflater)
        pager = binding.viewpager
        binding.viewpager.adapter = IntroAdapter(this)
        binding.apply {
            TabLayoutMediator(tabs, viewpager) { _, _ -> }.attach()
        }
	return binding.root
    }

    override fun onActivityResult(requestCode: Int, result: Int, data: Intent?) {
        android.widget.Toast.makeText(ctx, "Test", 5).show()
        var enabled = false
        val manager = ctx?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        for (inputmethod in manager.getEnabledInputMethodList()) {
            if (inputmethod.packageName == "com.redkey.keyboard") {
                enabled = true
                break
            }
        }

        if (enabled) {
            pager?.setCurrentItem(1)
            val holder = (pager?.getChildAt(0) as RecyclerView)?.findViewHolderForAdapterPosition(0) as IntroAdapter.ViewHolder
            holder?.binding?.apply {
                button.visibility = View.GONE
                complete.visibility = View.VISIBLE
            }
        }
    }
}
