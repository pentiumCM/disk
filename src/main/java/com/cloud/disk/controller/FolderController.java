package com.cloud.disk.controller;

import com.cloud.disk.core.ResponseBean;
import com.cloud.disk.core.UnicomResponseEnums;
import com.cloud.disk.domain.Folder;
import com.cloud.disk.dtos.EditUserDto;
import com.cloud.disk.repository.FolderJpaRepository;
import com.cloud.disk.service.FolderService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/folder")
public class FolderController {

    @Autowired
    private FolderJpaRepository folderJpaRepository;

    @Autowired
    private FolderService folderService;

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResponseBean<UnicomResponseEnums> editFolder(@RequestBody Folder folder, HttpServletRequest request) {
        if (folder.getId() == 0) {
            Long currentUserId = ((EditUserDto) SecurityUtils.getSubject().getPrincipal()).getId();
            folder.setAdduserid(currentUserId);
            folderJpaRepository.save(folder);
        } else {
            Folder jpaFile = folderJpaRepository.getOne(folder.getId());
            jpaFile.setFolderName(folder.getFolderName());
            folderJpaRepository.save(jpaFile);
        }
        return new ResponseBean(true, UnicomResponseEnums.SUCCESS_OPTION);
    }

    @RequestMapping(value = "/getTreePosition", method = RequestMethod.GET)
    public List<Folder> getTreePosition() {
        return folderService.GetTreePosition();
    }
}
