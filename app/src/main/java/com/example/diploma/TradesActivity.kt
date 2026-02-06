package com.example.diploma

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.example.diploma.type.TradeQueryDtoInput
import kotlinx.coroutines.launch
import java.io.IOException

class TradesActivity : BaseActivity() {

    private lateinit var adapter: TradesAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: LinearLayout
    private lateinit var errorTextView: TextView
    private lateinit var retryButton: Button
    private lateinit var recyclerView: RecyclerView

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
        errorLayout = findViewById(R.id.errorLayout)
        errorTextView = findViewById(R.id.errorTextView)
        retryButton = findViewById(R.id.retryButton)
        recyclerView = findViewById(R.id.tradesRecyclerView)
        
        adapter = TradesAdapter(emptyList()) { tradeId ->
            val intent = Intent(this, TradeInfoActivity::class.java)
            intent.putExtra("TRADE_ID", tradeId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        retryButton.setOnClickListener {
            loadTrades()
        }

        loadTrades()
    }

    private fun loadTrades() {
        showLoading()
        
        lifecycleScope.launch {
            try {
                val apolloClient = ApolloInstance.getApolloClient(authManager)
                val response = apolloClient.query(TradesQuery(Optional.present(TradeQueryDtoInput()))).execute()
                
                if (response.hasErrors()) {
                    showError("Ошибка сервера: ${response.errors?.first()?.message}")
                } else {
                    val tradesList = response.data?.trades?.items?.filterNotNull() ?: emptyList()
                    if (tradesList.isEmpty()) {
                        showError("Торги не найдены")
                    } else {
                        showContent()
                        adapter.updateData(tradesList)
                    }
                }
            } catch (e: IOException) {
                showError("Ошибка сети. Проверьте подключение к интернету.")
                e.printStackTrace()
            } catch (e: Exception) {
                showError("Произошла непредвиденная ошибка: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }

    private fun showContent() {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorTextView.text = message
    }
}
