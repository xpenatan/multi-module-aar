package com.github.xpenatan.aarmodule.demo.start

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.github.xpenatan.aarmodule.demo.base.BaseActivity
import com.github.xpenatan.aarmodule.demo.splash.SplashActivity
import com.github.xpenatan.aarmodule.demo.standalonelib.StandAloneLib
import com.xpenatan.aarmodule.demo.databinding.ActivityStartBinding

class StartActivity : BaseActivity() {

    private val binding: ActivityStartBinding by lazy { ActivityStartBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.txtStandAloneLib.text = StandAloneLib.getText()
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
            startActivity(SplashActivity.getStartIntent(this))
        }, 2000)
    }
}