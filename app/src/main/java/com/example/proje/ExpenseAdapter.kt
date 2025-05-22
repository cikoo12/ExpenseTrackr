package com.example.proje

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ExpenseAdapter(context: Context, private var expenses: List<Expense>)
    : ArrayAdapter<Expense>(context, 0, expenses) {

    fun updateList(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    override fun getCount(): Int = expenses.size
    override fun getItem(position: Int): Expense? = expenses[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val expense = expenses[position]
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_expense, parent, false)

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvCategory = view.findViewById<TextView>(R.id.tvCategory)
        val tvAmount = view.findViewById<TextView>(R.id.tvAmount)

        tvName.text = expense.name
        tvCategory.text = "Kategori: ${expense.category}"
        tvAmount.text = "Tutar: ${expense.amount} TL"

        return view
    }
}
