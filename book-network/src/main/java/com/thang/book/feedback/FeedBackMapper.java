package com.thang.book.feedback;

import com.thang.book.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
    public class FeedBackMapper {
        public FeedBack toFeedBack(FeedbackRequest request) {
            return FeedBack.builder()
                    .note(request.note())
                    .comment(request.comment())
                    .book(Book.builder()
                            .id(request.bookId())
                            .archived(false) // not required and has no impact just to satisfy lombok để thỏa mãn lombok
                            .shareable(false)
                            .build())
                    .build();
        }

        public FeedBackResponse toFeedBackResponse(FeedBack feedBack, Integer id) {
            return FeedBackResponse.builder()
                    .note(feedBack.getNote())
                    .comment(feedBack.getComment())
                    .ownFeedBack(Objects.equals(feedBack.getCreateBy(), id))
                    .build();

        }
    }
