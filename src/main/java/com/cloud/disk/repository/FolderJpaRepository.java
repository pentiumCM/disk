package com.cloud.disk.repository;

import com.cloud.disk.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FolderJpaRepository extends JpaSpecificationExecutor<Folder>,JpaRepository<Folder,Long> {

    @Transactional
    @Modifying
    @Query(value = "update folder sc set sc.deleted=false  where sc.id=?1")
    int updateFolderBack(long id);

    List<Folder> findAllByAdduserid(long adduserid);
}
