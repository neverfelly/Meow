package top.rechinx.meow.source

import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import top.rechinx.meow.core.JsonIterator
import top.rechinx.meow.core.Parser
import top.rechinx.meow.core.SearchIterator
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.model.ImageUrl
import java.util.*

open class Dmzj: Parser {

    override fun getSearchRequest(keyword: String, page: Int): Request? {
        if(page == 1) {
            val url = "http://v2.api.dmzj.com/search/show/0/$keyword/${page - 1}.json"
            return Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)")
                    .build()
        }
        return null
    }

    override fun getInfoRequest(cid: String): Request? {
        val url = "http://v2.api.dmzj.com/comic/$cid.json"
        return Request.Builder().url(url).build()
    }

    override fun parserInto(html: String, comic: Comic) {
        try {
            val obj = JSONObject(html)
            val title = obj.getString("title")
            val cover = obj.getString("cover")
            val time = if (obj.has("last_updatetime")) obj.getLong("last_updatetime") * 1000 else null
            val update = time
            val intro = obj.optString("description")
            val sb = StringBuilder()
            val array = obj.getJSONArray("authors")
            for (i in 0 until array.length()) {
                sb.append(array.getJSONObject(i).getString("tag_name")).append(" ")
            }
            val author = sb.toString()
            val status = obj.getJSONArray("status").getJSONObject(0).getInt("tag_id") == 2310
            comic.setInfo(title, cover, update.toString(), intro, author, status)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getImageRequest(cid: String, image: String): Request? {
        val url = "http://m.dmzj.com/view/$cid/$image.html"
        return Request.Builder().url(url).build()
    }

    override fun parseImage(html: String): List<ImageUrl>? {
        return null
    }


    override fun parseChapter(html: String): List<Chapter>? {
        val list = LinkedList<Chapter>()
        try {
            val obj = JSONObject(html)
            val array = obj.getJSONArray("chapters")
            for (i in 0 until array.length()) {
                val data = array.getJSONObject(i).getJSONArray("data")
                for (j in 0 until data.length()) {
                    val chapter = data.getJSONObject(j)
                    val title = chapter.getString("chapter_title")
                    val chapterId = chapter.getString("chapter_id")
                    list.add(Chapter(title, chapterId))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    override fun getSearchIterator(html: String, page: Int): SearchIterator? {
        try {
            return object : JsonIterator(JSONArray(html)) {
                override fun parse(obj: JSONObject): Comic? {
                    try {
                        val cid = obj.getString("id")
                        val title = obj.getString("title")
                        val cover = obj.getString("cover")
                        val author = obj.optString("authors")
                        return Comic(SOURCE_DMZJ, cid, title, cover, author)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return null
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        val SOURCE_DMZJ = 1
    }
}