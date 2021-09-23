package com.guet.ExperimentalPlatform.Controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.guet.ExperimentalPlatform.Entity.Class;
import com.guet.ExperimentalPlatform.Entity.User;
import com.guet.ExperimentalPlatform.Service.ClassService;
import com.guet.ExperimentalPlatform.Service.UserService;
import com.guet.ExperimentalPlatform.Utils.AES;
import com.guet.ExperimentalPlatform.pojo.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
public class PasswordController {

    private final UserService userService;

    private final ClassService classService;

    private final long otherStudentsClass;

    @Autowired
    public PasswordController(UserService userService, ClassService classService) {
        this.userService = userService;
        this.classService = classService;
        this.otherStudentsClass = classService.getOne(new QueryWrapper<Class>().eq("class_num", "其他学生")).getId();
    }

    @PostMapping("/register")
    public String register(@RequestBody LoginForm registerForm) throws Exception {

        String secretKey = registerForm.account + registerForm.account.substring(4);

        registerForm.password = AES.Decrypt(registerForm.password, secretKey);

        String status;

        try {

            User user = new User().setAccount(registerForm.account)
                    .setPassword(registerForm.password)
                    .setName(registerForm.name)
                    .setIdentity("other_student");

            userService.save(user);

            classService.addClassStudent(user.getId(), otherStudentsClass);

            status = "success";
        } catch (DuplicateKeyException e){
            e.printStackTrace();
            status = "此账号已存在";
        }

        JSONObject result = new JSONObject();

        result.put("result", status);
        result.put("timeStamp", (System.currentTimeMillis()));

        return AES.Encrypt(result.toString(), secretKey);
    }

    @PostMapping("/changePasswd")
    public String changePassword(@RequestBody LoginForm newPasswd) throws Exception {

        String secretKey = newPasswd.account + newPasswd.account.substring(4);

        newPasswd.password = AES.Decrypt(newPasswd.password, secretKey);

        String status;

        try {
            userService.update(
                    new UpdateWrapper<User>().set("password", newPasswd.password).eq("account", newPasswd.account)
            );
            status = "success";
        }catch (Exception ignored){
            status = "修改失败";
        }

        JSONObject result = new JSONObject();

        result.put("result", status);
        result.put("timeStamp", (System.currentTimeMillis()));

        return AES.Encrypt(result.toString(), secretKey);

    }

}
