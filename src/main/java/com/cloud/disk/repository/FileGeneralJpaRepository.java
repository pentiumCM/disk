package com.cloud.disk.repository;

import com.cloud.disk.domain.FileGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface FileGeneralJpaRepository extends JpaSpecificationExecutor<FileGeneral>,JpaRepository<FileGeneral,Long> {

    @Transactional
    @Modifying
    @Query(value = "update file_general sc set sc.deleted=true  where sc.fileId=?1")
    int deleteFileGeneral(long fileId);


    @Transactional
    @Modifying
    @Query(value = "update file_general sc set sc.deleted=false  where sc.fileId=?1")
    int backFileGeneral(long fileId);
}