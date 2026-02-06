package com.example.diploma

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.example.diploma.type.TradeQueryDtoInput
import kotlinx.coroutines.launch

class TradesActivity : BaseActivity() {

    private lateinit var adapter: TradesAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (!authManager.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_trades)
        supportActionBar?.title = getString(R.string.menu_trades)

        progressBar = findViewById(R.id.tradesProgressBar)
        val recyclerView: RecyclerView = findViewById(R.id.tradesRecyclerView)
        
        adapter = TradesAdapter(emptyList()) { tradeId ->
            val intent = Intent(this, TradeInfoActivity::class.java)
            intent.putExtra("TRADE_ID", tradeId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        loadTrades()
    }

    private fun loadTrades() {
        progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val apolloClient = ApolloInstance.getApolloClient(authManager)
                val response = apolloClient.query(TradesQuery(Optional.present(TradeQueryDtoInput()))).execute()
                
                val tradesList = response.data?.trades?.items?.filterNotNull() ?: emptyList()
                
                if (response.hasErrors()) {
                    Toast.makeText(this@TradesActivity, "Ошибка сервера: ${response.errors?.first()?.message}", Toast.LENGTH_LONG).show()
                } else {
                    adapter.updateData(tradesList)
                }
            } catch (e: Exception) {
                Toast.makeText(this@TradesActivity, "Ошибка при загрузке: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
