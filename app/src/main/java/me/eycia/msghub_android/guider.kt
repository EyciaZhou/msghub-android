package me.eycia.msghub_android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import me.eycia.api.API
import me.eycia.api.MsgBase

class guider : AppCompatActivity() {
    var mid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guider)

        val mid: String

        if (savedInstanceState != null) {
            mid = savedInstanceState.getString("mid")
        } else {
            val intent = intent
            mid = intent.getStringExtra("mid")
        }

        object : API.Msgs.FullMessageGetTask(mid) {
            override fun onSuccess(result: MsgBase) {
                val intent_to: Intent
                if (result.ViewType == API.VIEW_NORMAL) {
                    intent_to = Intent(this@guider, MoreInfoActivity::class.java)
                } else
                /*if (msg.ViewType == API.VIEW_PICTURE)*/ {
                    intent_to = Intent(this@guider, pictures::class.java)
                    intent_to.putExtra("clicked_pic", intent.getIntExtra("clicked_pic", 0))
                }
                intent_to.putExtra("m", result)
                this@guider.startActivity(intent_to)

                //close the window of "guider"
                finish()
            }
        }.execute()

        this.mid = mid
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("mid", mid)
    }
}
