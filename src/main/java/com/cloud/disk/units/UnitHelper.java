package com.cloud.disk.units;

import com.cloud.disk.dtos.EditUserDto;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class UnitHelper {
    public static ModelAndView GetCurrentUserAndView()
    {
        EditUserDto user=(EditUserDto)SecurityUtils.getSubject().getPrincipal();
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("currentUserId",user.getLoginName());
        return  modelAndView;
    }
    /**
     * 得到几天前的时间
     * @param d
     * @param day
     * @return
     */

    public static Date getDateBefore(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)+day);
        return now.getTime();
    }

    public static  String uuid()
    {
        String str="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String uuid=new String();
        for(int i=0;i<4;i++)
        {
            char ch=str.charAt(new Random().nextInt(str.length()));
            uuid+=ch;
        }
        return uuid;
    }
}
