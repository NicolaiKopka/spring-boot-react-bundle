package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
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
    // TODO no test yet for Not Found
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable String id, Principal principal) {
        try {
            return ResponseEntity.ok(kanbanService.getItemById(id, principal.getName()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Item> editItem(@RequestBody Item item, Principal principal) {
        try {
            return ResponseEntity.ok(kanbanService.editItem(item, principal.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/next")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Item> moveToNext(@RequestBody Item item, Principal principal) {
        try {
            return ResponseEntity.ok(kanbanService.moveToNext(item, principal.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/prev")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Item> moveToPrev(@RequestBody Item item, Principal principal) {
        try {
            return ResponseEntity.ok(kanbanService.moveToPrev(item, principal.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody Item item, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(kanbanService.addItem(item, principal.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Item> deleteItem(@PathVariable String id) {
        try {
            return ResponseEntity.ok(kanbanService.deleteItem(id, "testUser"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
