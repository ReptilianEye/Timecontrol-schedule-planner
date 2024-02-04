package com.example.timecontrol.components.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.timecontrol.R
import com.example.timecontrol.filter.FilterController
import com.example.timecontrol.ui.theme.Blue20
import com.example.timecontrol.ui.theme.BlueLogo

@Composable
fun FilterBar(onOpen: () -> Unit, filterController: FilterController) {
//    val filterController = FilterController().apply {
////        setAgeFilter(1 to 2)
////        setLevelsFilter(
////            LevelController.fromString("1A") to
////                    LevelController.fromString("1B")
////        )
//    }
    val filters by filterController.filters.collectAsStateWithLifecycle(initialValue = emptyList())
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .fillMaxHeight(0.1f)


    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            item {
                Button(
                    onClick = {
                        println("Filter button clicked")
                        onOpen()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = BlueLogo,
                        contentColor = Color.White
                    ), shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "filter",
                    )
                    Text(text = "Filter")
                }
            }
            itemsIndexed(filters.filter { it.isActive() }) { _, filter ->
                Button(
                    onClick = { filter.resetFilter() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Blue20,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(text = filter.toString())
                    Icon(
                        painter = painterResource(id = R.drawable.cancelicon),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, heightDp = 100)
fun FilterBarPreview() {
    FilterBar({}, FilterController())
}


