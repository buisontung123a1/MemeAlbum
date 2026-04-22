package com.memealbum.app.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.memealbum.app.R
import com.memealbum.app.databinding.ActivityMainBinding
import com.memealbum.app.ui.adapter.MemeAdapter
import com.memealbum.app.ui.viewmodel.LoadState
import com.memealbum.app.ui.viewmodel.MemeViewModel
import com.memealbum.app.ui.viewmodel.SortType

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MemeViewModel by viewModels()
    private lateinit var adapter: MemeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        setupTabs()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MemeAdapter(
            onItemClick = { meme, position, sharedView ->
                PhotoViewActivity.start(this, meme, position, sharedView)
            },
            onItemLongClick = { meme ->
                viewModel.toggleSelection(meme.id)
            }
        )

        val spanCount = 3
        val layoutManager = GridLayoutManager(this, spanCount)

        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            adapter = this@MainActivity.adapter
            setHasFixedSize(true)

            // Infinite scroll
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    val total = layoutManager.itemCount
                    if (dy > 0 && lastVisible >= total - 6 && viewModel.hasMore()) {
                        viewModel.loadMore()
                    }
                }
            })
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.loadMemes(SortType.HOT)
                    1 -> viewModel.loadMemes(SortType.NEW)
                    2 -> viewModel.loadMemes(SortType.TOP)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        binding.swipeRefresh.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorSecondary
        )
    }

    private fun observeViewModel() {
        viewModel.memes.observe(this) { memes ->
            adapter.submitList(memes)
        }

        viewModel.loadState.observe(this) { state ->
            when (state) {
                LoadState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                }
                LoadState.LOADING_MORE -> {
                    binding.progressBarBottom.visibility = View.VISIBLE
                }
                LoadState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    binding.progressBarBottom.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.swipeRefresh.isRefreshing = false
                    binding.tvError.visibility = View.GONE
                }
                LoadState.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.progressBarBottom.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    if (adapter.itemCount == 0) {
                        binding.tvError.visibility = View.VISIBLE
                    }
                }
                else -> {}
            }
        }

        viewModel.errorMessage.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.selectedMemes.observe(this) { selected ->
            adapter.setSelectedIds(selected)
            if (selected.isEmpty()) {
                binding.bottomActionBar.visibility = View.GONE
                supportActionBar?.title = getString(R.string.app_name)
            } else {
                binding.bottomActionBar.visibility = View.VISIBLE
                supportActionBar?.title = "${selected.size} đã chọn"
            }
        }

        // Bottom action bar buttons
        binding.btnDelete.setOnClickListener {
            viewModel.deleteSelected()
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show()
        }

        binding.btnClearSelection.setOnClickListener {
            viewModel.clearSelection()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
