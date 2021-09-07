package com.guet.ExperimentalPlatform.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.entity.Student;
import com.guet.ExperimentalPlatform.service.UserService;
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
                    String path = "/home/lyp/Codes/JavaCodes/ExperimentalPlatform/UploadFiles/" + file.getOriginalFilename();
                    //上传
                    File uploadFile = new File(path);
                    file.transferTo(uploadFile);

                    if (uploadFile.exists()) {
                        RunCMD.execute("python3 UploadFiles/importStudents.py " + path + " " + classId);
                    }

                }
            }
        }

        return "success";
    }

    @GetMapping("select")
    public JSONObject[] getStudent(@RequestParam("type") String type, @RequestParam("condition") String condition) {

        List<Student> students = userService.list(new QueryWrapper<Student>().eq(type, condition));

        return userService.getStudentsInfo(students);
    }

    @GetMapping("delete")
    public void deleteStudent(@RequestParam("type") String type, @RequestParam("condition") String condition) {
        userService.deleteStudents(type, condition);
    }

    @PostMapping("update")
    public String updateStudent(@RequestBody Student student) {
        try {
            userService.update(
                    new UpdateWrapper<Student>()
                            .set("account", student.getAccount())
                            .set("password", student.getPassword())
                            .set("name", student.getName())
                            .set("class_id", student.getClassId())
                            .eq("account", student.getAccount())
            );
            return "success";
        } catch (DuplicateKeyException e) {
            return "用户账号重复";
        }

    }

    @PostMapping("insert")
    public String insertStudent(@RequestBody Student student) {
        try {
            userService.save(student);
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

        List<Student> students = userService.page(new Page<>(page + 1, 10)).getRecords();

        JSONObject result = new JSONObject();
        result.put("pageNum", pageNum);
        result.put("students", userService.getStudentsInfo(students));

        return result;
    }

}
