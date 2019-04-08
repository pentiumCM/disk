/*负责用户的登录注册控制器*/
package com.cloud.disk.controller;

import com.cloud.disk.core.ResponseBean;
import com.cloud.disk.core.UnicomResponseEnums;
import com.cloud.disk.domain.User;
import com.cloud.disk.repository.UserJpaRepository;
import com.cloud.disk.units.MyDES;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
/*@RestController注解，相当于@Controller+@ResponseBody两个注解的结合，返回json数据不需要在方法前面加@ResponseBody注解了，
但使用@RestController这个注解，就不能返回jsp,html页面，视图解析器无法解析jsp,html页面*/
@RequestMapping(value = "/account")
public class AccountController {

    @Autowired
    private UserJpaRepository userJpaRepository;

    //登录
    @GetMapping(value="login")
    /*@GetMapping是一个组合注解，是@RequestMapping(method = RequestMethod.GET)的缩写。*/
    public ModelAndView login() {
        return new ModelAndView("account/login");
    }

    /**
     * ajax登录请求
     * @param user
     * @return
     */
    @RequestMapping(value="login.do",method= RequestMethod.POST)
    public ResponseBean<UnicomResponseEnums> Login(@RequestBody User user ,HttpSession session) {
        try{
            UsernamePasswordToken token = new UsernamePasswordToken(user.getLoginName(),user.getPwd(),true);
            Subject subject = SecurityUtils.getSubject();
            System.out.println("获取到信息，开始验证！！");
            subject.login(token);//登陆成功的话，放到session中
/*            User sessionUser = (User) subject.getPrincipal();
            session.setAttribute("user", sessionUser);
            session.setAttribute("currentUserId", sessionUser.getId());*/
            return new ResponseBean(true, UnicomResponseEnums.SUCCESS_OPTION);
        }catch (Exception ex){
            System.out.println("失败"+ex.getMessage());
            return new ResponseBean(false, "-1", ex.getMessage());
        }
    }


    /**
     * 退出
     * @return
     */
    @RequestMapping(value="logout",method =RequestMethod.GET)
    @ResponseBody
    public ModelAndView logout(){
        try {
            //退出
            SecurityUtils.getSubject().logout();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return new ModelAndView("redirect:login");
    }

    /**
     * 用户注册功能
     * @param user
     * @return
     */
    @RequestMapping(value="/register",method = RequestMethod.POST)
    public ResponseBean<UnicomResponseEnums>Register(@RequestBody User user)
    {
        List<User> users = userJpaRepository.findByLoginName(user.getLoginName());
        if(users!=null&&users.size()>0){
            return new ResponseBean(false,UnicomResponseEnums.REPEAT_LOGINNAME);
        }

        user.setPwd(MyDES.encryptBasedDes(user.getPwd()+user.getLoginName()));
        user.setRole(User.Role_User);
        user.setStatus(1);
        userJpaRepository.save(user);

        return new ResponseBean(true, UnicomResponseEnums.SUCCESS_OPTION);
    }
}
