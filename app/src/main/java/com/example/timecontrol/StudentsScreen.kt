package com.example.timecontrol

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.timecontrol.database.Student
import com.example.timecontrol.studentslistitem.StudentsListItem
import com.example.timecontrol.ui.theme.Blue10
import com.example.timecontrol.ui.theme.BlueLogo
import com.example.timecontrol.ui.theme.White80
// TODO when datebase connected
// fun StudentsScreen(students: List<Student>) {

@Composable
fun StudentsScreen() {
    Box()
    {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)

            //uncomment when database connected
        ) {
//        students.forEachIndexed { index, student ->
//            item(index) {
//                StudentsListItem(
//                    level = student.level,
//                    name = student.name,
//                    telNo = student.phoneNumber,
////                    placeOfStay = student.placeOfStay,
//                    departureDate = student.departureDate.format(DateTimeFormatter.ofPattern("dd.MM"))
//                        .toString(),
//                    onClick = {},
//        background = if (it % 2 == 0) White80 else Blue10,
//                    modifier = Modifier
//                        .width(390.dp)
//                        .height(102.dp)
//                )
//            }
            //comment when database connected
            items(20) {
                StudentsListItem(
                    level = "D",
                    name = "Tomek",
                    telNo = "123456789",
                    placeOfStay = "Chałupy",
                    departureDate = "10.09",
                    onClick = {},
                    background = if (it % 2 == 0) White80 else Blue10,
                    modifier = Modifier.padding(2.dp)
//                probably redundant - need to try on other device
//                modifier =
//                    Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight(0.5f).height(102.dp)
                )

            }
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(75.dp)
                .align(Alignment.BottomEnd),
            onClick = { /*TODO*/ },
            containerColor = BlueLogo
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add Student"
            )
        }
    }
}
