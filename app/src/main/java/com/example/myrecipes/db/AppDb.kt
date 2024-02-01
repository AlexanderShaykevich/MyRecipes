package com.example.myrecipes.db

import android.content.Context
import androidx.room.*
import com.example.myrecipes.entity.Converter
import com.example.myrecipes.entity.RecipeEntity
import com.example.myrecipes.dao.RecipeDao

@Database(
    entities = [RecipeEntity::class],
    version = 1
)
@TypeConverters(Converter::class)

abstract class AppDb : RoomDatabase() {
    abstract val recipeDao: RecipeDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .allowMainThreadQueries()
                .addTypeConverter(Converter())
                .build()
    }
}

