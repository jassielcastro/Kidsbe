package com.ajcm.kidstube

import org.junit.Assert
import org.junit.Test

class FilteringTest {

    private val tempBlackListVideos: List<String> by lazy {
        listOf(
            "halloween",
            "death",
            "peppa",
            "carcel",
            "bruja",
            "witch",
            "calavera",
            "muerto",
            "fantasma",
            "ghost",
            "susto",
            "scare",
            "mounstro",
            "mounstruo",
            "monster",
            "zombie",
            "apocalypse",
            "terror",
            "miedo"
        )
    }

    private val tempListVideos: List<String> by lazy {
        listOf(
            "halloween",
            "peppa Pig",
            "carcel del infierno",
            "paw patrol",
            "numbers and games",
            "number of death",
            "muerto",
            "fantasma"
        )
    }

    @Test
    fun `filter list`() {
        val filteredVideos = tempListVideos.filter { video ->
            tempBlackListVideos.none {
                video.toLowerCase().contains(it)
            }
        }

        println("FilteringTest.filter list -> $filteredVideos")

        Assert.assertTrue(filteredVideos.size == 2)
    }

}