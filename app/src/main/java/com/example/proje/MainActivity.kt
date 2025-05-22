package com.example.proje

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var expenses: ArrayList<Expense>
    private lateinit var filteredExpenses: ArrayList<Expense>
    private lateinit var adapter: ExpenseAdapter
    private lateinit var listView: ListView
    private lateinit var tvTotal: TextView
    private lateinit var spinnerFilter: Spinner
    private lateinit var simplePieChart: PieChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        expenses = ArrayList()
        filteredExpenses = ArrayList()
        adapter = ExpenseAdapter(this, filteredExpenses)

        listView = findViewById(R.id.listExpenses)
        listView.adapter = adapter

        tvTotal = findViewById(R.id.tvTotal)
        spinnerFilter = findViewById(R.id.spinnerFilter)
        simplePieChart = findViewById(R.id.simplePieChart)

        val btnAdd: Button = findViewById(R.id.btnAddExpense)
        btnAdd.setOnClickListener {
            showAddExpenseDialog()
        }

        // Kayıt tıklama için ekledik
        listView.setOnItemClickListener { _, _, position, _ ->
            val expense = filteredExpenses[position]
            showItemOptionsDialog(expense)
        }

        updateTotal()
        updateFilterSpinner()
        updatePieChart()

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedCategory = spinnerFilter.selectedItem.toString()
                filterByCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun showAddExpenseDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null)
        val etName = dialogView.findViewById<EditText>(R.id.etExpenseName)
        val etCategory = dialogView.findViewById<EditText>(R.id.etExpenseCategory)
        val etAmount = dialogView.findViewById<EditText>(R.id.etExpenseAmount)

        AlertDialog.Builder(this)
            .setTitle("Harcama Ekle")
            .setView(dialogView)
            .setPositiveButton("Ekle") { _, _ ->
                val name = etName.text.toString()
                val category = etCategory.text.toString()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotBlank() && category.isNotBlank() && amount > 0) {
                    val expense = Expense(name, category, amount)
                    expenses.add(expense)
                    filterByCategory(spinnerFilter.selectedItem?.toString() ?: "Tümü")
                    updateTotal()
                    updateFilterSpinner()
                    updatePieChart()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun showItemOptionsDialog(expense: Expense) {
        val options = arrayOf("Düzenle", "Sil")
        AlertDialog.Builder(this)
            .setTitle("İşlem Seçin")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditExpenseDialog(expense)
                    1 -> deleteExpense(expense)
                }
            }
            .setNegativeButton("Vazgeç", null)
            .show()
    }

    private fun deleteExpense(expense: Expense) {
        expenses.remove(expense)
        filterByCategory(spinnerFilter.selectedItem?.toString() ?: "Tümü")
        updateTotal()
        updateFilterSpinner()
        updatePieChart()
    }

    private fun showEditExpenseDialog(expense: Expense) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null)
        val etName = dialogView.findViewById<EditText>(R.id.etExpenseName)
        val etCategory = dialogView.findViewById<EditText>(R.id.etExpenseCategory)
        val etAmount = dialogView.findViewById<EditText>(R.id.etExpenseAmount)

        etName.setText(expense.name)
        etCategory.setText(expense.category)
        etAmount.setText(expense.amount.toString())

        AlertDialog.Builder(this)
            .setTitle("Harcama Düzenle")
            .setView(dialogView)
            .setPositiveButton("Kaydet") { _, _ ->
                val name = etName.text.toString()
                val category = etCategory.text.toString()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotBlank() && category.isNotBlank() && amount > 0) {
                    expenses.remove(expense)
                    val updatedExpense = Expense(name, category, amount)
                    expenses.add(updatedExpense)
                    filterByCategory(spinnerFilter.selectedItem?.toString() ?: "Tümü")
                    updateTotal()
                    updateFilterSpinner()
                    updatePieChart()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun updateTotal() {
        val total = filteredExpenses.sumOf { it.amount }
        tvTotal.text = "Toplam Harcama: $total TL"
    }

    private fun updateFilterSpinner() {
        val categories = listOf("Tümü") + expenses.map { it.category }.distinct()
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = spinnerAdapter
    }

    private fun filterByCategory(category: String) {
        filteredExpenses.clear()
        if (category == "Tümü") {
            filteredExpenses.addAll(expenses)
        } else {
            filteredExpenses.addAll(expenses.filter { it.category == category })
        }
        adapter.notifyDataSetChanged()
        updateTotal()
        updatePieChart()
    }

    private fun updatePieChart() {
        val categoryTotals = expenses
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .filter { it.value > 0 }

        val data = categoryTotals.map { it.value.toFloat() to it.key }
        simplePieChart.setData(data)
    }
}
