package com.example.demo.repository;



import com.example.demo.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value="SELECT * FROM user_flyway u WHERE u.user_id = ?1", nativeQuery = true)
    User findByUser_id(int user_id);
}
