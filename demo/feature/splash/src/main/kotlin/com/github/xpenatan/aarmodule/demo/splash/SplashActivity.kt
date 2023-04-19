package com.github.xpenatan.aarmodule.demo.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.github.xpenatan.aarmodule.demo.base.BaseActivity
import com.github.xpenatan.aarmodule.demo.login.start.LoginLauncher
import com.github.xpenatan.aarmodule.demo.standalonelib.StandAloneLib
import com.xpenatan.aarmodule.demo.splash.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {

    private val binding: ActivitySplashBinding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.txtStandAloneLib.text = StandAloneLib.getText()
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
            LoginLauncher.startLogin(this)
        }, 2000)
    }
}