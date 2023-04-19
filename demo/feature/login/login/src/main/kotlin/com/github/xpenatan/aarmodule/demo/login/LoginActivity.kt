package com.github.xpenatan.aarmodule.demo.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.xpenatan.aarmodule.demo.base.BaseActivity
import com.github.xpenatan.aarmodule.demo.dashboard.start.DashboardLauncher
import com.github.xpenatan.aarmodule.demo.standalonelib.StandAloneLib
import com.xpenatan.aarmodule.demo.login.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.txtStandAloneLib.text = StandAloneLib.getText()

        binding.loginBtn.setOnClickListener {
            DashboardLauncher.startDashboard(this)
        }
    }
}