package com.example.bikerack2

import android.util.Log
import com.example.avgjoe.BikeRack.Customer
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class BikeRequest() {
    var customerID = ""
    var confirm = false
    var madeNewUser = false
    var TAG = "Error"

    suspend fun getBikes(): String {
        val client = HttpClient()
        var data: String = ""

        try {
            coroutineScope {
                var read: HttpResponse = client.request("http://10.0.2.2:8080/allbikes") {

                    method = HttpMethod.Get

                    headers {
//                        contentType(ContentType.Application.Json)
                        append(HttpHeaders.Accept, "application/json")
                        append(HttpHeaders.Accept, "text/html")
                    }
                }

                if (read.status == HttpStatusCode.OK) {
                    data = read.readText()
//                    val id = Gson().fromJson(data, Customer::class.java)
//                    customerID = id.userid.toString()//.toString()
                } else if (read.status == HttpStatusCode.BadRequest) { //400
                    Log.e(TAG, "Bad Request")
                } else {
                    Log.e(TAG, "Undefined Error")
                }
//                data = read.readText()
            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        } finally {
            client.close()
        }
        return data
    }

    suspend fun getid(name: String) = runBlocking {

        val client = HttpClient()
        var data: String = ""
        var link = "http://10.0.2.2:8080/checkcustomer/customerName=${name.replace(" ", "_")}"

        try {
            coroutineScope {
                var read: HttpResponse =
                    client.request(link) {

                        method = HttpMethod.Get

                        headers {
//                        contentType(ContentType.Application.Json)
                            append(HttpHeaders.Accept, "application/json")
                            append(HttpHeaders.Accept, "text/html")
                        }
                    }

                if (read.status == HttpStatusCode.OK) {
                    data = read.readText()
                    val id = Gson().fromJson(data, Customer::class.java)
                    customerID = id.userid.toString()//.toString()
                } else if (read.status == HttpStatusCode.BadRequest) { //400
                    Log.e(TAG, "Bad Request")
                } else {
                    Log.e(TAG, "Undefined Error")
                }

//                if(read.status == HttpStatusCode.NotFound) {
//                    customerID = ""
//                } else {
//
//                    data = read.readText()
//                    val id = Gson().fromJson(data, Customer::class.java)
//                    customerID = id.userid.toString()//.toString()
//                }

            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        } finally {
            client.close()
        }

        confirm = true

    }

    suspend fun returningCustomerAddTRX(order: String, total: Int, id: String = "") {
        val client = HttpClient()

        //add customer, make trx w
        //make trx with id
        if (id == "")
            Log.e("asdf","no id found")
        else {
            try {
                coroutineScope {
                    var link = "http://10.0.2.2:8080/addtrx/order=$order/total=$total/userid=$id"
                    var read: HttpResponse =
                        client.request(link.replace(" ", "_")) {
                            method = HttpMethod.Post

                            headers {
                                append(HttpHeaders.Accept, "application/json")
                                append(HttpHeaders.Accept, "text/html")
                            }
                        }
                    //data = read.readText()
                    if(read.status != HttpStatusCode.OK)
                        Log.e(TAG, "Something Went Wrong")
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            } finally {
                client.close()
            }
        }
    }
    suspend fun newCustomerAddTRX(customerName: String = "") {
        val client = HttpClient()

        //add customer, make trx w
        //make trx with id
        if (customerName == "")
            Log.e("asdf","no name found")
        else {
            try {
                coroutineScope {
                    var link = "http://10.0.2.2:8080/newCustomer/name=${customerName.replace(" ", "_")}"
                    var read: HttpResponse =
                        client.request(link) {
                            method = HttpMethod.Post

                            headers {
                                append(HttpHeaders.Accept, "application/json")
                                append(HttpHeaders.Accept, "text/html")
                            }
                        }
                    //data = read.readText()
                    if(read.status != HttpStatusCode.OK)
                        Log.e(TAG, "Something Went Wrong")
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            } finally {
                client.close()
            }
        }
        madeNewUser = true
    }
    suspend fun deleteCustomer() {
        val client = HttpClient()

        try {
            coroutineScope {
                var link = "http://10.0.2.2:8080/delete-customer/id=$customerID"
                var read: HttpResponse =
                    client.request(link) {
                        method = HttpMethod.Delete

                        headers {
                            append(HttpHeaders.Accept, "application/json")
                            append(HttpHeaders.Accept, "text/html")
                        }
                    }
                //data = read.readText()
                if(read.status != HttpStatusCode.OK)
                    Log.e(TAG, "Something Went Wrong")
            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        } finally {
            client.close()
        }
//        madeNewUser = true
    }
    suspend fun deleteCustomerTRX() {
        val client = HttpClient()

        try {
            coroutineScope {
                var link = "http://10.0.2.2:8080/delete-customer-trx/id=$customerID"
                var read: HttpResponse =
                    client.request(link) {
                        method = HttpMethod.Delete

                        headers {
                            append(HttpHeaders.Accept, "application/json")
                            append(HttpHeaders.Accept, "text/html")
                        }
                    }
                //data = read.readText()
                if(read.status != HttpStatusCode.OK)
                    Log.e(TAG, "Something Went Wrong")
            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        } finally {
            client.close()
        }
//        madeNewUser = true
    }
}