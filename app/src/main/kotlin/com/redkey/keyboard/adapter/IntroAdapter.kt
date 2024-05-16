package com.redkey.keyboard.adapter

import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redkey.keyboard.controller.IntroController
import com.redkey.keyboard.databinding.IntroScreenBinding

class IntroAdapter(val controller: IntroController) : RecyclerView.Adapter<IntroAdapter.ViewHolder>() {
    class ViewHolder(val binding: IntroScreenBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int) = ViewHolder(
        IntroScreenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.apply {
                when(position) {
                    0 -> { 
                        binding.title.text = "Step 1"
                        binding.subtitle.text = "Select RedKey in Language & Input settings"
                        binding.button.text = "Enable"
                        binding.button.setOnClickListener {
                            controller.startActivityForResult(
                                Intent(Settings.ACTION_INPUT_METHOD_SETTINGS),
                                1
                            )
                        }
                    }
		    1 -> {
                        binding.title.text = "Step 2"
                        binding.subtitle.text = "Select RedKey as your default keyboard"
                        binding.button.text = "Select Input Method"
                    }
		    2 -> {
                        binding.title.text = "Setup finished"
                        binding.subtitle.text = "You can move on to settings"
                        binding.button.text = "Done"
                    }
                }
            }
        }
    }

    override fun getItemCount() = 3
}
