package com.example.chatapp.ui.theme.screens.home


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.chatapp.data.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val channels = viewModel.channels.collectAsState()
    val addChannel = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    Scaffold() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .clickable{addChannel.value = true}
        ){
            LazyColumn {
                items(channels.value) {channel ->
                    Column {
                        Text(
                            text = channel.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .clickable{
                                    navController.navigate("chat/${channel.id}")
                                }
                            )
                    }

                }

            }
            if (addChannel.value){
                ModalBottomSheet(
                    onDismissRequest = { addChannel.value = false},
                    sheetState = sheetState
                ) {
                    AddChannelDialog{
                        viewModel.addChannel(it)
                        addChannel.value = false

                    }
                }
            }
        }
    }

@Composable
fun AddChannelDialogue(
    onAddChannel: (String) -> Unit
){
    val channelName = remember { mutableStateOf("") }

    Column( modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "add Channel")
        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = channelName.value,
            onValueChange = {channelName.value = it},
            label = {Text(text = "Channel Name")},
            singleLine = true
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Button(
            onClick = {onAddChannel(channelName.value)},
            modifier = Modifier.fillMaxWidth()
            ) {
            Text(text = "Add")

        }


    }


}









}



@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}