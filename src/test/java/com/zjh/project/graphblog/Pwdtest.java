package com.zjh.project.graphblog;

import com.zjh.project.graphblog.modal.Vo.UserVo;
import com.zjh.project.graphblog.utils.TaleUtils;

/**
 * Created by shuaihan on 2017/4/2.
 */
public class Pwdtest {
    public static void main(String args[]){
        UserVo user = new UserVo();
        user.setUsername("admin");
        user.setPassword("asdfasdfs");
        String encodePwd = TaleUtils.MD5encode(user.getUsername() + user.getPassword());
        System.out.println(encodePwd);
    }
}
