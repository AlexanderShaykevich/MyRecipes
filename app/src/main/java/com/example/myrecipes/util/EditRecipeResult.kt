package com.example.myrecipes.util

import com.example.myrecipes.dto.Step
import java.io.Serializable

class EditRecipeResult(
    val name: String,
    val categoryId: Int,
    val imageUri: String?,
    val steps: List<Step>,
) : Serializable
