package com.cloud.disk.repository;

import com.cloud.disk.domain.FileSaveInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface FileJpaRepository extends JpaSpecificationExecutor<FileSaveInfo>,JpaRepository<FileSaveInfo,Long> {
    @Transactional
    @Query(value = "update file xx set xx.deleted=false where xx.id=?1")
    @Modifying
    int updateFileBack(long id);

    List<FileSaveInfo> findByfileToken(String fileToken);

    List<FileSaveInfo>findAllById(Long id);
}