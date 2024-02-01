package com.example.myrecipes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myrecipes.dto.Recipe
import com.example.myrecipes.entity.toEntity
import com.example.myrecipes.dao.RecipeDao
import com.example.myrecipes.entity.toModel

class RecipeRepositoryRoomImpl(
    private val dao: RecipeDao,
) : RecipeRepository {
    private var allRecipes = emptyList<Recipe>()
    private var filteredRecipes = emptyList<Recipe>()
    private val data = MutableLiveData(allRecipes)

    init {
        getDataFromBase()
    }

    private fun getDataFromBase() {
        allRecipes = dao.getDataForFilter().map { it.toModel() }
        data.value = allRecipes
    }

    override fun get(): LiveData<List<Recipe>> = data

    override fun save(recipe: Recipe) {
        dao.save(recipe.toEntity())
        getDataFromBase()
    }

    override fun delete(id: Long) {
        dao.delete(id)
        getDataFromBase()
    }

    override fun like(id: Long) {
        dao.inFavorites(id)
        getDataFromBase()
    }

    override fun deleteAllFromFavorites() {
        dao.clearFavorites()
        getDataFromBase()
    }

    override fun filterByCategoryMain(categoryId: Int): Boolean {
        if (categoryId == 11) {
            data.value = dao.getDataForFilter().map { it.toModel() }
            return true
        } else {
            val chosenCategory = dao.filterByCategory(categoryId).map { it.toModel() }
            if (chosenCategory.isNullOrEmpty()) {
                return false
            }
            data.value = chosenCategory
            return true
        }
    }

    override fun removeCategoryFromFilterChips(categoryId: Int) {
        filteredRecipes = filteredRecipes.filterNot { it.categoryId == categoryId }
        data.value = filteredRecipes
    }

    override fun addCategoryToFilterChips(categoryId: Int) {
        filteredRecipes = filteredRecipes + allRecipes.filter { it.categoryId == categoryId }
        data.value = filteredRecipes
    }

    override fun startFilterChips() {
        filteredRecipes = allRecipes
        data.value = allRecipes
    }

    override fun search(query: String) {
        data.value = dao.searchByName(query).map { it.toModel() }
    }

}