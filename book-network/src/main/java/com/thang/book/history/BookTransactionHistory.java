package com.thang.book.history;

import com.thang.book.book.Book;
import com.thang.book.common.BaseEntity;
import com.thang.book.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookTransactionHistory extends BaseEntity {
    // lưu giao dịch lịch sử saách


    // quan hệ between table
    // user - book
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;


    // kiểm tra xem cuốn sách đã được trả hay chưa
    private boolean returned;

    // trả lại có được chủ chấp thuận hay không phee duyet
    private boolean returnApproved;



}
