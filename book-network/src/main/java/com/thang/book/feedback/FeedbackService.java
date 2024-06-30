package com.thang.book.feedback;


import com.thang.book.book.Book;
import com.thang.book.book.BookRepository;
import com.thang.book.common.PageResponse;
import com.thang.book.exception.OperationNotPremittedException;
import com.thang.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    private final BookRepository bookRepository;

    private final FeedBackMapper feedBackMapper;
    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("no book found with id:" + request.bookId()));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPremittedException("You can not give feedback for an archived or not shareable");
        }
        User myUser = ((User) connectedUser.getPrincipal());
        // kiểm tra sách không phải của người đó
        if (Objects.equals(myUser.getId(), book.getOwner().getId())) {
            // throw exception
            throw new OperationNotPremittedException("You can not give a feedback for to your own book");
        }

        FeedBack feedBack = feedBackMapper.toFeedBack(request);
        return feedbackRepository.save(feedBack).getId();
    }

    public PageResponse<FeedBackResponse> findAllFeedbacksByBook(Integer bookId, Integer page, Integer size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<FeedBack> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedBackResponse> feedBackResponses = feedbacks
                .stream().map(feedBack -> feedBackMapper.toFeedBackResponse(feedBack, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedBackResponses,
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
