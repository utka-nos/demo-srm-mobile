package com.example.diploma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diploma.databinding.ActivityNotificationsBinding
import kotlinx.coroutines.launch

class NotificationsActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var adapter: NotificationsAdapter
    private lateinit var repository: NotificationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = NotificationRepository(applicationContext)
        
        supportActionBar?.apply {
            title = getString(R.string.menu_notifications)
            setDisplayHomeAsUpEnabled(true)
        }

        setupRecyclerView()
        syncNotifications()
        observeNotifications()
    }

    private fun setupRecyclerView() {
        adapter = NotificationsAdapter { notification ->
            lifecycleScope.launch {
                repository.markAsRead(notification.id)
            }
            val intent = Intent(this, NotificationInfoActivity::class.java).apply {
                putExtra(NotificationInfoActivity.EXTRA_NOTIFICATION_ID, notification.id)
            }
            startActivity(intent)
        }

        binding.notificationsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            this.adapter = adapter
        }
    }

    private fun observeNotifications() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.observeNotifications().collect { notifications ->
                    adapter.submitList(notifications)
                    val isEmpty = notifications.isEmpty()
                    binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    binding.notificationsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun syncNotifications() {
        lifecycleScope.launch {
            when (val result = repository.syncNotifications()) {
                NotificationRepository.SyncNotificationsResult.Success -> Unit
                NotificationRepository.SyncNotificationsResult.NotImplemented -> {
                    Log.d(TAG, "syncNotifications() is not implemented yet")
                }
                is NotificationRepository.SyncNotificationsResult.Error -> {
                    Log.w(TAG, "syncNotifications() failed: ${result.message}")
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val TAG = "NotificationsActivity"
    }
}
