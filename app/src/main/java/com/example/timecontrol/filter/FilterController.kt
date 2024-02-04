package com.example.timecontrol.filter

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecontrol.database.Student
import com.example.timecontrol.utils.LevelController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.Period


//default: all people currently on place
sealed class Filter() {
    abstract val viewModel: FilterController
    abstract fun resetFilter()
    abstract fun isActive(): Boolean
    abstract fun check(student: Student): Boolean
    abstract override fun toString(): String
    data class Age(
        var ageFiltered: Pair<Int, Int>? = null, override val viewModel: FilterController,
    ) : Filter() {
        override fun resetFilter() {
            viewModel.setAgeFilter(null)
        }

        override fun isActive() = ageFiltered != null
        override fun check(student: Student): Boolean {
            if (!isActive()) return true
            val age = Period.between(student.birthDate, LocalDate.now()).years
            return (age in ageFiltered!!.first..ageFiltered!!.second)
        }

        override fun toString() = "Age: ${ageFiltered?.first} - ${ageFiltered?.second}"

    }

    data class Levels(
        var levelsFiltered: Pair<LevelController, LevelController>? = null,
        override val viewModel: FilterController,
    ) : Filter() {
        override fun resetFilter() {
            viewModel.setLevelsFilter(null)
        }

        override fun isActive(): Boolean = levelsFiltered != null
        override fun check(student: Student): Boolean {
            if (!isActive()) return true
            val studentLevel = LevelController.fromString(student.level)
            return studentLevel >= levelsFiltered!!.first && studentLevel <= levelsFiltered!!.second
        }

        override fun toString() =
            "Levels: ${levelsFiltered?.first?.level} - ${levelsFiltered?.second?.level}"
    }

    data class Available(
        var areAvailable: Boolean = true,
        override val viewModel: FilterController,
    ) : Filter() {
        override fun resetFilter() {
            viewModel.setAvailableFilter(false)
        }

        override fun isActive() = areAvailable
        override fun check(student: Student): Boolean {
            if (!isActive()) return true
            return student.arrivalDate.isBefore(LocalDate.now()) && student.departureDate.isAfter(
                LocalDate.now()
            )
        }

        override fun toString() = "Available"
    }

    data class Departed(var departed: Boolean = false, override val viewModel: FilterController) :
        Filter() {
        override fun resetFilter() {
            viewModel.setDepartedFilter(false)
        }

        override fun isActive() = departed
        override fun check(student: Student): Boolean {
            if (!isActive()) return true
            return student.departureDate.isBefore(LocalDate.now())
        }

        override fun toString() = "Departed"
    }

    data class Incoming(var incoming: Boolean = false, override val viewModel: FilterController) :
        Filter() {
        override fun resetFilter() {
            viewModel.setDepartedFilter(false)
        }

        override fun isActive() = incoming
        override fun check(student: Student): Boolean {
            if (!isActive()) return true
            return student.arrivalDate.isAfter(LocalDate.now())
        }

        override fun toString() = "Incoming"
    }

}

class FilterController : ViewModel() {
    private var ageFiltered = MutableStateFlow(Filter.Age(viewModel = this))
    private var levelsFiltered = MutableStateFlow(Filter.Levels(viewModel = this))
    private var areAvailable = MutableStateFlow(Filter.Available(viewModel = this))
    private var departed = MutableStateFlow(Filter.Departed(viewModel = this))
    private var incoming = MutableStateFlow(Filter.Incoming(viewModel = this))
    val filters = combine(
        ageFiltered, levelsFiltered, areAvailable, departed, incoming
    ) { age, levels, available, departed, incoming ->
        listOf(age, levels, available, departed, incoming)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun filter(student: Student): Boolean {
        return filters.value.all { it.check(student) }
    }

    fun setAgeFilter(age: Pair<Int, Int>?) {
        ageFiltered.value = ageFiltered.value.copy(ageFiltered = age)
    }

    fun setLevelsFilter(levels: Pair<LevelController, LevelController>?) {
        levelsFiltered.value = levelsFiltered.value.copy(levelsFiltered = levels)
    }

    fun setAvailableFilter(available: Boolean) {
        areAvailable.value = areAvailable.value.copy(areAvailable = available)
    }

    fun setDepartedFilter(departed: Boolean) {
        this.departed.value = this.departed.value.copy(departed = departed)
    }
}