package com.github.xpenatan.aarmodule.demo.login.start

import android.content.Context
import com.github.xpenatan.aarmodule.demo.login.LoginActivity

class LoginLauncher {
    companion object {
        fun startLogin(context: Context) {
            context.startActivity(LoginActivity.getStartIntent(context))
        }
    }
}