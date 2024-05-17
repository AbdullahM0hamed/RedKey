package com.redkey.keyboard.adapter

import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redkey.keyboard.R
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
                val ctx = itemView.context
                when(position) {
                    0 -> { 
                        binding.title.text = ctx.getString(R.string.step_one)
                        binding.subtitle.text = ctx.getString(R.string.select_redkey)
                        binding.button.text = ctx.getString(R.string.enable)
                        binding.button.setOnClickListener {
                            controller.startActivityForResult(
                                Intent(Settings.ACTION_INPUT_METHOD_SETTINGS),
                                1
                            )
                        }
                    }
		    1 -> {
                        binding.title.text = ctx.getString(R.string.step_two)
                        binding.subtitle.text = ctx.getString(R.string.default_keyboard)
                        binding.button.text = ctx.getString(R.string.input_method)
                    }
		    2 -> {
                        binding.title.text = ctx.getString(R.string.setup_finished)
                        binding.subtitle.text = ctx.getString(R.string.move_on)
                        binding.button.text = ctx.getString(R.string.done)
                    }
                }
            }
        }
    }

    override fun getItemCount() = 3
}
