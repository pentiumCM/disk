package com.cloud.disk.repository;

import com.cloud.disk.domain.Recycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RecycleRepository  extends JpaSpecificationExecutor<Recycle>,JpaRepository<Recycle,Long> {
     List<Recycle> findAllByAdduserid(Long adduserid);
}

