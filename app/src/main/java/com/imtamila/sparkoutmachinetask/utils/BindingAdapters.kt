package com.imtamila.sparkoutmachinetask.utils

import android.os.Build
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(imageView: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .apply(
                RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
            )
            .into(imageView)
    }
}

@BindingAdapter(value = ["text", "htmlText"], requireAll = false)
fun bindHtmlText(textView: TextView, text: String?, isHtmlText: Boolean = false) {
    if (!text.isNullOrEmpty()) {
        if (isHtmlText)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
            } else {
                textView.text = Html.fromHtml(text)
            }
        else textView.text = text
    }
}
