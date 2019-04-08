package com.cloud.disk.controller;

import com.cloud.disk.domain.FileGeneral;
import com.cloud.disk.dtos.EditUserDto;
import com.cloud.disk.repository.FileGeneralJpaRepository;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@RestController
@RequestMapping(value = "/fileGeneral")
public class FileGeneralController {
    @Autowired
    private FileGeneralJpaRepository fileGeneralJpaRepository;

    @RequestMapping(value = "/")
    public List<FileGeneral> getFileGeneral(long fileId) {
        Long currentUserId =  ((EditUserDto)SecurityUtils.getSubject().getPrincipal()).getId();
        List<FileGeneral> files=fileGeneralJpaRepository.findAll(new Specification<FileGeneral>() {
            @Override
            public Predicate toPredicate(Root<FileGeneral> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate=criteriaBuilder.conjunction();
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("fileId"), fileId));
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("adduserid"), currentUserId));
                return predicate;
            }
        });
        return  files;
    }
}
