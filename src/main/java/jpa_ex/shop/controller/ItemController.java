package jpa_ex.shop.controller;

import jpa_ex.shop.domain.item.Book;
import jpa_ex.shop.domain.item.Item;
import jpa_ex.shop.dto.UpdateItemDto;
import jpa_ex.shop.service.ItemService;
import jpa_ex.shop.web.BookForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/new")
    String createForm(Model model) {
        BookForm bookForm = new BookForm();
        model.addAttribute("form", bookForm);

        return "items/createItemForm";
    }

    @PostMapping("/new")
    String create(BookForm form) {
        Book item = new Book();
        item.setName(form.getName());
        item.setPrice(form.getPrice());
        item.setStockQuantity(form.getStockQuantity());
        item.setAuthor(form.getAuthor());
        item.setIsbn(form.getIsbn());

        itemService.saveItem(item);
        return "redirect:/items";
    }

    @GetMapping
    String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/{itemId}/edit")
    String editItemForm(BookForm form, @PathVariable Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());
        form.setPrice(item.getPrice());
        form.setName(item.getName());
        form.setStockQuantity(item.getStockQuantity());
        form.setId(item.getId());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("/{itemId}/edit")
    String editItem(@PathVariable Long itemId, @ModelAttribute("form") UpdateItemDto form) {
        itemService.updateItem(itemId, form);
        return "redirect:/items";
    }
}
