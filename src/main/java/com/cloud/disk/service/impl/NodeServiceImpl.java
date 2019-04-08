package com.cloud.disk.service.impl;

import com.cloud.disk.domain.FileSaveInfo;
import com.cloud.disk.domain.Folder;
import com.cloud.disk.domain.Recycle;
import com.cloud.disk.dtos.EditUserDto;
import com.cloud.disk.repository.FileGeneralJpaRepository;
import com.cloud.disk.repository.FileJpaRepository;
import com.cloud.disk.repository.FolderJpaRepository;
import com.cloud.disk.service.NodeSerice;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
@Service
public class NodeServiceImpl implements NodeSerice {
    @Autowired
    private FileJpaRepository fileJpaRepository;
    @Autowired
    private FolderJpaRepository folderJpaRepository;
    @Override
    public List<FileSaveInfo> GetFileGrid(String search, int folderid) {
        //我的文档，所有自动上传的文件都会放到我的文档中
        List<FileSaveInfo> fileCatalist = new ArrayList<FileSaveInfo>() { };
        Long currentUserId =  ((EditUserDto)SecurityUtils.getSubject().getPrincipal()).getId();

        List<FileSaveInfo> files=fileJpaRepository.findAll(new Specification<FileSaveInfo>() {
            @Override
            public Predicate toPredicate(Root<FileSaveInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate=criteriaBuilder.conjunction();
                if(search!=""){
                    predicate.getExpressions().add(criteriaBuilder.like(root.get("fileName"), "%"+search.trim()+"%"));
                }
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("folderid"), folderid));
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("adduserid"), currentUserId));
                return predicate;
            }
        });
        fileCatalist.addAll(files);

        List<Folder> folders=folderJpaRepository.findAll(new Specification<Folder>() {
            @Override
            public Predicate toPredicate(Root<Folder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate=criteriaBuilder.conjunction();
                if(search!=""){
                    predicate.getExpressions().add(criteriaBuilder.like(root.get("folderName"), "%"+search.trim()+"%"));
                }
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("pid"), folderid));
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("adduserid"), currentUserId));
                return predicate;
            }
        });


        for (int i = 0; i < folders.size(); i++) {
            FileSaveInfo file = new FileSaveInfo();
            file.setFileName(folders.get(i).getFolderName());
            file.setUpdatetime(folders.get(i).getUpdatetime());
            file.setId(folders.get(i).getId());
            file.setFileType(0);
            fileCatalist.add(file);
        }

        return fileCatalist;
    }

    @Autowired
    private FileGeneralJpaRepository fileGeneralJpaRepository;
    public  void backfile(Recycle recycle)
    {
        if(recycle.getFiletype()==0){
            folderJpaRepository.updateFolderBack(recycle.getFileId());
        }else{
            fileJpaRepository.updateFileBack(recycle.getFileId());

           FileSaveInfo fileSaveInfo= fileJpaRepository.getOne(recycle.getFileId());
            if(fileSaveInfo.getFileType()==1){
                fileGeneralJpaRepository.backFileGeneral(fileSaveInfo.getId());
            }
        }
    }
}
