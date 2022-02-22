package ir.fallahpoor.releasetracker.addlibrary.ui

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun OutlinedTextFieldWithPrefix(
    modifier: Modifier = Modifier,
    prefix: String,
    text: String,
    onTextChange: (String) -> Unit,
    hint: String,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions = KeyboardActions(),
    isError: Boolean
) {
    OutlinedTextField(
        value = text,
        label = {
            Text(text = hint)
        },
        onValueChange = onTextChange,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction
        ),
        visualTransformation = PrefixTransformation(prefix),
        keyboardActions = keyboardActions,
        singleLine = true,
        isError = isError,
        modifier = modifier
    )
}

private class PrefixTransformation(private val prefix: String) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return prefixFilter(text, prefix)
    }

}

private fun prefixFilter(text: AnnotatedString, prefix: String): TransformedText {

    val prefixOffset = prefix.length

    val textOffsetTranslator = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            return offset + prefixOffset
        }

        override fun transformedToOriginal(offset: Int): Int =
            if (offset < prefixOffset) {
                prefixOffset
            } else {
                offset - prefixOffset
            }

    }

    val outputText = AnnotatedString(prefix + text.text)

    return TransformedText(outputText, textOffsetTranslator)

}

@Preview(showBackground = true)
@Composable
private fun OutlinedTextFieldWithPrefixPreview() {
    var text by remember { mutableStateOf("") }
    OutlinedTextFieldWithPrefix(
        prefix = "https://github.com/",
        hint = "Library URL",
        text = text,
        onTextChange = {
            text = it
        },
        isError = false,
        imeAction = ImeAction.Done
    )
}