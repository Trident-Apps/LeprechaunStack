package com.taptri.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.taptri.R
import com.taptri.databinding.GameActivityBinding

class GameActivity : AppCompatActivity() {
    private var _binding: GameActivityBinding? = null
    private val binding get() = _binding!!

    private var score = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = GameActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinBtn.setOnClickListener {
            spin()
            if (binding.imageView1.tag == binding.imageView2.tag &&
                binding.imageView2.tag == binding.imageView3.tag
            ) {
                score++
                binding.score.setText(score)
            }
        }
    }

    private fun spin() {
        val imageViewsList = listOf(

            R.drawable.icon2,
            R.drawable.icon3,
            R.drawable.icon5,
            R.drawable.icon6,

        ).shuffled()
        binding.imageView1.setImageResource(imageViewsList[0])
        binding.imageView1.tag = imageViewsList[0]
        binding.imageView2.setImageResource(imageViewsList[1])
        binding.imageView2.tag = imageViewsList[1]
        binding.imageView3.setImageResource(imageViewsList[2])
        binding.imageView3.tag = imageViewsList[2]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}