package com.example.diploma

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.diploma.databinding.ActivityTradeInfoBinding
import com.example.diploma.databinding.ItemLotBinding
import com.example.diploma.databinding.ItemPurchaseItemBinding
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TradeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradeInfoBinding
    private lateinit var authManager: AuthManager
    
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val moneyFormat = DecimalFormat("#,##0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTradeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager(this)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Детальная информация"

        val tradeId = intent.getStringExtra("TRADE_ID")
        if (tradeId != null) {
            loadTradeInfo(tradeId)
        } else {
            Toast.makeText(this, "ID закупки не найден", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadTradeInfo(id: String) {
        binding.tradeInfoProgressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val apolloClient = ApolloInstance.getApolloClient(authManager)
                val response = apolloClient.query(TradeQuery(id.toLong())).execute()
                val trade = response.data?.trade

                if (trade != null) {
                    displayTrade(trade)
                } else if (response.hasErrors()) {
                    Toast.makeText(this@TradeInfoActivity, response.errors?.first()?.message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TradeInfoActivity, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("Error", e.message ?: "Unknown error", e)
            } finally {
                binding.tradeInfoProgressBar.visibility = View.GONE
            }
        }
    }

    private fun displayTrade(trade: TradeQuery.Trade) {
        // Header
        binding.tradeHeader.text = "${trade.title ?: "Закупка"} №${trade.registeredNumber ?: "---"}"

        // Organizer
        trade.organizer?.let { org ->
            binding.orgName.text = org.title ?: "---"
            binding.orgTaxInfo.text = "ИНН: ${org.taxCode ?: "---"} | КПП: ${org.kpp ?: "---"}"
        }

        // Buyer / Target User
        trade.targetUser?.let { user ->
            binding.buyerName.text = user.title ?: "---"
            binding.buyerOrg.text = user.organization?.title ?: "---"
            
            // Detailed contact info
            binding.contactPerson.text = user.title ?: "---"
            binding.contactPosition.text = user.position ?: "---"
            binding.contactEmail.text = user.email ?: "---"
            binding.contactPhone.text = user.phone ?: "---"
        }

        // Procurement Info
        val classifier = trade.procurementClassifier?.firstOrNull()
        binding.procCategory.text = 
            if (classifier != null) "(${classifier.code ?: ""}) ${classifier.title ?: ""}" else "---"
            
        binding.procRegion.text = trade.destinationRegion?.firstOrNull()?.title ?: "---"
        binding.procMethod.text = trade.procurementMethod?.title ?: "---"
        binding.procReasoning.text = trade.tradeReasoning ?: "---"
        
        // Dates
        binding.dateBidStart.text = formatDate(trade.bidSubmissionStartDate)
        binding.dateBidEnd.text = formatDate(trade.bidSubmissionEndDate)
        binding.datePartStart.text = formatDate(trade.participationConfirmationStartDate)
        binding.datePartEnd.text = formatDate(trade.participationConfirmationEndDate)

        // Lots
        binding.lotsContainer.removeAllViews()
        trade.lots?.filterNotNull()?.forEachIndexed { index, lot ->
            val lotBinding = ItemLotBinding.inflate(layoutInflater, binding.lotsContainer, false)
            
            lotBinding.lotTitle.text = "Лот №${index + 1}"
            
            val price = lot.initialContractPrice?.toString()?.toDoubleOrNull() ?: 0.0
            val currency = lot.currency?.title ?: "₽"
            lotBinding.lotPrice.text = "${moneyFormat.format(price)} $currency"
            lotBinding.lotVat.text = lot.vatType?.name ?: "---"

            // Purchase Items for this Lot
            lotBinding.purchaseItemsContainer.removeAllViews()
            lot.purchaseItems?.filterNotNull()?.forEach { item ->
                val itemBinding = ItemPurchaseItemBinding.inflate(layoutInflater, lotBinding.purchaseItemsContainer, false)
                
                itemBinding.itemTitle.text = item.title ?: "Без названия"
                
                val itemClassifier = item.procurementClassifier?.firstOrNull()
                itemBinding.itemClassifier.text = 
                    if (itemClassifier != null) "(${itemClassifier.code ?: ""}) ${itemClassifier.title ?: ""}" else "---"
                
                val quantity = item.quantity?.toString() ?: "0"
                val unit = item.unit?.title ?: ""
                itemBinding.itemQuantity.text = "Кол-во: $quantity $unit"
                
                val itemPrice = item.price?.toString()?.toDoubleOrNull()
                if (itemPrice == null || itemPrice == 0.0) {
                    itemBinding.itemPrice.visibility = View.GONE
                } else {
                    itemBinding.itemPrice.visibility = View.VISIBLE
                    itemBinding.itemPrice.text = "${moneyFormat.format(itemPrice)} $currency"
                }
                
                lotBinding.purchaseItemsContainer.addView(itemBinding.root)
            }

            binding.lotsContainer.addView(lotBinding.root)
        }
    }

    private fun formatDate(timestamp: Any?): String {
        return when (timestamp) {
            is Long -> dateFormat.format(Date(timestamp))
            is String -> try { dateFormat.format(Date(timestamp.toLong())) } catch (e: Exception) { timestamp }
            else -> "---"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
