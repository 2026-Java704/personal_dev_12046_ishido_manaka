package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

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

		// エラーをチェック
		List<String> errorList = new ArrayList<>();

		if (name == null || name.length() == 0) {
			errorList.add("名前は必須です");
		}
		if (email == null || email.length() == 0) {
			errorList.add("メールアドレスは必須です");
		} else {
			// メールアドレスの重複を確認
			List<User> userList = userRepository.findByEmail(email);

			if (userList != null && userList.size() > 0) {
				errorList.add("登録済みのメールアドレスです");
			}
		}

		if (password == null || password.length() == 0) {
			errorList.add("パスワードは必須です");
		}

		if (passwordConfirm == null || passwordConfirm.length() == 0) {
			errorList.add("確認用パスワードは必須です");
		}

		// パスワードの確認
		if (password != null && passwordConfirm != null && !password.equals(passwordConfirm)) {
			errorList.add("パスワードと確認用パスワードが一致しません");
		}

		// エラーがあるときに新規登録画面へ
		if (errorList.size() > 0) {
			model.addAttribute("errorList", errorList);
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

		// セッションにIDと名前をセットする
		account.setId(user.getId());
		account.setName(user.getName());

		return "redirect:/recipes";
	}

	// ログアウトする
	@GetMapping("/logout")
	public String logout() {

		//セッションからIDと名前のセットを消す
		account.setId(null);
		account.setName(null);

		return "redirect:/login";
	}
}