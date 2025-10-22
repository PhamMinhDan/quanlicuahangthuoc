package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.entity.Promotion;
import com.example.quanlicuahangthuoc.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/promotions")
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    @GetMapping
    public String getAllPromotions(Model model) {
        List<Promotion> promotions = promotionService.getAllPromotions();
        model.addAttribute("promotions", promotions);
        return "promotion/list";
    }
    
    @GetMapping("/search/name")
    public String searchByName(@RequestParam String name, Model model) {
        List<Promotion> promotions = promotionService.searchByName(name);
        model.addAttribute("promotions", promotions);
        model.addAttribute("searchType", "name");
        model.addAttribute("searchValue", name);
        return "promotion/list";
    }
    
    @GetMapping("/search/type")
    public String searchByType(@RequestParam String type, Model model) {
        List<Promotion> promotions = promotionService.searchByType(type);
        model.addAttribute("promotions", promotions);
        model.addAttribute("searchType", "type");
        model.addAttribute("searchValue", type);
        return "promotion/list";
    }
    
    @GetMapping("/search/name-and-type")
    public String searchByNameAndType(
            @RequestParam String name, 
            @RequestParam String type,
            Model model) {
        List<Promotion> promotions = promotionService.searchByNameAndType(name, type);
        model.addAttribute("promotions", promotions);
        model.addAttribute("searchType", "name-and-type");
        model.addAttribute("searchName", name);
        model.addAttribute("searchTypeValue", type);
        return "promotion/list";
    }
    
    @GetMapping("/search/name-or-type")
    public String searchByNameOrType(
            @RequestParam String name, 
            @RequestParam String type,
            Model model) {
        List<Promotion> promotions = promotionService.searchByNameOrType(name, type);
        model.addAttribute("promotions", promotions);
        model.addAttribute("searchType", "name-or-type");
        model.addAttribute("searchName", name);
        model.addAttribute("searchTypeValue", type);
        return "promotion/list";
    }
}