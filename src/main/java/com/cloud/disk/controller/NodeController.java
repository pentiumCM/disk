package com.cloud.disk.controller;

import com.cloud.disk.core.ResponseBean;
import com.cloud.disk.core.UnicomResponseEnums;
import com.cloud.disk.domain.*;
import com.cloud.disk.dtos.EditUserDto;
import com.cloud.disk.repository.*;
import com.cloud.disk.service.NodeSerice;
import com.cloud.disk.units.UnitHelper;
import org.apache.shiro.SecurityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/node")
public class NodeController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private NodeSerice nodeSerice;
    @Autowired
    private FileJpaRepository fileJpaRepository;
    @Autowired
    private FolderJpaRepository folderJpaRepository;
    @Autowired
    private RecycleRepository recycleRepository;

    @Autowired
    private NodeJpaRepository nodeJpaRepository;

    @Autowired
    private FileGeneralJpaRepository fileGeneralJpaRepository;

    @Autowired
    private AdvancedGeneralJpaResitory advancedGeneralJpaResitory;


    @RequestMapping("/index")
    public ModelAndView index(String title) {
        ModelAndView modelAndView = UnitHelper.GetCurrentUserAndView();
        modelAndView.setViewName("/node/index");
        return modelAndView;
    }

    @RequestMapping(value = "/getNodes")
    public List<FileSaveInfo> getNodes(String search, int fileType) {
        Long currentUserId =  ((EditUserDto)SecurityUtils.getSubject().getPrincipal()).getId();
        List<FileSaveInfo> files=fileJpaRepository.findAll(new Specification<FileSaveInfo>() {
            @Override
            public Predicate toPredicate(Root<FileSaveInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate=criteriaBuilder.conjunction();
                if(search!=""){
                    predicate.getExpressions().add(criteriaBuilder.like(root.get("fileName"), "%"+search.trim()+"%"));
                }
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("fileType"), fileType));
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("adduserid"), currentUserId));
                return predicate;
            }
        });
        return  files;
    }


    @RequestMapping(value = "/")
    public List<FileSaveInfo> getFiles(String search, int folderid) {
        return nodeSerice.GetFileGrid(search, folderid);
    }

    @RequestMapping(value = "/edit")
    public ResponseBean<UnicomResponseEnums> editFileName(@RequestBody FileSaveInfo file) {

        List<FileSaveInfo> fileSaveInfos = fileJpaRepository.findAllById(file.getId());

        if (fileSaveInfos.size() > 0) {
            FileSaveInfo ff = fileSaveInfos.get(0);
            ff.setFileName(file.getFileName());
            fileJpaRepository.save(ff);             //将修改之后的信息保存到数据库中
            return new ResponseBean(true, UnicomResponseEnums.SUCCESS_OPTION);
        }
        return ResponseBean.error("无此文件！");
    }


    @RequestMapping(value = "/uploadFiles")
    public ResponseBean<UnicomResponseEnums> uploadFiles(@RequestBody FileSaveInfo file) {
        if (file == null || file.getFileToken() == null) {
            return ResponseBean.error("未选择要上传的文件!");
        }

        String[] tokens = file.getFileToken().split("\\*");
        EditUserDto user = (EditUserDto) SecurityUtils.getSubject().getPrincipal();
        List<FileSaveInfo> fileSaveInfos = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            List<Node> nodes = nodeJpaRepository.findAllByFileToken(tokens[i]);
            if (nodes.size() > 0) {
                Node nn = nodes.get(0);
                FileSaveInfo f1 = new FileSaveInfo();
                f1.setFileType(nn.getFileType());
                f1.setFolderid(file.getFolderid());
                f1.setFileToken(tokens[i]);
                f1.setFileName(nn.getFileName());
                f1.setFileSize(nn.getFileSize());
                f1.setMd5Val(nn.getMd5Val());
                f1.setFileType(nn.getFileType());
                f1.setAdduserid(user.getId());
                f1.setContentType(nn.getContentType());
                f1.setExt(nn.getExt());
                FileSaveInfo newSaveInfo= fileJpaRepository.saveAndFlush(f1);   //往数据库中插入信息

                if(nn.getFileType()==1) {
                    List<AdvancedGeneral> advancedGenerals = advancedGeneralJpaResitory.findAllByNodeId(nn.getId());
                    for (int j = 0; j < advancedGenerals.size(); j++) {
                        FileGeneral fg=new FileGeneral(advancedGenerals.get(j).getScore(), advancedGenerals.get(j).getRoot(), advancedGenerals.get(j).getKeyword());
                        fg.setFileId(newSaveInfo.getId());
                        fg.setAdduserid(user.getId());
                        fileGeneralJpaRepository.save(fg);
                    }
                }
            } else {
                logger.error("no find!!" + tokens[i]);
            }
        }

        return new ResponseBean(true, UnicomResponseEnums.SUCCESS_OPTION);
    }


    @RequestMapping(value = "/delete/{id}/{fileType}")
    public ResponseBean<UnicomResponseEnums> deleteFile(@PathVariable long id, @PathVariable int fileType) {
        EditUserDto sessionUser = (EditUserDto) SecurityUtils.getSubject().getPrincipal();
        long adduserid = sessionUser.getId();
        if (fileType == 0) {
            Folder folder = folderJpaRepository.getOne(id);
            Recycle recycle = new Recycle(folder.getId(), folder.getFolderName(), adduserid, 0);
            recycleRepository.save(recycle);
            folderJpaRepository.deleteById(id);
        } else {
            FileSaveInfo file = fileJpaRepository.getOne(id);
            Recycle recycle = new Recycle(file.getId(), file.getFileName(), file.getFileSize(), adduserid, 1);
            recycleRepository.save(recycle);
            fileJpaRepository.deleteById(id);
            fileGeneralJpaRepository.deleteFileGeneral(id);
        }
        return new ResponseBean(true, UnicomResponseEnums.SUCCESS_OPTION);
    }
}
