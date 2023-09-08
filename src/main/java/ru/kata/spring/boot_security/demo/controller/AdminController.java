package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;

    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping()
    public String getAdminPage (ModelMap model, Principal principal) {
        model.addAttribute("userList", userService.getAll());
        model.addAttribute("user", userService.getByUsername(principal.getName()));
        model.addAttribute("roles", userService.getByUsername(principal.getName()).getRoles());
        model.addAttribute("all_roles", roleService.getAll());
        model.addAttribute("new_user", new User());
        return "admin";
    }


    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") User user) {
        if(userService.getByUsername(user.getName()) == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.save(user);
        } else {
            return "redirect:/error";
        }
        return "redirect:/admin";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @PatchMapping("/update/{id}")
    public String updateUser (@ModelAttribute User user) {
        if (user.getPassword().isEmpty()) {
            user.setPassword(userService.getById(user.getId()).getPassword());
            userService.update(user);
            return "redirect:/admin";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.update(user);
        return "redirect:/admin";
    }

}
