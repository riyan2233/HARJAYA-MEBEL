package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppSettingsEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.KloterEntity
import com.example.data.model.KloterMemberEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
  @Query("SELECT * FROM customers ORDER BY name ASC")
  fun getAllCustomers(): Flow<List<CustomerEntity>>

  @Query("SELECT * FROM customers WHERE id = :id")
  suspend fun getCustomerById(id: Long): CustomerEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomer(customer: CustomerEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(customers: List<CustomerEntity>)

  @Update
  suspend fun updateCustomer(customer: CustomerEntity)

  @Delete
  suspend fun deleteCustomer(customer: CustomerEntity)

  @Query("SELECT COUNT(*) FROM customers")
  suspend fun getCount(): Int
}

@Dao
interface ProductDao {
  @Query("SELECT * FROM products ORDER BY category ASC, name ASC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id")
  suspend fun getProductById(id: Long): ProductEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(products: List<ProductEntity>)

  @Update
  suspend fun updateProduct(product: ProductEntity)

  @Delete
  suspend fun deleteProduct(product: ProductEntity)

  @Query("SELECT COUNT(*) FROM products")
  suspend fun getCount(): Int
}

@Dao
interface KloterDao {
  @Query("SELECT * FROM kloters ORDER BY id DESC")
  fun getAllKloters(): Flow<List<KloterEntity>>

  @Query("SELECT * FROM kloters WHERE id = :id")
  suspend fun getKloterById(id: Long): KloterEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertKloter(kloter: KloterEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(kloters: List<KloterEntity>)

  @Update
  suspend fun updateKloter(kloter: KloterEntity)

  @Delete
  suspend fun deleteKloter(kloter: KloterEntity)

  @Query("SELECT COUNT(*) FROM kloters")
  suspend fun getCount(): Int
}

@Dao
interface KloterMemberDao {
  @Query("SELECT * FROM kloter_members")
  fun getAllMembers(): Flow<List<KloterMemberEntity>>

  @Query("SELECT * FROM kloter_members WHERE kloterId = :kloterId ORDER BY lotteryNumber ASC")
  fun getMembersByKloter(kloterId: Long): Flow<List<KloterMemberEntity>>

  @Query("SELECT * FROM kloter_members WHERE customerId = :customerId")
  fun getMembersByCustomer(customerId: Long): Flow<List<KloterMemberEntity>>

  @Query("SELECT * FROM kloter_members WHERE id = :id")
  suspend fun getMemberById(id: Long): KloterMemberEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMember(member: KloterMemberEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(members: List<KloterMemberEntity>)

  @Update
  suspend fun updateMember(member: KloterMemberEntity)

  @Delete
  suspend fun deleteMember(member: KloterMemberEntity)

  @Query("SELECT * FROM kloter_members WHERE deliveryStatus != 'MENUNGGU_UNDIAN'")
  fun getActiveShipments(): Flow<List<KloterMemberEntity>>

  @Query("SELECT COUNT(*) FROM kloter_members")
  suspend fun getCount(): Int
}

@Dao
interface PaymentDao {
  @Query("SELECT * FROM payments ORDER BY id DESC")
  fun getAllPayments(): Flow<List<PaymentEntity>>

  @Query("SELECT * FROM payments WHERE kloterId = :kloterId AND customerId = :customerId")
  fun getPaymentsForMember(kloterId: Long, customerId: Long): Flow<List<PaymentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPayment(payment: PaymentEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(payments: List<PaymentEntity>)

  @Delete
  suspend fun deletePayment(payment: PaymentEntity)
}

@Dao
interface AppSettingsDao {
  @Query("SELECT * FROM app_settings WHERE id = 1")
  fun getSettings(): Flow<AppSettingsEntity?>

  @Query("SELECT * FROM app_settings WHERE id = 1")
  suspend fun getSettingsDirect(): AppSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(settings: AppSettingsEntity)
}
