package com.test.askai


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


class MainActivity : AppCompatActivity() {
    val API_KEY = "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"
    var userInputEditText: EditText? = null
    var responseTextView: TextView? = null
    var contact: ImageView? = null
    var choiceText = ""
    var loader: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loader = findViewById(R.id.loader)

         var textView222222:TextView=findViewById(R.id.textView222222)
         var imageView2222:ImageView=findViewById(R.id.imageView2222)
        imageView2222.setOnClickListener {
            val intent = Intent(this, Contact::class.java)
            startActivity(intent)
        }
        textView222222.setText("AskAI")

        userInputEditText = findViewById(R.id.userInputEditText)
        responseTextView = findViewById(R.id.responseTextView)
        var userInput = "";
        val sendButton = findViewById<Button>(R.id.sendButton)
        val cleardButton = findViewById<Button>(R.id.clearButton)

      /*  contact = findViewById(R.id.contact_info)
        contact!!.setOnClickListener {
            val intent = Intent(this, Contact::class.java)
            startActivity(intent)
        }
*/

        sendButton.setOnClickListener {
            hideKeyboard(it);
            loader!!.setVisibility(View.VISIBLE);


            if (connectivityManager()) {
                userInput = userInputEditText!!.text.toString()
                // Using coroutines to run this block in backgroud thread

                val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
                val okHttpClient = OkHttpClient.Builder()
                    .callTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)


                    // Set the call timeout to 30 seconds
                    .build()
                var khan = "tell me some pashto quotres in pashto language"
                val json = "{" +
                        "  \"model\": \"text-davinci-003\"," +
                        "  \"prompt\": \"$userInput\"," +
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
                            "Bearer " + "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"
                        )
                        .post(requestBody)
                        .build()

                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        MainScope().launch {

                            val json = response.body!!.string()

                            Log.i("TAG", "onResponse: ${json}")
                            val jsonobject = JSONObject(json) //your response
// Inside your API call onResponse or onFailure method

                            loader!!.visibility = View.GONE;

                            try {
                                val jsonObject = JSONObject(json)
                                val choicesArray: JSONArray = jsonObject.getJSONArray("choices")
                                if (choicesArray.length() > 0) {
                                    val choiceObject = choicesArray.getJSONObject(0)
                                    choiceText = choiceObject.getString("text")

                                    // Now you can use the 'choiceText' variable which contains the text value from the choices array
                                    Log.i("TAGRESPONSE", choicesArray[0].toString())
                                    Log.i("TAGRESPONSEInner", choiceText)
                                    responseTextView!!.text = choiceText
//                                        responseTextView!!.movementMethod = ScrollingMovementMethod()


                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }//                     val jsonobject = JSONObject(json) //your response

                            /*  try {
                                    val result: String =
                                        jsonobject.getString("choices")



                                    val name = result.get(0..!).toString()

                                    //result is key for which you need to retrieve data
                                    Log.i("TAGRESPONSE", jsonobject.getString("choices"))
                                    Log.i("TAGRESPONSEInner", name)


                                } catch (e: JSONException) {
                                     e.printStackTrace()
                                    Log.i("TAGRESPONSEERROR", jsonobject.getString("choices"))

                                }*/
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.i("TAG", "onFailure: $e")
                        Looper.prepare();
                        Snackbar.make(
                            sendButton,
                            "Failed to fetch data. Please try again later.",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Cancel") {
                                // Responds to click on the action
                            }.show()
                    }
                })
            } else {
                Snackbar.make(sendButton, "No internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Cancel") {
                        // Responds to click on the action
                    }.show()
            }

        }



        cleardButton.setOnClickListener {

            userInputEditText!!.text.clear() // Clear the text
            responseTextView!!.text = ""


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
        MaterialAlertDialogBuilder(this,R.style.AlertDialogTheme)
            .setTitle("Exit")
            .setMessage("Do you want to exit the App ?")
//            .setNeutralButton("Neutral") { dialog, which ->
//                // Respond to neutral button press
//            }
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