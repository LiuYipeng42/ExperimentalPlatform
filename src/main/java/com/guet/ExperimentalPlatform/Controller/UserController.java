package com.guet.ExperimentalPlatform.Controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guet.ExperimentalPlatform.Entity.User;
import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.Service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


@CrossOrigin
@RestController
@RequestMapping("/student")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/uploadStudents")
    public String uploadStudents(HttpServletRequest request, @RequestParam("classId") String classId)
            throws IllegalStateException, IOException {

        // 将当前上下文初始化给  CommonsMultipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        // 检查 form中是否有 Content-Type = "multipart/form-data"
        if (multipartResolver.isMultipart(request)) {
            // 将 request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            // 获取 multiRequest 中所有的文件名
            Iterator<String> iter = multiRequest.getFileNames();

            while (iter.hasNext()) {
                //一次遍历所有文件
                MultipartFile file = multiRequest.getFile(iter.next());
                if (file != null) {

                    String path = "/root/filetransmission/springboot/UploadFiles/" + file.getOriginalFilename();
                    //上传
                    File uploadFile = new File(path);
                    file.transferTo(uploadFile);
                    System.out.println(path);
                    System.out.println(classId);
                    if (uploadFile.exists()) {
                        String result = RunCMD.execute("python3 UploadFiles/importStudents.py " + path + " " + classId);
                        System.out.println(result);
                    }

                }
            }
        }

        return "success";
    }

    @GetMapping("select")
    public JSONObject[] getStudent(@RequestParam("type") String type, @RequestParam("condition") String condition) {

        List<User> users = userService.list(new QueryWrapper<User>().eq(type, condition));

        return userService.getStudentsInfo(users);
    }

    @GetMapping("delete")
    public void deleteStudent(@RequestParam("type") String type, @RequestParam("condition") String condition) {
        userService.deleteStudents(type, condition);
    }

    @PostMapping("update")
    public String updateStudent(@RequestBody User user) {
        try {
            userService.update(
                    new UpdateWrapper<User>()
                            .set("account", user.getAccount())
                            .set("password", user.getPassword())
                            .set("name", user.getName())
                            .set("class_id", user.getClassId())
                            .eq("account", user.getAccount())
            );
            return "success";
        } catch (DuplicateKeyException e) {
            return "用户账号重复";
        }

    }

    @PostMapping("insert")
    public String insertStudent(@RequestBody User user) {
        try {
            userService.save(user);
            return "success";
        } catch (DuplicateKeyException e) {
            return "用户账号重复";
        }
    }

    @GetMapping("listStudents")
    public JSONObject listStudentsPage(@RequestParam("page") int page) {

        int count = userService.count();
        int pageNum;

        if (count % 10 != 0) {
            pageNum = count / 10 + 1;
        } else {
            pageNum = count / 10;
        }

        List<User> users = userService.page(new Page<>(page + 1, 10)).getRecords();

        users = users.stream().filter(
                x -> !x.getName().equals("teacher")
        ).collect(Collectors.toList());

        JSONObject result = new JSONObject();
        result.put("pageNum", pageNum);
        result.put("students", userService.getStudentsInfo(users));

        return result;
    }

}
