package com.example.screenshotblocking

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
private lateinit var screenshotDetector: ScreenshotDetector

class MainActivity : AppCompatActivity() {
    var isBlocking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button_block.setOnClickListener {
            if (!isBlocking) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                button_block.text = "Stop blocking"
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                button_block.text = "Block screenshot"
            }
            isBlocking = !isBlocking

        }
        screenshotDetector = ScreenshotDetector(baseContext)
    }

    override fun onStart() {
        super.onStart()
        detectScreenshots()
    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(this, "stop listening", Toast.LENGTH_SHORT).show()
        screenshotDetector.stop()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    detectScreenshots()
                }
                return
            }
        }
    }

    private fun haveStoragePermission() =
            ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    private fun detectScreenshots() {
        if (haveStoragePermission()) {
            screenshotDetector.start()
        } else {
            requestPermission()
        }
    }

}