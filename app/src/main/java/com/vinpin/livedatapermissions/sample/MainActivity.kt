package com.vinpin.livedatapermissions.sample

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vinpin.livedatapermissions.LiveDataPermissions
import com.vinpin.livedatapermissions.PermissionDeny
import com.vinpin.livedatapermissions.PermissionGrant

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LiveDataPermissions(this).request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        ).observe(this, {
            when (it) {
                is PermissionGrant -> {
                    Toast.makeText(this, "同意", Toast.LENGTH_LONG).show()
                }
                is PermissionDeny -> {
                    if (it.deny.isNotEmpty()) {
                        Toast.makeText(this, "拒绝下次不再提醒：${it.deny.size}", Toast.LENGTH_LONG).show()
                    } else if (it.rationale.isNotEmpty()) {
                        Toast.makeText(this, "拒绝下次提醒：${it.rationale.size}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        })
    }
}
