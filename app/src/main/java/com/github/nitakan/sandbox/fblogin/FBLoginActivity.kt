package com.github.nitakan.sandbox.fblogin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.github.nitakan.sandbox.R

class FBLoginActivity : AppCompatActivity() {

    companion object {
        fun startActivity(activity: Activity) {
            activity.startActivity(Intent(activity, FBLoginActivity::class.java))
        }
    }


    private lateinit var manager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fblogin)

        manager = CallbackManager.Factory.create()


        val fb = findViewById<LoginButton>(R.id.login)
        fb.loginBehavior = LoginBehavior.NATIVE_WITH_FALLBACK
        fb.setPermissions(
            "public_profile",
            "user_friends",
            "email"
        )
        fb.registerCallback(manager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Toast.makeText(this@FBLoginActivity, "Success !", Toast.LENGTH_SHORT).show()
            }

            override fun onCancel() {
                Toast.makeText(this@FBLoginActivity, "Cancel !", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@FBLoginActivity, "Error ! ${error?.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }

}
