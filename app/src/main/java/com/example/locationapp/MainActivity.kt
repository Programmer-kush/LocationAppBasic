package com.example.locationapp

import android.content.Context
import android.os.Bundle
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //val viewModel:LocationViewModel=viewModel()
            val viewModel=LocationViewModel()
            LocationAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    myApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun myApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val locationUtils=LocationUtils(context)
    LocationDisplay(locationUtils,context,viewModel)
}





@Composable
fun LocationDisplay(
    locationUtils: LocationUtils,
    context : Context,
    viewModel: LocationViewModel
){

    val location=viewModel.location.value

    //will return a string and will be saved as adress
    val adress=location?.let {
        locationUtils.reverseGeocodeLocation(location)
    }

    val requestPermissionLauncher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions() ,
        onResult ={
            permissions ->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true){
                //have permissions

                locationUtils.requestLocationUpdates(viewModel)

            }
            else{
                //ask permissions

                val rationaleRequired=ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context ,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )


                if(rationaleRequired){
                    Toast.makeText(context,
                        "Location required for this feature",Toast.LENGTH_LONG).show()
                }

                else{
                    Toast.makeText(context,
                        "Location permission required enable it",Toast.LENGTH_LONG).show()
                }

            }
        }
        )




    Column(
        modifier=Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(location!=null){
            Text(text = "Longitude: ${location.Longitude}")
            Text(text = "Latitude: ${location.Latitude}")
            Text("${adress}")
        }
        else{
            Text(text = "Location not available")
        }


        Button(onClick = {
            if(locationUtils.hasLocationPermission(context)){
                //permission granted
                locationUtils.requestLocationUpdates(viewModel)
            }
            else{
                //request location permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                )
            }
        }) {
            Text(text = "Get Location")
        }
    }
}
