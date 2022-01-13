package com.iblkotlinmvvm.languageapi

import android.content.ContentValues
import android.os.AsyncTask
import android.text.Editable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

class MainViewModel : ViewModel() {

    private var langFrom: String = Language.AUTO_DETECT
    private var langTo: String = Language.HINDI
    var resp: String? = null
    var url: String? = null
    var text: String? = null
    var mutText: MutableLiveData<String> = MutableLiveData()

    fun afterSomeTextChanged(editable: Editable) {
        text = editable.toString()
    }

    fun onTranslateClick() {
        Async().execute()
    }

    inner class Async : AsyncTask<String?, String?, String?>() {

        override fun onPostExecute(s: String?) {
            var temp = ""
            if (resp.isNullOrEmpty()) {
                onFailure("Network Error")
            } else {
                try {
                    val main = JSONArray(resp)
                    val total = main[0] as JSONArray
                    for (i in 0 until total.length()) {
                        val currentLine = total[i] as JSONArray
                        temp += currentLine[0].toString()
                    }
                    Log.d(ContentValues.TAG, "onPostExecute: $temp")
                    if (temp.length > 2) {
                        onSuccess(temp)
                    } else {
                        onFailure("Invalid Input String")
                    }
                } catch (e: JSONException) {
                    onFailure(e.localizedMessage)
                    e.printStackTrace()
                }
            }
            super.onPostExecute(s)
        }

        override fun doInBackground(vararg params: String?): String? {
            try {
                url =
                    "https://translate.googleapis.com/translate_a/single?" + "client=gtx&" + "sl=" +
                            langFrom + "&tl=" + langTo + "&dt=t&q=" + URLEncoder.encode(
                        text,
                        "UTF-8"
                    )
                val obj = URL(url)
                val con =
                    obj.openConnection() as HttpURLConnection
                con.setRequestProperty("User-Agent", "Mozilla/5.0")
                val `in` =
                    BufferedReader(InputStreamReader(con.inputStream))
                var inputLine: String?
                val response = StringBuffer()
                while (`in`.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                `in`.close()
                resp = response.toString()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

    fun onSuccess(translatedText: String?) {
        Log.d("MainActivity.TAG", "onSuccess: $translatedText")
        mutText.value = translatedText
    }

    fun onFailure(ErrorText: String?) {
        Log.d("MainActivity.TAG", "onFailure: $ErrorText")
    }
}