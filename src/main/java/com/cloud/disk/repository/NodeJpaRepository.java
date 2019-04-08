package com.cloud.disk.repository;

import com.cloud.disk.domain.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NodeJpaRepository extends JpaSpecificationExecutor<Node>, JpaRepository<Node,Long> {
    List<Node> findAllByMd5Val(String md5);
    List<Node>findAllByFileToken(String fileToken);
}