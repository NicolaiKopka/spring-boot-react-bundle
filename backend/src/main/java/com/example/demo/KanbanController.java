package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin
@RestController
@RequestMapping("/api/kanban")
@RequiredArgsConstructor
public class KanbanController {

    private final KanbanService kanbanService;

    @GetMapping
    public Collection<Item> getItemsByUser(Principal principal) {
        return kanbanService.getItemsByUser(principal.getName());
    }
    //no test yet for Not Found
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(kanbanService.getItemById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Item> editItem(@RequestBody Item item) {
        try {
            return ResponseEntity.ok(kanbanService.editItem(item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/next")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Item> moveToNext(@RequestBody Item item) {
        try {
            return ResponseEntity.ok(kanbanService.moveToNext(item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/prev")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Item> moveToPrev(@RequestBody Item item) {
        try {
            return ResponseEntity.ok(kanbanService.moveToPrev(item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    // TODO ask how to get a response entity created
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addItem(@RequestBody Item item, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(kanbanService.addItem(item, principal.getName()));
//            return ResponseEntity.ok(kanbanService.addItem(item));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Item> deleteItem(@PathVariable String id) {
        try {
            return ResponseEntity.ok(kanbanService.deleteItem(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
