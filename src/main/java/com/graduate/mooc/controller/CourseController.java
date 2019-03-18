package com.graduate.mooc.controller;

import com.alibaba.fastjson.JSONObject;
import com.graduate.mooc.domain.Course;
import com.graduate.mooc.domain.Match;
import com.graduate.mooc.domain.Subject;
import com.graduate.mooc.domain.Task;
import com.graduate.mooc.mapper.CourseMap;
import com.graduate.mooc.mapper.SubjectMap;
import com.graduate.mooc.mapper.TaskMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Chao Wax on 2019/3/9
 */
@Controller
@RequestMapping("/course")
public class CourseController {

    @Autowired
    CourseMap cMap;

    @Autowired
    TaskMap tMap;

    @Autowired
    SubjectMap subMap;
    //跳转课程详情页面  需要
    @GetMapping("/front")
    public String front(@RequestParam("cour")String cid, HttpSession session){
        session.setAttribute("reqCid",cid);
        session.setAttribute("cRoot",cid); //方便调用teacher已有接口
        return "CoursesInfo";
    }

    //进入页面就查询该门课程(没有结束的)
    @GetMapping("/info")
    @ResponseBody
    public List<Task> query(HttpSession session){
        String cid=(String)session.getAttribute("reqCid");
        System.out.println(tMap.findTaskByCID(cid));
       /* String user = (String)session.getAttribute("suser");//这坨似乎不需要 如果前端你是java代码获取的话
        JSONObject nn = new JSONObject();
        nn.put("suser", user);
        session.setAttribute("suser", nn);*/
        return tMap.findTaskByCID(cid);
    }


    @GetMapping("/study/{chid}")
    public String study(@PathVariable String chid,HttpSession session){
        String sno = (String)session.getAttribute("suser");
        String taskno = (String)session.getAttribute("taskno");
        System.out.println(sno+" "+taskno);
        //匹配随机题目
  //if match  表中当前task里面该名学生没有和该章节的关联则
        if()
        List<Subject> subList = subMap.findSubjectByChid(chid);
        System.out.println(subList);
        Set<Integer> set = new HashSet<Integer>();
        if(subList.size()<5)
            while(set.size()<3)
                set.add(new Random().nextInt(subList.size())+1);
        else
            while(set.size()<5)
                set.add(new Random().nextInt(subList.size())+1);
        List<Subject> subRes = new ArrayList<>();
        Iterator<Integer> iterator=set.iterator();
        for(int i = 0;i<set.size()&&iterator.hasNext();i++){
            subRes.add(subList.get(iterator.next()));
        }
        System.out.println(subRes);
        return "Chapters";
    }
    /*@GetMapping("/info")
    @ResponseBody
    public Course info(@RequestParam("cid")String cid){
        return cMap.findCourseByID(cid);
    }*/


}
