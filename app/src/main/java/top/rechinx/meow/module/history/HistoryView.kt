package top.rechinx.meow.module.favorite

import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseView

interface HistoryView: BaseView {

    fun onComicLoadSuccess(list: List<Comic>)

    fun onComicLoadFailure()

}