package top.rechinx.meow.module.detail

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import top.rechinx.meow.R
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.widget.ChapterButton

class DetailAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private var mContext: Context
    private var mData: ArrayList<Chapter>
    private var mInflater: LayoutInflater
    private var mComic: Comic? = null

    constructor(context: Context, list: ArrayList<Chapter>) {
        this.mContext = context
        this.mData = list
        this.mInflater = LayoutInflater.from(mContext)
    }

    fun add(data: Chapter) {
        if (mData.add(data)) {
            notifyItemInserted(mData.size)
        }
    }

    fun add(location: Int, data: Chapter) {
        mData.add(location, data)
        notifyItemInserted(location)
    }

    fun addAll(collection: Collection<Chapter>) {
        addAll(mData.size, collection)
    }

    fun addAll(location: Int, collection: Collection<Chapter>) {
        if (mData.addAll(location, collection)) {
            notifyItemRangeInserted(location, location + collection.size)
        }
    }

    fun setComic(comic: Comic) {
        this.mComic = comic
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            return HeaderViewHolder(mInflater.inflate(R.layout.item_chapter_header, parent, false))
        }
        return ChapterViewHolder(mInflater.inflate(R.layout.item_chapter, parent, false))
    }

    override fun getItemViewType(position: Int): Int = if(position == 0) 0 else 1


    override fun getItemCount(): Int = mData.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == 0) {
            var headerHolder = holder as HeaderViewHolder
            if(mComic != null) {
                val glideUrl = GlideUrl(mComic?.image, LazyHeaders.Builder()
                        .addHeader("Referer", "http://images.dmzj.com/")
                        .build())
                Glide.with(mContext).load(glideUrl).into(headerHolder.mComicImage)
                headerHolder.mComicTitle.text = mComic?.title
                headerHolder.mComicIntro.text = mComic?.description
                headerHolder.mComicAuthor.text = mComic?.author
                headerHolder.mComicStatus.text = mComic?.status
                headerHolder.mComicUpdate.text = "最后更新： ${mComic?.update}"
            }
        } else {
            val chapter = mData[position - 1]
            var chapterHolder = holder as ChapterViewHolder
            chapterHolder.mChapterButton.text = chapter.title
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager as GridLayoutManager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) manager.spanCount else 1
            }
        }
    }

    class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.item_header_comic_image) lateinit var mComicImage: ImageView
        @BindView(R.id.item_header_comic_title) lateinit var mComicTitle: TextView
        @BindView(R.id.item_header_comic_intro) lateinit var mComicIntro: TextView
        @BindView(R.id.item_header_comic_status) lateinit var mComicStatus: TextView
        @BindView(R.id.item_header_comic_update) lateinit var mComicUpdate: TextView
        @BindView(R.id.item_header_comic_author) lateinit var mComicAuthor: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    class ChapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.item_chapter_button) lateinit var mChapterButton: ChapterButton

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}