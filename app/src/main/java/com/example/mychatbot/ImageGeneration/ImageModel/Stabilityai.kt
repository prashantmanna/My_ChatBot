package com.example.mychatbot.ImageGeneration.ImageModel

data class Stabilityai(
    val cost: Double,
    val items: List<Item>,
    val status: String
)