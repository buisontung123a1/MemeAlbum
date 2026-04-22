package com.memealbum.app.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.memealbum.app.data.model.MemeItem
import com.memealbum.app.databinding.ActivityPhotoViewBinding
import java.text.SimpleDateFormat
import java.util.*

class PhotoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoViewBinding
    private lateinit var meme: MemeItem

    companion object {
        private const val EXTRA_MEME = "extra_meme"
        private const val EXTRA_POSITION = "extra_position"
        const val TRANSITION_NAME = "meme_image_transition"

        fun start(activity: Activity, meme: MemeItem, position: Int, sharedView: View) {
            val intent = Intent(activity, PhotoViewActivity::class.java).apply {
                putExtra(EXTRA_MEME, meme.id)
                putExtra("meme_title", meme.title)
                putExtra("meme_url", meme.imageUrl)
                putExtra("meme_author", meme.author)
                putExtra("meme_score", meme.score)
                putExtra("meme_created", meme.createdUtc)
                putExtra("meme_permalink", meme.permalink)
                putExtra(EXTRA_POSITION, position)
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, sharedView, TRANSITION_NAME
            )
            activity.startActivity(intent, options.toBundle())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Reconstruct meme from intent
        meme = MemeItem(
            id = intent.getStringExtra(EXTRA_MEME) ?: "",
            title = intent.getStringExtra("meme_title") ?: "",
            imageUrl = intent.getStringExtra("meme_url") ?: "",
            author = intent.getStringExtra("meme_author") ?: "",
            score = intent.getIntExtra("meme_score", 0),
            createdUtc = intent.getLongExtra("meme_created", 0),
            permalink = intent.getStringExtra("meme_permalink") ?: ""
        )

        setupToolbar()
        loadImage()
        setupInfo()
        setupActions()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.toolbar.setNavigationOnClickListener { finishAfterTransition() }
    }

    private fun loadImage() {
        ViewCompat.setTransitionName(binding.photoView, TRANSITION_NAME)

        Glide.with(this)
            .load(meme.imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(binding.photoView)
    }

    private fun setupInfo() {
        binding.tvTitle.text = meme.title
        binding.tvAuthor.text = "u/${meme.author}"
        binding.tvScore.text = "⬆ ${formatScore(meme.score)}"

        val date = Date(meme.createdUtc * 1000)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        binding.tvDate.text = sdf.format(date)

        // Toggle info panel
        binding.btnInfo.setOnClickListener {
            toggleInfoPanel()
        }
    }

    private fun toggleInfoPanel() {
        val panel = binding.infoPanel
        if (panel.visibility == View.VISIBLE) {
            panel.animate()
                .translationY(panel.height.toFloat())
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction { panel.visibility = View.GONE }
                .start()
        } else {
            panel.visibility = View.VISIBLE
            panel.translationY = panel.height.toFloat()
            panel.alpha = 0f
            panel.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    private fun setupActions() {
        binding.btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "${meme.title}\n${meme.imageUrl}\n\nVia MemeAlbum")
            }
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ meme"))
        }

        binding.btnCopy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Meme URL", meme.imageUrl)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Đã copy link ảnh!", Toast.LENGTH_SHORT).show()
        }

        binding.btnOpenReddit.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                android.net.Uri.parse(meme.permalink))
            startActivity(intent)
        }
    }

    private fun formatScore(score: Int): String {
        return if (score >= 1000) "${score / 1000}k" else score.toString()
    }
}
