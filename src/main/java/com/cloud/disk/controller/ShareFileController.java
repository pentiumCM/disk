package com.cloud.disk.controller;

import com.cloud.disk.core.ResponseBean;
import com.cloud.disk.core.UnicomResponseEnums;
import com.cloud.disk.domain.FileSaveInfo;
import com.cloud.disk.domain.ShareFile;
import com.cloud.disk.dtos.EditUserDto;
import com.cloud.disk.repository.FileJpaRepository;
import com.cloud.disk.repository.ShareFileJpaRepository;
import com.cloud.disk.units.UnitHelper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件分享的控制层
 */
@RestController
@RequestMapping(value = "/shareFile")
public class ShareFileController {
    @Autowired
    private ShareFileJpaRepository shareFileJpaRepository;

    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = UnitHelper.GetCurrentUserAndView();
        modelAndView.setViewName("/shareFile/index");
        return modelAndView;
    }

    @RequestMapping(value = "/delete/{id}")
    public ResponseBean<UnicomResponseEnums> deleteShareFile(@PathVariable long id) {
        shareFileJpaRepository.deleteById(id);
        return new ResponseBean(true, UnicomResponseEnums.SUCCESS_OPTION);
    }

    @RequestMapping(value = "/")
    public Page<ShareFile> getShareFiles(Pageable pageable, String search) {
        Long currentUserId =  ((EditUserDto) SecurityUtils.getSubject().getPrincipal()).getId();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pp = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<ShareFile> list = shareFileJpaRepository.findAll(new Specification<ShareFile>() {
            @Override
            public Predicate toPredicate(Root<ShareFile> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("adduserid"), currentUserId));
                return predicate;
            }
        }, pp);
        return list;
    }


    @RequestMapping(value = "/edit")
    public ResponseBean<UnicomResponseEnums> ShareFile(@RequestBody ShareFile shareFile) {
        List<ShareFile> shareFiles = shareFileJpaRepository.findAllByNodeId(shareFile.getNodeId());
        EditUserDto user = (EditUserDto) SecurityUtils.getSubject().getPrincipal();
        shareFile.setAdduserid(user.getId());
        if (shareFile.getOverType() == 0) {
            shareFile.setOvertime(new java.sql.Date(UnitHelper.getDateAfter(new Date(), 7).getTime()));
        } else if (shareFile.getOverType() == 1) {
            shareFile.setOvertime(new java.sql.Date(UnitHelper.getDateAfter(new Date(), 30).getTime()));
        } else {
            shareFile.setOvertime(null);
        }
        shareFile.setShareToken(UUID.randomUUID().toString());

        if (shareFile.isIfSecert() == true) {
            shareFile.setExtractState(UnitHelper.uuid());
        }
        shareFile = shareFileJpaRepository.save(shareFile);

        return ResponseBean.success(String.valueOf(shareFile.getId()), "分享成功");
    }

    @RequestMapping(value = "/{id}")
    public ShareFile get(@PathVariable long id) {
        ShareFile ss = shareFileJpaRepository.findById(id).get();
        return ss;
    }

    @RequestMapping(value = "/getByToken")
    public ShareFile getByToken(@PathParam(value = "shareToken") String shareToken) {
        List<ShareFile> ss = shareFileJpaRepository.findAllByShareToken(shareToken);
        if (ss.size() > 0) {
            return ss.get(0);
        }
        return null;
    }

    @GetMapping("/shareIndex")
    public ModelAndView shareIndex(@PathParam(value = "shareToken") String shareToken) {
        List<ShareFile> shareFiles = shareFileJpaRepository.findAllByShareToken(shareToken);
        ModelAndView modelAndView = UnitHelper.GetCurrentUserAndView();

        if (shareFiles.size() > 0) {
            ShareFile ss = shareFiles.get(0);
            ss.setViewCount(ss.getViewCount() + 1);
            shareFileJpaRepository.save(ss);
            modelAndView.setViewName("/shareFile/shareIndex");
        } else {
            modelAndView.setViewName("/error/index");
        }
        return modelAndView;
    }


    @Autowired
    private FileJpaRepository fileJpaRepository;

    @RequestMapping(value = "/extractState")
    public ResponseBean<UnicomResponseEnums> extractState(@RequestParam(value = "fileToken") String fileToken, @RequestParam(value = "extractState") String extractState) {
        List<ShareFile> shareFiles = shareFileJpaRepository.findAllByShareToken(fileToken);
        if (shareFiles.size() > 0) {
            ShareFile sf = shareFiles.get(0);
            if (sf.getOverType() != 2) {
                float days = (float) (sf.getOvertime().getTime() - sf.getShareTime().getTime()) / (1000 * 3600 * 24);
                if (sf.getOverType() == 0 && days > 7) {
                    return ResponseBean.error("7天有效期已过!");
                }

                float dd = (float) (sf.getOvertime().getTime() - sf.getShareTime().getTime()) / (1000 * 3600 * 24);
                if (sf.getOverType() == 1 && dd > 30) {
                    return ResponseBean.error("30有效期已过!");
                }
            }
                if (sf.isIfSecert() == true) {
                    if (sf.getExtractState().equals(extractState)) {
                        List<FileSaveInfo> fileSaveInfos = fileJpaRepository.findAllById(sf.getNodeId());
                        if (fileSaveInfos.size() > 0) {
                            return ResponseBean.success(fileSaveInfos.get(0).getFileToken(), fileSaveInfos.get(0).getFileName());
                        }
                    } else {
                        return ResponseBean.error("提取码不正确!");
                    }
                } else {
                    List<FileSaveInfo> fileSaveInfos = fileJpaRepository.findAllById(sf.getNodeId());
                    if (fileSaveInfos.size() > 0) {
                        return ResponseBean.success(fileSaveInfos.get(0).getFileToken(), fileSaveInfos.get(0).getFileName());
                    }
                }
        }
        return ResponseBean.error("用户已删除此文件，不存在此文件!");
    }


    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseBean<UnicomResponseEnums> save(@RequestBody FileSaveInfo fileSaveInfo) {
        List<FileSaveInfo> fileSaveInfos = fileJpaRepository.findByfileToken(fileSaveInfo.getFileToken());
        if (fileSaveInfos.size() == 0) {
            return ResponseBean.success("用户已删除此文件，不存在此文件!");
        } else {
            FileSaveInfo fff = fileSaveInfos.get(0);
            EditUserDto editUserDto = (EditUserDto) SecurityUtils.getSubject().getPrincipal();

            fff.setFolderid(fileSaveInfo.getFolderid());
            fileSaveInfo.setAdduserid(editUserDto.getId());
            fileSaveInfo.setFileName(fff.getFileName());
            fileSaveInfo.setFileToken(fff.getFileToken());
            fileSaveInfo.setContentType(fff.getContentType());
            fileSaveInfo.setFileType(fff.getFileType());
            fileSaveInfo.setMd5Val(fff.getMd5Val());
            fileSaveInfo.setFileSize(fff.getFileSize());
            fileSaveInfo.setExt(fff.getEix());

            fileJpaRepository.save(fileSaveInfo);
        }
        return ResponseBean.success("保存成功!");
    }

    @RequestMapping(value = "/saveCount", method = RequestMethod.POST)
    public ResponseBean<UnicomResponseEnums> saveCount(@RequestBody ShareFile shareFile) {

        List<ShareFile> shareFiles = shareFileJpaRepository.findAllByShareToken(shareFile.getShareToken());
        if (shareFiles.size() > 0) {
            ShareFile ss = shareFiles.get(0);
            ss.setSaveCount(ss.getSaveCount() + 1);
            shareFileJpaRepository.save(ss);
        }
        return ResponseBean.success("保存成功!");
    }

    @RequestMapping(value = "/downloadCount", method = RequestMethod.POST)
    public ResponseBean<UnicomResponseEnums> downloadCount(@RequestBody ShareFile shareFile) {

        List<ShareFile> shareFiles = shareFileJpaRepository.findAllByShareToken(shareFile.getShareToken());
        if (shareFiles.size() > 0) {
            ShareFile ss = shareFiles.get(0);
            ss.setDownloadCount(ss.getDownloadCount() + 1);
            shareFileJpaRepository.save(ss);
        }
        return ResponseBean.success("保存成功!");
    }
}
