package com.example.timecontrol

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timecontrol.calendaritem.CalendarItem
import com.example.timecontrol.data.dto.Quote
import com.example.timecontrol.database.Student
import com.example.timecontrol.quote.Quote
import com.example.timecontrol.statstile.StatsTile
import com.example.timecontrol.studentslistitem.StudentsListItem
import com.example.timecontrol.ui.theme.Blue20
import java.time.format.DateTimeFormatter

@Composable
fun StudentsScreen(students: List<Student>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
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
//                    modifier = Modifier
//                        .width(390.dp)
//                        .height(102.dp)
//                )
//            }

        items(20) {
            StudentsListItem(
                level = "D",
                name = "Tomek",
                telNo = "123456789",
                placeOfStay = "Chałupy",
                departureDate = "10.09",
                onClick = {},
                modifier = Modifier
                    .width(390.dp)
                    .height(102.dp)
            )
        }
    }
}

