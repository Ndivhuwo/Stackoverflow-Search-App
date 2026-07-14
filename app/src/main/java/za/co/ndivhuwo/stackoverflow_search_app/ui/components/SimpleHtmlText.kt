package za.co.ndivhuwo.stackoverflow_search_app.ui.components

import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
fun SimpleHtmlText(
    html: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int? = null,
    textAlign: TextAlign? = null
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
            
            // Apply text color
            if (color != Color.Unspecified) {
                textView.setTextColor(color.toArgb())
            } else if (style.color != Color.Unspecified) {
                textView.setTextColor(style.color.toArgb())
            }

            // Apply font weight
            if (fontWeight != null || style.fontWeight != null) {
                val weight = fontWeight ?: style.fontWeight
                if (weight == FontWeight.Bold || weight == FontWeight.W700) {
                    textView.typeface = android.graphics.Typeface.DEFAULT_BOLD
                } else {
                    textView.typeface = android.graphics.Typeface.DEFAULT
                }
            }

            // Apply font size
            if (style.fontSize.isSp) {
                textView.textSize = style.fontSize.value
            }

            // Apply alignment
            textAlign?.let {
                textView.gravity = when (it) {
                    TextAlign.Center -> android.view.Gravity.CENTER
                    TextAlign.End -> android.view.Gravity.END
                    else -> android.view.Gravity.START
                }
            }

            // Apply max lines and ellipsis
            maxLines?.let {
                textView.maxLines = it
                textView.ellipsize = TextUtils.TruncateAt.END
            }
        }
    )
}
