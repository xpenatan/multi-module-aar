package com.github.xpenatan.aarmodule.demo.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.xpenatan.aarmodule.demo.base.BaseActivity
import com.github.xpenatan.aarmodule.demo.standaloneaar.StandAloneAARLib
import com.github.xpenatan.aarmodule.demo.standalonelib.StandAloneLib
import com.xpenatan.aarmodule.demo.dashboard.databinding.ActivityDashboardBinding

class DashboardActivity : BaseActivity() {

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, DashboardActivity::class.java)
        }
    }

    private val binding: ActivityDashboardBinding by lazy { ActivityDashboardBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.txtStandAloneAARLib.text = StandAloneAARLib.getText()
        binding.txtStandAloneLib.text = StandAloneLib.getText()
    }
}