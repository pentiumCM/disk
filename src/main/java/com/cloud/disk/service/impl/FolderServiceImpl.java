package com.cloud.disk.service.impl;

import com.cloud.disk.domain.Folder;
import com.cloud.disk.dtos.EditUserDto;
import com.cloud.disk.repository.FolderJpaRepository;
import com.cloud.disk.service.FolderService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl implements FolderService {
    @Autowired
    private FolderJpaRepository folderJpaRepository;
    public  List<Folder>GetTreePosition()
    {
        Long currentUserId =  ((EditUserDto)SecurityUtils.getSubject().getPrincipal()).getId();
        List<Folder> cataList =  folderJpaRepository.findAllByAdduserid(currentUserId);

        List<Folder> cataInputList = new ArrayList<Folder>();
        for (Folder item : cataList)
        {
            if (item.getId() == item.getPid()) continue;
            String name = "/" + item.getFolderName();
            name = this.FindParentName(cataList, item.getPid()) + name;
            Folder node = new Folder(name,item.getId());
            cataInputList.add(node);
        }
        return cataInputList;
    }

    private String FindParentName(List<Folder> cataList, long PId)
    {
        if(cataList.size()==0)return "";
        List<Folder> dd = cataList.stream().filter(r->r.getId()==PId).collect(Collectors.toList());
        Folder d;
        if(dd!=null&&dd.size()>0){
            d=dd.get(0);
        }else {
            return "";
        }
        if (d==null|| d.getId() == d.getPid())
        {
            return "";
        }

        if (  d.getPid() == 0)
        {
            return "/" + d.getFolderName();
        }
        else
        {
            return this.FindParentName(cataList, d.getPid()) + "/" + d.getFolderName();
        }
    }
}
