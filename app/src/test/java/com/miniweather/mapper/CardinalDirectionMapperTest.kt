package com.miniweather.mapper

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class CardinalDirectionMapperTest(private val value: Double, private val expected: Int) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "map {0} degrees to index {1}")
        fun testData(): List<Array<Any>> = listOf(
            arrayOf(0.0, 0),
            arrayOf(22.5, 1),
            arrayOf(45.0, 1),
            arrayOf(67.5, 2),
            arrayOf(90.0, 2),
            arrayOf(112.5, 3),
            arrayOf(135.0, 3),
            arrayOf(157.5, 4),
            arrayOf(180.0, 4),
            arrayOf(202.5, 5),
            arrayOf(225.0, 5),
            arrayOf(257.5, 6),
            arrayOf(270.0, 6),
            arrayOf(292.5, 7),
            arrayOf(315.0, 7),
            arrayOf(337.5, 0)
        )
    }

    @Test
    fun whenMap_returnsCorrectCardinality() {
        val actual = CardinalDirectionMapper.map(value)
        assertThat(actual).isEqualTo(expected)
    }
}
