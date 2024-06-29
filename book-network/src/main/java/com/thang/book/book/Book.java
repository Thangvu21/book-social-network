package com.thang.book.book;

import com.thang.book.common.BaseEntity;
import com.thang.book.feedback.FeedBack;
import com.thang.book.history.BookTransactionHistory;
import com.thang.book.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

    private String title;

    private String authorName;

    // ma nhan dang
    private String isbn;

    // so yeu ly lich
    private String synopsis;

    // bia co the la hinh anh
    private String bookCover;

    private boolean archived;

    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<FeedBack> feedBackList;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> historyList;

    @Transient
    public double getRate() {
        if (feedBackList == null || feedBackList.isEmpty() ) {
            return 0.0;
        }
        var rate = this.feedBackList
                .stream()
                .mapToDouble(FeedBack::getNote)
                .average()
                .orElse(0.0);
        return Math.round(rate * 10.0) / 10.0;
    }
}
