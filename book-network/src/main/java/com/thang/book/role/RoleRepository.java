package com.thang.book.role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Đây là tên của phương thức. Trong Spring Data JPA,
    // phương thức này được gọi là "query method" và tuân theo quy tắc đặt tên đặc biệt để tự động tạo truy vấn.
    // findBy: Đây là phần tiền tố của phương thức, biểu thị rằng phương thức này sẽ tìm kiếm các đối tượng.
    // Name: Đây là tên của thuộc tính trong entity Role mà phương thức sẽ tìm kiếm.
    // Spring Data JPA sẽ tự động tạo truy vấn dựa trên tên phương thức.
    Optional<Role> findByName (String role);
}
