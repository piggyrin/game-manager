
package com.example.gamemanager.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.gamemanager.entity.Game;
import com.example.gamemanager.repository.GameRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameRepository gameRepository;

    @GetMapping
    public String listGames(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            Model model) {

        List<Game> games;

        if ((keyword == null || keyword.isEmpty()) && (genre == null || genre.isEmpty())) {
            games = gameRepository.findAll();
        } else if (genre == null || genre.isEmpty()) {
            games = gameRepository.findByTitleContainingIgnoreCaseOrPublisherContainingIgnoreCase(keyword, keyword);
        } else if (keyword == null || keyword.isEmpty()) {
            games = gameRepository.findByGenreIgnoreCase(genre);
        } else {
            games = gameRepository.findByGenreIgnoreCaseAndTitleContainingIgnoreCaseOrPublisherContainingIgnoreCase(
                    genre, keyword, keyword);
        }

        if ("genre".equals(sort)) {
            games.sort(Comparator.comparing(Game::getGenre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        } else if ("title".equals(sort)) {
            games.sort(Comparator.comparing(Game::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        } else if ("releaseYear".equals(sort)) {
            games.sort(Comparator.comparing(Game::getReleaseYear, Comparator.nullsLast(Integer::compareTo)));
        }

        if ("desc".equalsIgnoreCase(sortDir)) {
            Collections.reverse(games);
        }

        Set<String> genres = gameRepository.findAll().stream()
                .map(Game::getGenre)
                .filter(g -> g != null && !g.isBlank())
                .collect(Collectors.toSet());

        model.addAttribute("games", games);
        model.addAttribute("keyword", keyword);
        model.addAttribute("genre", genre);
        model.addAttribute("genres", genres);
        model.addAttribute("sort", sort);
        model.addAttribute("sortDir", sortDir);

        return "games/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("game", new Game());
        return "games/form";
    }

    @PostMapping
    public String saveGame(@ModelAttribute Game game) {
        gameRepository.save(game);
        return "redirect:/games";
    }

    @GetMapping("/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        gameRepository.deleteById(id);
        return "redirect:/games";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Game game = gameRepository.findById(id).orElseThrow();
        model.addAttribute("game", game);
        return "games/form";
    }

    @PostMapping("/update")
    public String updateGame(@ModelAttribute Game game) {
        gameRepository.save(game);
        return "redirect:/games";
    }
}
