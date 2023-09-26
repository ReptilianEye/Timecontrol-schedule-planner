package com.example.timecontrol.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.timecontrol.DragTarget
import com.example.timecontrol.DraggableScreen
import com.example.timecontrol.DropItem
import com.example.timecontrol.MainViewModel
import com.example.timecontrol.PersonUiItem
import com.example.timecontrol.database.getFullName
import com.example.timecontrol.ui.theme.Blue20
import com.example.timecontrol.viewModel.DatabaseViewModel

@Composable
fun ScheduleScreen(
    databaseViewModel: DatabaseViewModel, navController: NavController, owner: ViewModelStoreOwner
) {
    val mainViewModel = remember { MainViewModel() }
//    val lessons by databaseViewModel.lessons.collectAsStateWithLifecycle(initialValue = emptyList())
    val currentStudents by databaseViewModel.currentStudents.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    DraggableScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.8f))
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                currentStudents.forEach { student ->
                    items(currentStudents) {
                        DragTarget(
                            dataToDrop = student.student, viewModel = mainViewModel
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(Dp(screenWidth / 5f))
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
            }
            DropItem<PersonUiItem>(
                modifier = Modifier.size(Dp(screenWidth / 3.5f))
            ) { isInBound, personItem ->
                if (personItem != null) {
                    LaunchedEffect(key1 = personItem) {
                        mainViewModel.addPerson(personItem)
                    }
                }
                if (isInBound) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                1.dp, color = Color.Red, shape = RoundedCornerShape(15.dp)
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
                                1.dp, color = Color.White, shape = RoundedCornerShape(15.dp)
                            )
                            .background(
                                Color.Black.copy(0.5f), RoundedCornerShape(15.dp)
                            ), contentAlignment = Alignment.Center
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Added Persons",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                mainViewModel.addedPersons.forEach { person ->
                    Text(
                        text = person.name,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
