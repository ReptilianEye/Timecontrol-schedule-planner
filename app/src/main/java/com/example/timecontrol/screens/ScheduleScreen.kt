package com.example.timecontrol.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.example.timecontrol.DragTarget
import com.example.timecontrol.DraggableScreen
import com.example.timecontrol.DropItem
import com.example.timecontrol.database.StudentWithLessons
import com.example.timecontrol.database.getFullName
import com.example.timecontrol.lessonRangePretty
import com.example.timecontrol.ui.theme.Blue10
import com.example.timecontrol.ui.theme.Blue20
import com.example.timecontrol.ui.theme.Blue40
import com.example.timecontrol.viewModel.DatabaseViewModel
import com.example.timecontrol.viewModel.ScheduleViewModel
import com.google.relay.compose.RowScopeInstanceImpl.weight


@Composable
fun ScheduleScreen(
    databaseViewModel: DatabaseViewModel,
    navController: NavController,
    owner: ViewModelStoreOwner
) {
    val viewModel = remember {
        ScheduleViewModel(
            databaseViewModel
        )
    }
    val students = databaseViewModel.currentStudents.collectAsState(initial = emptyList())
    val instructors = databaseViewModel.currentInstructors.collectAsState(initial = emptyList())
    val lessonRanges = listOf(
        Pair("9:00", "11:00"),
        Pair("11:15", "13:15"),
        Pair("13:30", "15:30"),
        Pair("15:45", "17:45"),
        Pair("18:00", "20:00"),
    )

    DraggableScreen(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp)
                    .background(Blue10),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(students.value) { student ->
                    DragTarget(
                        dataToDrop = student, viewModel = viewModel
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .shadow(5.dp, RoundedCornerShape(15.dp))
                                .background(Blue20, RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = student.student.getFullName(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            var i = 0
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    titleBox()//fill
                    lessonRanges.forEach {
                        titleBox(lessonRangePretty(it))//lesson ranges
                    }
                }
                instructors.value.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        titleBox(it.instructor.nickname)//instructor name
                        repeat(lessonRanges.size) {
                            dropBox(i, viewModel)
                            i += 1
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun titleBox(mess: String = "lesson") {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Box(
        modifier = Modifier
            .width((screenWidth / 6f).dp)
            .background(Blue40)
    ) {

        Text(text = mess)
    }
}

@Composable
fun dropBox(i: Int = -1, viewModel: ScheduleViewModel) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    DropItem<StudentWithLessons>(
        Modifier
            .size((screenWidth / 6f).dp)
    )
    { isInBound, student ->
        if (student != null) {
            Toast.makeText(
                LocalContext.current, student.student.firstName + i.toString(), Toast.LENGTH_LONG
            ).show()
            LaunchedEffect(key1 = student) {
                viewModel.addStudentToLesson(student, i)
            }
        }
        if (isInBound) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        1.dp,
                        color = Color.Red,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .background(Color.Gray.copy(0.5f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add Person",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        1.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .background(
                        Color.Black.copy(0.5f),
                        RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add Person",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}
