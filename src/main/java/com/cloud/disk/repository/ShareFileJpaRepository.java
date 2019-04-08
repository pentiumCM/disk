package com.cloud.disk.repository;

import com.cloud.disk.domain.ShareFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ShareFileJpaRepository extends JpaSpecificationExecutor<ShareFile>, JpaRepository<ShareFile, Long> {
    List<ShareFile> findAllByNodeId(Long nodeId);
    List<ShareFile>findAllByShareToken(String shareToken);
}