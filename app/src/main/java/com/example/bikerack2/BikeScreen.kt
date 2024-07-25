package com.example.bikerack2

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.avgjoe.BikeRack.Bike
import com.google.gson.Gson
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeScreen(navController: NavHostController) {


    /*
        Declares variables to store data and set flags for LaunchedEffects
     */
    val req = BikeRequest()
    var data by remember { mutableStateOf(arrayOf<Bike>()) }
    var cartTotal by remember { mutableStateOf(0) }
    var populated by remember { mutableStateOf(false) }
    var selection by remember { mutableStateOf(false) }
    var makeTRX by remember { mutableStateOf(false) }
    var alertCustomer by remember { mutableStateOf(false) }
    var priorCustomerTRX by remember { mutableStateOf(false) }
    var deleteCustomer by remember { mutableStateOf(false) }
    var newCustomerTRX by remember { mutableStateOf(false) }
    var refreshPage by remember { mutableStateOf(false) }
    var customerID by remember { mutableStateOf("") }
    var receipt by remember { mutableStateOf("") }
    val bikeMap = mutableMapOf<String?, Int>()
    var customerName by remember { mutableStateOf("") }


    /*
        Creates a UI template for inventory items
     */
    @Composable
    fun SelectionBox(): Int {
        var quantity by remember { mutableStateOf(0) }
        Box(modifier = Modifier.fillMaxWidth()) {
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = {
                    if (quantity > 0) {
                        quantity--
                    }
                    selection = true
                }) { Text(text = "-") }

                Text(text = quantity.toString())

                Button(onClick = {
                    quantity++
                    selection = true
                }) { Text(text = "+") }
            }
        }
        return quantity
    }

//    fun check() {
//        if (req.customerID != "") {
//            priorCustomerTRX = true
//        } else if (req.customerID == "" && req.confirm) {
//            newCustomerTRX = true
//        }
//    }


    /*
        Makes an API request to find what items are for sale and the prices
     */
    LaunchedEffect(Unit) {
        val rawData = req.getBikes()
        data = Gson().fromJson(rawData, Array<Bike>::class.java)
    }

    /*
        Creates the UI
     */
    Column(modifier = Modifier.fillMaxWidth()) {
        // Populate the screen with Bike data using the UI function template above
        if (data.isNotEmpty()) {
            if (!populated) {
                for (i in 0..data.size - 1) {
                    bikeMap.plus(Pair(data[i].bikeName, 0))
                }
            }
            data.forEach {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = it.bikeName + " " + it.cost)
                        bikeMap[it.bikeName] = SelectionBox()
                    }
            }

        }

        // Tracks user input for each item in a Map
        if (selection) {
            selection = false
            cartTotal = 0
            // Calculates total by multiplying the values in the Map with the cost values in data array
            for (i in 0..data.size - 1) {
                cartTotal += bikeMap[data[i].bikeName]!! * data[i].cost!!
            }
        }

        // Displays total
        Text(text = "Total: $cartTotal")

        // Input field for receiving customer name from user
        OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Full Name") })

        // Process user order, making sure a name was provided
        Row {
            Button(onClick = {
                if (customerName != "") {
                    // Set flag to make LaunchedEffect
                    makeTRX = true
                } else {
                    alertCustomer = true
                }
            }) {
                Text(text = "Make TRX")
            }
            Button(onClick = {
                if (customerName != "") {
                    // Set flag to make LaunchedEffect
                    deleteCustomer = true
                } else {
                    alertCustomer = true
                }
            }) {
                Text(text = "Delete Customer")
            }
        }

        // Alert customer to input name before proceeding
        if (alertCustomer) {
            Text(text = "Please Enter Name!")
        }

        // Check if transaction flag was set
        if (makeTRX) {
            makeTRX = false
            receipt = ""

            // Make a string to summarize customer order
            for (i in 0..data.size - 1) {
                if (bikeMap[data[i].bikeName]!! != 0) {
                    if (receipt != "") {
                        receipt += "-"
                    }
                    receipt += bikeMap[data[i].bikeName]!!.toString()
                    receipt += data[i].bikeName
                }
            }

            // API request to check if customer name was previously used
            LaunchedEffect(Unit) {
                req.getid(customerName)
                customerID = req.customerID

                if (req.customerID != "") {
                    priorCustomerTRX = true
                } else if (req.customerID == "" && req.confirm) {
                    newCustomerTRX = true
                }
            }

        }

        // User is a new customer, add name to customer list
        if (newCustomerTRX) {
            LaunchedEffect(Unit) {
                req.addNewCustomer(customerName)
                req.getid(customerName)
                customerID = req.customerID
                req.customerTRX(receipt, cartTotal, customerID)
            }

            refreshPage = true
        }

        // User is a returning customer, make record of order and ID number
        if (priorCustomerTRX) {
            LaunchedEffect(Unit) {
                req.customerTRX(receipt, cartTotal, customerID)
            }

            // Reset Page
            refreshPage = true
        }

        // User is deleting a customer from all records
        if (deleteCustomer) {
            LaunchedEffect(Unit) {
                req.getid(customerName)
                req.deleteCustomer()
                req.deleteCustomerTRX()
            }
            refreshPage = true
        }

        // User is adding a new customer and make a transaction
        if(newCustomerTRX) {
            Text(text = "Thank you for your first patronage $customerName!")
        } else if (priorCustomerTRX) {
            Text(text = "Welcome back $customerName!")
        } else if (deleteCustomer) {
            Text(text = "We'll miss you $customerName!")
        }

        // A transaction was made and needs to open a new screen
        if (refreshPage) {
            Button(onClick = {
                refreshPage = false
                navController.navigate("BikeScreen") }) {
                Text(text = "Clear")
            }
        }

    }
}