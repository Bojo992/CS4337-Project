package com.cs4337.project.repository;

import com.cs4337.project.model.Users;
import com.cs4337.project.util.SQLQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    public boolean existsById(Integer id);
    public Optional<Users> getByUsername(String username);
    @Query(nativeQuery = true, value = SQLQuery.existAllByIdForUser)
    public Long existsAllById(@Param("ids")List<Integer> ids, @Param("len") int len);
}
