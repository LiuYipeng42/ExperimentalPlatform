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
public class UserManageController {

    private final UserService userService;
    private final ClassService classService;

    @Autowired
    public UserManageController(UserService userService, ClassService classService) {
        this.userService = userService;
        this.classService = classService;
    }

    @PostMapping("/uploadStudents")
    public String uploadStudents(HttpServletRequest request, @RequestParam("classId") String classId)
            throws IllegalStateException, IOException {

        long teacherId = (long) request.getSession().getAttribute("userId");

        String identity = userService.getOne(
                new QueryWrapper<User>().eq("id", teacherId)
        ).getIdentity();

        // 将当前上下文初始化给  CommonsMultipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        // 检查 form中是否有 Content-Type = "multipart/form-data"
        if (multipartResolver.isMultipart(request)) {
            // 将 request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            // 获取 multiRequest 中所有的文件名
            Iterator<String> iter = multiRequest.getFileNames();

            while (iter.hasNext()) {
                // 一次遍历所有文件
                MultipartFile file = multiRequest.getFile(iter.next());
                if (file != null) {

                    String path = "/root/ExperimentalPlatform/springboot/UploadFiles/students/" + file.getOriginalFilename();
                    //上传
                    File uploadFile = new File(path);
                    file.transferTo(uploadFile);
                    System.out.println(classId);
                    if (uploadFile.exists()) {
                        String result = RunCMD.execute(
                                "python3 UploadFiles/importStudents.py "
                                        + path + " " + classId + " " + teacherId + " " + identity
                        );
                        System.out.println(result);
                    }

                }
            }
        }

        return "success";
    }

    @GetMapping("/template")
    public ResponseEntity<ByteArrayResource> downLoadTemplate(HttpServletRequest request) throws IOException {
        String identity = (String) request.getSession().getAttribute("identity");

        if (identity.equals("teacher")) {
            return FileOperation.sentToUser("UploadFiles", "teacher_template.xlsx");
        }
        if (identity.equals("admin")) {
            return FileOperation.sentToUser("UploadFiles", "admin_template.xlsx");
        }

        return null;
    }

    @GetMapping("/select")
    public JSONObject[] getStudent(HttpServletRequest request, @RequestParam("type") String type, @RequestParam("condition") String condition) {

        long teacherId = (long) request.getSession().getAttribute("userId");

        String identity = (String) request.getSession().getAttribute("identity");

        List<Long> trueTeacher = null;

        List<User> users;

        if (identity.equals("teacher")) {

            if (type.equals("class_id")) {
                trueTeacher = classService.selectTeacherIdByClassNum(condition);
            }

            if (type.equals("account")) {
                trueTeacher = userService.selectTeacherIdByAccount(condition);
            }

            assert trueTeacher != null;
            if (trueTeacher.contains(teacherId)) {
                if (type.equals("class_id")) {
                    users = userService.selectUserByClassNum(condition);
                } else {
                    users = userService.list(new QueryWrapper<User>().eq(type, condition));
                }
                return userService.getStudentsScore(users);
            }
        }

        if (identity.equals("admin")) {
            if (type.equals("class_id")) {
                users = userService.selectUserByClassNum(condition);
            } else {
                users = userService.list(new QueryWrapper<User>().eq(type, condition));
            }
            return userService.getStudentsScore(users);
        }

        return null;
    }

    @PostMapping("/delete")
    public void deleteStudent(HttpServletRequest request, @RequestBody User user) {

        long teacherId = (long) request.getSession().getAttribute("userId");

        String identity = (String) request.getSession().getAttribute("identity");


        long studentId = userService.getOne(
                new QueryWrapper<User>()
                        .select("id").eq("account", user.getAccount())
        ).getId();

        if (identity.equals("teacher")) {
            List<Long> trueTeacher = userService.selectTeacherIdByAccount(user.getAccount());

            if (trueTeacher.contains(teacherId)) {
                userService.deleteStudents(studentId);
            }
        }

        if (identity.equals("admin")) {
            userService.deleteStudents(studentId);
        }

    }

    @PostMapping("/update")
    public String updateStudent(HttpServletRequest request, @RequestBody User user) {

        long teacherId = (long) request.getSession().getAttribute("userId");

        String identity = (String) request.getSession().getAttribute("identity");

        List<Long> trueTeacher;

        if (identity.equals("teacher")) {

            trueTeacher = userService.selectTeacherIdById(user.getId());

            if (trueTeacher.contains(teacherId)) {
                try {
                    userService.update(
                            new UpdateWrapper<User>()
                                    .set("account", user.getAccount())
                                    .set("password", user.getPassword())
                                    .set("name", user.getName())
                                    .eq("id", user.getId())
                    );
                    return "success";
                } catch (DuplicateKeyException e) {
                    return "用户账号重复";
                }
            }
        }

        if (identity.equals("admin")) {
            try {
                userService.update(
                        new UpdateWrapper<User>()
                                .set("account", user.getAccount())
                                .set("password", user.getPassword())
                                .set("name", user.getName())
                                .eq("id", user.getId())
                );
                return "success";
            } catch (DuplicateKeyException e) {
                return "用户账号重复";
            }
        }

        return "无此学生";
    }

    @PostMapping("/insert")
    public String insertStudent(HttpServletRequest request, @RequestBody HashMap<String, String> params) {

        long teacherId = (long) request.getSession().getAttribute("userId");

        String identity = (String) request.getSession().getAttribute("identity");

        User user = new User().setAccount(params.get("account"))
                .setPassword(params.get("password"))
                .setName(params.get("name"));

        Class aClass = classService.getOne(
                new QueryWrapper<Class>().select("id", "class_num").eq("class_num", params.getClass())
        );
        if (identity.equals("teacher")) {

            if (aClass != null) {
                if (aClass.getTeacherId() == teacherId) {
                    try {
                        userService.save(user);
                    } catch (DuplicateKeyException e) {
                        user = userService.getOne(
                                new QueryWrapper<User>().select("id", "account").eq("account", user.getAccount())
                        );
                    }
                    try {
                        classService.addClassStudent(user.getId(), aClass.getId());
                    } catch (DuplicateKeyException ignored) {
                    }
                } else {
                    return "无此班级";
                }
            } else {
                return "无此班级";
            }
        }

        if (identity.equals("admin")) {
            if (aClass != null) {
                try {
                    userService.save(user);
                } catch (DuplicateKeyException e) {
                    user = userService.getOne(
                            new QueryWrapper<User>().select("id", "account").eq("account", user.getAccount())
                    );
                }
                try {
                    classService.addClassStudent(user.getId(), aClass.getId());
                } catch (DuplicateKeyException ignored) {
                }

            } else {
                return "无此班级";
            }
        }

        return "success";
    }

    @GetMapping("/listStudents")
    public JSONObject listStudentsPage(HttpServletRequest request, @RequestParam("classId") String classNum, @RequestParam("page") int page) {

        long teacherId = (long) request.getSession().getAttribute("userId");

        String identity = (String) request.getSession().getAttribute("identity");

        Class aClass = classService.getOne(
                new QueryWrapper<Class>().eq("class_num", classNum)
        );

        if (aClass.getTeacherId() == teacherId || identity.equals("admin")) {

            int count = userService.countStudentsByClassNum(classNum);

            int pageNum;

            if (count % 10 != 0) {
                pageNum = count / 10 + 1;
            } else {
                pageNum = count / 10;
            }

            pageNum += 1;

            List<User> users = userService.selectUserByClassNum(classNum);

            users = users.stream().filter(
                    x -> x.getIdentity().equals("guet_student")
            ).collect(Collectors.toList());

            List<JSONObject> students = Arrays.stream(
                    userService.getStudentsScore(users)).sorted(
                    (user1, user2) -> {
                        double score1 = (double) user1.get("score");
                        double score2 = (double) user2.get("score");
                        if (score1 - score2 > 0) {
                            return -1;
                        } else if (score1 - score2 < 0) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
            ).collect(Collectors.toList());

            if (students.size() > 10) {
                students = students.subList(page * 10, page * 10 + 10);
            }

            JSONObject result = new JSONObject();
            result.put("pageNum", pageNum);
            result.put("students", students);

            return result;
        }
        return null;
    }

    @GetMapping("/class/delete")
    public String deleteClass(@RequestParam("classNum") String classNum){

        return "success";
    }

    @GetMapping("/class/changeNum")
    public String changeClassNum(@RequestParam("classNum") String classNum){

        return "success";
    }

    @GetMapping("/classes")
    public List<String> getClasses(HttpServletRequest request) {
        long teacherId = (long) request.getSession().getAttribute("userId");

        String identity = (String) request.getSession().getAttribute("identity");

        if (identity.equals("teacher")) {
            return classService.list(new QueryWrapper<Class>().eq("teacher_id", teacherId))
                    .stream().map(Class::getClassNum).collect(Collectors.toList());
        }

        if (identity.equals("admin")){
            return classService.list().stream().map(Class::getClassNum).collect(Collectors.toList());
        }

        return null;
    }

}
