package com.example.diploma

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diploma.databinding.ActivityNotificationsBinding

class NotificationsActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.apply {
            title = getString(R.string.menu_notifications)
            setDisplayHomeAsUpEnabled(true)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val mockNotifications = listOf(
            Notification("1", "Новая закупка №100806914", "12.05.2024 10:15", "Описание новой закупки №100806914: поставка офисной мебели.", false),
            Notification("2", "Изменение статуса закупки №99283741", "11.05.2024 16:40", "Статус закупки №99283741 изменен на 'Торги завершены'.", true),
            Notification("3", "Ваша заявка принята к рассмотрению", "10.05.2024 09:00", "Ваша заявка на участие в тендере №100500 была успешно принята.", true),
            Notification("4", "Завершение приема заявок: Закупка №100806914", "09.05.2024 23:59", "Прием заявок по закупке №100806914 окончен.", false),
            Notification("5", "Результаты торгов опубликованы", "08.05.2024 11:20", "Ознакомьтесь с протоколом результатов торгов в личном кабинете.", true),
            Notification("6", "Технические работы на портале", "07.05.2024 14:00", "Уведомляем вас о том, что на портале будут производиться технические работы в течение 5 дней. Просьба перенести все важные действия на более дальний срок.", true)
        )

        val adapter = NotificationsAdapter(mockNotifications) { notification ->
            notification.isRead = true
            val intent = Intent(this, NotificationInfoActivity::class.java).apply {
                putExtra(NotificationInfoActivity.EXTRA_NOTIFICATION, notification)
            }
            startActivity(intent)
        }

        binding.notificationsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            this.adapter = adapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
