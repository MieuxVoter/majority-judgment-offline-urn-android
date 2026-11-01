package com.illiouchine.jm.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.ui.theme.JmTheme
import org.commonmark.node.Code
import org.commonmark.node.Emphasis
import org.commonmark.node.Image
import org.commonmark.node.Link
import org.commonmark.node.Node
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.parser.Parser
import org.commonmark.node.Text as NodeText

private const val ANNOTATED_STRING_URL_TAG = "url"

private val markdownParser = Parser.builder().build()

/**
 * Composable Material3.Text with rudimentary Markdown syntax.
 * Heavily inspired from https://gist.github.com/LennyLizowzskiy/099db3520b35811bac73937d391ef701
 * With a bunch of fixes for multiline (multi-paragraph) strings.
 *
 * Supports only:
 * - `*italic*`
 * - `**bold**`
 * - ``code``
 * - `[link](https://example.org)`.
 *
 * For wider support we're going to need a lib, or to cook our own.
 * We already found a Kotlin Multiplatform Markdown parser, so it's not that far away.
 */
@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    textColor: Color = Color.Unspecified,
    linkColor: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Start,
) {
    val uriHandler = LocalUriHandler.current
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    val styledText = buildAnnotatedString {
        val root = markdownParser.parse(text)

        withStyle(style.toSpanStyle()) {
            appendMarkdownChildren(
                parent = root,
                textColor = textColor,
                linkColor = linkColor,
            )
        }
    }

    Text(
        text = styledText,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                layoutResult.value?.let { layoutResult ->
                    val position = layoutResult.getOffsetForPosition(offset)

                    styledText.getStringAnnotations(position, position).firstOrNull()
                        ?.let { annotatedString ->
                            if (annotatedString.tag == ANNOTATED_STRING_URL_TAG) {
                                uriHandler.openUri(annotatedString.item)
                            }
                        }
                }
            }
        },
        style = style,
        textAlign = textAlign,
        onTextLayout = { layoutResult.value = it },
    )
}

private fun AnnotatedString.Builder.appendMarkdownChildren(
    parent: Node,
    textColor: Color,
    linkColor: Color,
) {
    var child = parent.firstChild
    while (child != null) {
        when (child) {
            is SoftLineBreak -> {
                append("\n")
            }

            is Paragraph -> {
                if (this.length > 0) append("\n\n")
                appendMarkdownChildren(child, textColor, linkColor)
            }

            is NodeText -> {
                append(child.literal)
            }

            is Emphasis -> {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    appendMarkdownChildren(child, textColor, linkColor)
                }
            }

            is StrongEmphasis -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    appendMarkdownChildren(child, textColor, linkColor)
                }
            }

            is Code -> {
                withStyle(TextStyle(fontFamily = FontFamily.Monospace).toSpanStyle()) {
                    append(child.literal)
                }
            }

            is Link, is Image -> {
                pushStyle(
                    SpanStyle(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline,
                    )
                )
                pushStringAnnotation(
                    tag = ANNOTATED_STRING_URL_TAG,
                    annotation = (child as Link).destination,
                )

                appendMarkdownChildren(
                    parent = child,
                    textColor = textColor,
                    linkColor = linkColor,
                )

                pop()
                pop()
            }
        }

        child = child.next
    }
}

@Preview
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun MarkdownTextPreview() {
    JmTheme {
        MarkdownText(
            text = "Text with *italic*, **bold**, ***italic and bold***, " +
                "[MV](https://mieuxvoter.fr)." +
                "\n" +
                "After a newline…" +
                "\n\n" +
                "And a _second_ paragraph !" +
                "\n" +
                "Lorem ipsum dolor sit amet, sed `in girum imus nocte et consumimur igni`. " +
                "Dura lex sed lex. " +
                "_VISITA INTERIORES TERRAE RECTIFICANDO INVENIES OCCULTUM LAPIDEM_",
        )
    }
}
