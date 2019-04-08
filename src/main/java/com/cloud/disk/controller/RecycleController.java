package com.cloud.disk.controller;

import com.cloud.disk.core.ResponseBean;
import com.cloud.disk.core.UnicomResponseEnums;
import com.cloud.disk.domain.Recycle;
import com.cloud.disk.dtos.EditUserDto;
import com.cloud.disk.repository.RecycleRepository;
import com.cloud.disk.service.NodeSerice;
import com.cloud.disk.units.UnitHelper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/recycle")
public class RecycleController
{
    @Autowired
    private RecycleRepository recycleRepository;

    @Autowired
    private NodeSerice nodeSerice;


    @GetMapping("/index")
    public ModelAndView index()
    {
        ModelAndView modelAndView= UnitHelper.GetCurrentUserAndView();
        modelAndView.setViewName("/recycle/index");
        return modelAndView;
    }

    @RequestMapping(value = "/delete/{id}")
    public ResponseBean<UnicomResponseEnums> deleteRecycle(@PathVariable long id)
    {
        recycleRepository.deleteById(id);
        return new ResponseBean(true,UnicomResponseEnums.SUCCESS_OPTION);
    }

    @RequestMapping(value = "/deleteall")
    public ResponseBean<UnicomResponseEnums> deleteRecycle()
    {
        Long userid=((EditUserDto) SecurityUtils.getSubject().getPrincipal()).getId();
        List<Recycle>recycles= recycleRepository.findAllByAdduserid(userid);
        recycleRepository.deleteInBatch(recycles);
        return new ResponseBean(true,UnicomResponseEnums.SUCCESS_OPTION);
    }

    @RequestMapping(value = "/")
    public Page<Recycle> getRecycle(Pageable pageable, String search)
    {
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pp =  PageRequest.of(pageable.getPageNumber(),pageable.getPageSize(),sort);
        Long currentUserId =  ((EditUserDto) SecurityUtils.getSubject().getPrincipal()).getId();
       String dd= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(UnitHelper.getDateBefore(new Date(),30));
       System.out.println(dd);
        Page<Recycle> recycles=recycleRepository.findAll(new Specification<Recycle>() {
            @Override
            public Predicate toPredicate(Root<Recycle> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate=criteriaBuilder.conjunction();
                if(search!=""){
                    predicate.getExpressions().add(criteriaBuilder.like(root.get("filename"), "%"+search.trim()+"%"));
                }

                predicate.getExpressions().add(criteriaBuilder.equal(root.get("adduserid"), currentUserId));

                predicate.getExpressions().add(criteriaBuilder.greaterThanOrEqualTo(root.get("addtime").as(String.class), dd));
                return predicate;
            }
        },pp);

        return  recycles;
    }
    @RequestMapping(value = "/backfile/{id}")
    public ResponseBean<UnicomResponseEnums> backfile(@PathVariable long id)
    {
        Recycle recycle=recycleRepository.getOne(id);
        nodeSerice.backfile(recycle);
        recycleRepository.deleteById(id);
        return new ResponseBean(true,UnicomResponseEnums.SUCCESS_OPTION);
    }

    @RequestMapping(value = "/backall")
    public ResponseBean<UnicomResponseEnums>backall()
    {
        Long userid=((EditUserDto) SecurityUtils.getSubject().getPrincipal()).getId();
        List<Recycle>recycles= recycleRepository.findAllByAdduserid(userid);
        for(Recycle recycle :recycles){
            nodeSerice.backfile(recycle);
        }
        recycleRepository.deleteInBatch(recycles);
        return new ResponseBean(true,UnicomResponseEnums.SUCCESS_OPTION);
    }
}