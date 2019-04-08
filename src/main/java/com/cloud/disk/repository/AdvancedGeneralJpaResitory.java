/*JPA（Java Persistence API)是SUN官方推出的Java持久化规范，它为Java开发人员提供了一种对象/关联映射工具来管理Java应用中的关系数据。
        它的出现主要是为了简化现有的持久化开发工作和整合ORM技术，结束现在Hibernate，TopLink，JDO等ORM框架各自为营的局面。*/
/*继承JpaRepository即可*/
package com.cloud.disk.repository;

import com.cloud.disk.domain.AdvancedGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvancedGeneralJpaResitory extends JpaSpecificationExecutor<AdvancedGeneral>,JpaRepository<AdvancedGeneral ,Long> {
    List<AdvancedGeneral>findAllByNodeId(Long nodeId);
}