package com.thang.book.feedback;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackResponse {

    private Double note;

    private String comment;

    private boolean ownFeedBack;

}
