package io.github.talhahasanzia.expiry.marker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.talhahasanzia.expiry.annotation.Expiry

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}