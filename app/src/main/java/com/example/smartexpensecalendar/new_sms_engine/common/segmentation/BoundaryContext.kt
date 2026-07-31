package com.example.smartexpensecalendar.new_sms_engine.common.segmentation

import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token

data class BoundaryContext(

    val relation: SegmentRelation,

    val segment: List<Token>,

    val previous: Token?,

    val current: Token,

    val next: Token?

)