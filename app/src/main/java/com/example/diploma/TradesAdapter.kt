package com.example.diploma

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class TradesAdapter(
    private var trades: List<TradesQuery.Item>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<TradesAdapter.TradeViewHolder>() {

    class TradeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: View = view
        val number: TextView = view.findViewById(R.id.tradeRegisteredNumber)
        val title: TextView = view.findViewById(R.id.tradeTitle)
        val status: TextView = view.findViewById(R.id.tradeStatus)
        val organizer: TextView = view.findViewById(R.id.tradeOrganizer)
        val price: TextView = view.findViewById(R.id.tradePrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trade, parent, false)
        return TradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TradeViewHolder, position: Int) {
        val trade = trades[position]
        holder.number.text = "№ ${trade.registeredNumber ?: "---"}"
        holder.title.text = trade.title ?: "Без названия"
        holder.status.text = trade.status?.title ?: "Неизвестно"
        holder.organizer.text = "Организатор: ${trade.organizer?.title ?: "---"}"
        
        val totalPrice = trade.lots?.sumOf { 
            val price = it?.initialContractPrice
            if (price is Number) price.toDouble() else 0.0
        } ?: 0.0
        
        val formatter = DecimalFormat("#,###.## ₽")
        holder.price.text = formatter.format(totalPrice)

        holder.container.setOnClickListener {
            trade.id?.let { id -> onItemClick(id.toString()) }
        }
    }

    override fun getItemCount() = trades.size

    fun updateData(newTrades: List<TradesQuery.Item>) {
        trades = newTrades
        notifyDataSetChanged()
    }
}
