package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppSettingsEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.KloterEntity
import com.example.data.model.KloterMemberEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.ProductEntity

@Database(
  entities = [
    CustomerEntity::class,
    ProductEntity::class,
    KloterEntity::class,
    KloterMemberEntity::class,
    PaymentEntity::class,
    AppSettingsEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class HarjayaDatabase : RoomDatabase() {
  abstract fun customerDao(): CustomerDao
  abstract fun productDao(): ProductDao
  abstract fun kloterDao(): KloterDao
  abstract fun kloterMemberDao(): KloterMemberDao
  abstract fun paymentDao(): PaymentDao
  abstract fun appSettingsDao(): AppSettingsDao

  companion object {
    @Volatile
    private var INSTANCE: HarjayaDatabase? = null

    fun getDatabase(context: Context): HarjayaDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          HarjayaDatabase::class.java,
          "harjaya_mebel_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
