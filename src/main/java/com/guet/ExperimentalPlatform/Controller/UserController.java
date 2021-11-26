package com.guet.ExperimentalPlatform.Controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Entity.Class;
import com.guet.ExperimentalPlatform.Entity.User;
import com.guet.ExperimentalPlatform.Service.ClassService;
import com.guet.ExperimentalPlatform.Utils.FileOperation;
import com.guet.ExperimentalPlatform.Utils.RunCMD;
import com.guet.ExperimentalPlatform.Service.UserService;
import com.guet.ExperimentalPlatform.pojo.ClassPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin
@RestController
@RequestMapping("/student")
public class UserController {

    private final UserService userService;
    private final ClassService classService;

    @Autowired
    public UserController(UserService userService, ClassService classService) {
        this.userService = userService;
        this.classService = classService;
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

                    String path = "/root/ExperimentalPlatform/springboot/UploadFiles/" + file.getOriginalFilename();
                    //上传
                    File uploadFile = new File(path);
                    file.transferTo(uploadFile);
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

    @GetMapping("/template")
    public ResponseEntity<ByteArrayResource> downLoadTemplate() throws IOException {
        return FileOperation.sentToUser("UploadFiles", "Template.xlsx");
    }

    @GetMapping("/select")
    public JSONObject[] getStudent(@RequestParam("type") String type, @RequestParam("condition") String condition) {
        List<User> users;

        if (type.equals("class_id")){
            users = userService.selectUserByClassNum(condition);
        }else {
            users = userService.list(new QueryWrapper<User>().eq(type, condition));
        }

        return userService.getStudentsScore(users);
    }

    @PostMapping("/delete")
    public void deleteStudent(@RequestBody User user) {

        long userId = userService.getOne(new QueryWrapper<User>().select("id").eq("account", user.getAccount())).getId();

        userService.deleteStudents("account", user.getAccount());
        classService.removeClassStudent(userId);

    }

    @PostMapping("/update")
    public String updateStudent(@RequestBody User user) {

        try {
            userService.update(
                    new UpdateWrapper<User>()
                            .set("account", user.getAccount())
                            .set("password", user.getPassword())
                            .set("name", user.getName())
                            .eq("account", user.getAccount())
            );
            return "success";
        } catch (DuplicateKeyException e) {
            return "用户账号重复";
        }
    }

    @PostMapping("/insert")
    public String insertStudent(@RequestBody HashMap<String, String> params) {

        User user = new User().setAccount(params.get("account")).setPassword(params.get("password")).setName(params.get("name"));

        System.out.println(user);

        try {
            userService.save(user);
        } catch (DuplicateKeyException e) {
            user = userService.getOne(
                    new QueryWrapper<User>().select("id", "account").eq("account", user.getAccount())
            );
        }

        Class newClass = new Class().setClassNum(params.get("classId"));

        try {
            classService.save(newClass);
        } catch (DuplicateKeyException e) {
            newClass = classService.getOne(
                    new QueryWrapper<Class>().select("id", "class_num").eq("class_num", newClass.getClassNum())
            );
        }

        try {
            classService.addClassStudent(user.getId(), newClass.getId());
        } catch (DuplicateKeyException ignored) {
        }

        return "success";
    }

    @GetMapping("/listStudents")
    public JSONObject listStudentsPage(@RequestParam("classId") String classNum, @RequestParam("page") int page) {

        int count = userService.countStudentsByClassNum(classNum);

        int pageNum;

        if (count % 10 != 0) {
            pageNum = count / 10 + 1;
        } else {
            pageNum = count / 10;
        }

//        List<User> users = userService.selectClassPage(new ClassPage<User>(page + 1, 10).setClassNum(classNum)).getRecords();

        List<User> users = userService.list();

        users = users.stream().filter(
                x -> !x.getName().equals("teacher")
        ).collect(Collectors.toList());

        List<JSONObject> students = Arrays.stream(userService.getStudentsScore(users)).sorted(
                (user1, user2) -> {
                    double score1 = (double) user1.get("score");
                    double score2 = (double) user2.get("score");
                    if (score1 - score2 > 0){
                        return -1;
                    }else if (score1 - score2 < 0){
                        return 1;
                    }else {
                        return 0;
                    }
                }
        ).collect(Collectors.toList()).subList(page * 10, page * 10 + 10);

        JSONObject result = new JSONObject();
        result.put("pageNum", pageNum);
        result.put("students", students);

        return result;
    }

    @GetMapping("/classes")
    public List<String> getClasses(HttpServletRequest request) {
        long teacherId = (long) request.getSession().getAttribute("userId");
        return classService.list(new QueryWrapper<Class>().eq("teacher_id", teacherId))
                .stream().map(Class::getClassNum).collect(Collectors.toList());
    }

}
