package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.model.Account;
import com.example.demo.repository.UserRepository;

@Controller
public class UserController {

	private final UserRepository userRepository;
	private final Account account;

	public UserController(UserRepository userRepository, Account account) {
		this.userRepository = userRepository;
		this.account = account;
	}

	// ログイン画面を表示
	@GetMapping({ "/", "/login", "/logout" })
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

	// ログインする
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

		//セッション管理されたアカウント情報に名前をセット
		account.setName(user.getName());

		return "redirect:/recipes";
	}
}