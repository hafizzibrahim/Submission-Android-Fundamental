package com.example.github_user_app2.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github_user_app2.data.model.User
import com.example.github_user_app2.databinding.ActivityMainBinding
import com.example.github_user_app2.ui.detail.DetailUserActivity
import com.example.github_user_app2.ui.favorite.FavoriteActivity
import com.example.github_user_app2.ui.theme.SettingPreferences
import com.example.github_user_app2.ui.theme.SwitchThemeActivity
import com.example.github_user_app2.ui.theme.ThemeViewModel
import com.example.github_user_app2.ui.theme.ViewModelFactory
import com.example.github_user_app2.ui.theme.dataStore
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: UserAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserAdapter()
        adapter.notifyDataSetChanged()

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback{
            override fun OnItemClicked(data: User) {
                Intent(this@MainActivity, DetailUserActivity::class.java).also {
                    it.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                    it.putExtra(DetailUserActivity.EXTRA_ID, data.id)
                    it.putExtra(DetailUserActivity.EXTRA_URL, data.avatar_url)
                    startActivity(it)
                }
            }
        })

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
        val pref = SettingPreferences.getInstance(application.dataStore)
        val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(ThemeViewModel::class.java)

        binding.apply {
            rvUser.layoutManager = LinearLayoutManager(this@MainActivity)
            rvUser.setHasFixedSize(true)
            rvUser.adapter = adapter

            toggleFavorite.setOnClickListener {
                val isFavoriteMode = toggleFavorite.isPressed
                if (isFavoriteMode) {
                    Intent(this@MainActivity, FavoriteActivity::class.java).also {
                        startActivity(it)
                    }
                }
            }

            mainViewModel.getThemeSettings().observe(this@MainActivity) { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

            toggleTheme.setOnClickListener {
                val isThemeMode = toggleTheme.isPressed
                if (isThemeMode){
                    Intent(this@MainActivity, SwitchThemeActivity::class.java).also {
                        startActivity(it)
                    }
                }
            }

            btnSearch.setOnClickListener {
                searchUser()
            }

            etQuery.setOnKeyListener{ _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    searchUser()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }

        lifecycleScope.launch {
            viewModel.loadPopularUsers()
        }

        viewModel.getSearchUsers().observe(this) { users ->
            if (users != null) {
                adapter.setList(users)
                showLoading(false)
            }
        }
    }

    private fun searchUser() {
        binding.apply {
            val query = etQuery.text.toString()
            if (query.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter a query", Toast.LENGTH_SHORT).show()
                return
            }
            showLoading(true)
            viewModel.setSearchUsers(query)
            Log.d("SearchUser", "Searching for user: $query")
        }
    }

    private fun showLoading(state: Boolean){
        if (state){
            binding.progresBar.visibility = View.VISIBLE
        }else{
            binding.progresBar.visibility = View.GONE
        }
    }

}
