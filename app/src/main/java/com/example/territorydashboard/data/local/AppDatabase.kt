package com.example.territorydashboard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TerritorioEntity::class,
        IndicadorEntity::class,
        LeituraEntity::class,
        DecisaoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): AppDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                val banco = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "territory.db"
                ).build()
                instancia = banco
                banco
            }
        }
    }
}