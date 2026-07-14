package za.co.ndivhuwo.stackoverflow_search_app.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode


private sealed class HtmlBlock {
    data class Paragraph(val text: AnnotatedString) : HtmlBlock()
    data class CodeBlock(val code: String) : HtmlBlock()
}

private fun parseHtmlBlocks(html: String): List<HtmlBlock> {
    val body = Jsoup.parse(html).body()
    val blocks = mutableListOf<HtmlBlock>()

    fun handle(el: Element) {
        when (el.tagName()) {
            "pre" -> {
                val codeEl = el.selectFirst("code")
                val code = (codeEl ?: el).wholeText().trim('\n')
                blocks.add(HtmlBlock.CodeBlock(code))
            }
            "ul", "ol" -> {
                val builder = AnnotatedString.Builder()
                el.children().forEachIndexed { index, li ->
                    if (index > 0) builder.append("\n")
                    val bullet = if (el.tagName() == "ol") "${index + 1}. " else "•  "
                    builder.append(bullet)
                    appendInline(builder, li)
                }
                if (builder.length > 0) blocks.add(HtmlBlock.Paragraph(builder.toAnnotatedString()))
            }
            else -> {
                val builder = AnnotatedString.Builder()
                appendInline(builder, el)
                if (builder.length > 0) blocks.add(HtmlBlock.Paragraph(builder.toAnnotatedString()))
            }
        }
    }

    if (body.children().isEmpty()) {
        val builder = AnnotatedString.Builder()
        appendInline(builder, body)
        if (builder.length > 0) blocks.add(HtmlBlock.Paragraph(builder.toAnnotatedString()))
    } else {
        body.children().forEach { handle(it) }
    }
    return blocks
}

private val inlineCodeStyle = SpanStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 0.9.em,
    background = Color(0x1F888888)
)

private fun appendInline(builder: AnnotatedString.Builder, element: Element) {
    for (node in element.childNodes()) {
        appendNode(builder, node)
    }
}

private fun appendNode(builder: AnnotatedString.Builder, node: Node) {
    when (node) {
        is TextNode -> builder.append(node.text())
        is Element -> when (node.tagName()) {
            "code" -> {
                val start = builder.length
                builder.append(node.text())
                val end = builder.length
                builder.addStyle(inlineCodeStyle, start, end)
            }
            "b", "strong" -> {
                val start = builder.length
                appendInline(builder, node)
                val end = builder.length
                if (end > start) builder.addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
            }
            "i", "em" -> {
                val start = builder.length
                appendInline(builder, node)
                val end = builder.length
                if (end > start) builder.addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
            }
            "u" -> {
                val start = builder.length
                appendInline(builder, node)
                val end = builder.length
                if (end > start) builder.addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
            }
            "br" -> builder.append("\n")
            "a" -> {
                val href = node.attr("href")
                val start = builder.length
                appendInline(builder, node)
                val end = builder.length
                if (end > start && href.isNotBlank()) {
                    builder.addStringAnnotation(tag = "URL", annotation = href, start = start, end = end)
                    builder.addStyle(
                        SpanStyle(
                            color = Color(0xFF3B82F6),
                            textDecoration = TextDecoration.Underline
                        ),
                        start,
                        end
                    )
                }
            }
            else -> appendInline(builder, node)
        }
        else -> Unit
    }
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int = Int.MAX_VALUE
) {
    val blocks = remember(html) { parseHtmlBlocks(html) }

    if (maxLines == Int.MAX_VALUE) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            blocks.forEach { RenderBlock(it, style, Int.MAX_VALUE) }
        }
        return
    }

    BoxWithConstraints(modifier = modifier) {
        val textMeasurer = rememberTextMeasurer()
        val density = LocalDensity.current
        val maxWidthPx = with(density) { maxWidth.roundToPx() }

        // Single, synchronous pass — no layout feedback loop, no flicker.
        val allocation = remember(html, maxLines, maxWidthPx, style) {
            allocateLines(blocks, maxLines, maxWidthPx, textMeasurer, style)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            blocks.forEachIndexed { index, block ->
                val linesForBlock = allocation[index]
                if (linesForBlock > 0) {
                    RenderBlock(block, style, linesForBlock)
                }
            }
        }
    }
}

@Composable
private fun RenderBlock(block: HtmlBlock, style: TextStyle, maxLines: Int) {
    when (block) {
        is HtmlBlock.Paragraph -> Text(
            text = block.text,
            style = style,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        is HtmlBlock.CodeBlock -> CodeBlock(block.code, maxLines)
    }
}

@Composable
private fun CodeBlock(code: String, maxLines: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = code,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(12.dp)
        )
    }
}

/**
 * Decides, ahead of render time, how many lines each block is allowed to use,
 * given a shared budget of [maxLines].
 */
private fun allocateLines(
    blocks: List<HtmlBlock>,
    maxLines: Int,
    maxWidthPx: Int,
    textMeasurer: TextMeasurer,
    style: TextStyle
): List<Int> {
    var remaining = maxLines
    val result = mutableListOf<Int>()
    val constraints = Constraints(maxWidth = maxWidthPx)

    for (block in blocks) {
        if (remaining <= 0) {
            result.add(0)
            continue
        }

        val used = when (block) {
            is HtmlBlock.Paragraph -> {
                // Wraps normally, so width-constrained measurement is accurate.
                val layout = textMeasurer.measure(
                    text = block.text,
                    style = style,
                    constraints = constraints,
                    maxLines = remaining
                )
                minOf(layout.lineCount, remaining)
            }
            is HtmlBlock.CodeBlock -> {
                // CodeBlock lives inside horizontalScroll, so it never wraps —
                // its "lines" are purely the newlines in the source. Measuring
                // against maxWidthPx here would give a wrong (wrapped) count.
                val newlineCount = block.code.count { it == '\n' } + 1
                minOf(newlineCount, remaining)
            }
        }

        result.add(used)
        remaining -= used
    }
    return result
}

