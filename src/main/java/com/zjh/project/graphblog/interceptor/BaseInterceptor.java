package com.zjh.project.graphblog.interceptor;

import com.zjh.project.graphblog.modal.Vo.VisitLog;
import com.zjh.project.graphblog.modal.Vo.UserVo;

import com.zjh.project.graphblog.service.ILogService;
import com.zjh.project.graphblog.service.IUserService;
import com.zjh.project.graphblog.utils.*;
import com.zjh.project.graphblog.constant.Const;
import com.zjh.project.graphblog.dto.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 * Created by BlueT on 2017/3/9.
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {
    private static final Logger LOGGE = LoggerFactory.getLogger(BaseInterceptor.class);
    private static final String USER_AGENT = "user-agent";

    @Resource
    private IUserService userService;

    private MapCache cache = MapCache.single();

    @Resource
    private Commons commons;

    @Resource
    private AdminCommons adminCommons;

    @Resource
    private ILogService logService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String uri = request.getRequestURI();

        LOGGE.info("UserAgent: {}", request.getHeader(USER_AGENT));
        LOGGE.info("用户访问地址: {}, 来路地址: {}", uri, IPKit.getIpAddrByRequest(request));


        VisitLog visitLog = new VisitLog();
        visitLog.setIp(IPKit.getIpAddrByRequest(request));
        visitLog.setUrl(uri);
        visitLog.setUseragent(request.getHeader(USER_AGENT).substring(0,10));

        UserVo vo = TaleUtils.getLoginUser(request);
        Integer tempuid=0;
        if (vo!=null){
            visitLog.setUsername(vo.getUsername());
            visitLog.setUid(vo.getUid());
            tempuid=vo.getUid();
        }



        logService.insertLog("visit", GsonUtils.toJsonString(visitLog), IPKit.getIpAddrByRequest(request), tempuid);


        //请求拦截处理
        UserVo user = TaleUtils.getLoginUser(request);
        if (null == user) {
            Integer uid = TaleUtils.getCookieUid(request);
            if (null != uid) {
                //这里还是有安全隐患,cookie是可以伪造的
                user = userService.queryUserById(uid);
                request.getSession().setAttribute(Const.LOGIN_SESSION_KEY, user);
            }
        }
        if (uri.startsWith("/admin") && !uri.startsWith("/admin/login") && null == user) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }
        //设置get请求的token
        if (request.getMethod().equals("GET")) {
            String csrf_token = UUID.UU64();
            // 默认存储30分钟
            cache.hset(Types.CSRF_TOKEN.getType(), csrf_token, uri, 30 * 60);
            request.setAttribute("_csrf_token", csrf_token);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        httpServletRequest.setAttribute("commons", commons);//一些工具类和公共方法
        httpServletRequest.setAttribute("adminCommons", adminCommons);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
