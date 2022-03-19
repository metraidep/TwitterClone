package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose:EditText
    lateinit var btnTweet: Button
    lateinit var ltCount:TextView

    lateinit var client:TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        ltCount = findViewById(R.id.wrdLimit)

        client = TwitterApplication.getRestClient(this)


        //Handling the user's click on the tweet button
        btnTweet.setOnClickListener{
            //Grab the content of edittext(etCompose)
            val tweetContent = etCompose.text.toString()

            //1. Make sure the tweet isn't empty
            if (tweetContent.isEmpty()){
                Toast.makeText(this,"Empty tweets not allowed!",Toast.LENGTH_SHORT).show()
            //Look into displaying SnackBar message
            }
            //2. Make sure the tweet is under character count
            if (tweetContent.length > 280){
                Toast.makeText(this,"Tweet is too long! Limit is 280 characters",Toast.LENGTH_SHORT)
                    .show()
            }else {
                //Make an api call to Twitter to publish tweet
                client.publishTweet(tweetContent, object:JsonHttpResponseHandler(){
                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        Log.i(TAG,"Successfully pupblished tweet!")
                        // TODO Send the tweet back to TimeLineActivity

                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet",tweet)
                        setResult(RESULT_OK,intent)
                        finish()

                    }
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?,
                    ) {
                        Log.e(TAG,"Failed to publish tweet",throwable)
                    }
                })
            }

        }
        //Handling word count
        etCompose.addTextChangedListener(object: TextWatcher{

            override fun onTextChanged(s: CharSequence,start: Int, before: Int, count: Int){
                // Fires right as the text is being changed (even supplies the range of text)
                val charRemain = 280 - count
                ltCount.text = "$charRemain character(s) remaining"
                if (charRemain <= 0) {
                    ltCount.text = "0 character remaining"
                    btnTweet.isClickable = false
                }

            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                btnTweet.isClickable = true
            }
        })

    }
    companion object {
        val TAG = "ComposeActivity"
    }
}