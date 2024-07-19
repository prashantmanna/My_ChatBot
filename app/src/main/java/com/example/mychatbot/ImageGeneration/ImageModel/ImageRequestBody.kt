package com.example.mychatbot.ImageGeneration.ImageModel

data class ImageRequestBody(
    var providers : String,
    var text : String,
    var resolution : String,
    var n : String
)
