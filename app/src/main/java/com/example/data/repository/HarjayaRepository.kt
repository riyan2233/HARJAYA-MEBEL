package com.example.data.repository

import com.example.data.db.HarjayaDatabase
import com.example.data.model.AppSettingsEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.DeliveryStatus
import com.example.data.model.KloterEntity
import com.example.data.model.KloterMemberEntity
import com.example.data.model.KloterMemberWithDetail
import com.example.data.model.PaymentEntity
import com.example.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class HarjayaRepository(private val database: HarjayaDatabase) {

  private val customerDao = database.customerDao()
  private val productDao = database.productDao()
  private val kloterDao = database.kloterDao()
  private val kloterMemberDao = database.kloterMemberDao()
  private val paymentDao = database.paymentDao()
  private val settingsDao = database.appSettingsDao()

  val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
  val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
  val allKloters: Flow<List<KloterEntity>> = kloterDao.getAllKloters()
  val allMembers: Flow<List<KloterMemberEntity>> = kloterMemberDao.getAllMembers()
  val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()
  val appSettings: Flow<AppSettingsEntity?> = settingsDao.getSettings()

  // Combined shipments with Customer, Product, and Kloter information
  val shipmentsWithDetail: Flow<List<KloterMemberWithDetail>> = combine(
    allMembers,
    allCustomers,
    allProducts,
    allKloters
  ) { members, customers, products, kloters ->
    val customerMap = customers.associateBy { it.id }
    val productMap = products.associateBy { it.id }
    val kloterMap = kloters.associateBy { it.id }

    members.map { member ->
      KloterMemberWithDetail(
        member = member,
        customer = customerMap[member.customerId],
        product = productMap[member.productId],
        kloter = kloterMap[member.kloterId]
      )
    }
  }

  suspend fun insertCustomer(customer: CustomerEntity): Long = customerDao.insertCustomer(customer)
  suspend fun updateCustomer(customer: CustomerEntity) = customerDao.updateCustomer(customer)
  suspend fun deleteCustomer(customer: CustomerEntity) = customerDao.deleteCustomer(customer)

  suspend fun insertProduct(product: ProductEntity): Long = productDao.insertProduct(product)
  suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
  suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)

  suspend fun insertKloter(kloter: KloterEntity): Long = kloterDao.insertKloter(kloter)
  suspend fun updateKloter(kloter: KloterEntity) = kloterDao.updateKloter(kloter)
  suspend fun deleteKloter(kloter: KloterEntity) = kloterDao.deleteKloter(kloter)

  suspend fun insertMember(member: KloterMemberEntity): Long = kloterMemberDao.insertMember(member)
  suspend fun updateMember(member: KloterMemberEntity) = kloterMemberDao.updateMember(member)
  suspend fun deleteMember(member: KloterMemberEntity) = kloterMemberDao.deleteMember(member)

  suspend fun insertPayment(payment: PaymentEntity): Long = paymentDao.insertPayment(payment)
  suspend fun updateSettings(settings: AppSettingsEntity) = settingsDao.insertOrUpdate(settings)

  suspend fun seedInitialDataIfEmpty() {
    val customerCount = customerDao.getCount()
    if (customerCount == 0) {
      // 1. Initial App Settings
      settingsDao.insertOrUpdate(
        AppSettingsEntity(
          id = 1,
          storeName = "HARJAYA MEBEL",
          slogan = "Arisan Mebel & Perabot Kayu Jati Mewah, Amanah & Bergaransi",
          ownerName = "by Cak Riyan",
          phone = "0812-9876-5432",
          address = "Jl. Raya Mebel & Ukir Jepara No. 88, Jawa Timur",
          adminPin = "1234",
          themeIndex = 0,
          logoIndex = 0,
          isMusicEnabled = false,
          musicVolume = 0.5f,
          isSfxEnabled = true
        )
      )

      // 2. Customers
      val sampleCustomers = listOf(
        CustomerEntity(
          name = "Ibu Siti Rahmawati",
          phone = "0813-2234-5567",
          address = "Dusun Krajan RT 02/RW 03, Sukosari",
          avatarKey = "avatar_1",
          notes = "Pelanggan setia arisan mebel sejak 2024",
          joinedDate = "15 Jan 2025"
        ),
        CustomerEntity(
          name = "Pak Bambang Supriyadi",
          phone = "0812-4456-7890",
          address = "Jl. Pahlawan No. 45, Sumberrejo",
          avatarKey = "avatar_2",
          notes = "Suka kayu jati perhutani finishing natural",
          joinedDate = "01 Feb 2025"
        ),
        CustomerEntity(
          name = "Ibu Hj. Aminah Dewi",
          phone = "0857-1122-3344",
          address = "Perum Griya Indah Blok B-12",
          avatarKey = "avatar_3",
          notes = "Pembayaran selalu tepat waktu awal bulan",
          joinedDate = "10 Feb 2025"
        ),
        CustomerEntity(
          name = "Mas Wahyu Pratama",
          phone = "0878-9988-7766",
          address = "Desa Karanganyar RT 04/RW 01",
          avatarKey = "avatar_4",
          notes = "Memilih lemari pakaian 3 pintu jati",
          joinedDate = "20 Feb 2025"
        ),
        CustomerEntity(
          name = "Mbak Nurul Hidayah",
          phone = "0821-6677-8899",
          address = "Jl. Melati No. 18, Kebonagung",
          avatarKey = "avatar_5",
          notes = "Pemenang kloter 1 periode 1",
          joinedDate = "01 Mar 2025"
        ),
        CustomerEntity(
          name = "Pak Suwandi Santoso",
          phone = "0813-5544-3322",
          address = "Dusun Sidomulyo No. 7, Wringin",
          avatarKey = "avatar_6",
          notes = "Permintaan antar hari Sabtu pagi",
          joinedDate = "10 Mar 2025"
        )
      )
      customerDao.insertAll(sampleCustomers)

      // 3. Products (Mebel Jepara)
      val sampleProducts = listOf(
        ProductEntity(
          name = "Kursi Tamu Sudut Ganesa Jati Jepara",
          category = "Kursi & Sofa",
          price = 4500000,
          description = "Set kursi tamu sudut mewah lengkap dengan meja ukiran khas Jepara, busa empuk royal foam, kain oscar premium tahan air.",
          woodType = "Kayu Jati Solid TPK",
          iconKey = "chair"
        ),
        ProductEntity(
          name = "Lemari Pakaian 3 Pintu Ukir Mawar",
          category = "Lemari & Buffet",
          price = 5200000,
          description = "Lemari pakaian 3 pintu kaca tengah, laci tersembunyi beranak kunci, ukiran mahkota mawar anggun, finishing melamin doff/glossy.",
          woodType = "Kayu Jati Perhutani",
          iconKey = "wardrobe"
        ),
        ProductEntity(
          name = "Meja Makan Salina Gendong 6 Kursi",
          category = "Meja Makan",
          price = 3800000,
          description = "Meja makan oval panjang 180cm dengan 6 kursi busa jok empuk, kaca meja tebal 8mm beveled, sangat kokoh untuk keluarga.",
          woodType = "Kayu Jati Grade A",
          iconKey = "table"
        ),
        ProductEntity(
          name = "Dipan Tempat Tidur Rahwana Ukir Uk. 180x200",
          category = "Tempat Tidur",
          price = 4200000,
          description = "Ranjang tempat tidur ukir Rahwana tiang bubut, sandaran kepala megah, konstruksi sambungan pasak kuat anti bunyi.",
          woodType = "Kayu Jati Asli",
          iconKey = "bed"
        ),
        ProductEntity(
          name = "Buffet TV Minimalis & Rak Hias Mewah",
          category = "Lemari & Buffet",
          price = 2800000,
          description = "Meja buffet rak pajangan TV ukuran 160cm x 50cm x 75cm, pintu kaca geser dan laci roda rel besi halus.",
          woodType = "Kayu Jati Finishing Walnuts",
          iconKey = "cabinet"
        ),
        ProductEntity(
          name = "Sofa Bed Scandinavia Minimalis Teak Leg",
          category = "Kursi & Sofa",
          price = 3200000,
          description = "Sofa santai modern 3 dudukan yang bisa direbahkan jadi kasur tidur, kaki kayu jati bubut kokoh, kain fabric linen breathable.",
          woodType = "Rangka Jati & Kaki Teak",
          iconKey = "sofa"
        )
      )
      productDao.insertAll(sampleProducts)

      // 4. Kloter Arisan
      val sampleKloters = listOf(
        KloterEntity(
          name = "Kloter Jati Jepara Gold (10 Periode)",
          installmentAmount = 450000,
          frequency = "Bulanan",
          totalPeriods = 10,
          currentPeriod = 3,
          status = "Aktif",
          createdAt = "01 Jan 2025"
        ),
        KloterEntity(
          name = "Kloter Mebel Ruang Tamu Berkah (6 Periode)",
          installmentAmount = 350000,
          frequency = "Bulanan",
          totalPeriods = 6,
          currentPeriod = 2,
          status = "Aktif",
          createdAt = "15 Jan 2025"
        ),
        KloterEntity(
          name = "Kloter Kamar Tidur Idaman (8 Periode)",
          installmentAmount = 500000,
          frequency = "Bulanan",
          totalPeriods = 8,
          currentPeriod = 1,
          status = "Aktif",
          createdAt = "01 Feb 2025"
        )
      )
      kloterDao.insertAll(sampleKloters)

      // Fetch IDs to create associations
      val customers = customerDao.getAllCustomers().first()
      val products = productDao.getAllProducts().first()
      val kloters = kloterDao.getAllKloters().first()

      if (customers.size >= 5 && products.size >= 4 && kloters.isNotEmpty()) {
        val kloter1 = kloters[0]
        val kloter2 = if (kloters.size > 1) kloters[1] else kloter1

        // Members for Kloter 1
        val members = listOf(
          KloterMemberEntity(
            kloterId = kloter1.id,
            customerId = customers[0].id,
            productId = products[0].id,
            lotteryNumber = 1,
            isWon = true,
            wonPeriod = 1,
            deliveryStatus = DeliveryStatus.TERKIRIM.name,
            driverName = "Armada Harjaya - Cak Riyan (Pickup L300)",
            deliveryReceiptNo = "HM-NOTA-2025-0101",
            deliveryDate = "10 Feb 2025",
            deliveryNotes = "Barang sudah sampai & dirakit di ruang tamu pelanggan, diterima langsung oleh Ibu Siti."
          ),
          KloterMemberEntity(
            kloterId = kloter1.id,
            customerId = customers[1].id,
            productId = products[1].id,
            lotteryNumber = 2,
            isWon = true,
            wonPeriod = 2,
            deliveryStatus = DeliveryStatus.DALAM_PENGIRIMAN.name,
            driverName = "Armada Harjaya - Mas Anto",
            deliveryReceiptNo = "HM-NOTA-2025-0202",
            deliveryDate = "18 Mar 2025",
            deliveryNotes = "Sedang dalam perjalanan pengantaran mebel menggunakan truk engkel."
          ),
          KloterMemberEntity(
            kloterId = kloter1.id,
            customerId = customers[2].id,
            productId = products[2].id,
            lotteryNumber = 3,
            isWon = true,
            wonPeriod = 3,
            deliveryStatus = DeliveryStatus.SIAP_KIRIM.name,
            driverName = "Armada Harjaya - Cak Riyan",
            deliveryReceiptNo = "HM-NOTA-2025-0303",
            deliveryDate = "22 Mar 2025",
            deliveryNotes = "Mebel sudah selesai finishing melamin & packing busa aman di gudang toko."
          ),
          KloterMemberEntity(
            kloterId = kloter1.id,
            customerId = customers[3].id,
            productId = products[3].id,
            lotteryNumber = 4,
            isWon = false,
            wonPeriod = 0,
            deliveryStatus = DeliveryStatus.MENUNGGU_UNDIAN.name,
            driverName = "-",
            deliveryReceiptNo = "",
            deliveryDate = "",
            deliveryNotes = "Menunggu jadwal kocokan arisan periode berikutnya."
          ),
          KloterMemberEntity(
            kloterId = kloter1.id,
            customerId = customers[4].id,
            productId = products[4].id,
            lotteryNumber = 5,
            isWon = false,
            wonPeriod = 0,
            deliveryStatus = DeliveryStatus.MENUNGGU_UNDIAN.name,
            driverName = "-",
            deliveryReceiptNo = "",
            deliveryDate = "",
            deliveryNotes = "Menunggu jadwal kocokan arisan."
          ),
          // Member for Kloter 2
          KloterMemberEntity(
            kloterId = kloter2.id,
            customerId = customers[5].id,
            productId = products[0].id,
            lotteryNumber = 1,
            isWon = true,
            wonPeriod = 1,
            deliveryStatus = DeliveryStatus.DIPROSES.name,
            driverName = "Tukang Ukir Jepara (Pengrajin)",
            deliveryReceiptNo = "HM-NOTA-2025-0404",
            deliveryDate = "25 Mar 2025",
            deliveryNotes = "Tahap amplas halus dan pewarnaan walnut brown oleh pengrajin mebel."
          )
        )
        kloterMemberDao.insertAll(members)

        // Initial Payments for transparency
        val payments = listOf(
          PaymentEntity(
            kloterId = kloter1.id,
            customerId = customers[0].id,
            periodNumber = 1,
            amount = kloter1.installmentAmount,
            paymentDate = "05 Jan 2025",
            receiptNumber = "KWT-HM-001",
            notes = "Lunas via Tunai ke Cak Riyan"
          ),
          PaymentEntity(
            kloterId = kloter1.id,
            customerId = customers[1].id,
            periodNumber = 1,
            amount = kloter1.installmentAmount,
            paymentDate = "06 Jan 2025",
            receiptNumber = "KWT-HM-002",
            notes = "Lunas Transfer Mandiri"
          ),
          PaymentEntity(
            kloterId = kloter1.id,
            customerId = customers[2].id,
            periodNumber = 1,
            amount = kloter1.installmentAmount,
            paymentDate = "08 Jan 2025",
            receiptNumber = "KWT-HM-003",
            notes = "Lunas Transfer BCA"
          ),
          PaymentEntity(
            kloterId = kloter1.id,
            customerId = customers[0].id,
            periodNumber = 2,
            amount = kloter1.installmentAmount,
            paymentDate = "04 Feb 2025",
            receiptNumber = "KWT-HM-004",
            notes = "Lunas via Tunai"
          ),
          PaymentEntity(
            kloterId = kloter1.id,
            customerId = customers[1].id,
            periodNumber = 2,
            amount = kloter1.installmentAmount,
            paymentDate = "05 Feb 2025",
            receiptNumber = "KWT-HM-005",
            notes = "Lunas Transfer"
          )
        )
        paymentDao.insertAll(payments)
      }
    }
  }
}
