package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Category;
import com.example.demo.entity.Recipe;
import com.example.demo.entity.User;
import com.example.demo.model.Account;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.repository.UserRepository;

@Controller
public class RecipeController {

	private final RecipeRepository recipeRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final Account account;

	public RecipeController(
			RecipeRepository recipeRepository,
			CategoryRepository categoryRepository,
			UserRepository userRepository,
			Account account) {
		this.recipeRepository = recipeRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.account = account;
	}

	//レシピの検索
	@GetMapping("/recipes")
	public String index(
			@RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "") Integer categoryId,
			Model model) {

		List<Recipe> recipes;

		if (categoryId != null) {
			recipes = recipeRepository.findByCategoryId(categoryId);
		} else if (name.length() != 0) {
			recipes = recipeRepository.findByNameContaining(name);
		} else {
			recipes = recipeRepository.findAll();
		}

		List<Category> categories = categoryRepository.findAll();

		model.addAttribute("recipes", recipes);
		model.addAttribute("categories", categories);
		model.addAttribute("name", name);
		model.addAttribute("account", account);

		return "home";
	}

	// レシピの閲覧画面に行く
	@GetMapping("/recipes/detail")
	public String detail(
			@RequestParam Integer id,
			Model model) {

		Recipe recipe = recipeRepository.findById(id).get();
		model.addAttribute("recipe", recipe);

		return "recipe";
	}

	// レシピの投稿画面に行く
	@GetMapping("/recipes/add")
	public String create(Model model) {

		if (account.getId() == null) {
			return "login";
		}

		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);

		return "recipeForm";
	}

	// レシピの登録する
	@PostMapping("/recipes/add")
	public String add(
			@RequestParam Integer categoryId,
			@RequestParam String name,
			@RequestParam String recipe) {

		Category category = categoryRepository.findById(categoryId).get();
		User user = userRepository.findById(account.getId()).get();

		//セッションにレシピを登録
		Recipe newRecipe = new Recipe();
		newRecipe.setName(name);
		newRecipe.setRecipe(recipe);
		newRecipe.setCategory(category);
		newRecipe.setUser(user);

		recipeRepository.save(newRecipe);

		return "redirect:/recipes";
	}

	//	//編集画面表示
	//	@GetMapping("/recipes/{id}/edit")
	//	public String edit(@PathVariable Integer id, Model model) {
	//
	//		Recipe recipe = recipeRepository.findById(id).get();
	//		List<Category> categories = categoryRepository.findAll();
	//
	//		model.addAttribute("recipe", recipe);
	//		model.addAttribute("categories", categories);
	//
	//		return "editRecipe";
	//	}
	//
	//	// レシピの更新処理
	//	@PostMapping("/recipes/{id}/edit")
	//	public String update(
	//			@PathVariable Integer id,
	//			@RequestParam Integer categoryId,
	//			@RequestParam(defaultValue = "") String name,
	//			@RequestParam(defaultValue = "") String recipe) {
	//
	//		Recipe updateRecipe = recipeRepository.findById(id).get();
	//		Category category = categoryRepository.findById(categoryId).get();
	//
	//		updateRecipe.setName(name);
	//		updateRecipe.setRecipe(recipe);
	//		updateRecipe.setCategory(category);
	//
	//		recipeRepository.save(updateRecipe);
	//
	//		return "redirect:/recipes";
	//	}
	//
	//	// レシピを削除
	//	@PostMapping("/recipes/{id}/delete")
	//	public String delete(@PathVariable Integer id) {
	//
	//		recipeRepository.deleteById(id);
	//
	//		return "redirect:/recipes";
	//	}

	// 編集画面表示
	@GetMapping("/recipes/{id}/edit")
	public String edit(@PathVariable Integer id, Model model) {

		// 1. 未ログインならログイン画面へ（または一覧へリダイレクト）
		if (account.getId() == null) {
			return "login";
		}

		Recipe recipe = recipeRepository.findById(id).get();
		List<Category> categories = categoryRepository.findAll();

		// 2. 投稿者とログインユーザーが一致しない場合は一覧に弾く
		if (!recipe.getUser().getId().equals(account.getId())) {
			return "redirect:/recipes";
		}

		model.addAttribute("recipe", recipe);
		model.addAttribute("categories", categories);

		return "editRecipe";
	}

	// レシピの更新処理
	@PostMapping("/recipes/{id}/edit")
	public String update(
			@PathVariable Integer id,
			@RequestParam Integer categoryId,
			@RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "") String recipe) {

		// 1. 未ログインチェック
		if (account.getId() == null) {
			return "login";
		}

		Recipe updateRecipe = recipeRepository.findById(id).get();

		// 2. 投稿者チェック（他人のレシピをPOSTで書き換えられるのを防ぐ）
		if (!updateRecipe.getUser().getId().equals(account.getId())) {
			return "redirect:/recipes";
		}

		Category category = categoryRepository.findById(categoryId).get();

		updateRecipe.setName(name);
		updateRecipe.setRecipe(recipe);
		updateRecipe.setCategory(category);

		recipeRepository.save(updateRecipe);

		return "redirect:/recipes";
	}

	// レシピを削除
	@PostMapping("/recipes/{id}/delete")
	public String delete(@PathVariable Integer id) {

		// 1. 未ログインチェック
		if (account.getId() == null) {
			return "login";
		}

		Recipe recipe = recipeRepository.findById(id).get();

		// 2. 投稿者チェック
		if (!recipe.getUser().getId().equals(account.getId())) {
			return "redirect:/recipes";
		}

		recipeRepository.deleteById(id);

		return "redirect:/recipes";
	}

	//マイページの表示
	@GetMapping("/mypage")
	public String mypage(Model model) {

		if (account.getId() == null) {
			return "redirect:/login";
		}

		List<Recipe> recipes = recipeRepository.findByUserId(account.getId());

		model.addAttribute("userName", account.getName());
		model.addAttribute("recipes", recipes);

		return "mypage";
	}
}
