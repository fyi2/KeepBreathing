package org.sherman.magic.keepbreathing

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import com.github.florent37.viewanimator.AnimationListener
import com.github.florent37.viewanimator.ViewAnimator
import kotlinx.android.synthetic.main.activity_main.*
import org.sherman.magic.keepbreathing.R.id.image
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var image: ImageView? = null
    val PREFS_NAME = "myPrefs"
    lateinit var myPrefs: SharedPreferences
    lateinit var editor:SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = myPrefs.edit()

        image = findViewById(R.id.lotusImage)

        startIntroAnimation()

        updateCounts()

        startButton.setOnClickListener(View.OnClickListener {
            startAnimation()
        })
    }

    fun startIntroAnimation() {
        ViewAnimator
                .animate(guideTxt)
                .scale(0.0f, 1.0f)
                .onStart(AnimationListener.Start {
                    guideTxt.setText("Breath")
                })
                .start()
    }

    fun startAnimation() {
        ViewAnimator
                .animate(image)
                .alpha(0.0f, 1.0f)
                .onStart(AnimationListener.Start {
                    guideTxt.setText("Inhale...Exhale")
                })
                .decelerate()
                .duration(5000)
                .thenAnimate(image)
                .scale(0.02f, 1.5f, 0.02f)
                .rotation(270.0f)
                .repeatCount(6)
                .accelerate()
                .duration(8000)
                .onStop(AnimationListener.Stop {
                    guideTxt.setText("Good Job")
                    image?.scaleX = 1.0f
                    image?.scaleY = 1.0f
                    val number = myPrefs.getInt("number",0)
                    val breaths = myPrefs.getInt("breaths", 0)
                    val timeCompleted = Calendar.getInstance().timeInMillis
                    editor.putInt("number", number+1).apply()
                    editor.putInt("breaths", breaths+1).apply()
                    editor.putLong("latest", timeCompleted).apply()
                    editor.commit()
                    updateCounts()
                    object: CountDownTimer(2000, 1000){
                        override fun onFinish() {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        override fun onTick(millisUntilFinished: Long) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                    }
                })
                .start()
    }

    fun updateCounts(){
        todayMinutesTxt.setText(myPrefs.getInt("number", 0).toString()+" min today.")
        breathsTakenTxt.setText(myPrefs.getInt("breaths", 0).toString()+" breaths.")
        lastBreathTxt.setText(nowString(myPrefs.getLong("latest",0)))
    }

    fun nowString(timeMilli : Long) : String {
        val dateFormat = SimpleDateFormat("hh:mm a")
        val formattedTime = dateFormat.format(timeMilli).toString()
        return formattedTime
    }
}
