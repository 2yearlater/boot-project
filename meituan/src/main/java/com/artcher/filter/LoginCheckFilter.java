package com.artcher.filter;

import com.alibaba.fastjson.JSON;
import com.artcher.common.BaseContext;
import com.artcher.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.IOException;

/**
 * 过滤器来过滤用户请求
 *  ①获取本次请求的uri
 *  ②判断本次请求是否需要处理
 *  ③不需要处理,直接放行
 *  ④判断登陆状态,如果登陆,直接放行
 *  ⑤未登录,返回未登录结果
 */
//过滤器注解,①@WebFilter(filterName=“过滤器名称”,urlPatterns=”拦截过滤地址”)
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 进行过滤操作
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("当前线程"+Thread.currentThread().getName());

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //放行地址列表
        String [] paths= new String[] {"/backend/**",
                "/front/**",
                "/employee/login",
                "/employee/logout",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
                };

        // 获取本次请求的uri
        String requestURI = request.getRequestURI();


        // 判断本次请求是否需要处理
        boolean check = this.check(paths, requestURI);
        // 不需要处理,直接放行
        if (check){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        // 判断登陆状态,如果登陆,直接放行
        if (request.getSession().getAttribute("employee") != null){
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        if (request.getSession().getAttribute("user") != null){
            //等路成功,将userId放到BaseContext(基于ThreadLocal实现,同一个线程共享数据)中
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        log.info("拦截到请求: {}",requestURI);
        // 未登录,通过响应流返回未登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * 检查请求地址是否放行
     * @param paths
     * @param requestURI
     * @return
     */
    public boolean check(String [] paths,String requestURI){
        for (String path : paths) {
            //地址匹配判断
            if (PATH_MATCHER.match(path,requestURI)){
                return true;
            }
        }

        return false;
    }

}
