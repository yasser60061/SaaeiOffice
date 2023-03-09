package com.saaei12092021.office.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saaei12092021.office.databinding.ActivityAccountUnderReviewBinding
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity

class AccountUnderReviewActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountUnderReviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountUnderReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rememberMe.setOnClickListener {
            val intent = Intent(this@AccountUnderReviewActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
           // overridePendingTransition(0, 0)
        }
    }
}