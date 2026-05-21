package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

		//ログインしてるユーザーを取得
		User user = userRepository.findById(account.getId()).get();

		Recipe newRecipe = new Recipe();
		newRecipe.setName(name);
		newRecipe.setRecipe(recipe);
		newRecipe.setCategory(category);

		//投稿者をセット
		newRecipe.setUser(user);

		recipeRepository.save(newRecipe);

		return "redirect:/recipes";
	}

}
