package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Controller
public class UserController {

	private final UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// ログイン画面を表示
	@GetMapping({ "/", "/login" })
	public String index() {
		return "login";
	}

	// 新規登録画面を表示
	@GetMapping("/users/new")
	public String create() {
		return "accountForm";
	}

	// 新規登録処理
	@PostMapping("/users/add")
	public String add(
			@RequestParam String name,
			@RequestParam String email,
			@RequestParam String password,
			@RequestParam String passwordConfirm,
			Model model) {

		if (!password.equals(passwordConfirm)) {
			model.addAttribute("error", "パスワードと確認用パスワードが一致しません");
			model.addAttribute("name", name);
			model.addAttribute("email", email);
			return "accountForm";
		}

		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setPassword(password);

		userRepository.save(user);

		return "redirect:/login";
	}

	// ログイン処理
	@PostMapping("/login")
	public String login(
			@RequestParam String email,
			@RequestParam String password,
			Model model) {

		User user = userRepository.findByEmailAndPassword(email, password);

		if (user == null) {
			model.addAttribute("error", "メールアドレスまたはパスワードが正しくありません");
			return "login";
		}

		return "redirect:/recipes";
	}
}