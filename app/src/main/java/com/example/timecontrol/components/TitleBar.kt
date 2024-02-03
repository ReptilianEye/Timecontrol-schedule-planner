package com.example.timecontrol.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.timecontrol.R


@Composable
fun TitleBar(onLogoClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
            .padding(start = 10.dp, end = 10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.timecontrollogo),
            contentDescription = "Notification", modifier = Modifier
                .clickable {
                    onLogoClick()
                }
        )

    }
    Divider(
        color = Color.Black,
        thickness = Dp.Hairline,

    )
}

@Composable
@Preview(showBackground = true, heightDp = 100)
fun TitleBarPreview() {
    TitleBar(onLogoClick = { println("Logo tapped") })
}
