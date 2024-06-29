package com.thang.book.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    // chứa phương thức tĩnh
    public static Specification<Book> withOwnerId(Integer ownerId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId));
    }
}
