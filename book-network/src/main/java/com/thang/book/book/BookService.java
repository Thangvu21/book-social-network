package com.thang.book.book;

import com.thang.book.common.PageResponse;
import com.thang.book.exception.OperationNotPremittedException;
import com.thang.book.file.FileStorageService;
import com.thang.book.history.BookTransactionHistory;
import com.thang.book.history.BookTransactionHistoryRepository;
import com.thang.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.thang.book.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
    public class BookService {

    private final BookMapper bookMapper;

    private final BookRepository bookRepository;

    private final BookTransactionHistoryRepository transactionHistoryRepository;

    private final FileStorageService storageService;

    public Integer save(BookRequest bookRequest, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    // all ko phair nguoi tra
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream().map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                    books.getNumber(),
                    books.getSize(),
                    bookResponse,
                    books.getTotalElements(),
                    books.getTotalPages(),
                    books.isFirst(),
                    books.isLast()
                );
    }


    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                books.getNumber(),
                books.getSize(),
                bookResponses,
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );

    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponses
                = allBorrowedBooks.stream().map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                borrowedBookResponses,
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );

    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allReturnedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponses
                = allReturnedBooks.stream().map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                allReturnedBooks.getNumber(),
                allReturnedBooks.getSize(),
                borrowedBookResponses,
                allReturnedBooks.getTotalElements(),
                allReturnedBooks.getTotalPages(),
                allReturnedBooks.isFirst(),
                allReturnedBooks.isLast()
        );

    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("no book found with id:" + bookId));
        User user = ((User) connectedUser.getPrincipal());
        // chỉ ai sở hữu mới sửa được
        if (!Objects.equals(user.getId(), book.getOwner().getId())) {
            // throw exception
            throw new OperationNotPremittedException("You can not others update book shareable status");
        }
        // khi mà chỉnh thì ngược lại so với cái ban đầu thôi
        book.setShareable(!book.isShareable());
        // thay đổi xong thì lưu vô
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("no book found with id:" + bookId));
        User user = ((User) connectedUser.getPrincipal());
        // chỉ ai sở hữu mới sửa được
        if (!Objects.equals(user.getId(), book.getOwner().getId())) {
            // throw exception
            throw new OperationNotPremittedException("You can not update others book archived status");
        }
        // khi mà chỉnh thì ngược lại so với cái ban đầu thôi
        book.setArchived(!book.isArchived());
        // thay đổi xong thì lưu vô
        bookRepository.save(book);
        return bookId;

    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(("not book found with id:" + bookId)));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPremittedException("book cant not be borrowed since it is archived or not shareable");
        }
        User myUser = ((User) connectedUser.getPrincipal());
        // kiểm tra sách không phải của người đó
        if (Objects.equals(myUser.getId(), book.getOwner().getId())) {
            // throw exception
            throw new OperationNotPremittedException("You can not borrow your own book");
        }
        // check book is already
        final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, myUser.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPremittedException("The request book is already Borrowed");
        }
        BookTransactionHistory myHistory = BookTransactionHistory.builder()
                .user(myUser)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(myHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(("not book found with id:" + bookId)));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPremittedException("book cant not be borrowed since it is archived or not shareable");
        }
        User myUser = ((User) connectedUser.getPrincipal());
        // kiểm tra sách không phải của người đó
        if (Objects.equals(myUser.getId(), book.getOwner().getId())) {
            // throw exception
            throw new OperationNotPremittedException("You can not borrow or return your own book");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository
                .findAllByBookIdAndUserId(bookId, myUser.getId())
                .orElseThrow(() -> new OperationNotPremittedException("You did not borrow this book"));
        bookTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(("not book found with id:" + bookId)));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPremittedException("book cant not be borrowed since it is archived or not shareable");
        }
        User myUser = ((User) connectedUser.getPrincipal());
        // kiểm tra sách không phải của người đó
        if (Objects.equals(myUser.getId(), book.getOwner().getId())) {
            // throw exception
            throw new OperationNotPremittedException("You can not borrow or return your own book");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository
                .findAllByBookIdAndOwnerId(bookId, myUser.getId())
                .orElseThrow(() -> new OperationNotPremittedException("The book is not returned"));
        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(("not book found with id:" + bookId)));
        User myUser = ((User) connectedUser.getPrincipal());
        var bookCover = storageService.saveFile(file, myUser.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);

    }
}
