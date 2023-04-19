package com.github.xpenatan.aarmodule.demo.dashboard.start

import android.content.Context
import com.github.xpenatan.aarmodule.demo.dashboard.DashboardActivity

class DashboardLauncher {
    companion object {
        fun startDashboard(context: Context) {
            context.startActivity(DashboardActivity.getStartIntent(context))
        }
    }
}