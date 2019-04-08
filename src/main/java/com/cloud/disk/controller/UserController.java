package com.cloud.disk.controller;

import com.cloud.disk.core.ResponseBean;
import com.cloud.disk.core.UnicomResponseEnums;
import com.cloud.disk.domain.User;
import com.cloud.disk.dtos.EditUserDto;
import com.cloud.disk.dtos.ResetPwdUserDto;
import com.cloud.disk.dtos.SaveUserDto;
import com.cloud.disk.repository.UserJpaRepository;
import com.cloud.disk.units.MyDES;
import com.cloud.disk.units.UnitHelper;
import org.apache.commons.beanutils.BeanUtils;
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
import java.lang.reflect.InvocationTargetException;
import java.util.List;


@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = UnitHelper.GetCurrentUserAndView();
        modelAndView.setViewName("/user/index");
        return modelAndView;
    }

    @GetMapping("/profile")
    public ModelAndView profile() {
        ModelAndView modelAndView = UnitHelper.GetCurrentUserAndView();
        modelAndView.setViewName("/user/profile");
        return modelAndView;
    }

    @RequestMapping(value = "/edit")
    public ResponseBean<UnicomResponseEnums> editUser(@RequestBody EditUserDto user) throws InvocationTargetException, IllegalAccessException {
        List<User> users = userJpaRepository.findByLoginName(user.getLoginName());
        if (user.getId() == 0) {
            if (users != null && users.size() > 0) {
                return new ResponseBean(false, UnicomResponseEnums.REPEAT_LOGINNAME);
            }
        } else {
            if (users != null) {
                boolean isAny = users.stream().anyMatch(r -> r.getId() != user.getId());
                if (isAny) {
                    return new ResponseBean(false, UnicomResponseEnums.REPEAT_LOGINNAME);
                }
            }
        }
        User saveUser = new User();
        if (user.getId() != 0) {
            saveUser = users.get(0);
        }else{
            saveUser.setPwd(MyDES.encryptBasedDes("1234"+user.getLoginName()));
        }
        BeanUtils.copyProperties(saveUser, user);
        userJpaRepository.save(saveUser);
        return new ResponseBean(true, UnicomResponseEnums.SUCCESS_OPTION);
    }

    @RequestMapping(value = "/delete/{id}")
    public ResponseBean<UnicomResponseEnums> deleteUser(@PathVariable long id) {
        userJpaRepository.deleteById(id);
        return new ResponseBean(true, UnicomResponseEnums.SUCCESS_OPTION);
    }

    @RequestMapping(value = "/")
    public Page<User> getUsers(Pageable pageable, String search) {
        //根据sort对象进行排序
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pp = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<User> users = userJpaRepository.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (search != "") {
                    predicate.getExpressions().add(criteriaBuilder.like(root.get("loginName"), "%" + search.trim() + "%"));
                }
                return predicate;
            }
        }, pp);

        users.forEach(u -> {
            u.setPwd("");
        });
        return users;
    }

    @RequestMapping(value = "/{id}")
    public User getUser(@PathVariable long id) {
        User user = userJpaRepository.findById(id).get();
        user.setPwd("");
        return user;
    }

    public String getCurrentProfile() {
        EditUserDto sessionUser = (EditUserDto) SecurityUtils.getSubject().getPrincipal();
        List<User> currentUser = userJpaRepository.findByLoginName(sessionUser.getLoginName());
        User ccUser = currentUser.get(0);
        return ccUser.getPicture();
    }

    @RequestMapping(value = "/getCurrentUser")
    public User getCurrentUser() {
        EditUserDto sessionUser = (EditUserDto) SecurityUtils.getSubject().getPrincipal();
        List<User> currentUser = userJpaRepository.findByLoginName(sessionUser.getLoginName());
        User ccUser = currentUser.get(0);
        ccUser.setPwd("");
        return ccUser;
    }

    @RequestMapping(value = "/saveCurrentUser")
    public ResponseBean<UnicomResponseEnums> saveCurrentUser(@RequestBody SaveUserDto user) {
        EditUserDto sessionUser = (EditUserDto) SecurityUtils.getSubject().getPrincipal();
        List<User> currentUser = userJpaRepository.findByLoginName(sessionUser.getLoginName());
        User ccUser = currentUser.size() > 0 ? currentUser.get(0) : null;
        if (ccUser == null) {
            return ResponseBean.error();
        }
        ccUser.setSex(user.getSex());
        ccUser.setNickName(user.getNickName());
        if (user.getPicture() != null) {
            ccUser.setPicture(user.getPicture());
        }
        userJpaRepository.save(ccUser);
        return ResponseBean.success();
    }

    @RequestMapping(value = "/ResetPwd")
    public ResponseBean<UnicomResponseEnums> ResetPwd(@RequestBody ResetPwdUserDto resetPwdUserDto) {
        EditUserDto sessionUser = (EditUserDto) SecurityUtils.getSubject().getPrincipal();
        List<User> currentUser = userJpaRepository.findByLoginName(sessionUser.getLoginName());
        User ccUser = currentUser.size() > 0 ? currentUser.get(0) : null;
        if (ccUser == null) {
            return ResponseBean.error();
        }
        if (!resetPwdUserDto.getPwd().equals(resetPwdUserDto.getPwdrepeat())) {
            return ResponseBean.error("二次密码不一致!");
        }
        String oldPwdDES = MyDES.encryptBasedDes(resetPwdUserDto.getPwdold() + ccUser.getLoginName());
        if (ccUser.getPwd() == null || !ccUser.getPwd().equals(oldPwdDES)) {
            return ResponseBean.error("旧密码不正确!");
        }
        String pawDES = MyDES.encryptBasedDes(resetPwdUserDto.getPwd() + ccUser.getLoginName());
        ccUser.setPwd(pawDES);
        userJpaRepository.save(ccUser);
        return ResponseBean.success();
    }

    @RequestMapping(value = "/ResetUserPwd")
    public ResponseBean<UnicomResponseEnums> ResetUserPwd(@RequestBody User resetPwdUserDto) {
        List<User> currentUser = userJpaRepository.findByLoginName(resetPwdUserDto.getLoginName());
        User ccUser = currentUser.size() > 0 ? currentUser.get(0) : null;
        if (ccUser == null) {
            return ResponseBean.error();
        }
        String pawDES = MyDES.encryptBasedDes(resetPwdUserDto.getPwd() + ccUser.getLoginName());
        ccUser.setPwd(pawDES);
        userJpaRepository.save(ccUser);
        return ResponseBean.success();
    }

    @RequestMapping(value = "/validatePwd")
    public ResponseBean<UnicomResponseEnums> validatePwd(@RequestBody String pwd) {
        EditUserDto sessionUser = (EditUserDto) SecurityUtils.getSubject().getPrincipal();
        List<User> currentUser = userJpaRepository.findByLoginName(sessionUser.getLoginName());
        User ccUser = currentUser.get(0);

        String encryptPwd = MyDES.encryptBasedDes(pwd + ccUser.getLoginName());
        if (!encryptPwd.equals(ccUser.getPwd())) {
            return ResponseBean.error("密码不正确!");
        }
        return ResponseBean.success();
    }
}