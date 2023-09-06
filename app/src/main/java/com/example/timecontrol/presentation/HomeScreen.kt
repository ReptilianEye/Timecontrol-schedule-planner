package com.example.timecontrol.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.timecontrol.R
import com.example.timecontrol.calendaritem.CalendarItem
import com.example.timecontrol.preferences.dto.Quote
import com.example.timecontrol.quote.Quote
import com.example.timecontrol.statstile.StatsTile
import com.example.timecontrol.ui.theme.Blue20
import com.example.timecontrol.viewModel.DatabaseViewModel

@Composable
fun HomeScreen(
    quote: Quote,
    navController: NavController,
    viewModel: DatabaseViewModel,
    context: Context
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Quote(
            author = quote.author,
            content = quote.quote, modifier = Modifier
                .width(345.dp)
                .height(140.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
//                        modifier = Modifier.height(172.dp)
        ) {
            repeat(2) {
                StatsTile(
                    content = "10", title = "abc", onClick = {
                        Toast.makeText(context, "Dziala", Toast.LENGTH_LONG).show()
                    }, modifier = Modifier.size(180.dp)
                )
            }

        }
        Box(
            modifier = Modifier
                .background(Blue20)
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val weekNo = "1.07 - 7.07"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = {/*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_left),
                            contentDescription = "arrow left"
                        )
                    }
                    Text(
                        text = "Week: $weekNo",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = {/*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_right),
                            contentDescription = "arrow right"
                        )
                    }
                }
                Divider(thickness = 1.dp, color = Color.Black, modifier = Modifier.padding(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(7) {
                        CalendarItem(
                            dayOfWeek = "Mon",
                            date = "1.01",
                            modifier = Modifier
                                .width(42.dp)
                                .height(66.dp)
                        )
                    }
                }
            }

        }

    }
}

