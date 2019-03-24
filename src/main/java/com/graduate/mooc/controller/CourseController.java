package com.graduate.mooc.controller;

import com.alibaba.fastjson.JSONObject;
import com.graduate.mooc.domain.*;
import com.graduate.mooc.entity.Dict;
import com.graduate.mooc.entity.Hiddendanger;
import com.graduate.mooc.mapper.*;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

    @Autowired
    MatchMap matMap;

    @Autowired
    ChapterMap chMap;

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

/*
progress点击章节链接过来的
 */
    @GetMapping("/study/{chid}")  //习题关联 准备观看视频
    public String study(@PathVariable String chid,HttpSession session){
        String sno = (String)session.getAttribute("suser");
        String taskno = (String)session.getAttribute("myTask");
        System.out.println(sno+" "+taskno);

        Chapter chapter = chMap.findChapterByID(chid);
        session.setAttribute("myChapter",chid);
        //session.setAttribute("myTask",null);

        //匹配随机题目
  //if match  表中当前task里面该名学生没有和该章节的关联则  insert match
        List<Match> mlist = matMap.findMatchByInfo(sno,taskno);
        if(mlist==null) {
            List<Subject> subList = subMap.findSubjectByChid(chid);
            System.out.println(subList);
            Set<Integer> set = new HashSet<Integer>();
            if (subList.size() < 5)
                while (set.size() < 3)
                    set.add(new Random().nextInt(subList.size()) + 1);
            else
                while (set.size() < 5)
                    set.add(new Random().nextInt(subList.size()) + 1);
            List<Subject> subRes = new ArrayList<>();
            Iterator<Integer> iterator = set.iterator();
            for (int i = 0; i < set.size() && iterator.hasNext(); i++) {
                subRes.add(subList.get(iterator.next()));
            }
            System.out.println(subRes);

            Match mat = new Match();
            mat.setSno(sno);
            mat.setState(-1);
            //mat.setTaskno(taskno); //task
            matMap.insertMatch(mat);
        }
        return "Chapters";
    }


    @GetMapping("/getVideo")
    @ResponseBody
    public String getVideo(@RequestParam("myCh")String myCh, HttpSession session){
        //Chapter ch=(Chapter) session.getAttribute("myChapter");

        System.out.println("video ch "+myCh);
        Chapter ch = chMap.findChapterByID(myCh);
        System.out.println(ch);
        String vPath = ch.getVideo();
        System.out.println(vPath);
        return vPath;
    }


    @PostMapping("/play")   //
    @ResponseBody
    public String play(@RequestParam("myCh")String mych){
        System.out.println(mych);
        int click=chMap.findChapterByID(mych).getClick()+1;
        chMap.click(mych,click);
        System.out.println(chMap.findChapterByID(mych));
        return "success";
    }


//小程序测试
@RequestMapping(value = "/hidden/uploadPic", method = {RequestMethod.POST, RequestMethod.GET})
@ResponseBody  //@RequestParam(value = "file", required = false) MultipartFile[] multipartFile
public void uploadPicture(Hiddendanger hiddendanger,HttpServletRequest request) throws IOException {
    System.out.println("start：");
    System.out.println("request：" + request);
    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
    if(isMultipart){
        MultipartHttpServletRequest mulReq= WebUtils.getNativeRequest(request,MultipartHttpServletRequest.class);
        List<MultipartFile> multipartFile = mulReq.getFiles("file");
        System.out.println("multipartFile：" + multipartFile);
        for (int i = 0; i < multipartFile.size(); i++){
            String originalFirstName = multipartFile.get(i).getOriginalFilename();
            String picFirstName = originalFirstName.substring(0, originalFirstName.indexOf("."));
            //realPath填写电脑文件夹路径
            String realPath = "C:\\WeChatPic";
            //取得图片的格式后缀
            String originalLastName = multipartFile.get(i).getOriginalFilename();
            String picLastName = originalLastName.substring(originalLastName.lastIndexOf("."));
            //格式化时间戳
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String nowTime = sdf.format(new Date().getTime());
            //拼接：名字+时间戳+后缀
            String picName = picFirstName + "." + nowTime + picLastName;
            System.out.println("picName：" + picName);
        }
    }
    //对应前端的upload的name参数"file"
   // List<MultipartFile> multipartFile = req.getFiles("file");



    ;
    //裁剪用户id

    System.out.println(hiddendanger);
}

    @GetMapping("/getEquipById")
    @ResponseBody
    public List<Map<String,Object>> getEquipById(@RequestParam(name = "ID") String ID){
        List<Map<String,Object>> dic = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("ID","1");
        map.put("EQUIP_NUMBER","num1");
        map.put("EQUIP_TYPE","t1");
        dic.add(map);
        Map<String,Object> map2 = new HashMap<>();
        map2.put("ID","11");
        map2.put("EQUIP_NUMBER","num11");
        map2.put("EQUIP_TYPE","t11");
        dic.add(map2);
        return dic;
    }

    @RequestMapping("/getDict")
    @ResponseBody
    public List<Dict> getDict(@RequestParam(name = "TNAME") String TNAME){
        try {
            List<Dict> dic = new ArrayList<>();
            dic.add(new Dict("value1","type1"));
            dic.add(new Dict("value2","type2"));
            dic.add(new Dict("value3","type3"));
            return dic;
        } catch (Exception e) {
            return null;
        }
    }
}
