package com.example.pametnipaketnik

import okhttp3.OkHttpClient
import okhttp3.Request
import android.content.Context
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class AdressReader {

    fun synchronousGetRequest(url: String,destinationList: MutableList<List<String>>,item: List<String> ): String {
        val client = OkHttpClient()
        val buildUrl=    url.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("destinations", destinationList.joinToString(" | ") { it.joinToString ("  ") })
            .addQueryParameter("origins", item.joinToString("  "))
            .addQueryParameter("key", "APIKEY").build()
   //     println("build url: $buildUrl")
        val request = Request.Builder()
            .url(
                buildUrl
            )
            .build()

        val response = client.newCall(request).execute()

        return response.body?.string() ?: ""
    }

    fun Testread(context: Context) {
        var inputStream: InputStream? = null
        inputStream = try {
            context.assets.open("realWorldProblemDistance.tsp")
        } catch (e: IOException) {
            System.err.println("File not found!")
            return
        }

        fun synchronousGetRequest(url: String): String {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string() ?: ""
        }

        val inputAsString = inputStream!!.bufferedReader().use { it.readText() }
        val linesString = inputAsString.split("\n")
        val stringsArray: MutableList<List<String>> = mutableListOf()
        for (line in linesString) {
            if (line != "") {

                val namesString = line.split(" ")
                println(namesString.size)
                stringsArray.add(namesString)
                //       println(namesString)
                //println(namesString)
            }
        }
    }
    // Example
    // usage:
    fun read(context: Context) {
        var inputStream: InputStream? = null
        inputStream = try {
            context.assets.open("lokacije.csv")
        } catch (e: IOException) {
            System.err.println("File not found!")
            return
        }

        fun synchronousGetRequest(url: String): String {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            return response.body?.string() ?: ""
        }

        val inputAsString = inputStream!!.bufferedReader().use { it.readText() }
        val linesString = inputAsString.split("\n")
        val stringsArray: MutableList<List<String>> = mutableListOf()
        for (line in linesString) {
            if (line != "") {

                val namesString = line.split(";")
                stringsArray.add(namesString)
         //       println(namesString)
                //println(namesString)
            }
        }
        //println(stringsArray.joinToString(" | ") { it.joinToString ("  ") })
//        var inputStreamNew: InputStream? = null
//        inputStreamNew = try {
//            context.assets.open("citiesDistanceAndTime.json")
//        } catch (e: IOException) {
//            System.err.println("File not found!")
//            return
//        }
        val timeMatrix: MutableList<List<Int>> = mutableListOf()
        val distanceMatrix: MutableList<List<Int>> = mutableListOf()
//        val inputAsStringNew = inputStreamNew!!.bufferedReader().use { it.readText() }
//        val jsonObject: JSONObject = JSONObject(inputAsStringNew)
//        val rows = jsonObject.getJSONArray("rows")

        var t=0;
        for(item in stringsArray) {
            println("item: "+stringsArray.size)
            if(t<122)
            {
                t++
                continue

            }
            println("loop outside")

            val tempTimeMatrix: MutableList<Int> = mutableListOf()
            val tempDistanceMatrix: MutableList<Int> = mutableListOf()
            var itemsLeft= stringsArray.size
            var currentItem=0;
            var isROwDone=false
            while(!isROwDone) {
                 var sublist : MutableList<List<String>> = mutableListOf()
                if(itemsLeft>20) {

                    sublist = stringsArray.subList(fromIndex = currentItem, toIndex = currentItem+20)
                    currentItem+=20;
                    itemsLeft-=20
                }
                else if(itemsLeft<20)
                {isROwDone=true
                    sublist = stringsArray.subList(fromIndex = currentItem, toIndex = currentItem+itemsLeft)

                }
                var result = synchronousGetRequest(
                    "https://maps.googleapis.com/maps/api/distancematrix/json",
                    sublist,
                    item
                )
                println(result)
                var jsonObject: JSONObject = JSONObject(result)
                var rows = jsonObject.getJSONArray("rows")
                for (i in 0 until rows.length()) {
                    val elements = rows.getJSONObject(i).getJSONArray("elements")
                    for (j in 0 until elements.length()) {
                       // println("loop inside")
                        val itemT = elements.getJSONObject(j)
                       // println(itemT)
                        val distance = itemT.getJSONObject("distance").getInt("value")
                        val time = itemT.getJSONObject("duration").getInt("value")
                        tempTimeMatrix.add(time)
                        tempDistanceMatrix.add(distance)
                        //  println(distance)
                    }
                }
            }
            timeMatrix.add(tempTimeMatrix)
            distanceMatrix.add(tempDistanceMatrix)
            if(t==141)
            {
                break

            }
            t++
        }
        println(distanceMatrix.joinToString("\n") { it.joinToString(" ") })
        println("-------------------------------------------------------------------------------------------------------------------------------------------------------------")

        println(timeMatrix.joinToString("\n") { it.joinToString(" ") })
        //  println(jsonObject.toString())


    }

}