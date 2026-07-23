package com.example.smartexpensecalendar.new_sms_engine.common.segmentation

import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

data class MessageSegment(

    val relation: SegmentRelation,

    val tokens: List<Token>

) {

    val bodyTokens: List<Token>
        get() = tokens.drop(1)

    val bodyText: String
        get() = bodyTokens.joinToString(" ") { it.text }

    val text: String
        get() = tokens.joinToString(" ") { it.text }

}