package com.thang.book.history;

import com.thang.book.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("""
        SELECT history
        from BookTransactionHistory history 
        WHERE history.user.id = :userId
""")
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, @Param("userId") Integer userId);


    @Query("""
        SELECT history
        from BookTransactionHistory history 
        WHERE history.book.owner.id = :userId 
""")
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable,@Param("userId") Integer userId);


    @Query("""
        SELECT 
            COUNT(*) as isBorrowed
        from BookTransactionHistory history 
        WHERE history.book.id = :bookId
        AND history.user.id = :userId
        AND history.returnApproved= false 
""")
    boolean isAlreadyBorrowedByUser(@Param("bookId") Integer bookId, @Param("userId") Integer userId);


    @Query("""
        SELECT transaction
        from BookTransactionHistory transaction
        WHERE transaction.book.id = :bookId
        AND transaction.user.id = :userId
        AND transaction.returned = false 
        AND transaction.returnApproved= false 
""")
    Optional<BookTransactionHistory> findAllByBookIdAndUserId(@Param("bookId") Integer bookId, @Param("userId") Integer userId);


    @Query("""
        SELECT transaction
        from BookTransactionHistory transaction 
        WHERE transaction.book.id = :bookId
        AND transaction.book.owner.id = :userId
        AND transaction.returned = true 
        AND transaction.returnApproved= false 
""")
    Optional<BookTransactionHistory> findAllByBookIdAndOwnerId(@Param("bookId") Integer bookId, @Param("userId") Integer userId);
}
