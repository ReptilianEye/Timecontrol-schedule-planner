package com.example.timecontrol.filter

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
sealed class Filter<T> {
    abstract val filterController: FilterController
    open fun resetFilter() {
        setFilter(default)
    }

    abstract fun get(): T
    abstract fun setFilter(value: T)
    abstract fun isActive(): Boolean
    abstract fun check(student: Student): Boolean
    abstract override fun toString(): String
    abstract val default: T

    data class Age(
        override val filterController: FilterController,
        override val default: Pair<Int, Int> = 1 to 100,
        var ageFiltered: Pair<Int, Int> = default,
    ) : Filter<Pair<Int, Int>>() {
        override fun get() = ageFiltered

        override fun setFilter(value: Pair<Int, Int>) {
            filterController.setAgeFilter(value)
        }

        override fun isActive() = ageFiltered != default
        override fun check(student: Student): Boolean {
            if (!isActive()) return true
            val age = Period.between(student.birthDate, LocalDate.now()).years
            return (age in ageFiltered.first..ageFiltered.second)
        }

        override fun toString() = "Age: ${ageFiltered.first} - ${ageFiltered.second}"

    }

    data class Levels(
        override val filterController: FilterController,
        override val default: Pair<LevelController, LevelController> = LevelController.getMinMaxLevel(),
        var levelsFiltered: Pair<LevelController, LevelController> = default,
    ) : Filter<Pair<LevelController, LevelController>>() {
        override fun get() = levelsFiltered
        override fun setFilter(value: Pair<LevelController, LevelController>) {
//            levelsFiltered = value
            filterController.setLevelsFilter(value)
        }

        override fun isActive(): Boolean = levelsFiltered != default
        override fun check(student: Student): Boolean {
            if (!isActive()) return true
            val studentLevel = LevelController.fromString(student.level)
            return studentLevel >= levelsFiltered.first && studentLevel <= levelsFiltered.second
        }

        override fun toString() =
            "Levels: ${levelsFiltered.first.level} - ${levelsFiltered.second.level}"
    }

    data class Available(
        override val filterController: FilterController,
        override val default: Boolean = true,
        var available: Boolean = default,
    ) : Filter<Boolean>() {
        override fun get() = available
        override fun resetFilter() {
            setFilter(false)
        }

        override fun setFilter(value: Boolean) {
//            available = value
            filterController.setAvailableFilter(value)
        }

        override fun isActive() = available == default
        override fun check(student: Student): Boolean {
            if (!isActive()) return true
            return student.arrivalDate.isBefore(LocalDate.now()) && student.departureDate.isAfter(
                LocalDate.now()
            )
        }

        override fun toString() = "Available"
    }

    data class Departed(
        override val filterController: FilterController,
        override val default: Boolean = false,
        var departed: Boolean = default,
    ) : Filter<Boolean>() {
        override fun get() = departed

        override fun setFilter(value: Boolean) {
//            departed = value
            filterController.setDepartedFilter(value)
        }

        override fun isActive() = departed
        override fun check(student: Student): Boolean {
            if (!isActive()) return true
            return student.departureDate.isBefore(LocalDate.now())
        }

        override fun toString() = "Departed"
    }

    data class Incoming(
        override val filterController: FilterController,
        override val default: Boolean = false,
        var incoming: Boolean = default,
    ) : Filter<Boolean>() {
        override fun get() = incoming
        override fun setFilter(value: Boolean) {
//            incoming = value
            filterController.setIncomingFilter(value)
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

    val ageFiltered = MutableStateFlow(Filter.Age(filterController = this))
    var levelsFiltered = MutableStateFlow(Filter.Levels(filterController = this))
    var available = MutableStateFlow(Filter.Available(filterController = this))
    var departed = MutableStateFlow(Filter.Departed(filterController = this))
    var incoming = MutableStateFlow(Filter.Incoming(filterController = this))
    val filters = combine(
        ageFiltered, levelsFiltered, available, departed, incoming
    ) { age, levels, available, departed, incoming ->
        listOf(age, levels, available, departed, incoming)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(2000), emptyList())

    fun getAllFilters() = filters.value

    fun setAgeFilter(newAge: Pair<Int, Int>) {
        ageFiltered.value = ageFiltered.value.copy(ageFiltered = newAge)
    }

    fun setLevelsFilter(newLevels: Pair<LevelController, LevelController>) {
        levelsFiltered.value = levelsFiltered.value.copy(levelsFiltered = newLevels)
    }

    fun setAvailableFilter(newAvailable: Boolean) {
        available.value = available.value.copy(available = newAvailable)
    }

    fun setDepartedFilter(newDeparted: Boolean) {
        departed.value = departed.value.copy(departed = newDeparted)
    }

    fun setIncomingFilter(newIncoming: Boolean) {
        incoming.value = incoming.value.copy(incoming = newIncoming)
    }

}