package com.cloud.disk.repository;

import com.cloud.disk.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface UserJpaRepository extends JpaSpecificationExecutor<User>,JpaRepository<User,Long> {
        User findByloginNameAndPwd(String loginName,String pwd);
        List<User> findByLoginName(String loginName);
}