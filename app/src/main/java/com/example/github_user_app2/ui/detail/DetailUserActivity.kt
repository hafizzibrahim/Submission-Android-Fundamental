package com.example.github_user_app2.ui.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.github_user_app2.databinding.ActivityDetailUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailUserActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_URL = "extra_url"
    }

    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var viewModel: DetailUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        val id = intent.getIntExtra(EXTRA_ID, 0)
        val  avatarUrl = intent.getStringExtra(EXTRA_URL)
        val bundle = Bundle()
        bundle.putString(EXTRA_USERNAME, username)

        showLoading(true)

        viewModel = ViewModelProvider(this)[DetailUserViewModel::class.java]
        if (username != null) {
            viewModel.setUserDetail(username)
        }
        viewModel.getUserDetail().observe(this) {
            if (it != null) {
                binding.apply {
                    tvName.text = it.name
                    tvUsername.text = it.login
                    tvFollowers.text = "${it.followers} Followers"
                    tvFollowing.text = "${it.following} Following"
                    Glide.with(this@DetailUserActivity)
                        .load(it.avatar_url)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .centerCrop()
                        .into(ivProfile)
                }
                showLoading(false)
            } else {
                showLoading(true)
            }
        }

        var _isChecked = false
        CoroutineScope(Dispatchers.IO).launch {
            val count = viewModel.checkUser(id)
            withContext(Dispatchers.Main){
                if (count != null){
                    if (count > 0){
                        binding.toggleFavorite.isChecked = true
                        _isChecked = true
                    }else{
                        binding.toggleFavorite.isChecked = false
                        _isChecked = false
                    }
                }
            }
        }

        binding.toggleFavorite.setOnClickListener {
            _isChecked = !_isChecked
            if (_isChecked) {
                if (username != null) {
                    if (avatarUrl != null) {
                        viewModel.addToFavorite(username, id, avatarUrl)
                    }
                }
            } else {
                viewModel.removeFromFavorite(id)
            }
            binding.toggleFavorite.isChecked = _isChecked
        }

        val sectionPagerAdapter = SectionPagerAdapter(this, supportFragmentManager, bundle)
        binding.apply {
            viewPager.adapter = sectionPagerAdapter
            tabs.setupWithViewPager(viewPager)
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progresBar.visibility = View.VISIBLE
        } else {
            binding.progresBar.visibility = View.GONE
        }
    }
}