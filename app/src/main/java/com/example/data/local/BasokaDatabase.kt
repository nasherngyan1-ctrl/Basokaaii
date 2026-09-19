package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    UserEntity::class,
    ConversationEntity::class,
    MessageEntity::class,
    AiMemoryEntity::class
  ],
  version = 4,
  exportSchema = false
)
abstract class BasokaDatabase : RoomDatabase() {
  abstract fun basokaDao(): BasokaDao

  companion object {
    @Volatile
    private var INSTANCE: BasokaDatabase? = null

    fun getDatabase(context: Context): BasokaDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          BasokaDatabase::class.java,
          "basoka_ai_db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
