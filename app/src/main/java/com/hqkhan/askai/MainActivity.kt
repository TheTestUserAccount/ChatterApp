package com.hqkhan.askai


import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Created by HQKhan */

class MainActivity : AppCompatActivity() {

    val API_KEY = "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"
    var mChoiceText = ""
    var mUserInput = "";
    lateinit var mSendButton: Button
    lateinit var mClearButton: Button
    lateinit var mProgressLoader: ProgressBar
    lateinit var mUserInputET: EditText
    lateinit var mResponseTV: TextView
    lateinit var mToolbarTitle: TextView
    lateinit var mToolbarInfo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mProgressLoader = findViewById(R.id.loader)
        mToolbarTitle = findViewById(R.id.title_screen)

        mToolbarInfo = findViewById(R.id.iv_info)
        mToolbarInfo.setOnClickListener {
            val intent = Intent(this, Contact::class.java)
            startActivity(intent)
        }
        mToolbarTitle.text = "AskAI"

        mUserInputET = findViewById(R.id.userInputEditText)
        mResponseTV = findViewById(R.id.responseTextView)
        mSendButton = findViewById(R.id.sendButton)

        mClearButton = findViewById(R.id.clearButton)
        mSendButton.setOnClickListener {
            hideKeyboard(it);
            mProgressLoader!!.visibility = View.VISIBLE;

            if (connectivityManager()) {
                mUserInput = mUserInputET!!.text.toString()
                // Using coroutines to run this block in background thread
                val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
                val okHttpClient = OkHttpClient.Builder()
                    .callTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)

                    .build()
                val json = "{" +
                        "  \"model\": \"text-davinci-003\"," +
                        "  \"prompt\": \"$mUserInput\"," +
                        "  \"temperature\": 0," +
                        "  \"max_tokens\": 1300," +
                        "  \"top_p\": 1," +
                        "  \"frequency_penalty\": 0," +
                        "  \"presence_penalty\": 0" +
                        "}"

                val requestBody: RequestBody = json.toRequestBody(mediaType)
                val request: Request =
                    Request.Builder()
                        .url("https://api.openai.com/v1/completions")
                        .addHeader(
                            "Authorization",
                            "Bearer " + API_KEY
                        )
                        .post(requestBody)
                        .build()

                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        MainScope().launch {

                            val json = response.body!!.string()
                            Log.i("TAG", "onResponse: ${json}")
                            mProgressLoader!!.visibility = View.GONE;
                            try {
                                val jsonObject = JSONObject(json)
                                val choicesArray: JSONArray = jsonObject.getJSONArray("choices")
                                if (choicesArray.length() > 0) {
                                    val choiceObject = choicesArray.getJSONObject(0)
                                    mChoiceText = choiceObject.getString("text")
                                    mResponseTV!!.text = mChoiceText
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.i("TAG", "onFailure: $e")
                        Looper.prepare();
                        Snackbar.make(
                            mSendButton,
                            "Failed to fetch data. Please try again later.",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Cancel") {
                            }.show()
                    }
                })
            } else {
                Snackbar.make(mSendButton, "No internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Cancel") {
                    }.show()
            }
        }
        mClearButton.setOnClickListener {
            mUserInputET!!.text.clear()
            mResponseTV!!.text = ""
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun connectivityManager(): Boolean {
        var cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        if (cm != null) {
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun exit() {
        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle("Exit")
            .setMessage("Do you want to exit the App ?")
            .setNegativeButton("CANCEL") { dialog, which ->
            }
            .setPositiveButton("YES") { dialog, which ->
                finishAffinity()
            }
            .show()
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        exit()
    }
}